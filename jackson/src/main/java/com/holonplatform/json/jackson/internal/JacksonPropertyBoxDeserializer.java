/*
 * Copyright 2000-2016 Holon TDCN.
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.holonplatform.core.Context;
import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.Property.PropertyReadException;

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
	@SuppressWarnings("unchecked")
	@Override
	public PropertyBox deserialize(JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		// get property set
		final PropertySet<?> propertySet = Context.get().resource(PropertySet.CONTEXT_KEY, PropertySet.class)
				.orElseThrow(
						() -> new JsonParseException(parser, "Missing PropertySet instance to build a PropertyBox. "
								+ "A PropertySet instance must be available as context resource to perform PropertyBox deserialization."));

		// map to store
		Map<String, Object> propertyValues = new HashMap<>();

		// read tree
		JsonNode node = parser.getCodec().readTree(parser);

		if (node.isObject()) {
			// get fields
			Iterator<Entry<String, JsonNode>> fi = node.fields();
			while (fi.hasNext()) {
				Entry<String, JsonNode> entry = fi.next();
				String fieldName = entry.getKey();
				JsonNode n = entry.getValue();

				Object value = null;

				if (!n.isNull()) {
					if (n.isValueNode()) {
						if (n.isNumber()) {
							value = n.numberValue();
						} else if (n.isBoolean()) {
							value = Boolean.valueOf(n.booleanValue());
						} else if (n.isTextual()) {
							value = n.textValue();
							// check if it is a date/time value
							Class<?> ft = getFieldType(propertySet, fieldName);
							if (ft != Object.class) {
								JsonDeserializer<?> dateTimeDeserializer = DateDeserializers.find(ft, ft.getName());
								if (dateTimeDeserializer != null) {
									try (JsonParser np = n.traverse(parser.getCodec())) {
										np.nextToken();
										value = dateTimeDeserializer.deserialize(np, ctxt);
									}
								}
							}
						}
					} else {
						try (JsonParser np = n.traverse(parser.getCodec())) {
							value = np.readValueAs(getFieldType(propertySet, fieldName));
						}
					}
				}
				propertyValues.put(fieldName, value);
			}

		}

		try {
			PropertyBox.Builder builder = PropertyBox.builder(propertySet).invalidAllowed(true);
			propertySet.forEach(p -> {
				getPathName(p).ifPresent(n -> {
					final Object value = checkupPropertyValue(p, propertyValues.get(n));
					if (value != null) {
						builder.setIgnoreReadOnly(p, value);
					}
				});
			});
			return builder.build();
		} catch (Exception e) {
			throw new JsonMappingException(parser, "Failed to deserialize properties into a PropertyBox", e);
		}
	}

	@SuppressWarnings("rawtypes")
	private static Class<?> getFieldType(PropertySet<?> set, String fieldName) {
		return set.stream().filter(p -> Path.class.isAssignableFrom(p.getClass())).map(p -> (Path) p)
				.filter(q -> fieldName.equals(q.getName())).map(qp -> qp.getType()).findFirst().orElse(Object.class);
	}

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
			if (TypeUtils.isTemporal(type) && TypeUtils.isNumber(value.getClass())) {
				try {
					Long time = ConversionUtils.convertNumberToTargetClass((Number) value, long.class);
					if (time == null) {
						throw new PropertyReadException(property, "Property conversion failed [" + property
								+ "]: Failed to convert numeric value " + value + " into a date time type");
					}
					if (LocalTime.class.isAssignableFrom(type)) {
						return LocalTime.ofSecondOfDay(time.longValue());
					}
					if (LocalDateTime.class.isAssignableFrom(type)) {
						return LocalDateTime.ofEpochSecond(time.longValue(), 0, ZoneOffset.UTC);
					}
					if (LocalDate.class.isAssignableFrom(type)) {
						return LocalDate.ofEpochDay(time.longValue());
					}
				} catch (IllegalArgumentException e) {
					throw new PropertyReadException(property, "Property conversion failed [" + property + "]", e);
				}
			}
			if (TypeUtils.isDate(type) && TypeUtils.isNumber(value.getClass())) {
				try {
					Long time = ConversionUtils.convertNumberToTargetClass((Number) value, long.class);
					if (time == null) {
						throw new RuntimeException(
								"Failed to convert numeric value " + value + " into a date time type");
					}
					return new Date(time.longValue());
				} catch (IllegalArgumentException e) {
					throw new PropertyReadException(property, "Property conversion failed [" + property + "]", e);
				}
			}
			if (TypeUtils.isCalendar(type) && TypeUtils.isNumber(value.getClass())) {
				try {
					Long time = ConversionUtils.convertNumberToTargetClass((Number) value, long.class);
					if (time == null) {
						throw new RuntimeException(
								"Failed to convert numeric value " + value + " into a date time type");
					}
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(time.longValue());
					return c;
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
		}
		return value;
	}

	/**
	 * If given property is a {@link Path}, returns the path name.
	 * @param property Property (must be not null)
	 * @return The path name if given property is a {@link Path}, an empty Optional otherwise.
	 */
	private static Optional<String> getPathName(Property<?> property) {
		if (property instanceof Path) {
			return Optional.ofNullable(((Path<?>) property).getName());
		}
		return Optional.empty();
	}

}
