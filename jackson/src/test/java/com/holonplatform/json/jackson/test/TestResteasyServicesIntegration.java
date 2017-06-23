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
package com.holonplatform.json.jackson.test;

import static com.holonplatform.json.jackson.test.TestJerseyIntegration.NUM;
import static com.holonplatform.json.jackson.test.TestJerseyIntegration.SET;
import static com.holonplatform.json.jackson.test.TestJerseyIntegration.STR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.test.TestJerseyIntegration.TestEndpoint;

public class TestResteasyServicesIntegration {

	private static UndertowJaxrsServer server;

	public static class Config extends Application {
		@Override
		public Set<Class<?>> getClasses() {
			final HashSet<Class<?>> classes = new HashSet<>();
			// endpoint
			classes.add(TestEndpoint.class);
			return classes;
		}
	}

	@BeforeClass
	public static void init() {
		server = new UndertowJaxrsServer();
		server.start();

		// deploy
		server.deploy(Config.class);
	}

	@AfterClass
	public static void shutdown() {
		server.stop();
	}

	@SuppressWarnings("static-access")
	@Test
	public void testGsonConfig() {

		Client client = ResteasyClientBuilder.newClient(); // Avoid conflict with Jersey in classpath
				// ClientBuilder.newClient()

		String pong = client.target(TestPortProvider.generateURL("/test/ping")).request().get(String.class);
		assertEquals("pong", pong);

		PropertyBox box = SET.execute(() -> client.target(TestPortProvider.generateURL("/test/data/{num}"))
				.resolveTemplate("num", 1).request().get(PropertyBox.class));
		assertNotNull(box);
		assertEquals(Integer.valueOf(1), box.getValue(NUM));
		assertEquals("Str_1", box.getValue(STR));

		box = SET.execute(
				() -> client.target(TestPortProvider.generateURL("/test/data/2")).request().get(PropertyBox.class));
		assertNotNull(box);
		assertEquals(Integer.valueOf(2), box.getValue(NUM));
		assertEquals("Str_2", box.getValue(STR));

	}

}
