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
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.config.JsonConfigProperties;
import com.holonplatform.json.config.PropertyBoxSerializationMode;
import com.holonplatform.json.datetime.CurrentSerializationTemporalType;
import com.holonplatform.json.exceptions.JsonSerializationException;
import com.holonplatform.json.model.JsonPropertySetSerializationTreeResolver;
import com.holonplatform.json.model.PropertySetSerializationNode;
import com.holonplatform.json.model.PropertySetSerializationTree;

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
	 * Serialize a PropertyBox as a {@link PropertySetSerializationNode} tree.
	 * @param context Serialization context
	 * @param obj Json object to use
	 * @param propertyBox PropertyBox to serialize
	 * @param nodes Serialization tree nodes
	 * @throws JsonSerializationException If an error occurred
	 */
	private static void serializePropertyBoxNodes(JsonSerializationContext context, JsonObject obj,
			PropertyBox propertyBox, Iterable<PropertySetSerializationNode> nodes) throws JsonSerializationException {
		for (PropertySetSerializationNode node : nodes) {
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
			PropertyBox propertyBox, PropertySetSerializationNode node) throws JsonSerializationException {
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

}
