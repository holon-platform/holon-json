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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
import com.holonplatform.core.internal.utils.CalendarUtils;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.Property.PropertyReadException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.temporal.TemporalType;

/**
 * Jackson JSON deserializer to handle {@link PropertyBox} deserialization
 * 
 * @since 5.0.0
 */
public class JacksonPropertyBoxDeserializer extends JsonDeserializer<PropertyBox> {

	/*
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
	 * com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public PropertyBox deserialize(JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		// get property set
		final PropertySet<?> propertySet = Context.get().resource(PropertySet.CONTEXT_KEY, PropertySet.class)
				.orElseThrow(
						() -> new JsonParseException(parser, "Missing PropertySet instance to build a PropertyBox. "
								+ "A PropertySet instance must be available as context resource to perform PropertyBox deserialization."));

		// map to store
		final Map<Property, Object> propertyValues = new HashMap<>();

		// read tree
		JsonNode node = parser.getCodec().readTree(parser);

		if (node.isObject()) {
			// get fields
			Iterator<Entry<String, JsonNode>> fi = node.fields();
			while (fi.hasNext()) {
				final Entry<String, JsonNode> entry = fi.next();
				final String fieldName = entry.getKey();
				final JsonNode n = entry.getValue();

				if (!n.isNull()) {
					Optional<Property> property = getProperty(propertySet, fieldName);
					if (property.isPresent()) {
						if (n.isValueNode()) {
							// simple value
							propertyValues.put(property.get(),
									parser.getCodec().treeToValue(n, property.get().getType()));
						} else {
							// nested object
							try (JsonParser np = n.traverse(parser.getCodec())) {
								propertyValues.put(property.get(), np.readValueAs(property.get().getType()));
							}
						}
					}
				}
			}

		}

		try {
			final PropertyBox.Builder builder = PropertyBox.builder(propertySet).invalidAllowed(true);
			propertySet.forEach(p -> {
				final Object value = checkupPropertyValue(p, propertyValues.get(p));
				if (value != null) {
					builder.setIgnoreReadOnly(p, value);
				}
			});
			return builder.build();
		} catch (Exception e) {
			throw new JsonMappingException(parser, "Failed to deserialize properties into a PropertyBox", e);
		}
	}

	/**
	 * Get the {@link Property} which corresponds to to the field named <code>fieldName</code> in given property set, if
	 * available.
	 * @param set Property set
	 * @param fieldName Field name
	 * @return Optional {@link Property}
	 */
	@SuppressWarnings("rawtypes")
	private static Optional<Property> getProperty(PropertySet<?> set, String fieldName) {
		return set.stream().filter(p -> Path.class.isAssignableFrom(p.getClass())).map(p -> (Path) p)
				.filter(q -> fieldName.equals(q.getName())).map(qp -> (Property) qp).findFirst();
	}

	/**
	 * Checkup property value, applying conversions if required.
	 * @param property Property
	 * @param value Property value
	 * @return Sanitized property value
	 * @throws PropertyReadException Error reading property
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Object checkupPropertyValue(Property property, Object value) throws PropertyReadException {
		if (value != null) {

			final Class<?> type = property.getType();

			if (TypeUtils.isEnum(type)) {
				try {
					return ConversionUtils.convertEnumValue((Class<Enum>) type, value);
				} catch (IllegalArgumentException e) {
					throw new PropertyReadException(property, "Property conversion failed [" + property + "]", e);
				}
			}

			if (TypeUtils.isNumber(type) && TypeUtils.isNumber(value.getClass())) {
				try {
					return ConversionUtils.convertNumberToTargetClass((Number) value, (Class<Number>) type);
				} catch (IllegalArgumentException e) {
					throw new PropertyReadException(property, "Property conversion failed [" + property + "]", e);
				}
			}

			if (TypeUtils.isDate(value.getClass()) && property.getConfiguration().getTemporalType()
					.orElse(TemporalType.DATE_TIME) == TemporalType.DATE) {
				// reset time
				return CalendarUtils.floorTime((Date) value);
			}
		}
		return value;
	}

}
