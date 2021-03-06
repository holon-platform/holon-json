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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.json.gson.jaxrs.GsonFeature;
import com.holonplatform.test.JerseyTest5;

public class TestJerseyIntegration extends JerseyTest5 {

	public static final Property<Integer> NUM = PathProperty.create("num", Integer.class);
	public static final Property<Double> DBL = PathProperty.create("dbl", Double.class);
	public static final Property<String> STR = VirtualProperty.create(String.class)
			.valueProvider(b -> "Str_" + b.getValue(NUM));

	public static final PropertySet<?> SET = PropertySet.of(NUM, DBL, STR);

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
			return PropertyBox.builder(SET).set(NUM, num).set(DBL, 7.5).build();
		}

		@PUT
		@Path("srlz")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response srlz(@PropertySetRef(TestJerseyIntegration.class) PropertyBox data) {
			data.getValue(NUM);
			return Response.accepted().build();
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
				.property(GsonFeature.JAXRS_JSON_PRETTY_PRINT, true);
	}

	// Avoid conflict with Resteasy in classpath
	@Override
	protected Client getClient() {
		return JerseyClientBuilder.createClient();
	}

	@Test
	public void testGsonConfig() {

		String pong = target("/test/ping").request().get(String.class);
		assertEquals("pong", pong);

		PropertyBox box = SET
				.execute(() -> target("/test/data/{num}").resolveTemplate("num", 1).request().get(PropertyBox.class));
		assertNotNull(box);
		assertEquals(Integer.valueOf(1), box.getValue(NUM));
		assertEquals("Str_1", box.getValue(STR));

		box = SET.execute(() -> target("/test/data/2").request().get(PropertyBox.class));
		assertNotNull(box);
		assertEquals(Integer.valueOf(2), box.getValue(NUM));
		assertEquals("Str_2", box.getValue(STR));

		PropertyBox boxToSrlz = PropertyBox.builder(SET).set(NUM, 100).set(DBL, 77.7).build();

		try (Response response = target("/test/srlz").request()
				.put(Entity.entity(boxToSrlz, MediaType.APPLICATION_JSON))) {
			assertNotNull(response);
			assertEquals(Status.ACCEPTED.getStatusCode(), response.getStatus());
		}

	}

}
