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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.json.Json;
import com.holonplatform.json.gson.internal.DefaultGsonJson;

/**
 * {@link Json} implementation using {@link Gson}.
 *
 * @since 5.1.0
 * 
 * @see Json
 */
public interface GsonJson extends Json {

	/**
	 * Create a new {@link Json} instance using a default {@link GsonBuilder}.
	 * @return a new {@link Json} instance
	 */
	public static Json create() {
		return create(new GsonBuilder());
	}

	/**
	 * Create a new {@link Json} instance using a given <code>builder</code>.
	 * @param builder The {@link GsonBuilder} to use (not null)
	 * @return a new {@link Json} instance
	 */
	public static Json create(GsonBuilder builder) {
		ObjectUtils.argumentNotNull(builder, "GsonBuilder must be not null");
		GsonConfiguration.configure(builder);
		return new DefaultGsonJson(builder.create());
	}

}
