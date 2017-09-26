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
package com.holonplatform.json.gson.jaxrs.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.jaxrs.test.TestJerseyIntegration.TestEndpoint;

public class TestJerseyIntegrationJsonProvider extends JerseyTest {

	@BeforeClass
	public static void setup() {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	@Override
	protected Application configure() {
		return new ResourceConfig().register(LoggingFeature.class).register(TestEndpoint.class)
				.register(TestJsonProvider.class);
	}

	// Avoid conflict with Resteasy in classpath
	@Override
	protected Client getClient() {
		ClientConfig config = new ClientConfig();
		config = config.register(TestJsonProvider.class);
		return JerseyClientBuilder.createClient(config);
	}

	@Test
	public void testJsonProvider() {
		Object value = target("/test/data/{num}").resolveTemplate("num", 1).request().get(PropertyBox.class);
		assertEquals("Test", value);
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public static class TestJsonProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

		@Override
		public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type,
		 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
		 * java.io.InputStream)
		 */
		@Override
		public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
				MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
				throws IOException, WebApplicationException {
			return "Test";
		}

		@Override
		public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
		 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
		 */
		@Override
		public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
			return -1;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
		 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
		 * java.io.OutputStream)
		 */
		@Override
		public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
				MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
				throws IOException, WebApplicationException {
			// noop
		}

	}

}
