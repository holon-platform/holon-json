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
package com.holonplatform.json.gson.test;

import static com.holonplatform.core.property.PathProperty.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.json.gson.GsonConfiguration;

public class TestPropertyBoxSerialization {

	private static Date DATE_VALUE;

	private static final TestData TEST_DATA_VALUE = new TestData(1, "One", TestEnum.TWO);

	private static GsonBuilder builder;

	@BeforeClass
	public static void init() {
		Calendar c = Calendar.getInstance();
		c.set(1979, 2, 9, 11, 30);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		DATE_VALUE = c.getTime();

		builder = GsonConfiguration.builder();
	}

	@Test
	public void testPropertyBox() {

		final Gson gson = builder.create();

		// serialize

		PropertyBox box = PropertyBox.builder(PTest.PROPERTIES).set(PTest.KEY, 1L).set(PTest.NAME, "Test")
				.set(PTest.NUMBER, 7.1d).set(PTest.BOOL, Boolean.TRUE).set(PTest.DATE, DATE_VALUE)
				.set(PTest.OBJECT_DATA, TEST_DATA_VALUE).set(PTest.ENUM, TestEnum.ONE)
				.set(PTest.ARRAY_DATA, new int[] { 1, 2, 3 }).set(PTest.LOCAL_DATE, LocalDate.of(1979, Month.MARCH, 9))
				.set(PTest.LOCAL_DATETIME, LocalDateTime.of(1979, Month.MARCH, 9, 11, 30))
				.set(PTest.NUMBOOL, Boolean.TRUE).build();

		String json = gson.toJson(box);

		PropertyBox readBox = PTest.PROPERTIES.execute(() -> gson.fromJson(json, PropertyBox.class));

		assertNotNull(readBox);

		assertEquals(new Long(1), readBox.getValue(PTest.KEY));
		assertEquals("Test", readBox.getValue(PTest.NAME));
		assertEquals(new Double(7.1), readBox.getValue(PTest.NUMBER));
		assertEquals("Name is: Test", readBox.getValue(PTest.TEST));
		assertEquals(Boolean.TRUE, readBox.getValue(PTest.BOOL));
		assertEquals(DATE_VALUE, readBox.getValue(PTest.DATE));
		assertEquals(TEST_DATA_VALUE, readBox.getValue(PTest.OBJECT_DATA));
		assertEquals(TestEnum.ONE, readBox.getValue(PTest.ENUM));
		assertTrue(Arrays.equals(new int[] { 1, 2, 3 }, readBox.getValue(PTest.ARRAY_DATA)));
		assertEquals(LocalDate.of(1979, Month.MARCH, 9), readBox.getValue(PTest.LOCAL_DATE));
		assertEquals(LocalDateTime.of(1979, Month.MARCH, 9, 11, 30), readBox.getValue(PTest.LOCAL_DATETIME));
		assertEquals(Boolean.TRUE, readBox.getValue(PTest.NUMBOOL));

	}

