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
package com.holonplatform.json.jackson.jaxrs.internal;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.holonplatform.core.Context;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.JacksonConfiguration;

/**
 * JAX-RS {@link ContextResolver} to replace default Jackson
 * {@link ObjectMapper} for JSON marshalling with an ObjectMapper with
 * {@link PropertyBox} handling capabilities.
 * 
 * @since 5.0.0
 */
@Produces(MediaType.APPLICATION_JSON)
public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

	/**
	 * Default ObjectMapper instance
	 */
	private ObjectMapper mapper;

	/**
	 * Pretty printing
	 */
	private final boolean prettyPrint;

	/**
	 * Constructor.
	 * @param prettyPrint <code>true</code> to enable <em>pretty printing</em> of
	 *                    serialized JSON
	 */
	public JacksonContextResolver(boolean prettyPrint) {
		super();
		this.prettyPrint = prettyPrint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.ContextResolver#getContext(java.lang.Class)
	 */
	@Override
	public ObjectMapper getContext(Class<?> type) {
		return Context.get().resource(ObjectMapper.class.getName(), ObjectMapper.class)
				.orElseGet(this::getDefaultObjectMapper);
	}

	/**
	 * Get an {@link ObjectMapper} instance configured according to
	 * {@link JacksonConfiguration} configuration strategy.
	 * @param prettyPrint Whether to enable <em>pretty printing</em> of serialized
	 *                    JSON
	 * @return A default {@link ObjectMapper} instance
	 */
	private ObjectMapper getDefaultObjectMapper() {
		if (mapper == null) {
			mapper = JacksonConfiguration.mapper();
			if (prettyPrint) {
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
			}
		}
		return mapper;
	}

}
