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
import com.holonplatform.core.Path;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.internal.datetime.ISO8601DateFormats;

/**
 * Jackson JSON serializer to handle {@link PropertyBox} serialization
 * 
 * @since 5.0.0
 */
public class JacksonPropertyBoxSerializer extends JsonSerializer<PropertyBox> {

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
					// check temporals
					Optional<TemporalType> temporalType = property.getConfiguration().getTemporalType();
					if (temporalType.isPresent()) {
						try {
							ISO8601DateFormats.setCurrentTemporalType(temporalType.get());
							gen.writeObjectField(name, value);
						} finally {
							ISO8601DateFormats.removeCurrentTemporalType();
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
