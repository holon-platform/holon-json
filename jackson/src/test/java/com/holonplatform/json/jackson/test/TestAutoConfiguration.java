/*
 * Copyright 2000-2016 Holon TDCN.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.test.TestPropertyBoxSerialization.PTest;
import com.holonplatform.json.jackson.test.TestPropertyBoxSerialization.TestData;
import com.holonplatform.json.jackson.test.TestPropertyBoxSerialization.TestEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestAutoConfiguration {

	@Configuration
	@EnableAutoConfiguration
	protected static class Config {

	}

	private static final TestData TEST_DATA_VALUE = new TestData(1, "One", TestEnum.TWO);

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Test
	public void testAutoConfig() throws Exception {
		testOperation(mapper);
	}

	@Test
	public void testRestTemplate() throws Exception {
		RestTemplate rt = restTemplateBuilder.build();

		MappingJackson2HttpMessageConverter converter = null;
		for (HttpMessageConverter<?> c : rt.getMessageConverters()) {
			if (MappingJackson2HttpMessageConverter.class.isAssignableFrom(c.getClass())) {
				converter = (MappingJackson2HttpMessageConverter) c;
				break;
			}
		}

		assertNotNull(converter);

		testOperation(converter.getObjectMapper());
	}

	private static void testOperation(ObjectMapper mapper) throws Exception {
		// serialize

		final ObjectWriter writer = mapper.writer();

		PropertyBox box = PropertyBox.builder(PTest.PROPERTIES).set(PTest.KEY, 1L).set(PTest.NAME, "Test")
				.set(PTest.NUMBER, 7.1d).set(PTest.BOOL, Boolean.TRUE).set(PTest.OBJECT_DATA, TEST_DATA_VALUE)
				.set(PTest.ENUM, TestEnum.ONE).set(PTest.ARRAY_DATA, new int[] { 1, 2, 3 }).build();

		String json = writer.writeValueAsString(box);

		// deserialize

		final ObjectReader reader = mapper.reader();

		PropertyBox readBox = PTest.PROPERTIES.execute(() -> reader.forType(PropertyBox.class).readValue(json));

		assertNotNull(readBox);

		assertEquals(new Long(1), readBox.getValue(PTest.KEY));
		assertEquals("Test", readBox.getValue(PTest.NAME));
		assertEquals(new Double(7.1), readBox.getValue(PTest.NUMBER));
		assertEquals("Name is: Test", readBox.getValue(PTest.TEST));
		assertEquals(Boolean.TRUE, readBox.getValue(PTest.BOOL));
		assertEquals(TEST_DATA_VALUE, readBox.getValue(PTest.OBJECT_DATA));
		assertEquals(TestEnum.ONE, readBox.getValue(PTest.ENUM));
		assertTrue(Arrays.equals(new int[] { 1, 2, 3 }, readBox.getValue(PTest.ARRAY_DATA)));
	}

}
