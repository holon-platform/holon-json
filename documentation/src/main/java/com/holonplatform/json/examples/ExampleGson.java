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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holonplatform.core.Context;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.Json;
import com.holonplatform.json.config.PropertyBoxSerializationMode;
import com.holonplatform.json.datetime.CurrentSerializationTemporalType;
import com.holonplatform.json.gson.GsonConfiguration;
import com.holonplatform.json.gson.GsonJson;
import com.holonplatform.json.gson.spring.SpringGsonConfiguration;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ContextResolver;

@SuppressWarnings("unused")
public class ExampleGson {

	public void configuration() {
		// tag::configuration[]
		GsonBuilder builder = GsonConfiguration.builder(); // <1>

		builder = GsonConfiguration.configure(new GsonBuilder()); // <2>
		// end::configuration[]
	}

	public void temporals() throws IOException {
		// tag::temporals[]
		Gson gson = GsonConfiguration.builder().create(); // <1>

		LocalDate date = LocalDate.of(2018, Month.JANUARY, 5);

		String serialized = gson.toJson(date); // <2>

		LocalDate deserialized = gson.fromJson(serialized, LocalDate.class); // <3>
		// end::temporals[]
	}

	public void json() {
		// tag::json[]
		Json jsonApi = Json.require(); // <1>

		jsonApi = GsonJson.create(); // <2>
		// end::json[]
	}

	public void ttype() throws IOException {
		// tag::ttype[]
		Gson gson = GsonConfiguration.builder().create(); // <1>

		Calendar c = Calendar.getInstance();
		c.set(2018, 0, 5);
		final Date date = c.getTime();

		try {
			CurrentSerializationTemporalType.setCurrentTemporalType(TemporalType.DATE); // <2>
			String json = gson.toJson(date); // <3>
		} finally {
			CurrentSerializationTemporalType.removeCurrentTemporalType(); // <4>
		}
		// end::ttype[]
	}

	// tag::serdeser[]
	final static PathProperty<Long> ID = PathProperty.create("id", Long.class);
	final static PathProperty<String> DESCRIPTION = PathProperty.create("description", String.class);

	final static PropertySet<?> PROPERTY_SET = PropertySet.of(ID, DESCRIPTION);

	public void serializeAndDeserialize() {
		Gson gson = GsonConfiguration.builder().create(); // <1>

		PropertyBox box = PropertyBox.builder(PROPERTY_SET).set(ID, 1L).set(DESCRIPTION, "Test").build(); // <2>

		// serialize
		String json = gson.toJson(box); // <3>
		// deserialize
		box = PROPERTY_SET.execute(() -> gson.fromJson(json, PropertyBox.class)); // <4>
	}
	// end::serdeser[]

	public void serializationMode() {
		// tag::sermode[]
		GsonBuilder builder = GsonConfiguration.builder(PropertyBoxSerializationMode.ALL); // <1>

		builder = GsonConfiguration.configure(new GsonBuilder(), PropertyBoxSerializationMode.ALL); // <2>
		// end::sermode[]
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
		public Response create(@PropertySetRef(value = ExampleGson.class, field = "PROPERTYSET") PropertyBox data) {
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
		Client client = ClientBuilder.newClient(); // <1>

		PropertyBox box1 = PropertyBox.builder(PROPERTYSET).set(CODE, 1).set(NAME, "Test").build();

		client.target("https://host/test/serialize").request().put(Entity.entity(box1, MediaType.APPLICATION_JSON)); // <2>

		PropertyBox box2 = PROPERTYSET
				.execute(() -> client.target("https://host/test/deserialize").request().get(PropertyBox.class)); // <3>

	}
	// end::jaxrs[]

	// tag::jaxrsor1[]
	@Produces(MediaType.APPLICATION_JSON) // <1>
	public static class MyObjectMapperResolver implements ContextResolver<Gson> {

		private final Gson gson;

		public MyObjectMapperResolver() {
			super();
			GsonBuilder builder = GsonConfiguration.builder(); // <2>
			// additional GsonBuilder configuration ...
			gson = builder.create();
		}

		@Override
		public Gson getContext(Class<?> type) {
			return gson;
		}

	}
	// end::jaxrsor1[]

	public void gsonResource() {
		// tag::jaxrsor2[]

		GsonBuilder builder = GsonConfiguration.builder();
		// additional GsonBuilder configuration ...
		final Gson gson = builder.create();

		Context.get().classLoaderScope().map(s -> s.put(Gson.class.getName(), gson)); // <1>
		// end::jaxrsor2[]
	}

	// tag::spring[]
	@Configuration
	class Config {

		@Bean
		public RestTemplate restTemplate() {
			return SpringGsonConfiguration.configure(new RestTemplate()); // <1>
		}

	}
	// end::spring[]

}
