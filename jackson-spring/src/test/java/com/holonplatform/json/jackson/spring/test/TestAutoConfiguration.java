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
package com.holonplatform.json.jackson.spring.test;

import static com.holonplatform.json.jackson.spring.test.data.TestData.DESCRIPTION;
import static com.holonplatform.json.jackson.spring.test.data.TestData.ID;
import static com.holonplatform.json.jackson.spring.test.data.TestData.PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.json.jackson.spring.test.data.TestData;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
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

		@PUT
		@Path("srlz")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response srlz(@PropertySetRef(TestData.class) PropertyBox data) {
			data.getValue(ID);
			return Response.accepted().build();
		}

	}

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@LocalServerPort
	private int port;

	@Test
	public void testAutoConfig() throws Exception {

		// serialize

		final ObjectWriter writer = mapper.writer();

		PropertyBox box = PropertyBox.builder(PROPERTIES).set(ID, 1).set(DESCRIPTION, "Test").build();

		String json = writer.writeValueAsString(box);

		// deserialize

		final ObjectReader reader = mapper.reader();

		PropertyBox readBox = PROPERTIES.execute(() -> reader.forType(PropertyBox.class).readValue(json));

		assertNotNull(readBox);

		assertEquals(new Integer(1), readBox.getValue(ID));
		assertEquals("Test", readBox.getValue(DESCRIPTION));
	}

	@Test
	public void testRestTemplateConfig() throws Exception {
		RestTemplate rt = restTemplateBuilder.build();

		MappingJackson2HttpMessageConverter converter = null;
		for (HttpMessageConverter<?> c : rt.getMessageConverters()) {
			if (MappingJackson2HttpMessageConverter.class.isAssignableFrom(c.getClass())) {
				converter = (MappingJackson2HttpMessageConverter) c;
				break;
			}
		}

		assertNotNull(converter);
	}

	@Test
	public void testRestTemplate() throws Exception {

		final String baseUrl = "http://localhost:" + port + "/test";

		PropertyBox box = PROPERTIES
				.execute(() -> restTemplateBuilder.build().getForObject(baseUrl + "/data/7", PropertyBox.class));
		assertNotNull(box);
		assertEquals(Integer.valueOf(7), box.getValue(ID));
		assertEquals("Test-7", box.getValue(DESCRIPTION));

		final PropertyBox box2 = PropertyBox.builder(PROPERTIES).set(ID, 7).set(DESCRIPTION, "Test7").build();

		restTemplateBuilder.build().put(baseUrl + "/srlz", box2);
	}

}
