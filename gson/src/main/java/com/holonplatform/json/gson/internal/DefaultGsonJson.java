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
package com.holonplatform.json.gson.internal;

import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.json.JsonReader;
import com.holonplatform.json.JsonWriter;
import com.holonplatform.json.gson.GsonJson;

/**
 * Default {@link GsonJson} implementation.
 *
 * @since 5.1.0
 */
public class DefaultGsonJson implements GsonJson {

	private final Gson gson;

	/**
	 * Constructor
	 * @param gson The {@link Gson} parser (not null)
	 */
	public DefaultGsonJson(Gson gson) {
		super();
		ObjectUtils.argumentNotNull(gson, "Gson instance must be not null");
		this.gson = gson;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.Json#toJson(java.lang.Object)
	 */
	@Override
	public JsonWriter toJson(Object value) {
		return new GsonJsonWriter(gson, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.Json#toJsonArray(java.lang.Class, java.util.Collection)
	 */
	@Override
	public <T> JsonWriter toJsonArray(Class<T> type, Collection<T> values) {
		ObjectUtils.argumentNotNull(type, "Type must be not null");
		if (values != null) {
			return new GsonJsonWriter(gson, values, TypeToken.getParameterized(Collection.class, type).getType());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.Json#fromJson(com.holonplatform.json.JsonReader, java.lang.Class)
	 */
	@Override
	public <T> T fromJson(JsonReader reader, Class<T> type) {
		ObjectUtils.argumentNotNull(reader, "JsonReader must be not null");
		ObjectUtils.argumentNotNull(type, "Type must be not null");
		try {
			return gson.fromJson(reader.getReader(), type);
		} catch (Exception e) {
			throw new JsonReadException("Failed to deserialize JSON for type [" + type + "]", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.Json#fromJsonArray(com.holonplatform.json.JsonReader, java.lang.Class)
	 */
	@Override
	public <T> List<T> fromJsonArray(JsonReader reader, Class<T> type) {
		ObjectUtils.argumentNotNull(reader, "JsonReader must be not null");
		ObjectUtils.argumentNotNull(type, "Type must be not null");
		try {
			return gson.fromJson(reader.getReader(), TypeToken.getParameterized(List.class, type).getType());
		} catch (Exception e) {
			throw new JsonReadException("Failed to deserialize JSON for array type [" + type + "]", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.Json#fromJson(com.holonplatform.json.JsonReader, java.lang.Iterable)
	 */
	@Override
	public <P extends Property<?>> PropertyBox fromJson(JsonReader reader, Iterable<P> propertySet) {
		ObjectUtils.argumentNotNull(reader, "JsonReader must be not null");
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");

		final PropertySet<?> ps = (PropertySet.class.isAssignableFrom(propertySet.getClass()))
				? (PropertySet<?>) propertySet
				: PropertySet.of(propertySet);

		return ps.execute(() -> fromJson(reader, PropertyBox.class));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.Json#fromJsonArray(com.holonplatform.json.JsonReader, java.lang.Iterable)
	 */
	@Override
	public <P extends Property<?>> List<PropertyBox> fromJsonArray(JsonReader reader, Iterable<P> propertySet) {
		ObjectUtils.argumentNotNull(reader, "JsonReader must be not null");
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");

		final PropertySet<?> ps = (PropertySet.class.isAssignableFrom(propertySet.getClass()))
				? (PropertySet<?>) propertySet
				: PropertySet.of(propertySet);

		return ps.execute(() -> fromJsonArray(reader, PropertyBox.class));
	}

}
