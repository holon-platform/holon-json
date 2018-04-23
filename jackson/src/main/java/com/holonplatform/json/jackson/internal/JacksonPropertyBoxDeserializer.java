/*
 * Copyright 2016-2017 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.json.jackson.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.holonplatform.core.Context;
import com.holonplatform.core.Path;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.CalendarUtils;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.PathPropertySetAdapter;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyConfiguration;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.exceptions.JsonDeserializationException;
import com.holonplatform.json.internal.JsonLogger;

/**
 * Jackson JSON deserializer to handle {@link PropertyBox} deserialization
 * 
 * @since 5.0.0
 */
public class JacksonPropertyBoxDeserializer extends JsonDeserializer<PropertyBox> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = JsonLogger.create();

	/*
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
	 * com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public PropertyBox deserialize(JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		// get property set
		final PropertySet<?> propertySet = Context.get().resource(PropertySet.CONTEXT_KEY, PropertySet.class)
				.orElseThrow(
						() -> new JsonParseException(parser, "Missing PropertySet instance to build a PropertyBox. "
								+ "A PropertySet instance must be available as context resource to perform PropertyBox deserialization."));
		// read tree
		JsonNode node = parser.getCodec().readTree(parser);

		if (node == null) {
			return null;
		}

		try {
			// deserialize as PropertyBox
			return deserializePropertyBox(parser, node, propertySet);
		} catch (JsonDeserializationException e) {
			throw new JsonMappingException(parser, "Failed to deserialize JSON node as a PropertyBox", e);
		}
	}

	/**
	 * Deserialize given JSON node into a {@link PropertyBox} instance.
	 * @param parser JSON parser
	 * @param node JSON node to deserialize
	 * @param propertySet PropertySet to use
	 * @return Deserialized {@link PropertyBox} instance
	 * @throws JsonDeserializationException If an error occurred
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static PropertyBox deserializePropertyBox(JsonParser parser, JsonNode node, PropertySet<?> propertySet)
			throws JsonDeserializationException {

		if (!node.isObject()) {
			throw new JsonDeserializationException(
					"Failed to deserialize JSON node [" + node.toString() + "]: node must be a JSON object");
		}
		try {
			final PropertyBox.Builder builder = PropertyBox.builder(propertySet).invalidAllowed(true);
			final PathPropertySetAdapter adapter = PathPropertySetAdapter.create(propertySet);
			for (Property property : propertySet) {
				Optional<Path> propertyPath = adapter.getPath(property);
				propertyPath.ifPresent(path -> {
					Optional<?> value = deserializePath(parser, node, ((Property<?>) property).getConfiguration(),
							path);
					if (value.isPresent()) {
						builder.setIgnoreReadOnly(property, value.get());
					} else {
						LOGGER.debug(() -> "Property [" + property + "] value not found in JSON node [" + node
								+ "] - skip PropertyBox value setting");
					}
				});
			}
			return builder.build();
		} catch (JsonDeserializationException e) {
			throw e;
		} catch (Exception e) {
			throw new JsonDeserializationException(
					"Failed to deserialize JSON node [" + node.toString() + "] as a PropertyBox", e);
		}
	}

	/**
	 * Deserialize a path value from given JSON node.
	 * @param <T> Path type
	 * @param parser Parser
	 * @param node Json none
	 * @param config Property configuration which corresponds to given path
	 * @param path Path to deserialize
	 * @return Deserialized path value, if available
	 * @throws JsonDeserializationException IF an error occurred
	 */
	private static <T> Optional<T> deserializePath(JsonParser parser, JsonNode node, PropertyConfiguration config,
			Path<T> path) throws JsonDeserializationException {
		// get the path name
		final List<String> pathNames = getPathNameHierarchy(path);
		if (!pathNames.isEmpty()) {
			return getJsonNode(node, pathNames)
					.flatMap(n -> deserializeField(parser, n, config, path, pathNames.get(pathNames.size() - 1)));
		}
		return Optional.empty();
	}

	/**
	 * Get the Json node which corresponds to given path names hierarchy.
	 * @param node Root node
	 * @param pathNames Path names hierarchy
	 * @return the Json node which corresponds to given path names hierarchy, if available
	 */
	private static Optional<JsonNode> getJsonNode(JsonNode node, List<String> pathNames) {
		JsonNode currentNode = node;
		for (String name : pathNames) {
			if (currentNode == null) {
				break;
			}
			currentNode = currentNode.get(name);
		}
		return Optional.ofNullable(currentNode);
	}

	/**
	 * Deserialize the Json object field whith given name.
	 * @param <T> Path type
	 * @param parser Parser
	 * @param node Json node from which to deserialize the field value
	 * @param config Property configuration
	 * @param path Path which corresponds to given field name
	 * @param fieldName Field name to deserialize
	 * @return Deserialized field value, if available
	 * @throws JsonDeserializationException If an error occurred
	 */
	@SuppressWarnings("unchecked")
	private static <T> Optional<T> deserializeField(JsonParser parser, JsonNode node, PropertyConfiguration config,
			Path<T> path, String fieldName) throws JsonDeserializationException {
		if (!node.isNull()) {
			if (node.isValueNode()) {
				try {
					return Optional
							.ofNullable(deserializeValue(path, parser.getCodec().treeToValue(node, path.getType())));
				} catch (JsonProcessingException e) {
					throw new JsonDeserializationException("Failed to deserialize path [" + path
							+ "] using field name [" + fieldName + "] in JSON node [" + node + "]", e);
				}
			} else {
				// nested object, check PropertyBox
				if (PropertyBox.class.isAssignableFrom(path.getType())) {
					return Optional.ofNullable((T) deserializePropertyBox(parser, node,
							config.getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE)
									.orElseThrow(() -> new JsonDeserializationException(
											"Failed to deserialize PropertyBox type path [" + path
													+ "] for JSON field [" + fieldName
													+ "]: missing PropertySet. Check property configuration attribute ["
													+ PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE.getKey() + "]"))));
				} else {
					// traverse node using default deserializers
					try (JsonParser np = node.traverse(parser.getCodec())) {
						return Optional.ofNullable(np.readValueAs(path.getType()));
					} catch (IOException e) {
						throw new JsonDeserializationException("Failed to deserialize path [" + path
								+ "] using field name [" + fieldName + "] in JSON node [" + node + "]", e);
					}
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Get the path names hierarchy from given path, ujsing any parent path and splitting the path name if a dot
	 * notation is detected.
	 * @param path Path
	 * @return the path names hierarchy
	 */
	private static List<String> getPathNameHierarchy(Path<?> path) {
		final String pathName = path.relativeName();
		if (pathName == null) {
			return Collections.emptyList();
		}
		if (pathName.indexOf('.') < 1) {
			return Collections.singletonList(pathName);
		}
		return Arrays.asList(pathName.split("\\."));
	}

	/**
	 * Deserialize the value associated to given path, performing any suitable conversion if required.
	 * @param <T> Path type
	 * @param path Path
	 * @param value Path value
	 * @return Deserialized path value
	 * @throws JsonDeserializationException If an error occurred
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T deserializeValue(Path<T> path, T value) throws JsonDeserializationException {
		if (value != null) {
			final Class<? extends T> type = path.getType();
			try {
				if (TypeUtils.isEnum(type)) {
					return (T) ConversionUtils.convertEnumValue((Class<Enum>) type, value);

				}
				if (TypeUtils.isNumber(type) && TypeUtils.isNumber(value.getClass())) {
					return (T) ConversionUtils.convertNumberToTargetClass((Number) value, (Class<Number>) type);

				}
				if (TypeUtils.isDate(value.getClass())
						&& path.getTemporalType().orElse(TemporalType.DATE_TIME) == TemporalType.DATE) {
					// reset time
					return (T) CalendarUtils.floorTime((Date) value);
				}
			} catch (Exception e) {
				throw new JsonDeserializationException(
						"Failed to deserialize path [" + path + "] value [" + value + "]", e);
			}
		}
		return value;
	}

}
