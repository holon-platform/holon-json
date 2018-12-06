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

import static com.holonplatform.core.property.PathProperty.create;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.json.gson.GsonConfiguration;

public class TestPropertyHierarchy {

	private static final PathProperty<Long> KEY = create("key", Long.class);
	private static final PathProperty<String> NAME = create("name", String.class);
	private static final PathProperty<Integer> NESTED_CODE = create("nested.code", Integer.class);
	private static final PathProperty<String> NESTED_VALUE = create("nested.value", String.class);

	private static final PropertySet<?> PROPERTIES = PropertySet.of(KEY, NAME, NESTED_CODE, NESTED_VALUE);

	public static final PathProperty<Integer> N_CODE = create("code", Integer.class);
	public static final PathProperty<String> N_VALUE = create("value", String.class);

	private static final PathProperty<PropertyBox> NESTED = create("nested", PropertyBox.class)
			.withConfiguration(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE, PropertySet.of(N_CODE, N_VALUE));

	private static final PropertySet<?> PROPERTIES2 = PropertySet.of(KEY, NAME, NESTED, NESTED_CODE, NESTED_VALUE);

	public static final PathProperty<Integer> N_CODE_P = create("code", Integer.class).parent(NESTED);
	public static final PathProperty<String> N_VALUE_P = create("value", String.class).parent(NESTED);

	private static final PropertySet<?> PROPERTIES3 = PropertySet.of(KEY, NAME, NESTED, N_CODE_P, N_VALUE_P);

	@Test
	public void testNestedPropertyPathName() {

		final Gson gson = GsonConfiguration.builder().create();

		// serialize

		PropertyBox box = PropertyBox.builder(PROPERTIES).set(KEY, 1L).set(NAME, "test").set(NESTED_CODE, 777)
				.set(NESTED_VALUE, "testNested").build();

		String json = gson.toJson(box);

		// deserialize

		PropertyBox readBox = PROPERTIES.execute(() -> gson.fromJson(json, PropertyBox.class));

		assertNotNull(readBox);

		assertEquals(Long.valueOf(1L), readBox.getValue(KEY));
		assertEquals("test", readBox.getValue(NAME));
		assertEquals(Integer.valueOf(777), readBox.getValue(NESTED_CODE));
		assertEquals("testNested", readBox.getValue(NESTED_VALUE));

		readBox = PROPERTIES2.execute(() -> gson.fromJson(json, PropertyBox.class));

		assertNotNull(readBox);

		assertEquals(Long.valueOf(1L), readBox.getValue(KEY));
		assertEquals("test", readBox.getValue(NAME));
		assertEquals(Integer.valueOf(777), readBox.getValue(NESTED_CODE));
		assertEquals("testNested", readBox.getValue(NESTED_VALUE));
		assertNotNull(readBox.getValue(NESTED));
		PropertyBox readNested = readBox.getValue(NESTED);
		assertTrue(readNested.contains(N_CODE));
		assertTrue(readNested.contains(N_VALUE));
		assertEquals(Integer.valueOf(777), readNested.getValue(N_CODE));
		assertEquals("testNested", readNested.getValue(N_VALUE));

		readBox = PROPERTIES3.execute(() -> gson.fromJson(json, PropertyBox.class));

		assertNotNull(readBox);

		assertEquals(Long.valueOf(1L), readBox.getValue(KEY));
		assertEquals("test", readBox.getValue(NAME));
		assertEquals(Integer.valueOf(777), readBox.getValue(N_CODE_P));
		assertEquals("testNested", readBox.getValue(N_VALUE_P));
		assertNotNull(readBox.getValue(NESTED));
		readNested = readBox.getValue(NESTED);
		assertTrue(readNested.contains(N_CODE));
		assertTrue(readNested.contains(N_VALUE));
		assertEquals(Integer.valueOf(777), readNested.getValue(N_CODE));
		assertEquals("testNested", readNested.getValue(N_VALUE));
	}

	private static final PathProperty<String> SN_ROOT1 = create("root1", String.class);
	private static final PathProperty<String> SN_ROOT2 = create("root2", String.class);
	private static final PathProperty<String> SN_L1_1 = create("level1.p1", String.class);
	private static final PathProperty<String> SN_L1_2 = create("level1.p2", String.class);
	private static final PathProperty<String> SN_L2_1 = create("level1.level2.p1", String.class);

	private static final PropertySet<?> SN_PROPERTIES = PropertySet.of(SN_ROOT1, SN_L1_1, SN_L1_2, SN_L2_1, SN_ROOT2);

	@Test
	public void testSubNestedPropertyPathName() {

		final Gson gson = GsonConfiguration.builder().create();

		// serialize

		PropertyBox box = PropertyBox.builder(SN_PROPERTIES).set(SN_ROOT1, "R1").set(SN_L1_1, "L1_1")
				.set(SN_L1_2, "L1_2").set(SN_L2_1, "L2_1").set(SN_ROOT2, "R2").build();

		String json = gson.toJson(box);

		// deserialize

		PropertyBox readBox = SN_PROPERTIES.execute(() -> gson.fromJson(json, PropertyBox.class));

		assertNotNull(readBox);

		assertEquals("R1", readBox.getValue(SN_ROOT1));
		assertEquals("R2", readBox.getValue(SN_ROOT2));
		assertEquals("L1_1", readBox.getValue(SN_L1_1));
		assertEquals("L1_2", readBox.getValue(SN_L1_2));
		assertEquals("L2_1", readBox.getValue(SN_L2_1));
	}

}
