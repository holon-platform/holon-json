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
package com.holonplatform.json.gson;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.google.gson.GsonBuilder;
import com.holonplatform.core.internal.property.DefaultPropertyBox;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.config.PropertyBoxSerializationMode;
import com.holonplatform.json.gson.internal.GsonPropertyBoxDeserializer;
import com.holonplatform.json.gson.internal.GsonPropertyBoxSerializer;
import com.holonplatform.json.gson.internal.datetime.GsonDateDeserializer;
import com.holonplatform.json.gson.internal.datetime.GsonDateSerializer;
import com.holonplatform.json.gson.internal.datetime.GsonSqlDateDeserializer;
import com.holonplatform.json.gson.internal.datetime.GsonTemporalDeserializer;
import com.holonplatform.json.gson.internal.datetime.GsonTemporalSerializer;

/**
 * Utility interface to handle Gson configuration for {@link PropertyBox} serializers and deserializers registration.
 *
 * @since 5.0.0
 */
public interface GsonConfiguration {

	/**
	 * Create a {@link GsonBuilder}, registering serializers and deserializers for {@link PropertyBox} type handling and
	 * using the default {@link PropertyBox} serialization mode.
	 * @return A new {@link GsonBuilder}
	 * @see PropertyBoxSerializationMode#getDefault()
	 */
	public static GsonBuilder builder() {
		return builder(PropertyBoxSerializationMode.getDefault());
	}

	/**
	 * Create a {@link GsonBuilder}, registering serializers and deserializers for {@link PropertyBox} type handling.
	 * @param serializationMode {@link PropertyBox} serialization mode
	 * @return A new {@link GsonBuilder}
	 * @see PropertyBoxSerializationMode
	 */
	public static GsonBuilder builder(PropertyBoxSerializationMode serializationMode) {
		GsonBuilder builder = new GsonBuilder();
		configure(builder, serializationMode);
		return builder;
	}

	/**
	 * Configure given Gson {@link GsonBuilder}, registering serializers and deserializers for {@link PropertyBox} type
	 * handling and using the default {@link PropertyBox} serialization mode.
	 * @param builder GsonBuilder (not null)
	 * @see PropertyBoxSerializationMode#getDefault()
	 */
	public static void configure(GsonBuilder builder) {
		configure(builder, PropertyBoxSerializationMode.getDefault());
	}

	/**
	 * Configure given Gson {@link GsonBuilder}, registering serializers and deserializers for {@link PropertyBox} type
	 * handling.
	 * @param builder GsonBuilder (not null)
	 * @param serializationMode {@link PropertyBox} serialization mode
	 * @see PropertyBoxSerializationMode
	 */
	public static void configure(GsonBuilder builder, PropertyBoxSerializationMode serializationMode) {
		ObjectUtils.argumentNotNull(builder, "Null GsonBuilder");

		// PropertyBox
		builder.registerTypeAdapter(PropertyBox.class, new GsonPropertyBoxSerializer(serializationMode));
		builder.registerTypeAdapter(PropertyBox.class, new GsonPropertyBoxDeserializer());
		builder.registerTypeAdapter(DefaultPropertyBox.class, new GsonPropertyBoxSerializer(serializationMode));
		builder.registerTypeAdapter(DefaultPropertyBox.class, new GsonPropertyBoxDeserializer());

		// jdk8 java.time.* API using ISO-8601 format
		builder.registerTypeAdapter(LocalDate.class, GsonTemporalSerializer.create(DateTimeFormatter.ISO_LOCAL_DATE));
		builder.registerTypeAdapter(LocalDateTime.class,
				GsonTemporalSerializer.create(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		builder.registerTypeAdapter(LocalTime.class, GsonTemporalSerializer.create(DateTimeFormatter.ISO_LOCAL_TIME));
		builder.registerTypeAdapter(OffsetDateTime.class,
				GsonTemporalSerializer.create(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		builder.registerTypeAdapter(OffsetTime.class, GsonTemporalSerializer.create(DateTimeFormatter.ISO_OFFSET_TIME));
		builder.registerTypeAdapter(ZonedDateTime.class,
				GsonTemporalSerializer.create(DateTimeFormatter.ISO_ZONED_DATE_TIME));
		builder.registerTypeAdapter(Instant.class, GsonTemporalSerializer.create(DateTimeFormatter.ISO_INSTANT));

		builder.registerTypeAdapter(LocalDate.class,
				GsonTemporalDeserializer.create(DateTimeFormatter.ISO_LOCAL_DATE, LocalDate::from));
		builder.registerTypeAdapter(LocalDateTime.class,
				GsonTemporalDeserializer.create(DateTimeFormatter.ISO_LOCAL_DATE_TIME, LocalDateTime::from));
		builder.registerTypeAdapter(LocalTime.class,
				GsonTemporalDeserializer.create(DateTimeFormatter.ISO_LOCAL_TIME, LocalTime::from));
		builder.registerTypeAdapter(OffsetDateTime.class,
				GsonTemporalDeserializer.create(DateTimeFormatter.ISO_OFFSET_DATE_TIME, OffsetDateTime::from));
		builder.registerTypeAdapter(OffsetTime.class,
				GsonTemporalDeserializer.create(DateTimeFormatter.ISO_OFFSET_TIME, OffsetTime::from));
		builder.registerTypeAdapter(ZonedDateTime.class,
				GsonTemporalDeserializer.create(DateTimeFormatter.ISO_ZONED_DATE_TIME, ZonedDateTime::from));
		builder.registerTypeAdapter(Instant.class,
				GsonTemporalDeserializer.create(DateTimeFormatter.ISO_INSTANT, Instant::from));

		// java.util.Date using ISO-8601 format
		builder.registerTypeAdapter(Date.class, new GsonDateSerializer());
		builder.registerTypeAdapter(Date.class, new GsonDateDeserializer());
		builder.registerTypeAdapter(java.sql.Date.class, new GsonDateSerializer());
		builder.registerTypeAdapter(java.sql.Date.class, new GsonSqlDateDeserializer());

	}

}
