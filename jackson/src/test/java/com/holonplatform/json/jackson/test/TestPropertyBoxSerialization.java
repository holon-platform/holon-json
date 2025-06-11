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
package com.holonplatform.json.jackson.test;

import static com.holonplatform.json.jackson.test.DataTest.ARRAY_DATA;
import static com.holonplatform.json.jackson.test.DataTest.BOOL;
import static com.holonplatform.json.jackson.test.DataTest.DATE;
import static com.holonplatform.json.jackson.test.DataTest.DATE_VALUE;
import static com.holonplatform.json.jackson.test.DataTest.ENUM;
import static com.holonplatform.json.jackson.test.DataTest.KEY;
import static com.holonplatform.json.jackson.test.DataTest.NAME;
import static com.holonplatform.json.jackson.test.DataTest.NUMBER;
import static com.holonplatform.json.jackson.test.DataTest.OBJECT_DATA;
import static com.holonplatform.json.jackson.test.DataTest.PROPERTIES;
import static com.holonplatform.json.jackson.test.DataTest.TEST;
import static com.holonplatform.json.jackson.test.DataTest.TEST_DATA_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.JacksonConfiguration;
import com.holonplatform.json.jackson.test.DataTest.TestEnum;

public class TestPropertyBoxSerialization {

	private static ObjectMapper mapper;

	@BeforeAll
	static void init() {
		mapper = JacksonConfiguration.configure(new ObjectMapper());
	}

	@Test
	public void testConfig() {
		ObjectMapper m1 = JacksonConfiguration.configure(new ObjectMapper());

		assertTrue(m1.canSerialize(PropertyBox.class));

		m1 = JacksonConfiguration.mapper();

		assertTrue(m1.canSerialize(PropertyBox.class));
	}

	@Test
	public void testAutoRegister() {
		ObjectMapper mpr = new ObjectMapper();
		mpr.findAndRegisterModules();
		assertTrue(mpr.canSerialize(PropertyBox.class));
	}

	@Test
	public void testPropertyBox() throws IOException {

		// serialize

		final ObjectWriter writer = mapper.writer();

		PropertyBox box = PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "Test").set(NUMBER, 7.1d)
				.set(BOOL, Boolean.TRUE).set(DATE, DATE_VALUE).set(OBJECT_DATA, TEST_DATA_VALUE).set(ENUM, TestEnum.ONE)
				.set(ARRAY_DATA, new int[] { 1, 2, 3 }).build();

		String json = writer.writeValueAsString(box);

		// deserialize

		final ObjectReader reader = mapper.reader();

		PropertyBox readBox = PROPERTIES.execute(() -> reader.forType(PropertyBox.class).readValue(json));

		assertNotNull(readBox);

		assertEquals(Long.valueOf(1), readBox.getValue(KEY));
		assertEquals("Test", readBox.getValue(NAME));
		assertEquals(Double.valueOf(7.1), readBox.getValue(NUMBER));
		assertEquals("Name is: Test", readBox.getValue(TEST));
		assertEquals(Boolean.TRUE, readBox.getValue(BOOL));
		assertEquals(DATE_VALUE, readBox.getValue(DATE));
		assertEquals(TEST_DATA_VALUE, readBox.getValue(OBJECT_DATA));
		assertEquals(TestEnum.ONE, readBox.getValue(ENUM));
		assertTrue(Arrays.equals(new int[] { 1, 2, 3 }, readBox.getValue(ARRAY_DATA)));

	}

	@Test
	public void testPropertyBoxes() throws IOException {

		// serialize

		final ObjectWriter writer = mapper.writer();

		final Collection<PropertyBox> boxes = new LinkedList<>();
		boxes.add(PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "Test").set(NUMBER, 7.1d)
				.set(BOOL, Boolean.TRUE).build());
		boxes.add(PropertyBox.builder(PROPERTIES).set(KEY, 2L).set(NAME, "Test2").set(NUMBER, 8.1d)
				.set(BOOL, Boolean.FALSE).build());

		String json = writer.writeValueAsString(boxes);

		// deserialize

		final ObjectReader reader = mapper.reader();

		PropertyBox[] read = PROPERTIES.execute(() -> reader.forType(PropertyBox[].class).readValue(json));
		assertNotNull(read);
		assertEquals(2, read.length);

		PropertyBox readBox = read[0];

		assertNotNull(readBox);
		assertEquals(Long.valueOf(1), readBox.getValue(KEY));
		assertEquals("Test", readBox.getValue(NAME));
		assertEquals(Double.valueOf(7.1), readBox.getValue(NUMBER));
		assertEquals("Name is: Test", readBox.getValue(TEST));
		assertEquals(Boolean.TRUE, readBox.getValue(BOOL));

		readBox = read[1];

		assertNotNull(readBox);
		assertEquals(Long.valueOf(2), readBox.getValue(KEY));
		assertEquals("Test2", readBox.getValue(NAME));
		assertEquals(Double.valueOf(8.1), readBox.getValue(NUMBER));
		assertEquals("Name is: Test2", readBox.getValue(TEST));
		assertEquals(Boolean.FALSE, readBox.getValue(BOOL));

		List<PropertyBox> lread = PROPERTIES.execute(
				() -> reader.forType(mapper.getTypeFactory().constructCollectionType(List.class, PropertyBox.class))
						.readValue(json));
		assertNotNull(lread);
		assertEquals(2, lread.size());

		readBox = lread.get(0);

		assertNotNull(readBox);
		assertEquals(Long.valueOf(1), readBox.getValue(KEY));
		assertEquals("Test", readBox.getValue(NAME));
		assertEquals(Double.valueOf(7.1), readBox.getValue(NUMBER));
		assertEquals("Name is: Test", readBox.getValue(TEST));
		assertEquals(Boolean.TRUE, readBox.getValue(BOOL));

		readBox = lread.get(1);

		assertNotNull(readBox);
		assertEquals(Long.valueOf(2), readBox.getValue(KEY));
		assertEquals("Test2", readBox.getValue(NAME));
		assertEquals(Double.valueOf(8.1), readBox.getValue(NUMBER));
		assertEquals("Name is: Test2", readBox.getValue(TEST));
		assertEquals(Boolean.FALSE, readBox.getValue(BOOL));

	}

}
