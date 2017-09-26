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
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Calendar;

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

	private final static String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

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
						gen.writeStringField(name, new SimpleDateFormat(ISO_DATE_FORMAT).format(value));
					} else if (TypeUtils.isCalendar(property.getType())) {
						gen.writeStringField(name,
								new SimpleDateFormat(ISO_DATE_FORMAT).format(((Calendar) value).getTime()));
					} else if (TypeUtils.isTemporal(property.getType())) {
						Temporal temporal = (Temporal) value;
						TemporalType temporalType = TemporalType.getTemporalType(temporal);
						switch (temporalType) {
						case DATE_TIME:
							gen.writeStringField(name, DateTimeFormatter.ISO_DATE_TIME.format(temporal));
							break;
						case TIME:
							gen.writeStringField(name, DateTimeFormatter.ISO_TIME.format(temporal));
							break;
						case DATE:
						default:
							gen.writeStringField(name, DateTimeFormatter.ISO_DATE.format(temporal));
							break;
						}
					} else {
						gen.writeObjectField(name, value);
					}
				}
			}
		}

		gen.writeEndObject();
	}

}
