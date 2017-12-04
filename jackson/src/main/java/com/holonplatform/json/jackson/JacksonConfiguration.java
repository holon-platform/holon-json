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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;

/**
 * Utility interface to handle Jackson configuration.
 *
 * @since 5.0.0
 */
public interface JacksonConfiguration {

	/**
	 * Configures given Jackson {@link ObjectMapper} in the following way:
	 * <ul>
	 * <li>Registers {@link PropertyBox} type serializers and deserializers using the {@link PropertyBoxModule}</li>
	 * <li>Adds the {@link JavaTimeModule} to support jdk8 java.time.* API</li>
	 * <li>Sets the {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS} to <code>false</code></li>
	 * </ul>
	 * @param objectMapper ObjectMapper to configure (not null)
	 */
	public static void configure(ObjectMapper objectMapper) {
		ObjectUtils.argumentNotNull(objectMapper, "ObjectMapper must be not null");
		
		// PropertyBox type handling
		objectMapper.registerModule(new PropertyBoxModule());
		
		// Jdk8 temporals handling
		objectMapper.registerModule(new JavaTimeModule());
		
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

}
