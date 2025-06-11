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
package com.holonplatform.json.gson.test;

import static com.holonplatform.json.gson.test.DataTest.ARRAY_DATA;
import static com.holonplatform.json.gson.test.DataTest.BOOL;
import static com.holonplatform.json.gson.test.DataTest.DATE;
import static com.holonplatform.json.gson.test.DataTest.DATE_VALUE;
import static com.holonplatform.json.gson.test.DataTest.ENUM;
import static com.holonplatform.json.gson.test.DataTest.KEY;
import static com.holonplatform.json.gson.test.DataTest.LOCAL_DATE;
import static com.holonplatform.json.gson.test.DataTest.LOCAL_DATETIME;
import static com.holonplatform.json.gson.test.DataTest.NAME;
import static com.holonplatform.json.gson.test.DataTest.NUMBER;
import static com.holonplatform.json.gson.test.DataTest.NUMBOOL;
import static com.holonplatform.json.gson.test.DataTest.OBJECT_DATA;
import static com.holonplatform.json.gson.test.DataTest.PROPERTIES;
import static com.holonplatform.json.gson.test.DataTest.TEST;
import static com.holonplatform.json.gson.test.DataTest.TEST_DATA_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.Json;
import com.holonplatform.json.JsonReader;
import com.holonplatform.json.gson.GsonJson;
import com.holonplatform.json.gson.test.DataTest.TestEnum;

public class TestJson {

	@Test
	public void testBuilder() {

		Json json = GsonJson.create();
		assertNotNull(json);

		json = GsonJson.create(new GsonBuilder());
		assertNotNull(json);

	}

	@Test
	public void testString() {

		final Json json = GsonJson.create();

		PropertyBox box = PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "Test").set(NUMBER, 7.1d)
				.set(BOOL, Boolean.TRUE).set(DATE, DATE_VALUE).set(OBJECT_DATA, TEST_DATA_VALUE).set(ENUM, TestEnum.ONE)
				.set(ARRAY_DATA, new int[] { 1, 2, 3 }).set(LOCAL_DATE, LocalDate.of(1979, Month.MARCH, 9))
				.set(LOCAL_DATETIME, LocalDateTime.of(1979, Month.MARCH, 9, 11, 30)).set(NUMBOOL, Boolean.TRUE).build();

		String jsonString = json.toJson(box).asString();

		assertNotNull(jsonString);

		String jsonString2 = json.toJsonString(box);

		assertNotNull(jsonString2);

		assertEquals(jsonString, jsonString2);

