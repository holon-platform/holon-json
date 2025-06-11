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

import static com.holonplatform.json.gson.jaxrs.test.TestJerseyIntegration.NUM;
import static com.holonplatform.json.gson.jaxrs.test.TestJerseyIntegration.SET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Type;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.GsonConfiguration;
import com.holonplatform.json.gson.jaxrs.GsonFeature;
import com.holonplatform.json.gson.jaxrs.test.TestJerseyIntegration.TestEndpoint;
import com.holonplatform.test.JerseyTest5;

public class TestJerseyIntegrationCustomResolver extends JerseyTest5 {

	@Path("test2")
	public static class TestEndpoint2 {

		@GET
		@Path("data2")
		@Produces(MediaType.APPLICATION_JSON)
		public TestJsonData data2() {
			return new TestJsonData(0);
		}

	}

	@BeforeAll
	static void setup() {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	@Override
	protected Application configure() {
		return new ResourceConfig().register(LoggingFeature.class).register(TestEndpoint.class)
				.register(TestEndpoint2.class).register(TestContextResolver.class)
				.property(GsonFeature.JAXRS_DISABLE_GSON_CONTEXT_RESOLVER, "");
	}

	// Avoid conflict with Resteasy in classpath
	@Override
	protected Client getClient() {
		ClientConfig config = new ClientConfig();
		config = config.register(TestContextResolver.class).property(GsonFeature.JAXRS_DISABLE_GSON_CONTEXT_RESOLVER,
				"");
		return JerseyClientBuilder.createClient(config);
	}

	@Test
	public void testJsonProvider() {
		PropertyBox box = SET
				.execute(() -> target("/test/data/{num}").resolveTemplate("num", 1).request().get(PropertyBox.class));
		assertNotNull(box);
		assertEquals(Integer.valueOf(1), box.getValue(NUM));

		TestJsonData d = target("/test2/data2").request().get(TestJsonData.class);
		assertNotNull(d);
		assertEquals(7, d.getSequence());
	}

	public static class TestJsonData {

		private final int sequence;

		public TestJsonData(int sequence) {
			super();
			this.sequence = sequence;
		}

		public int getSequence() {
			return sequence;
		}

	}

	public static class TestJsonDataDeserializer implements JsonDeserializer<TestJsonData> {

		@Override
		public TestJsonData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return new TestJsonData(7);
		}

	}

	@Produces(MediaType.APPLICATION_JSON)
	public static class TestContextResolver implements ContextResolver<Gson> {

		private final GsonBuilder builder;

		public TestContextResolver() {
			super();
			this.builder = GsonConfiguration.builder();
			builder.registerTypeAdapter(TestJsonData.class, new TestJsonDataDeserializer());
		}

		@Override
		public Gson getContext(Class<?> type) {
			return builder.create();
		}

	}

}
