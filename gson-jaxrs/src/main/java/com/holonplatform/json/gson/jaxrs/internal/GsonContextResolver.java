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
package com.holonplatform.json.gson.jaxrs.internal;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holonplatform.core.Context;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.GsonConfiguration;

/**
 * JAX-RS {@link ContextResolver} to replace default {@link GsonBuilder} for Gson marshalling using a builder with
 * {@link PropertyBox} handling capabilities.
 * 
 * @since 5.0.0
 */
@Produces(MediaType.APPLICATION_JSON)
public class GsonContextResolver implements ContextResolver<Gson> {

	/**
	 * Default GsonBuilder
	 */
	private final GsonBuilder _builder;

	/**
	 * Gson instance
	 */
	private Gson _gson;

	/**
	 * Constructor
	 * @param prettyPrint <code>true</code> to enable <em>pretty printing</em> for serialized JSON
	 */
	public GsonContextResolver(boolean prettyPrint) {
		super();
		this._builder = GsonConfiguration.builder();
		if (prettyPrint) {
			_builder.setPrettyPrinting();
		}
	}

	/**
	 * Get the default {@link Gson} instance.
	 * @return The {@link Gson} instance
	 */
	private Gson getDefaultGson() {
		if (_gson == null) {
			_gson = _builder.create();
		}
		return _gson;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.ContextResolver#getContext(java.lang.Class)
	 */
	@Override
	public Gson getContext(Class<?> type) {
		return Context.get().resource(Gson.class.getName(), Gson.class).orElseGet(() -> getDefaultGson());
	}

}
