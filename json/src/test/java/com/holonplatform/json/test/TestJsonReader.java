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
package com.holonplatform.json.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.holonplatform.json.JsonReader;

public class TestJsonReader {

	@Test
	public void testReader() throws IOException {

		final String json = "{a:1}";

		try (Reader reader = JsonReader.from(new StringReader(json)).getReader()) {
			assertNotNull(reader);
			assertEquals(json, read(reader));
		}
	}

	@Test
	public void testString() throws IOException {

		final String json = "{a:1}";

		try (Reader reader = JsonReader.from(json).getReader()) {
			assertNotNull(reader);
			assertEquals(json, read(reader));
		}
	}

	@Test
	public void testBytes() throws IOException {

		final String json = "{a:1}";

		try (Reader reader = JsonReader.from(json.getBytes(StandardCharsets.UTF_8)).getReader()) {
			assertNotNull(reader);
			assertEquals(json, read(reader));
		}
	}

	@Test
	public void testStream() throws IOException {

		final String json = "{a:1}";
		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

		try (Reader reader = JsonReader.from(new ByteArrayInputStream(bytes)).getReader()) {
			assertNotNull(reader);
			assertEquals(json, read(reader));
		}

		try (Reader reader = JsonReader.from(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8).getReader()) {
			assertNotNull(reader);
			assertEquals(json, read(reader));
		}

		try (Reader reader = JsonReader.from(new ByteArrayInputStream(bytes), "UTF-8").getReader()) {
			assertNotNull(reader);
			assertEquals(json, read(reader));
		}
	}

	private static String read(Reader reader) throws IOException {
		int intValueOfChar;
		String targetString = "";
		while ((intValueOfChar = reader.read()) != -1) {
			targetString += (char) intValueOfChar;
		}
		return targetString;
	}

}
