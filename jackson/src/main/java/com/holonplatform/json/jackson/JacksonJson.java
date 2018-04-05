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
import com.holonplatform.json.Json;
import com.holonplatform.json.jackson.internal.DefaultJacksonJson;

/**
 * {@link Json} implementation using Jackson {@link ObjectMapper}.
 *
 * @since 5.1.0
 * 
 * @see Json
 */
public interface JacksonJson extends Json {

	/**
	 * Create a new {@link Json} instance using a default {@link ObjectMapper}.
	 * @return a new {@link Json} instance
	 */
	public static Json create() {
		return create(new ObjectMapper());
	}

	/**
	 * Create a new {@link Json} instance using a given <code>objectMapper</code>.
	 * @param objectMapper The {@link ObjectMapper} to use (not null)
	 * @return a new {@link Json} instance
	 */
	public static Json create(ObjectMapper objectMapper) {
		return new DefaultJacksonJson(JacksonConfiguration.configure(objectMapper));
	}

}
