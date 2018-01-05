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
package com.holonplatform.json.examples;

import static com.holonplatform.core.property.PathProperty.create;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.json.Json;
import com.holonplatform.json.JsonReader;
import com.holonplatform.json.JsonWriter;

@SuppressWarnings("unused")
public class ExampleJson {

	public void get() {
		// tag::get[]
		Optional<Json> json1 = Json.get(); // <1>

		Json json2 = Json.require(); // <2>
		// end::get[]
	}

	public void serialize() {
		// tag::serialize[]
		Json json = Json.require(); // <1>

		Object myObject = getObject(); // build or obtain the object to serialize

		JsonWriter result = json.toJson(myObject); // <2>

		String asString = result.asString(); // <3>
		byte[] asBytes = result.asBytes(); // <4>
		result.write(new StringWriter()); // <5>
		result.write(new ByteArrayOutputStream()); // <6>
		result.write(new ByteArrayOutputStream(), StandardCharsets.ISO_8859_1); // <7>

		asString = json.toJsonString(myObject); // <8>
		// end::serialize[]
	}

	public void serializepb() {
		// tag::serializepb[]
		Json json = Json.require(); // <1>

		final PathProperty<Long> KEY = create("key", long.class);
		final PathProperty<String> NAME = create("name", String.class);
		final PropertySet<?> PROPERTIES = PropertySet.of(KEY, NAME);

		PropertyBox propertyBox = PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "Test").build(); // <2>

		final StringBuilder sb = new StringBuilder();
		json.toJson(propertyBox).write(sb); // <3>
		// end::serializepb[]
	}

	public void serializec() {
		// tag::serializec[]
		Json json = Json.require(); // <1>

		Collection<Integer> values = Arrays.asList(1, 2, 3, 4);

		JsonWriter result = json.toJsonArray(Integer.class, values); // <2>
		String asString = json.toJsonArrayString(Integer.class, values); // <3>
		result = json.toJsonArray(Integer.class, 1, 2, 3, 4); // <4>
		// end::serializec[]
	}

	public void deserialize() {
		// tag::deserialize[]
		Json json = Json.require(); // <1>

		MyObject result = json.fromJson(JsonReader.from("[JSON string]"), MyObject.class); // <2>
		result = json.fromJson("[JSON string]", MyObject.class); // <3>
		result = json.fromJson(JsonReader.from(new byte[] { 1, 2, 3 }), MyObject.class); // <4>
		result = json.fromJson(JsonReader.from(new StringReader("[JSON string]")), MyObject.class); // <5>
		result = json.fromJson(JsonReader.from(new ByteArrayInputStream(new byte[] { 1, 2, 3 })), MyObject.class); // <6>
		// end::deserialize[]
	}

	public void deserializepb() {
		// tag::deserializepb[]
		Json json = Json.require(); // <1>

		final PathProperty<Long> KEY = create("key", long.class);
		final PathProperty<String> NAME = create("name", String.class);
		final PropertySet<?> PROPERTIES = PropertySet.of(KEY, NAME);

		PropertyBox result = json.fromJson(JsonReader.from("[JSON string]"), PROPERTIES); // <2>
		result = json.fromJson("[JSON string]", PROPERTIES); // <3>
		result = json.fromJson(JsonReader.from("[JSON string]"), KEY, NAME); // <4>

		List<PropertyBox> results = json.fromJsonArray("[JSON string]", PROPERTIES); // <5>
		// end::deserializepb[]
	}

	public void pscontext() {
		// tag::pscontext[]
		final PathProperty<Long> KEY = create("key", long.class);
		final PathProperty<String> NAME = create("name", String.class);
		final PropertySet<?> PROPERTIES = PropertySet.of(KEY, NAME);

		PropertyBox box = PROPERTIES.execute(() -> deserializePropertyBox());
		// end::pscontext[]
	}

	private static PropertyBox deserializePropertyBox() {
		return null;
	}

	@SuppressWarnings("static-method")
	private Object getObject() {
		return null;
	}

	class MyObject {

	}

}
