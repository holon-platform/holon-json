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
package com.holonplatform.json.gson.internal;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.holonplatform.core.Path;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.config.JsonConfigProperties;
import com.holonplatform.json.config.PropertyBoxSerializationMode;
import com.holonplatform.json.datetime.CurrentSerializationTemporalType;
import com.holonplatform.json.exceptions.JsonSerializationException;
import com.holonplatform.json.internal.support.DefaultPropertyBoxSerializationNode;
import com.holonplatform.json.internal.support.PropertyBoxSerializationNode;

/**
 * Gson serializer to handle {@link PropertyBox} serialization.
 * 
 * @since 5.0.0
 */
public class GsonPropertyBoxSerializer implements JsonSerializer<PropertyBox> {

	/**
	 * Serialization mode
	 */
	private final PropertyBoxSerializationMode propertyBoxSerializationMode;

	/**
	 * Constructor.
	 * @param serializationMode Serialization mode
	 */
	public GsonPropertyBoxSerializer(PropertyBoxSerializationMode serializationMode) {
		super();
		this.propertyBoxSerializationMode = (serializationMode != null) ? serializationMode
				: PropertyBoxSerializationMode.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
	 * com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(PropertyBox propertyBox, Type typeOfSrc, JsonSerializationContext context) {

		// get serialization mode
		final PropertyBoxSerializationMode serializationMode = getPropertyBoxSerializationMode(propertyBox);

		// JSON object
		final JsonObject obj = new JsonObject();

		// serialize the PropertyBox instance
		serializePropertyBoxNodes(context, obj, propertyBox, getSerializationTree(propertyBox, serializationMode));

		return obj;
	}

	/**
	 * Get the {@link PropertyBoxSerializationMode} to use. Check {@link PropertyBox} configuration attributes using
	 * {@link JsonConfigProperties#PROPERTYBOX_SERIALIZATION_MODE}. If not available, the construction time value is
	 * used.
	 * @param propertyBox The PropertBox to serialize
	 * @return The {@link PropertyBoxSerializationMode} to use
	 */
	private PropertyBoxSerializationMode getPropertyBoxSerializationMode(PropertyBox propertyBox) {
		// check config parameter
		if (propertyBox.getConfiguration().hasParameter(JsonConfigProperties.PROPERTYBOX_SERIALIZATION_MODE)) {
			return propertyBox.getConfiguration().getParameter(JsonConfigProperties.PROPERTYBOX_SERIALIZATION_MODE,
					PropertyBoxSerializationMode.getDefault());
		}
		return propertyBoxSerializationMode;
	}

	/**
	 * Serialize a PropertyBox as a {@link PropertyBoxSerializationNode} tree.
	 * @param context Serialization context
	 * @param obj Json object to use
	 * @param propertyBox PropertyBox to serialize
	 * @param nodes Serialization tree nodes
	 * @throws JsonSerializationException If an error occurred
	 */
	private static void serializePropertyBoxNodes(JsonSerializationContext context, JsonObject obj,
			PropertyBox propertyBox, List<PropertyBoxSerializationNode> nodes) throws JsonSerializationException {
		for (PropertyBoxSerializationNode node : nodes) {
			serializePropertyBoxNode(context, obj, propertyBox, node);
		}
	}

	/**
	 * Serialize the node of a PropertyBox serialization tree.
	 * @param context Serialization context
	 * @param obj Json object to use
	 * @param propertyBox PropertyBox to serialize
	 * @param node The PropertyBox node to serialize
	 * @throws JsonSerializationException If an error occurred
	 */
	private static void serializePropertyBoxNode(JsonSerializationContext context, JsonObject obj,
			PropertyBox propertyBox, PropertyBoxSerializationNode node) throws JsonSerializationException {
		if (node.getProperty().isPresent()) {
			serializePropertyBoxProperty(context, obj, propertyBox, node.getProperty().get(), node.getName());
		} else {
			try {
				// add nested object
				JsonObject nested = new JsonObject();
				obj.add(node.getName(), nested);
				// serialize nested object
				serializePropertyBoxNodes(context, nested, propertyBox, node.getChildren());
			} catch (JsonSerializationException se) {
				throw se;
			} catch (Exception e) {
				throw new JsonSerializationException(
						"Failed to serialize PropertyBox [" + propertyBox + "] for field name [" + node.getName() + "]",
						e);
			}
		}
	}

	/**
	 * Serialize a PropertyBox property value.
	 * @param context Serialization context
	 * @param obj Json object to use
	 * @param propertyBox PropertyBox to which the property belongs
	 * @param property Property to serialize
	 * @param name Property serialization name
	 * @throws JsonSerializationException If an error occurred
	 */
	private static void serializePropertyBoxProperty(JsonSerializationContext context, JsonObject obj,
			PropertyBox propertyBox, Property<?> property, String name) throws JsonSerializationException {
		try {
			// check property value is not null
			propertyBox.getValueIfPresent(property).ifPresent(value -> {
				// check temporal type
				Optional<TemporalType> temporalType = property.getConfiguration().getTemporalType();
				try {
					temporalType.ifPresent(tt -> {
						CurrentSerializationTemporalType.setCurrentTemporalType(tt);
					});
					try {
						// add JSON property value
						obj.add(name, context.serialize(value));
					} catch (Exception e) {
						throw new JsonSerializationException("Failed to serialize property [" + property
								+ "] using name [" + name + "] and value [" + value + "]", e);
					}
				} finally {
					temporalType.ifPresent(tt -> {
						CurrentSerializationTemporalType.removeCurrentTemporalType();
					});
				}
			});
		} catch (Exception e) {
			throw new JsonSerializationException(
					"Failed to serialize Property [" + property + "] using field name [" + name + "]", e);
		}
	}

	/**
	 * Build the PropertyBox serialization tree.
	 * @param propertyBox PropertyBox to serialize
	 * @param serializationMode Serialization mode
	 * @return the PropertyBox serialization tree
	 */
	private static List<PropertyBoxSerializationNode> getSerializationTree(PropertyBox propertyBox,
			PropertyBoxSerializationMode serializationMode) {
		List<PropertyBoxSerializationNode> tree = new LinkedList<>();

		List<Property<?>> properties = new LinkedList<>();
		propertyBox.forEach(p -> properties.add(p));

		for (Property<?> property : propertyBox) {
			getSerializationNode(properties, property, serializationMode).ifPresent(n -> {
				tree.add(n);
			});
		}
		return tree;
	}

	/**
	 * Get the {@link PropertyBoxSerializationNode} for given property.
	 * @param properties Available properties
	 * @param property Property to parse
	 * @param serializationMode Serialization mode
	 * @return The optional {@link PropertyBoxSerializationNode} which corresponds to given property
	 */
	private static Optional<PropertyBoxSerializationNode> getSerializationNode(List<Property<?>> properties,
			Property<?> property, PropertyBoxSerializationMode serializationMode) {
		final List<String> pathNames = getPropertySerializationHierarchy(property, serializationMode);
		if (!pathNames.isEmpty()) {
			if (pathNames.size() == 1) {
				properties.remove(property);
				return Optional.of(new DefaultPropertyBoxSerializationNode(pathNames.get(0), property));
			} else {
				return getSerializationNode(properties, Collections.singletonList(pathNames.get(0)), serializationMode);
			}
		}
		return Optional.empty();
	}

	/**
	 * Get the {@link PropertyBoxSerializationNode} which corresponds to given parent path names hierarchy.
	 * @param properties Available properties
	 * @param parentPathNames Parent path names hierarchy
	 * @param serializationMode Serialization mode
	 * @return Optional {@link PropertyBoxSerializationNode} which corresponds to given parent path names hierarchy
	 */
	private static Optional<PropertyBoxSerializationNode> getSerializationNode(List<Property<?>> properties,
			List<String> parentPathNames, PropertyBoxSerializationMode serializationMode) {
		// check valid path names
		if (parentPathNames == null || parentPathNames.isEmpty()) {
			return Optional.empty();
		}
		// build node
		DefaultPropertyBoxSerializationNode node = new DefaultPropertyBoxSerializationNode(
				parentPathNames.get(parentPathNames.size() - 1));
		for (Property<?> property : properties) {
			final List<String> pathNames = getPropertySerializationHierarchy(property, serializationMode);
			if (pathNames.size() > parentPathNames.size()
					&& pathNamesEquals(pathNames.subList(0, parentPathNames.size()), parentPathNames)) {
				// check hierarchy
				if (pathNames.size() == parentPathNames.size() + 1) {
					getSerializationPropertyName(property).ifPresent(name -> {
						node.addChild(new DefaultPropertyBoxSerializationNode(name, property));
					});
				} else {
					List<String> parents = new LinkedList<>();
					parents.addAll(parentPathNames);
					parents.add(pathNames.get(parentPathNames.size()));
					// check sub hierarchy
					getSerializationNode(properties, parents, serializationMode).ifPresent(n -> {
						node.addChild(n);
					});
				}
			}
		}
		if (!node.getChildren().isEmpty()) {
			// remove processed properties
			node.getChildren().forEach(c -> c.getProperty().ifPresent(p -> properties.remove(p)));
			return Optional.of(node);
		}
		return Optional.empty();
	}

	/**
	 * CHecks if given path names hierarchies are equal.
	 * @param p1 First path name hierarchy
	 * @param p2 Second path name hierarchy
	 * @return <code>true</code> if given path names hierarchies are equal
	 */
	private static boolean pathNamesEquals(List<String> p1, List<String> p2) {
		if (p1 == null || p2 == null) {
			return false;
		}
		for (int i = 0; i < p1.size(); i++) {
			if (p1.get(i) == null || !p1.get(i).equals(p2.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the property serialization name, only if the property must be included in serialization according to given
	 * {@link PropertyBoxSerializationMode}.
	 * @param property The property for which to obtain the serialization name
	 * @param serializationMode Serialization mode
	 * @return the property serialization name, if available and if the property must be included in serialization
	 *         according to given serialization mode
	 */
	private static List<String> getPropertySerializationHierarchy(Property<?> property,
			PropertyBoxSerializationMode serializationMode) {
		final boolean isPath = Path.class.isAssignableFrom(property.getClass());
		if (serializationMode == PropertyBoxSerializationMode.PATH && !isPath) {
			return Collections.emptyList();
		}

		if (isPath) {
			return getPathNameHierarchy((Path<?>) property);
		}
		if (property.getName() != null) {
			return Collections.singletonList(property.getName());
		}

		return Collections.emptyList();
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

	private static Optional<String> getSerializationPropertyName(Property<?> property) {
		List<String> names = (Path.class.isAssignableFrom(property.getClass()))
				? getPathNameHierarchy((Path<?>) property)
				: Collections.singletonList(property.getName());
		if (!names.isEmpty()) {
			return Optional.ofNullable(names.get(names.size() - 1));
		}
		return Optional.empty();
	}

}
