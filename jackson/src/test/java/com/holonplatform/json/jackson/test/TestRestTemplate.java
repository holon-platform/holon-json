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
package com.holonplatform.json.jackson.test;

import static com.holonplatform.json.jackson.test.TestJaxrsIntegration.NUM;
import static com.holonplatform.json.jackson.test.TestJaxrsIntegration.SET;
import static com.holonplatform.json.jackson.test.TestJaxrsIntegration.STR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.JacksonConfiguration;
import com.holonplatform.json.jackson.test.TestJaxrsIntegration.TestEndpoint;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRestTemplate.Config.class)
public class TestRestTemplate extends JerseyTest {

	private static final String BASE_URI = "http://localhost:9998/";

	@Configuration
	static class Config {

		@Bean
		public RestTemplate restTemplate() {
			RestTemplate rt = new RestTemplate();
			JacksonConfiguration.configure(rt);
			return rt;
		}

	}

	@Override
	protected Application configure() {
		return new ResourceConfig().register(LoggingFeature.class).register(TestEndpoint.class);
	}

	@Autowired
	private RestTemplate restTemplate;

	@Test
	public void testJacksonConfig() {
		String pong = restTemplate
				.getForObject(UriComponentsBuilder.fromHttpUrl(BASE_URI + "/test/ping").build().toUri(), String.class);
		assertEquals("pong", pong);

		PropertyBox box = SET.execute(() -> restTemplate.getForObject(
				UriComponentsBuilder.fromHttpUrl(BASE_URI + "/test/data/{num}").buildAndExpand(1).toUri(),
				PropertyBox.class));
		assertNotNull(box);
		assertEquals(Integer.valueOf(1), box.getValue(NUM));
		assertEquals("Str_1", box.getValue(STR));

		box = SET.execute(() -> restTemplate.getForObject(
				UriComponentsBuilder.fromHttpUrl(BASE_URI + "/test/data/{num}").buildAndExpand(2).toUri(),
				PropertyBox.class));
		assertNotNull(box);
		assertEquals(Integer.valueOf(2), box.getValue(NUM));
		assertEquals("Str_2", box.getValue(STR));
	}

}
