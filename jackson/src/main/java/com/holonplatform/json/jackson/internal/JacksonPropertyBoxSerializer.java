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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.temporal.TemporalType;

/**
 * Jackson JSON serializer to handle {@link PropertyBox} serialization
 * 
 * @since 5.0.0
 */
public class JacksonPropertyBoxSerializer extends JsonSerializer<PropertyBox> {

	private final static String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private final static String ISO_DATE_FORMAT = "yyyy-MM-dd";
	private final static String ISO_TIME_FORMAT = "HH:mm:ss";
	

	/*
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object,
	 * com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
	 */
	@Override
	public void serialize(PropertyBox box, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		gen.writeStartObject();

		for (Property<?> property : box) {
			if (property instanceof Path) {
				String name = ((Path<?>) property).getName();
				Object value = box.getValue(property);
				if (value != null) {
					if (TypeUtils.isDate(property.getType())) {
						serializeDate(gen, property, name, (Date) value);
					} else if (TypeUtils.isCalendar(property.getType())) {
						serializeDate(gen, property, name, ((Calendar) value).getTime());
					} else {
						gen.writeObjectField(name, value);
					}
				}
			}
		}

		gen.writeEndObject();
	}

	/**
	 * Serialize a {@link Date} value according to property {@link TemporalType}, if available.
	 * @param gen JsonGenerator
	 * @param property Property
	 * @param name Field name
	 * @param date Date value
	 * @throws IOException If an error occurred
	 */
	private static final void serializeDate(JsonGenerator gen, Property<?> property, String name, Date date)
			throws IOException {
		if (date != null) {
			// Check property configuration
			final TemporalType temporalType = property.getConfiguration().getTemporalType()
					.orElse(TemporalType.DATE_TIME);
			switch (temporalType) {
			case DATE:
				gen.writeStringField(name, new SimpleDateFormat(ISO_DATE_FORMAT).format(date));
				break;
			case TIME:
				gen.writeStringField(name, new SimpleDateFormat(ISO_TIME_FORMAT).format(date));
				break;
			case DATE_TIME:
			default:
				gen.writeStringField(name, new SimpleDateFormat(ISO_DATETIME_FORMAT).format(date));
				break;

			}
		}
	}

}