	@Test
	public void testPropertyBoxes() {

		final Gson gson = builder.create();

		// serialize

		final Collection<PropertyBox> boxes = new LinkedList<>();
		boxes.add(PropertyBox.builder(PTest.PROPERTIES).set(PTest.KEY, 1L).set(PTest.NAME, "Test")
				.set(PTest.NUMBER, 7.1d).set(PTest.BOOL, Boolean.TRUE).build());
		boxes.add(PropertyBox.builder(PTest.PROPERTIES).set(PTest.KEY, 2L).set(PTest.NAME, "Test2")
				.set(PTest.NUMBER, 8.1d).set(PTest.BOOL, Boolean.FALSE).build());

		String json = gson.toJson(boxes);

		// deserialize

		PropertyBox[] read = PTest.PROPERTIES.execute(() -> gson.fromJson(json, PropertyBox[].class));
		assertNotNull(read);
		assertEquals(2, read.length);

		PropertyBox readBox = read[0];

		assertNotNull(readBox);
		assertEquals(new Long(1), readBox.getValue(PTest.KEY));
		assertEquals("Test", readBox.getValue(PTest.NAME));
		assertEquals(new Double(7.1), readBox.getValue(PTest.NUMBER));
		assertEquals("Name is: Test", readBox.getValue(PTest.TEST));
		assertEquals(Boolean.TRUE, readBox.getValue(PTest.BOOL));

		readBox = read[1];

		assertNotNull(readBox);
		assertEquals(new Long(2), readBox.getValue(PTest.KEY));
		assertEquals("Test2", readBox.getValue(PTest.NAME));
		assertEquals(new Double(8.1), readBox.getValue(PTest.NUMBER));
		assertEquals("Name is: Test2", readBox.getValue(PTest.TEST));
		assertEquals(Boolean.FALSE, readBox.getValue(PTest.BOOL));

		Type collectionType = new TypeToken<Collection<PropertyBox>>() {
		}.getType();
		List<PropertyBox> lread = PTest.PROPERTIES.execute(() -> gson.fromJson(json, collectionType));
		assertNotNull(lread);
		assertEquals(2, lread.size());

		readBox = lread.get(0);

		assertNotNull(readBox);
		assertEquals(new Long(1), readBox.getValue(PTest.KEY));
		assertEquals("Test", readBox.getValue(PTest.NAME));
		assertEquals(new Double(7.1), readBox.getValue(PTest.NUMBER));
		assertEquals("Name is: Test", readBox.getValue(PTest.TEST));
		assertEquals(Boolean.TRUE, readBox.getValue(PTest.BOOL));

		readBox = lread.get(1);

		assertNotNull(readBox);
		assertEquals(new Long(2), readBox.getValue(PTest.KEY));
		assertEquals("Test2", readBox.getValue(PTest.NAME));
		assertEquals(new Double(8.1), readBox.getValue(PTest.NUMBER));
		assertEquals("Name is: Test2", readBox.getValue(PTest.TEST));
		assertEquals(Boolean.FALSE, readBox.getValue(PTest.BOOL));

	}

	// --------------------------

	public static enum TestEnum {

		ONE, TWO;

	}

	@SuppressWarnings("serial")
	public static class TestData implements Serializable {

		private int sequence;
		private String description;
		private TestEnum type;

		public TestData() {
			super();
		}

		public TestData(int sequence, String description, TestEnum type) {
			super();
			this.sequence = sequence;
			this.description = description;
			this.type = type;
		}

		public int getSequence() {
			return sequence;
		}

		public void setSequence(int sequence) {
			this.sequence = sequence;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public TestEnum getType() {
			return type;
		}

		public void setType(TestEnum type) {
			this.type = type;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((description == null) ? 0 : description.hashCode());
			result = prime * result + sequence;
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestData other = (TestData) obj;
			if (description == null) {
				if (other.description != null)
					return false;
			} else if (!description.equals(other.description))
				return false;
			if (sequence != other.sequence)
				return false;
			if (type != other.type)
				return false;
			return true;
		}

	}

	public final static class PTest {

		public static final PathProperty<Long> KEY = create("key", long.class);
		public static final PathProperty<String> NAME = create("name", String.class);
		public static final PathProperty<Double> NUMBER = create("number", Double.class);
		public static final PathProperty<Date> DATE = create("date", Date.class);
		public static final PathProperty<TestEnum> ENUM = create("enum", TestEnum.class);
		public static final PathProperty<Boolean> BOOL = create("bool", boolean.class);
		public static final PathProperty<TestData> OBJECT_DATA = create("objectData", TestData.class);
		public static final PathProperty<int[]> ARRAY_DATA = create("arrayData", int[].class);
		public static final PathProperty<LocalDate> LOCAL_DATE = create("ldate", LocalDate.class);
		public static final PathProperty<LocalDateTime> LOCAL_DATETIME = create("ldatetime", LocalDateTime.class);

		public static final PathProperty<Boolean> NUMBOOL = create("numbool", Boolean.class)
				.converter(PropertyValueConverter.numericBoolean(Integer.class));

		public static final Property<String> TEST = VirtualProperty.create(String.class)
				.valueProvider(pb -> "Name is: " + pb.getValue(NAME));

		public static final PropertySet<?> PROPERTIES = PropertySet.of(KEY, NAME, NUMBER, DATE, ENUM, BOOL, OBJECT_DATA,
				ARRAY_DATA, LOCAL_DATE, LOCAL_DATETIME, NUMBOOL, TEST);

	}

}
