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
package com.holonplatform.json.jackson.internal.datetime;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.holonplatform.json.internal.datetime.ISO8601DateFormats;

/**
 * Jackson serializer for {@link Date}, using ISO-8601 format.
 *
 * @since 5.1.0
 */
public class JacksonISO8601DateSerializer extends JsonSerializer<Date> {

	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
	 */
	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value == null) {
			gen.writeNull();
		} else {
			gen.writeString(ISO8601DateFormats.format(value));
		}
	}

}
