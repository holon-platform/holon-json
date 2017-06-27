/*
 * Copyright 2000-2016 Holon TDCN.
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
package com.holonplatform.json.jackson.internal.jaxrs;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.JacksonConfiguration;

/**
 * JAX-RS {@link ContextResolver} to replace default Jackson {@link ObjectMapper} for JSON marshalling with an
 * ObjectMapper with {@link PropertyBox} handling capabilities.
 * 
 * @since 5.0.0
 */
@Produces(MediaType.APPLICATION_JSON)
public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;

	/**
	 * Constructor
	 * @param prettyPrint <code>true</code> to enable <em>pretty printing</em> of serialized JSON
	 */
	public JacksonContextResolver(boolean prettyPrint) {
		super();
		mapper = new ObjectMapper();
		if (prettyPrint) {
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
		}
		JacksonConfiguration.configure(mapper);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.ContextResolver#getContext(java.lang.Class)
	 */
	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}

}
