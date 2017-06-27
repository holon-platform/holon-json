/*
 * Copyright 2000-2017 Holon TDCN.
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import com.holonplatform.core.internal.property.PropertySetRefIntrospector;
import com.holonplatform.core.internal.property.PropertySetRefIntrospector.PropertySetIntrospectionException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;

/**
 * JAX-RS message body reader and writer using Jackson as JSON serializer/deserializer and with {@link PropertyBox}
 * support.
 *
 * @since 5.0.0
 */
@Priority(100)
@Provider
@Consumes(MediaType.WILDCARD) // NOTE: required to support "non-standard" JSON variants
@Produces(MediaType.WILDCARD)
public class JacksonJsonProvider extends com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider {

	@Context
	private Providers providers;

	private PropertySetRefIntrospector propertySetRefIntrospector;

	/**
	 * Get the {@link PropertySetRefIntrospector} instance to use.
	 * @return The {@link PropertySetRefIntrospector} instance to use, from {@link ContextResolver} if available or the
	 *         default one
	 */
	private PropertySetRefIntrospector getPropertySetRefIntrospector() {
		if (propertySetRefIntrospector == null) {
			// init using a contextresolver, if available
			ContextResolver<PropertySetRefIntrospector> contextResolver = providers
					.getContextResolver(PropertySetRefIntrospector.class, MediaType.APPLICATION_JSON_TYPE);
			if (contextResolver != null) {
				propertySetRefIntrospector = contextResolver.getContext(PropertySetRefIntrospector.class);
			}
			if (propertySetRefIntrospector == null) {
				// use default
				propertySetRefIntrospector = PropertySetRefIntrospector.getDefault();
			}
		}
		return propertySetRefIntrospector;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.jaxrs.base.ProviderBase#readFrom(java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
	 * java.io.InputStream)
	 */
	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {

		// check property set
		PropertySet<?> propertySet = null;
		if (!com.holonplatform.core.Context.get().resource(PropertySet.CONTEXT_KEY, PropertySet.class).isPresent()) {
			PropertySetRef propertySetRef = PropertySetRefIntrospector.getPropertySetRef(annotations).orElse(null);
			if (propertySetRef != null) {
				try {
					propertySet = getPropertySetRefIntrospector().getPropertySet(propertySetRef);
				} catch (PropertySetIntrospectionException e) {
					throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
				}
			}
		}
		if (propertySet != null) {
			return propertySet.execute(
					() -> super.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream));
		} else {
			return super.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
		}
	}

}
