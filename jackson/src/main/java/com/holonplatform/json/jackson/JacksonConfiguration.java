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
package com.holonplatform.json.jackson;

import java.io.Serializable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.internal.JacksonPropertyBoxDeserializer;
import com.holonplatform.json.jackson.internal.JacksonPropertyBoxSerializer;

/**
 * Utility class to handle Jackson configuration.
 *
 * @since 5.0.0
 */
public final class JacksonConfiguration implements Serializable {

	private static final long serialVersionUID = -8451471610783164158L;

	/**
	 * Module with {@link PropertyBox} serialization and deserialization capability.
	 */
	private static final SimpleModule PROPERTY_BOX_MODULE = new SimpleModule();

	static {
		PROPERTY_BOX_MODULE.addSerializer(PropertyBox.class, new JacksonPropertyBoxSerializer());
		PROPERTY_BOX_MODULE.addDeserializer(PropertyBox.class, new JacksonPropertyBoxDeserializer());
	}

	private JacksonConfiguration() {
	}

	/**
	 * Configure given Jackson {@link ObjectMapper}, registering serializers and deserializers for {@link PropertyBox}
	 * type handling.
	 * @param objectMapper ObjectMapper (not null)
	 */
	public static void configure(ObjectMapper objectMapper) {
		ObjectUtils.argumentNotNull(objectMapper, "Null ObjectMapper");
		objectMapper.registerModule(PROPERTY_BOX_MODULE);
	}

}
