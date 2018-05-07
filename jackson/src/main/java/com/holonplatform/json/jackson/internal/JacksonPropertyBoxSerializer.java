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
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.config.JsonConfigProperties;
import com.holonplatform.json.config.PropertyBoxSerializationMode;
import com.holonplatform.json.datetime.CurrentSerializationTemporalType;
import com.holonplatform.json.exceptions.JsonSerializationException;
import com.holonplatform.json.internal.JsonLogger;
import com.holonplatform.json.model.JsonPropertySetSerializationTreeResolver;
import com.holonplatform.json.model.PropertySetSerializationNode;
import com.holonplatform.json.model.PropertySetSerializationTree;

/**
 * Jackson JSON serializer to handle {@link PropertyBox} serialization
 * 
 * @since 5.0.0
 */
public class JacksonPropertyBoxSerializer extends JsonSerializer<PropertyBox> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = JsonLogger.create();

	/*
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object,
	 * com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
	 */
	@Override
	public void serialize(PropertyBox propertyBox, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {

		// get serialization mode
		final PropertyBoxSerializationMode serializationMode = getPropertyBoxSerializationMode(propertyBox, serializers);

		// JSON object start
		gen.writeStartObject();

		// serialize the PropertyBox instance
		serializePropertyBoxNodes(gen, propertyBox, getSerializationTree(propertyBox, serializationMode));

		// JSON object end
		gen.writeEndObject();
	}
	
	/**
	 * Build the PropertySet serialization tree for given PropertyBox.
	 * @param propertyBox PropertyBox to serialize
	 * @param serializationMode Serialization mode
	 * @return the PropertyBox serialization tree
	 */
	private static PropertySetSerializationTree getSerializationTree(PropertyBox propertyBox,
			PropertyBoxSerializationMode serializationMode) {
		return JsonPropertySetSerializationTreeResolver.getDefault().resolve(propertyBox, serializationMode);
	}

	/**
	 * Serialize a PropertyBox as a {@link PropertySetSerializationNode} tree.
	 * @param gen Json generator to use
	 * @param propertyBox PropertyBox to serialize
	 * @param nodes Serialization tree nodes
	 * @throws JsonSerializationException If an error occurred
	 */
	private static void serializePropertyBoxNodes(JsonGenerator gen, PropertyBox propertyBox,
			Iterable<PropertySetSerializationNode> nodes) throws JsonSerializationException {
		for (PropertySetSerializationNode node : nodes) {
			serializePropertyBoxNode(gen, propertyBox, node);
		}
	}

	/**
	 * Serialize the node of a PropertyBox serialization tree.
	 * @param gen Json generator to use
	 * @param propertyBox PropertyBox to serialize
	 * @param node The PropertyBox node to serialize
	 * @throws JsonSerializationException If an error occurred
	 */
	private static void serializePropertyBoxNode(JsonGenerator gen, PropertyBox propertyBox,
			PropertySetSerializationNode node) throws JsonSerializationException {
		if (node.getProperty().isPresent()) {
			serializePropertyBoxProperty(gen, propertyBox, node.getProperty().get(), node.getName());
		} else {
			try {
				gen.writeObjectFieldStart(node.getName());
				// nested object
				serializePropertyBoxNodes(gen, propertyBox, node.getChildren());
				gen.writeEndObject();
			} catch (IOException e) {
				throw new JsonSerializationException(
						"Failed to serialize PropertyBox [" + propertyBox + "] for field name [" + node.getName() + "]",
						e);
			}
		}
	}

	/**
	 * Serialize a PropertyBox property value.
	 * @param gen Json generator to use
	 * @param propertyBox PropertyBox to which the property belongs
	 * @param property Property to serialize
	 * @param name Property serialization name
	 * @throws JsonSerializationException If an error occurred
	 */
	private static void serializePropertyBoxProperty(JsonGenerator gen, PropertyBox propertyBox, Property<?> property,
			String name) throws JsonSerializationException {
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
						// write JSON property value
						gen.writeObjectField(name, value);
					} catch (IOException e) {
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
	 * Get the {@link PropertyBoxSerializationMode} to use. Check {@link PropertyBox} configuration attributes using
	 * {@link JsonConfigProperties#PROPERTYBOX_SERIALIZATION_MODE}. If not available, the
	 * {@link JsonConfigProperties#PROPERTYBOX_SERIALIZATION_MODE_ATTRIBUTE_NAME} is checked from serialization context.
	 * If no value is available, the {@link PropertyBoxSerializationMode#getDefault()} value is returned.
	 * @param propertyBox The PropertBox to serialize
	 * @param serializers Serialization context
	 * @return The {@link PropertyBoxSerializationMode} to use
	 */
	private static PropertyBoxSerializationMode getPropertyBoxSerializationMode(PropertyBox propertyBox,
			SerializerProvider serializers) {
		// check config parameter
		if (propertyBox.getConfiguration().hasParameter(JsonConfigProperties.PROPERTYBOX_SERIALIZATION_MODE)) {
			return propertyBox.getConfiguration().getParameter(JsonConfigProperties.PROPERTYBOX_SERIALIZATION_MODE,
					PropertyBoxSerializationMode.getDefault());
		}
		// check context parameter
		Object value = serializers.getAttribute(JsonConfigProperties.PROPERTYBOX_SERIALIZATION_MODE_ATTRIBUTE_NAME);
		if (value != null) {
			if (value instanceof PropertyBoxSerializationMode) {
				return (PropertyBoxSerializationMode) value;
			} else {
				LOGGER.warn("The serialization context attribute ["
						+ JsonConfigProperties.PROPERTYBOX_SERIALIZATION_MODE_ATTRIBUTE_NAME
						+ "] is not of expected type. Expected [" + PropertyBoxSerializationMode.class.getName()
						+ "] got [" + value.getClass().getName() + "]");
			}
		}
		return PropertyBoxSerializationMode.getDefault();
	}

}
