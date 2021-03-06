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
package com.holonplatform.json.examples;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.holonplatform.core.Context;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.core.property.TemporalProperty;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.Json;
import com.holonplatform.json.config.JsonConfigProperties;
import com.holonplatform.json.config.PropertyBoxSerializationMode;
import com.holonplatform.json.datetime.CurrentSerializationTemporalType;
import com.holonplatform.json.jackson.JacksonConfiguration;
import com.holonplatform.json.jackson.JacksonJson;
import com.holonplatform.json.jackson.spring.SpringJacksonConfiguration;

@SuppressWarnings("unused")
public class ExampleJackson {

	public void configuration() {
		// tag::configuration[]
		ObjectMapper mapper = JacksonConfiguration.configure(new ObjectMapper()); // <1>

		mapper = JacksonConfiguration.mapper(); // <2>
		// end::configuration[]
	}

	public void temporals() throws IOException {
		// tag::temporals[]
		ObjectMapper mapper = JacksonConfiguration.mapper(); // <1>

		LocalDate date = LocalDate.of(2018, Month.JANUARY, 5);

		String serialized = mapper.writeValueAsString(date); // <2>

		LocalDate deserialized = mapper.readValue(serialized, LocalDate.class); // <3>
		// end::temporals[]
	}

	public void json() {
		// tag::json[]
		Json jsonApi = Json.require(); // <1>

		jsonApi = JacksonJson.create(); // <2>
		// end::json[]
	}

	public void ttype() throws IOException {
		// tag::ttype[]
		final ObjectMapper mapper = JacksonConfiguration.mapper(); // <1>

		Calendar c = Calendar.getInstance();
		c.set(2018, 0, 5);
		final Date date = c.getTime();

		try {
			CurrentSerializationTemporalType.setCurrentTemporalType(TemporalType.DATE); // <2>
			String json = mapper.writeValueAsString(date); // <3>
		} finally {
			CurrentSerializationTemporalType.removeCurrentTemporalType(); // <4>
		}
		// end::ttype[]
	}

	// tag::serdeser[]
	final static PathProperty<Long> ID = PathProperty.create("id", Long.class);
	final static PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

	final static PropertySet<?> PROPERTY_SET = PropertySet.of(ID, DESCRIPTION);

	public void serializeAndDeserialize() throws JsonProcessingException {
		ObjectMapper mapper = JacksonConfiguration.mapper(); // <1>

		PropertyBox box = PropertyBox.builder(PROPERTY_SET).set(ID, 1L).set(DESCRIPTION, "Test").build(); // <2>

		// serialize
		String json = mapper.writer().writeValueAsString(box); // <3>
		// deserialize
		box = PROPERTY_SET.execute(() -> mapper.reader().forType(PropertyBox.class).readValue(json)); // <4>
	}
	// end::serdeser[]

	public void pttype() throws IOException {
		// tag::pttype[]
		final ObjectMapper mapper = JacksonConfiguration.mapper();

		final TemporalProperty<Date> DATE = TemporalProperty.date("date").temporalType(TemporalType.DATE); // <1>

		Calendar c = Calendar.getInstance();
		c.set(2018, 0, 5);

		PropertyBox value = PropertyBox.builder(DATE).set(DATE, c.getTime()).build(); // <2>

		String json = JacksonConfiguration.mapper().writeValueAsString(value); // <3>
		// end::pttype[]
	}

	// tag::jaxrs[]
	final static PathProperty<Integer> CODE = PathProperty.create("code", Integer.class);
	final static PathProperty<String> NAME = PathProperty.create("name", String.class);

	final static PropertySet<?> PROPERTYSET = PropertySet.of(CODE, NAME);

	// JAX-RS example endpoint
	@Path("test")
	public static class Endpoint {

		@PUT
		@Path("serialize")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response create(@PropertySetRef(value = ExampleJackson.class, field = "PROPERTYSET") PropertyBox data) { // <1>
			return Response.accepted().build();
		}

		@GET
		@Path("deserialize")
		@Produces(MediaType.APPLICATION_JSON)
		public PropertyBox getData() {
			return PropertyBox.builder(PROPERTYSET).set(CODE, 1).set(NAME, "Test").build();
		}

	}

	public void jaxrs() {
		Client client = ClientBuilder.newClient(); // <2>

		PropertyBox box1 = PropertyBox.builder(PROPERTYSET).set(CODE, 1).set(NAME, "Test").build();

		client.target("https://host/test/serialize").request().put(Entity.entity(box1, MediaType.APPLICATION_JSON)); // <3>

		PropertyBox box2 = PROPERTYSET
				.execute(() -> client.target("https://host/test/deserialize").request().get(PropertyBox.class)); // <4>

	}
	// end::jaxrs[]

	// tag::jaxrsor1[]
	@Produces(MediaType.APPLICATION_JSON) // <1>
	public static class MyObjectMapperResolver implements ContextResolver<ObjectMapper> {

		private final ObjectMapper mapper;

		public MyObjectMapperResolver() {
			super();
			mapper = JacksonConfiguration.mapper(); // <2>
			// additional ObjectMapper configuration
			// ...
		}

		@Override
		public ObjectMapper getContext(Class<?> type) {
			return mapper;
		}

	}
	// end::jaxrsor1[]

	public void objectMapperResource() {
		// tag::jaxrsor2[]

		final ObjectMapper mapper = JacksonConfiguration.mapper();
		// additional ObjectMapper configuration
		// ...

		Context.get().classLoaderScope().map(s -> s.put(ObjectMapper.class.getName(), mapper)); // <1>
		// end::jaxrsor2[]
	}

	public void serializationMode() {
		// tag::sermode[]
		final ObjectMapper mapper = JacksonConfiguration.mapper();

		final ObjectWriter writer = mapper.writer() // <1>
				.withAttribute(JsonConfigProperties.PROPERTYBOX_SERIALIZATION_MODE_ATTRIBUTE_NAME,
						PropertyBoxSerializationMode.ALL); // <2>
		// end::sermode[]
	}

	// tag::spring[]
	@Configuration
	class Config {

		@Bean
		public RestTemplate restTemplate() {
			return SpringJacksonConfiguration.configure(new RestTemplate()); // <1>
		}

	}
	// end::spring[]

}
