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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.GsonConfiguration;
import com.holonplatform.json.gson.test.DataTest.TestEnum;

public class TestPropertyBoxSerialization {

	private static GsonBuilder builder;

	@BeforeClass
	public static void init() {
		builder = GsonConfiguration.builder();
	}

	@Test
	public void testNull() {

		final Gson gson = builder.create();

		String json = gson.toJson(null);
		assertNotNull(json);
	}

	@Test
	public void testPropertyBox() {

		final Gson gson = builder.create();

		// serialize

		PropertyBox box = PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "Test").set(NUMBER, 7.1d)
				.set(BOOL, Boolean.TRUE).set(DATE, DATE_VALUE).set(OBJECT_DATA, TEST_DATA_VALUE).set(ENUM, TestEnum.ONE)
				.set(ARRAY_DATA, new int[] { 1, 2, 3 }).set(LOCAL_DATE, LocalDate.of(1979, Month.MARCH, 9))
				.set(LOCAL_DATETIME, LocalDateTime.of(1979, Month.MARCH, 9, 11, 30)).set(NUMBOOL, Boolean.TRUE).build();

		String json = gson.toJson(box);

		PropertyBox readBox = PROPERTIES.execute(() -> gson.fromJson(json, PropertyBox.class));

		assertNotNull(readBox);

		assertEquals(new Long(1), readBox.getValue(KEY));
		assertEquals("Test", readBox.getValue(NAME));
		assertEquals(new Double(7.1), readBox.getValue(NUMBER));
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
	public void testPropertyBoxes() {

		final Gson gson = builder.create();

		// serialize

		final Collection<PropertyBox> boxes = new LinkedList<>();
		boxes.add(PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "Test").set(NUMBER, 7.1d)
				.set(BOOL, Boolean.TRUE).build());
		boxes.add(PropertyBox.builder(PROPERTIES).set(KEY, 2L).set(NAME, "Test2").set(NUMBER, 8.1d)
				.set(BOOL, Boolean.FALSE).build());

		String json = gson.toJson(boxes);

		// deserialize

		PropertyBox[] read = PROPERTIES.execute(() -> gson.fromJson(json, PropertyBox[].class));
		assertNotNull(read);
		assertEquals(2, read.length);

		PropertyBox readBox = read[0];

		assertNotNull(readBox);
		assertEquals(new Long(1), readBox.getValue(KEY));
		assertEquals("Test", readBox.getValue(NAME));
		assertEquals(new Double(7.1), readBox.getValue(NUMBER));
		assertEquals("Name is: Test", readBox.getValue(TEST));
		assertEquals(Boolean.TRUE, readBox.getValue(BOOL));

		readBox = read[1];

		assertNotNull(readBox);
		assertEquals(new Long(2), readBox.getValue(KEY));
		assertEquals("Test2", readBox.getValue(NAME));
		assertEquals(new Double(8.1), readBox.getValue(NUMBER));
		assertEquals("Name is: Test2", readBox.getValue(TEST));
		assertEquals(Boolean.FALSE, readBox.getValue(BOOL));

		Type collectionType = new TypeToken<Collection<PropertyBox>>() {
		}.getType();
		List<PropertyBox> lread = PROPERTIES.execute(() -> gson.fromJson(json, collectionType));
		assertNotNull(lread);
		assertEquals(2, lread.size());

		readBox = lread.get(0);

		assertNotNull(readBox);
		assertEquals(new Long(1), readBox.getValue(KEY));
		assertEquals("Test", readBox.getValue(NAME));
		assertEquals(new Double(7.1), readBox.getValue(NUMBER));
		assertEquals("Name is: Test", readBox.getValue(TEST));
		assertEquals(Boolean.TRUE, readBox.getValue(BOOL));

		readBox = lread.get(1);

		assertNotNull(readBox);
		assertEquals(new Long(2), readBox.getValue(KEY));
		assertEquals("Test2", readBox.getValue(NAME));
		assertEquals(new Double(8.1), readBox.getValue(NUMBER));
		assertEquals("Name is: Test2", readBox.getValue(TEST));
		assertEquals(Boolean.FALSE, readBox.getValue(BOOL));

	}

}