		PropertyBox readBox = PROPERTIES.execute(() -> json.fromJson(jsonString, PropertyBox.class));

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
		assertEquals(LocalDate.of(1979, Month.MARCH, 9), readBox.getValue(LOCAL_DATE));
		assertEquals(LocalDateTime.of(1979, Month.MARCH, 9, 11, 30), readBox.getValue(LOCAL_DATETIME));
		assertEquals(Boolean.TRUE, readBox.getValue(NUMBOOL));

	}

	@Test
	public void testReader() {

		final Json json = GsonJson.create();

		PropertyBox box = PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "Test").set(NUMBER, 7.1d)
				.set(BOOL, Boolean.TRUE).set(DATE, DATE_VALUE).set(OBJECT_DATA, TEST_DATA_VALUE).set(ENUM, TestEnum.ONE)
				.set(ARRAY_DATA, new int[] { 1, 2, 3 }).set(LOCAL_DATE, LocalDate.of(1979, Month.MARCH, 9))
				.set(LOCAL_DATETIME, LocalDateTime.of(1979, Month.MARCH, 9, 11, 30)).set(NUMBOOL, Boolean.TRUE).build();

		final StringBuilder sb = new StringBuilder();
		json.toJson(box).write(sb);

		assertTrue(sb.length() > 0);

		String jsonString2 = json.toJsonString(box);

		assertNotNull(jsonString2);

		assertEquals(sb.toString(), jsonString2);

		PropertyBox readBox = PROPERTIES
				.execute(() -> json.fromJson(JsonReader.from(new StringReader(sb.toString())), PropertyBox.class));

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
		assertEquals(LocalDate.of(1979, Month.MARCH, 9), readBox.getValue(LOCAL_DATE));
		assertEquals(LocalDateTime.of(1979, Month.MARCH, 9, 11, 30), readBox.getValue(LOCAL_DATETIME));
		assertEquals(Boolean.TRUE, readBox.getValue(NUMBOOL));

	}

	@Test
	public void testBytes() {

		final Json json = GsonJson.create();

		PropertyBox box = PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "Test").set(NUMBER, 7.1d)
				.set(BOOL, Boolean.TRUE).set(DATE, DATE_VALUE).set(OBJECT_DATA, TEST_DATA_VALUE).set(ENUM, TestEnum.ONE)
				.set(ARRAY_DATA, new int[] { 1, 2, 3 }).set(LOCAL_DATE, LocalDate.of(1979, Month.MARCH, 9))
				.set(LOCAL_DATETIME, LocalDateTime.of(1979, Month.MARCH, 9, 11, 30)).set(NUMBOOL, Boolean.TRUE).build();

		byte[] bytes = json.toJson(box).asBytes();

		assertNotNull(bytes);

		PropertyBox readBox = PROPERTIES.execute(() -> json.fromJson(JsonReader.from(bytes), PropertyBox.class));

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
		assertEquals(LocalDate.of(1979, Month.MARCH, 9), readBox.getValue(LOCAL_DATE));
		assertEquals(LocalDateTime.of(1979, Month.MARCH, 9, 11, 30), readBox.getValue(LOCAL_DATETIME));
		assertEquals(Boolean.TRUE, readBox.getValue(NUMBOOL));

	}

	@Test
	public void testStreams() {

		final Json json = GsonJson.create();

		PropertyBox box = PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "Test").set(NUMBER, 7.1d)
				.set(BOOL, Boolean.TRUE).set(DATE, DATE_VALUE).set(OBJECT_DATA, TEST_DATA_VALUE).set(ENUM, TestEnum.ONE)
				.set(ARRAY_DATA, new int[] { 1, 2, 3 }).set(LOCAL_DATE, LocalDate.of(1979, Month.MARCH, 9))
				.set(LOCAL_DATETIME, LocalDateTime.of(1979, Month.MARCH, 9, 11, 30)).set(NUMBOOL, Boolean.TRUE).build();

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		json.toJson(box).write(bos);

		PropertyBox readBox = PROPERTIES.execute(
				() -> json.fromJson(JsonReader.from(new ByteArrayInputStream(bos.toByteArray())), PropertyBox.class));

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
		assertEquals(LocalDate.of(1979, Month.MARCH, 9), readBox.getValue(LOCAL_DATE));
		assertEquals(LocalDateTime.of(1979, Month.MARCH, 9, 11, 30), readBox.getValue(LOCAL_DATETIME));
		assertEquals(Boolean.TRUE, readBox.getValue(NUMBOOL));

	}

	@Test
	public void testPropertySet() {

		final Json json = GsonJson.create();

		PropertyBox box = PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "Test").set(NUMBER, 7.1d)
				.set(BOOL, Boolean.TRUE).set(DATE, DATE_VALUE).set(OBJECT_DATA, TEST_DATA_VALUE).set(ENUM, TestEnum.ONE)
				.set(ARRAY_DATA, new int[] { 1, 2, 3 }).set(LOCAL_DATE, LocalDate.of(1979, Month.MARCH, 9))
				.set(LOCAL_DATETIME, LocalDateTime.of(1979, Month.MARCH, 9, 11, 30)).set(NUMBOOL, Boolean.TRUE).build();

		String jsonString = json.toJson(box).asString();

		assertNotNull(jsonString);

		PropertyBox readBox = json.fromJson(JsonReader.from(jsonString), PROPERTIES);

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
		assertEquals(LocalDate.of(1979, Month.MARCH, 9), readBox.getValue(LOCAL_DATE));
		assertEquals(LocalDateTime.of(1979, Month.MARCH, 9, 11, 30), readBox.getValue(LOCAL_DATETIME));
		assertEquals(Boolean.TRUE, readBox.getValue(NUMBOOL));

		readBox = json.fromJson(jsonString, PROPERTIES);

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
		assertEquals(LocalDate.of(1979, Month.MARCH, 9), readBox.getValue(LOCAL_DATE));
		assertEquals(LocalDateTime.of(1979, Month.MARCH, 9, 11, 30), readBox.getValue(LOCAL_DATETIME));
		assertEquals(Boolean.TRUE, readBox.getValue(NUMBOOL));

		readBox = json.fromJson(jsonString, KEY, NAME);

		assertNotNull(readBox);

		assertTrue(readBox.contains(KEY));
		assertTrue(readBox.contains(NAME));
		assertFalse(readBox.contains(NUMBER));
		assertFalse(readBox.contains(TEST));
		assertFalse(readBox.contains(BOOL));
		assertFalse(readBox.contains(DATE));
		assertFalse(readBox.contains(OBJECT_DATA));
		assertFalse(readBox.contains(ENUM));

		assertEquals(Long.valueOf(1), readBox.getValue(KEY));
		assertEquals("Test", readBox.getValue(NAME));

	}

	@Test
	public void testArray() {

		final Json json = GsonJson.create();

		PropertyBox box1 = PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "Test").set(NUMBER, 7.1d)
				.set(BOOL, Boolean.TRUE).set(DATE, DATE_VALUE).set(OBJECT_DATA, TEST_DATA_VALUE).set(ENUM, TestEnum.ONE)
				.set(ARRAY_DATA, new int[] { 1, 2, 3 }).set(LOCAL_DATE, LocalDate.of(1979, Month.MARCH, 9))
				.set(LOCAL_DATETIME, LocalDateTime.of(1979, Month.MARCH, 9, 11, 30)).set(NUMBOOL, Boolean.TRUE).build();

		PropertyBox box2 = PropertyBox.builder(PROPERTIES).set(KEY, 2L).set(NAME, "Test2").build();

		String jsonString = json.toJsonArray(PropertyBox.class, box1, box2).asString();

		assertNotNull(jsonString);

		List<PropertyBox> boxes = json.fromJsonArray(JsonReader.from(jsonString), PROPERTIES);

		assertNotNull(boxes);
		assertEquals(2, boxes.size());

		PropertyBox readBox = boxes.get(0);

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
		assertEquals(LocalDate.of(1979, Month.MARCH, 9), readBox.getValue(LOCAL_DATE));
		assertEquals(LocalDateTime.of(1979, Month.MARCH, 9, 11, 30), readBox.getValue(LOCAL_DATETIME));
		assertEquals(Boolean.TRUE, readBox.getValue(NUMBOOL));

		readBox = boxes.get(1);

		assertNotNull(readBox);

		assertEquals(Long.valueOf(2), readBox.getValue(KEY));
		assertEquals("Test2", readBox.getValue(NAME));

		Collection<PropertyBox> bx2 = new ArrayList<>();
		bx2.add(box1);
		bx2.add(box2);

		jsonString = json.toJsonArray(PropertyBox.class, bx2).asString();

		assertNotNull(jsonString);

		boxes = json.fromJsonArray(jsonString, PROPERTIES);

		assertNotNull(boxes);
		assertEquals(2, boxes.size());

		boxes = json.fromJsonArray(jsonString, KEY, NAME);

		assertNotNull(boxes);
		assertEquals(2, boxes.size());

	}

	@Test
	public void testNulls() {

		final Json json = GsonJson.create();

		String sv = json.toJson(null).asString();
		assertEquals("null", sv);

		byte[] bv = json.toJson(null).asBytes();
		assertEquals("null", new String(bv));

		StringBuffer sb = new StringBuffer();
		json.toJson(null).write(sb);
		assertEquals("null", sb.toString());

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		json.toJson(null).write(stream);
		assertEquals("null", new String(stream.toByteArray()));

	}

}
