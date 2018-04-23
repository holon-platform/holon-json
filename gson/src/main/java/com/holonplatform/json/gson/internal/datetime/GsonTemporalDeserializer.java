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
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * Gson deserializer to handle java.time.* API data types.
 *
 * @since 5.1.0
 */
public class GsonTemporalDeserializer<T extends TemporalAccessor> implements JsonDeserializer<T> {

	private final DateTimeFormatter formatter;
	private final TemporalQuery<T> temporalQuery;

	/**
	 * Constructor
	 * @param formatter Temporal formatter (not null)
	 * @param temporalQuery Temporal query to use to obtain the actual temporal type (not null)
	 */
	public GsonTemporalDeserializer(DateTimeFormatter formatter, TemporalQuery<T> temporalQuery) {
		super();
		ObjectUtils.argumentNotNull(formatter, "DateTimeFormatter must be not null");
		ObjectUtils.argumentNotNull(temporalQuery, "TemporalQuery must be not null");
		this.formatter = formatter;
		this.temporalQuery = temporalQuery;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type,
	 * com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonNull()) {
			return null;
		}
		try {
			return formatter.parse(json.getAsString(), temporalQuery);
		} catch (Exception e) {
			throw new JsonParseException(e);
		}
	}

	/**
	 * Create a new {@link JsonDeserializer} to handle a {@link TemporalAccessor} type using give
	 * {@link DateTimeFormatter}.
	 * @param formatter The formatter to use (not null)
	 * @param temporalQuery Temporal query to use to obtain the actual temporal type (not null)
	 * @return a new {@link JsonDeserializer}
	 */
	public static JsonDeserializer<? extends TemporalAccessor> create(DateTimeFormatter formatter,
			TemporalQuery<? extends TemporalAccessor> temporalQuery) {
		return new GsonTemporalDeserializer<>(formatter, temporalQuery);
	}

}
