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

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.temporal.TemporalType;

public class DataTest {

	public static final PathProperty<Long> KEY = create("key", long.class);
	public static final PathProperty<String> NAME = create("name", String.class);
	public static final PathProperty<Double> NUMBER = create("number", Double.class);
	public static final PathProperty<Date> DATE = create("date", Date.class).temporalType(TemporalType.DATE);
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

	public static final Date DATE_VALUE;

	static {
		Calendar c = Calendar.getInstance();
		c.set(1979, 2, 9, 0, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		DATE_VALUE = c.getTime();
	}

	public static final TestData TEST_DATA_VALUE = new TestData(1, "One", TestEnum.TWO);

	// ------ nested

	public static final PathProperty<Long> N_CODE = create("code", long.class);
	public static final PathProperty<String> N_VALUE = create("value", String.class);

	public static final PropertySet<?> NESTED_PS = PropertySet.of(N_CODE, N_VALUE);

	public static final PathProperty<PropertyBox> NESTED1 = create("nested1", PropertyBox.class)
			.withConfiguration(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE, NESTED_PS);

	public static final PropertySet<?> PROPERTIES_NESTED = PropertySet.of(KEY, NAME, NUMBER, DATE, ENUM, BOOL,
			OBJECT_DATA, ARRAY_DATA, LOCAL_DATE, LOCAL_DATETIME, NUMBOOL, NESTED1, TEST);

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

}
