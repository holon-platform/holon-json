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

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.json.JsonReader;
import com.holonplatform.json.JsonWriter;
import com.holonplatform.json.jackson.JacksonJson;

/**
 * Default {@link JacksonJson} implementation.
 *
 * @since 5.1.0
 */
public class DefaultJacksonJson implements JacksonJson {

	private final ObjectMapper mapper;

	private static final String MESSAGE_JSON_READER_NOT_NULL = "JsonReader must be not null";

	/**
	 * Constructor
	 * @param mapper Jackson {@link ObjectMapper} (not null)
	 */
	public DefaultJacksonJson(ObjectMapper mapper) {
		super();
		ObjectUtils.argumentNotNull(mapper, "ObjectMapper must be not null");
		this.mapper = mapper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.json.Json#toJson(java.lang.Object)
	 */
	@Override
	public JsonWriter toJson(Object value) {
		return new JacksonJsonWriter(mapper, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.json.Json#toJsonArray(java.lang.Class,
	 * java.util.Collection)
	 */
	@Override
	public <T> JsonWriter toJsonArray(Class<T> type, Collection<T> values) {
		return new JacksonJsonWriter(mapper, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.json.Json#fromJson(com.holonplatform.json.JsonReader,
	 * java.lang.Class)
	 */
	@Override
	public <T> T fromJson(JsonReader reader, Class<T> type) {
		ObjectUtils.argumentNotNull(reader, MESSAGE_JSON_READER_NOT_NULL);
		ObjectUtils.argumentNotNull(type, "Type must be not null");
		try {
			return mapper.reader().forType(type).readValue(reader.getReader());
		} catch (Exception e) {
			throw new JsonReadException("Failed to deserialize JSON for type [" + type + "]", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.json.Json#fromJsonArray(com.holonplatform.json.JsonReader,
	 * java.lang.Class)
	 */
	@Override
	public <T> List<T> fromJsonArray(JsonReader reader, Class<T> type) {
		ObjectUtils.argumentNotNull(reader, MESSAGE_JSON_READER_NOT_NULL);
		ObjectUtils.argumentNotNull(type, "Type must be not null");
		try {

			return mapper.reader().forType(mapper.getTypeFactory().constructCollectionType(List.class, type))
					.readValue(reader.getReader());
		} catch (Exception e) {
			throw new JsonReadException("Failed to deserialize JSON for type [" + type + "]", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.json.Json#fromJson(com.holonplatform.json.JsonReader,
	 * java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> PropertyBox fromJson(JsonReader reader, Iterable<P> propertySet) {
		ObjectUtils.argumentNotNull(reader, MESSAGE_JSON_READER_NOT_NULL);
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");

		final PropertySet<?> ps = (PropertySet.class.isAssignableFrom(propertySet.getClass()))
				? (PropertySet<?>) propertySet
				: PropertySet.of(propertySet);

		return ps.execute(() -> fromJson(reader, PropertyBox.class));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.json.Json#fromJsonArray(com.holonplatform.json.JsonReader,
	 * java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> List<PropertyBox> fromJsonArray(JsonReader reader, Iterable<P> propertySet) {
		ObjectUtils.argumentNotNull(reader, MESSAGE_JSON_READER_NOT_NULL);
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");

		final PropertySet<?> ps = (PropertySet.class.isAssignableFrom(propertySet.getClass()))
				? (PropertySet<?>) propertySet
				: PropertySet.of(propertySet);

		return ps.execute(() -> fromJsonArray(reader, PropertyBox.class));
	}

}
