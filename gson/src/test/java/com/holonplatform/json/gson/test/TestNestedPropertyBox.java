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
import static com.holonplatform.json.gson.test.DataTest.NAME;
import static com.holonplatform.json.gson.test.DataTest.NESTED1;
import static com.holonplatform.json.gson.test.DataTest.NESTED_PS;
import static com.holonplatform.json.gson.test.DataTest.NUMBER;
import static com.holonplatform.json.gson.test.DataTest.N_CODE;
import static com.holonplatform.json.gson.test.DataTest.N_VALUE;
import static com.holonplatform.json.gson.test.DataTest.OBJECT_DATA;
import static com.holonplatform.json.gson.test.DataTest.PROPERTIES_NESTED;
import static com.holonplatform.json.gson.test.DataTest.TEST;
import static com.holonplatform.json.gson.test.DataTest.TEST_DATA_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.GsonConfiguration;
import com.holonplatform.json.gson.test.DataTest.TestEnum;

public class TestNestedPropertyBox {

	private static GsonBuilder builder;

	@BeforeAll
	static void init() {
		builder = GsonConfiguration.builder();
	}

	@Test
	public void testNestedPropertyBox() {

		// serialize

		final Gson gson = builder.create();

		PropertyBox nested1 = PropertyBox.builder(NESTED_PS).set(N_CODE, 777L).set(N_VALUE, "testNested1Value").build();

		PropertyBox box = PropertyBox.builder(PROPERTIES_NESTED).set(KEY, 1L).set(NAME, "Test").set(NUMBER, 7.1d)
				.set(BOOL, Boolean.TRUE).set(DATE, DATE_VALUE).set(OBJECT_DATA, TEST_DATA_VALUE).set(ENUM, TestEnum.ONE)
				.set(ARRAY_DATA, new int[] { 1, 2, 3 }).set(NESTED1, nested1).build();

		String json = gson.toJson(box);

		// deserialize

		PropertyBox readBox = PROPERTIES_NESTED.execute(() -> gson.fromJson(json, PropertyBox.class));

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

		assertNotNull(readBox.getValue(NESTED1));

		PropertyBox readNested = readBox.getValue(NESTED1);
		assertTrue(readNested.contains(N_CODE));

	}

}
