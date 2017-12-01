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

import com.google.gson.GsonBuilder;
import com.holonplatform.core.internal.property.DefaultPropertyBox;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.internal.GsonPropertyBoxDeserializer;
import com.holonplatform.json.gson.internal.GsonPropertyBoxSerializer;

/**
 * Utility interface to handle Gson configuration for {@link PropertyBox} serializers and deserializers registration.
 *
 * @since 5.0.0
 */
public interface GsonConfiguration {

	/**
	 * Create a {@link GsonBuilder}, registering serializers and deserializers for {@link PropertyBox} type handling.
	 * @return GsonBuilder
	 */
	public static GsonBuilder builder() {
		GsonBuilder builder = new GsonBuilder();
		configure(builder);
		return builder;
	}

	/**
	 * Configure given Gson {@link GsonBuilder}, registering serializers and deserializers for {@link PropertyBox} type
	 * handling.
	 * @param builder GsonBuilder (not null)
	 */
	public static void configure(GsonBuilder builder) {
		ObjectUtils.argumentNotNull(builder, "Null GsonBuilder");
		builder.registerTypeAdapter(PropertyBox.class, new GsonPropertyBoxSerializer());
		builder.registerTypeAdapter(PropertyBox.class, new GsonPropertyBoxDeserializer());
		builder.registerTypeAdapter(DefaultPropertyBox.class, new GsonPropertyBoxSerializer());
		builder.registerTypeAdapter(DefaultPropertyBox.class, new GsonPropertyBoxDeserializer());
	}

}
