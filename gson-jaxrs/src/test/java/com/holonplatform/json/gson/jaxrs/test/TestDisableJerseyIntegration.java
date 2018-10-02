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

import static com.holonplatform.json.gson.jaxrs.test.TestJerseyIntegration.SET;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.jaxrs.GsonFeature;
import com.holonplatform.json.gson.jaxrs.test.TestJerseyIntegration.TestEndpoint;
import com.holonplatform.test.JerseyTest5;

public class TestDisableJerseyIntegration extends JerseyTest5 {

	@BeforeAll
	static void setup() {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	@Override
	protected Application configure() {
		return new ResourceConfig().register(LoggingFeature.class).register(TestEndpoint.class)
				.property(GsonFeature.JAXRS_DISABLE_GSON_AUTO_CONFIG, "");
	}

	// Avoid conflict with Resteasy in classpath
	@Override
	protected Client getClient() {
		ClientConfig config = new ClientConfig();
		config = config.property(GsonFeature.JAXRS_DISABLE_GSON_AUTO_CONFIG, "");
		return JerseyClientBuilder.createClient(config);
	}

	@Test
	public void testGsonConfigDisabled() {
		Assertions.assertThrows(RuntimeException.class, () -> SET
				.execute(() -> target("/test/data/{num}").resolveTemplate("num", 1).request().get(PropertyBox.class)));
	}

}
