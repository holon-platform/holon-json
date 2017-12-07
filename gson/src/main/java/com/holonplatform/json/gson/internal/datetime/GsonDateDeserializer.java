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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.internal.datetime.ISO8601DateFormats;

/**
 * Gson serializer to handle {@link Date} types using ISO-8601 date format.
 *
 * @since 5.1.0
 */
public class GsonDateDeserializer implements JsonDeserializer<Date> {

	protected final TemporalType temporalType;

	/**
	 * Constructor
	 */
	public GsonDateDeserializer() {
		super();
		this.temporalType = null;
	}

	/**
	 * Constructor
	 * @param temporalType Temporal type to use to select the date format
	 */
	public GsonDateDeserializer(TemporalType temporalType) {
		super();
		this.temporalType = temporalType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type,
	 * com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (json.isJsonNull()) {
			return null;
		}
		try {
			return ISO8601DateFormats.parse(json.getAsString());
		} catch (Exception e) {
			throw new JsonParseException(e);
		}
	}

}
