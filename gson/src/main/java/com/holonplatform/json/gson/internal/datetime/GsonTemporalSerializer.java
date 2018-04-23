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

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * Gson serializer to handle java.time.* API data types.
 *
 * @since 5.1.0
 */
public class GsonTemporalSerializer<T extends TemporalAccessor> implements JsonSerializer<T> {

	private final DateTimeFormatter formatter;

	/**
	 * Constructor
	 * @param formatter Temporal formatter (not null)
	 */
	public GsonTemporalSerializer(DateTimeFormatter formatter) {
		super();
		ObjectUtils.argumentNotNull(formatter, "DateTimeFormatter must be not null");
		this.formatter = formatter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
	 * com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
		if (src == null) {
			return JsonNull.INSTANCE;
		}
		return new JsonPrimitive(formatter.format(src));
	}

	/**
	 * Create a new {@link JsonSerializer} to handle a {@link TemporalAccessor} type using give
	 * {@link DateTimeFormatter}.
	 * @param formatter The formatter to use (not null)
	 * @return a new {@link JsonSerializer}
	 */
	public static JsonSerializer<TemporalAccessor> create(DateTimeFormatter formatter) {
		return new GsonTemporalSerializer<>(formatter);
	}

}
