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

import static com.holonplatform.core.property.PathProperty.create;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.json.config.JsonConfigProperties;
import com.holonplatform.json.config.PropertyBoxSerializationMode;
import com.holonplatform.json.jackson.JacksonConfiguration;

public class TestSerializationConfiguration {

	private static final PathProperty<String> NAME = create("name", String.class);
	private static final VirtualProperty<String> VRT = VirtualProperty
			.create(String.class, pb -> "<" + pb.getValue(NAME) + ">").name("vrt");

	private static final PropertySet<?> PROPERTIES = PropertySet.of(NAME, VRT);

	private static final PropertySet<?> PROPERTIES_CFG = PropertySet.builderOf(NAME, VRT)
			.configuration(JsonConfigProperties.PROPERTYBOX_SERIALIZATION_MODE, PropertyBoxSerializationMode.ALL)
			.build();

	@Test
	public void testPropertyBoxSerializationMode() throws JsonProcessingException {

		final ObjectMapper mapper = JacksonConfiguration.mapper();

		final ObjectWriter writer = mapper.writer();

		PropertyBox box = PropertyBox.builder(PROPERTIES).set(NAME, "test").build();

		String json = writer.writeValueAsString(box);

		assertNotNull(json);
		assertFalse(json.contains("vrt"));

		box = PropertyBox.builder(PROPERTIES_CFG).set(NAME, "test").build();

		json = writer.writeValueAsString(box);

		assertNotNull(json);
		assertTrue(json.contains("vrt"));
		assertTrue(json.contains("<test>"));

		ObjectWriter writer2 = writer.withAttribute(JsonConfigProperties.PROPERTYBOX_SERIALIZATION_MODE_ATTRIBUTE_NAME,
				PropertyBoxSerializationMode.ALL);

		box = PropertyBox.builder(PROPERTIES).set(NAME, "test").build();

		json = writer2.writeValueAsString(box);

		assertNotNull(json);
		assertTrue(json.contains("vrt"));
		assertTrue(json.contains("<test>"));
	}

}
