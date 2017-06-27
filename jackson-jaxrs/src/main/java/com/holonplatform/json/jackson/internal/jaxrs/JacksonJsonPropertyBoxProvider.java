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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Priority;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.holonplatform.core.internal.property.PropertySetRefIntrospector;
import com.holonplatform.core.internal.property.PropertySetRefIntrospector.PropertySetIntrospectionException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.json.jackson.JacksonConfiguration;

/**
 * JAX-RS message body reader and writer for {@link PropertyBox} type using Jackson as JSON serializer/deserializer.
 *
 * @since 5.0.0
 */
@Priority(-100)
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JacksonJsonPropertyBoxProvider implements MessageBodyWriter<PropertyBox>, MessageBodyReader<PropertyBox> {

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	@Context
	private Providers providers;

	private ObjectMapper _mapper;

	private ObjectReader _reader;

	private ObjectWriter _writer;

	private PropertySetRefIntrospector propertySetRefIntrospector;

	/**
	 * Get the {@link ObjectMapper} to use.
	 * @return The {@link ObjectMapper} obtained from a suitable {@link ContextResolver}, or a default one if not
	 *         available
	 */
	private ObjectMapper getObjectMapper() {
		if (_mapper == null) {
			// init using a contextresolver, if available
			ContextResolver<ObjectMapper> contextResolver = providers.getContextResolver(ObjectMapper.class,
					MediaType.APPLICATION_JSON_TYPE);
			if (contextResolver != null) {
				_mapper = contextResolver.getContext(ObjectMapper.class);
			}
			if (_mapper == null) {
				// use default
				_mapper = new ObjectMapper();
				JacksonConfiguration.configure(_mapper);
			}
		}
		return _mapper;
	}

	/**
	 * Get the object reader to use to deserialize a {@link PropertyBox}.
	 * @return The object reader
	 */
	private ObjectReader getObjectReader() {
		if (_reader == null) {
			_reader = getObjectMapper().readerFor(PropertyBox.class);
		}
		return _reader;
	}

	/**
	 * Get the object writer to use to serialize a {@link PropertyBox}.
	 * @return The object writer
	 */
	private ObjectWriter getObjectWriter() {
		if (_writer == null) {
			System.err.println("--------PP> " + getObjectMapper().isEnabled(SerializationFeature.INDENT_OUTPUT));
			_writer = getObjectMapper().writerFor(PropertyBox.class);
		}
		return _writer;
	}

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
	 * @see javax.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return isPropertyBoxType(type.equals(genericType) ? type : genericType);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
	 * java.io.InputStream)
	 */
	@Override
	public PropertyBox readFrom(Class<PropertyBox> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		try (final Reader reader = new InputStreamReader(entityStream, CHARSET)) {

			// check property set
			PropertySet<?> propertySet = null;
			if (!com.holonplatform.core.Context.get().resource(PropertySet.CONTEXT_KEY, PropertySet.class)
					.isPresent()) {
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
				return propertySet.execute(() -> readPropertyBox(reader));
			} else {
				return readPropertyBox(reader);
			}
		}
	}

	/**
	 * Read a {@link PropertyBox} from JSON content, using current {@link com.holonplatform.core.Context} property set.
	 * @param reader Reader
	 * @return The deserialized {@link PropertyBox} instance
	 * @throws IOException IO read error
	 * @throws WebApplicationException JSON processing exception
	 */
	private PropertyBox readPropertyBox(Reader reader) throws IOException {
		try {
			return getObjectReader().readValue(reader);
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e.getMessage(), e, Status.BAD_REQUEST);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return isPropertyBoxType(type.equals(genericType) ? type : genericType);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	@Override
	public long getSize(PropertyBox t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
	 * java.io.OutputStream)
	 */
	@Override
	public void writeTo(PropertyBox t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		try (final Writer writer = new OutputStreamWriter(entityStream, CHARSET)) {
			try {
				getObjectWriter().writeValue(writer, t);
			} catch (JsonProcessingException e) {
				throw new WebApplicationException(e.getMessage(), e, Status.BAD_REQUEST);
			}
		}
	}

	/**
	 * Checks whether given <code>type</code> is a {@link PropertyBox} type.
	 * @param type Type to check
	 * @return <code>true</code> if given <code>type</code> is a {@link PropertyBox} type
	 */
	private static boolean isPropertyBoxType(Type type) {
		if (type != null) {
			if (PropertyBox.class == type) {
				return true;
			}
			if (type instanceof Class && PropertyBox.class.isAssignableFrom((Class<?>) type)) {
				return true;
			}
		}
		return false;
	}

}
