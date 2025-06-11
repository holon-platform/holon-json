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
package com.holonplatform.json.jackson.jaxrs.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.holonplatform.core.Context;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.json.jackson.JacksonConfiguration;
import com.holonplatform.json.jackson.jaxrs.test.data.MyType;
import com.holonplatform.json.jackson.jaxrs.test.data.MyTypeModule;
import com.holonplatform.test.JerseyTest5;

public class TestObjectMapperResource extends JerseyTest5 {

	public static final Property<Integer> NUM = PathProperty.create("num", Integer.class);
	public static final StringProperty STR1 = StringProperty.create("str");
	public static final PathProperty<MyType> MYT = PathProperty.create("myt", MyType.class);

	public static final PropertySet<?> SET = PropertySet.of(NUM, STR1, MYT);

	@Path("test")
	public static class TestEndpoint {

		@GET
		@Path("ping")
		@Produces(MediaType.APPLICATION_JSON)
		public String ping() {
			return "pong";
		}

		@GET
		@Path("data/{num}")
		@Produces(MediaType.APPLICATION_JSON)
		public PropertyBox getData(@PathParam("num") int num) {
			return PropertyBox.builder(SET).set(NUM, num).set(STR1, "Str-" + num).set(MYT, new MyType(num)).build();
		}

	}

	@BeforeAll
	static void setup() {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		// set ObjectMapper context resource
		final ObjectMapper mapper = JacksonConfiguration.mapper();
		mapper.registerModule(new MyTypeModule());

		Context.get().classLoaderScope().map(s -> s.put(ObjectMapper.class.getName(), mapper));
	}

	@AfterAll
	public static void finalizeResources() {
		Context.get().classLoaderScope().map(s -> s.remove(ObjectMapper.class.getName()));
	}

	@Override
	protected Application configure() {
		return new ResourceConfig().register(LoggingFeature.class).register(TestEndpoint.class);
		// .property("holon.jackson.disable-resolver", true);
	}

	// Avoid conflict with Resteasy in classpath
	@Override
	protected Client getClient() {
		return JerseyClientBuilder.createClient();
	}

	@Test
	public void testJacksonConfig() {
		String pong = target("/test/ping").request().get(String.class);
		assertEquals("pong", pong);

		PropertyBox box = SET
				.execute(() -> target("/test/data/{num}").resolveTemplate("num", 1).request().get(PropertyBox.class));
		assertNotNull(box);
		assertEquals(Integer.valueOf(1), box.getValue(NUM));
		assertEquals("Str-1", box.getValue(STR1));
		assertNotNull(box.getValue(MYT));
		assertEquals(1, box.getValue(MYT).getValue());
	}

}
