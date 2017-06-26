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

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.json.gson.GsonConfiguration;
import com.holonplatform.json.gson.spring.SpringGsonConfiguration;

@SuppressWarnings("unused")
public class ExampleGson {

	public void configuration() {
		// tag::configuration[]
		GsonBuilder builder = GsonConfiguration.builder(); // <1>
		Gson gson = builder.create();

		GsonBuilder mybuilder = getGsonBuilder(); // <2>
		GsonConfiguration.configure(builder); // <3>
		gson = mybuilder.create();
		// end::configuration[]
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

	// tag::spring[]
	class Config {

		@Bean
		public RestTemplate restTemplate() {
			RestTemplate rt = new RestTemplate();
			SpringGsonConfiguration.configure(rt); // <1>
			return rt;
		}

	}
	// end::spring[]

	@SuppressWarnings("static-method")
	private GsonBuilder getGsonBuilder() {
		return null;
	}

}
