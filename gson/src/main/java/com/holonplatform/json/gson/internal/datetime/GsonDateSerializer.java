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
package com.holonplatform.json.gson.internal.datetime;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.internal.datetime.ISO8601DateFormats;

/**
 * Gson serializer to handle {@link Date} types using ISO-8601 date format.
 *
 * @since 5.1.0
 */
public class GsonDateSerializer implements JsonSerializer<Date> {

	protected final TemporalType temporalType;

	/**
	 * Constructor
	 */
	public GsonDateSerializer() {
		super();
		this.temporalType = null;
	}

	/**
	 * Constructor
	 * @param temporalType Temporal type to use to select the date format
	 */
	public GsonDateSerializer(TemporalType temporalType) {
		super();
		this.temporalType = temporalType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
	 * com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
		if (src == null) {
			return JsonNull.INSTANCE;
		}
		return new JsonPrimitive(ISO8601DateFormats.format(src, temporalType));
	}

}
