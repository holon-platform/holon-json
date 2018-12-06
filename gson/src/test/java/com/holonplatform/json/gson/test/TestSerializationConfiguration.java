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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.json.config.JsonConfigProperties;
import com.holonplatform.json.config.PropertyBoxSerializationMode;
import com.holonplatform.json.gson.GsonConfiguration;

public class TestSerializationConfiguration {

	private static final PathProperty<String> NAME = create("name", String.class);
	private static final VirtualProperty<String> VRT = VirtualProperty
			.create(String.class, pb -> "[" + pb.getValue(NAME) + "]").name("vrt");

	private static final PropertySet<?> PROPERTIES = PropertySet.of(NAME, VRT);

	private static final PropertySet<?> PROPERTIES_CFG = PropertySet.builderOf(NAME, VRT)
			.withConfiguration(JsonConfigProperties.PROPERTYBOX_SERIALIZATION_MODE, PropertyBoxSerializationMode.ALL)
			.build();

	@Test
	public void testPropertyBoxSerializationMode() {

		final Gson gson = GsonConfiguration.builder().create();

		PropertyBox box = PropertyBox.builder(PROPERTIES).set(NAME, "test").build();

		String json = gson.toJson(box);

		assertNotNull(json);
		assertFalse(json.contains("vrt"));

		box = PropertyBox.builder(PROPERTIES_CFG).set(NAME, "test").build();

		json = gson.toJson(box);

		assertNotNull(json);
		assertTrue(json.contains("vrt"));
		assertTrue(json.contains("[test]"));

		final Gson gson2 = GsonConfiguration.builder(PropertyBoxSerializationMode.ALL).create();

		box = PropertyBox.builder(PROPERTIES).set(NAME, "test").build();

		json = gson2.toJson(box);

		assertNotNull(json);
		assertTrue(json.contains("vrt"));
		assertTrue(json.contains("[test]"));
	}

}
