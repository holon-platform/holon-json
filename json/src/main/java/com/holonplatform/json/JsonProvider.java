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
package com.holonplatform.json;

import java.util.ServiceLoader;

import jakarta.annotation.Priority;

/**
 * Concrete {@link Json} implementation provider.
 * 
 * <p>
 * The {@link JsonProvider} implementations can be registered using default Java {@link ServiceLoader} extension,
 * through a <code>com.holonplatform.json.JsonProvider</code> file under the <code>META-INF/services</code> folder.
 * </p>
 * 
 * <p>
 * The {@link Priority} annotation on the {@link JsonProvider} class (where less priority value means higher priority
 * order) can be used to order the providers.
 * </p>
 * 
 * @since 5.1.0
 *
 */
public interface JsonProvider {

	/**
	 * Default {@link JsonProvider} priority if not specified using {@link Priority} annotation.
	 */
	public static final int DEFAULT_PRIORITY = 10000;

	/**
	 * Provides the {@link Json} implementation.
	 * @return the {@link Json} implementation
	 */
	Json provide();

}
