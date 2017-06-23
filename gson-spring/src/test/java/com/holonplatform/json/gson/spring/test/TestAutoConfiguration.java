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
package com.holonplatform.json.gson.spring.test;

import static com.holonplatform.json.gson.spring.test.data.TestData.DESCRIPTION;
import static com.holonplatform.json.gson.spring.test.data.TestData.ID;
import static com.holonplatform.json.gson.spring.test.data.TestData.PROPERTIES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.holonplatform.core.property.PropertyBox;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class TestAutoConfiguration {

	@Configuration
	@EnableAutoConfiguration
	static class Config {

		@Bean
		public ResourceConfig jaxrsConfig() {
			ResourceConfig cfg = new ResourceConfig();
			cfg.register(Endpoint.class);
			return cfg;
		}

	}

	@Path("/test")
	public static class Endpoint {

		@GET
		@Path("data/{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public PropertyBox getData(@PathParam("id") int id) {
			return PropertyBox.builder(PROPERTIES).set(ID, id).set(DESCRIPTION, "Test-" + id).build();
		}

	}

	@Autowired
	private Gson gson;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Test
	public void testAutoConfig() throws Exception {
		PropertyBox box = PropertyBox.builder(PROPERTIES).set(ID, 1).set(DESCRIPTION, "Test").build();

		String json = gson.toJson(box);

		PropertyBox readBox = PROPERTIES.execute(() -> gson.fromJson(json, PropertyBox.class));

		assertNotNull(readBox);

		assertEquals(new Integer(1), readBox.getValue(ID));
		assertEquals("Test", readBox.getValue(DESCRIPTION));
	}
	
	@Test
	public void testRestTemplateConfig() throws Exception {
		RestTemplate rt = restTemplateBuilder.build();

		GsonHttpMessageConverter converter = null;
		for (HttpMessageConverter<?> c : rt.getMessageConverters()) {
			if (GsonHttpMessageConverter.class.isAssignableFrom(c.getClass())) {
				converter = (GsonHttpMessageConverter) c;
				break;
			}
		}

		assertNotNull(converter);
	}

	@Test
	public void testRestTemplate() throws Exception {
		PropertyBox box = PROPERTIES.execute(
				() -> restTemplateBuilder.build().getForObject("http://localhost:9999/test/data/7", PropertyBox.class));
		assertNotNull(box);
		assertEquals(Integer.valueOf(7), box.getValue(ID));
		assertEquals("Test-7", box.getValue(DESCRIPTION));
	}

}
