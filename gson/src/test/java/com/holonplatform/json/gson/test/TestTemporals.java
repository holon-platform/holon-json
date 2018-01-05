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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.datetime.CurrentSerializationTemporalType;
import com.holonplatform.json.gson.GsonConfiguration;

public class TestTemporals {

	@Test
	public void testDate() {

		Gson gson = GsonConfiguration.builder().create();

		Calendar c = Calendar.getInstance();
		c.set(1979, 2, 9, 10, 30);
		c.set(Calendar.SECOND, 25);
		c.set(Calendar.MILLISECOND, 0);
		Date date = c.getTime();

		final int offset = (c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000);
		final String offsetZ = ((offset < 0) ? "-" : "+") + StringUtils.leftPad("" + Math.abs(offset), 2, '0') + "00";

		String json = gson.toJson(date);
		Assert.assertEquals("\"1979-03-09T10:30:25" + offsetZ + "\"", json);

		Date deser = gson.fromJson(json, Date.class);
		Assert.assertEquals(date, deser);

		c = Calendar.getInstance();
		c.set(1979, 2, 9, 0, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		final Date date2 = c.getTime();

		try {
			CurrentSerializationTemporalType.setCurrentTemporalType(TemporalType.DATE);

			json = gson.toJson(date2);
			Assert.assertEquals("\"1979-03-09\"", json);

			deser = gson.fromJson(json, Date.class);
			Assert.assertEquals(date2, deser);

		} finally {
			CurrentSerializationTemporalType.removeCurrentTemporalType();
		}

	}

	@Test
	public void testDatePropertyBox() {

		Gson gson = GsonConfiguration.builder().create();

		Calendar c = Calendar.getInstance();
		c.set(1979, 2, 9, 10, 30);
		c.set(Calendar.SECOND, 25);
		c.set(Calendar.MILLISECOND, 0);
		Date date = c.getTime();

		PropertyBox pb = PropertyBox.builder(DataTest.DATE).set(DataTest.DATE, date).build();

		String json = gson.toJson(pb);

		c = Calendar.getInstance();
		c.set(1979, 2, 9, 0, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		final Date date2 = c.getTime();

		PropertyBox deser = PropertySet.of(DataTest.DATE).execute(() -> gson.fromJson(json, PropertyBox.class));
		Assert.assertEquals(date2, deser.getValue(DataTest.DATE));

	}

	@Test
	public void testSqlDate() {

		Gson gson = GsonConfiguration.builder().create();

		Calendar c = Calendar.getInstance();
		c.set(1979, 2, 9, 10, 30);
		c.set(Calendar.SECOND, 25);
		c.set(Calendar.MILLISECOND, 0);
		java.sql.Date date = new java.sql.Date(c.getTimeInMillis());

		final int offset = (c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000);
		final String offsetZ = ((offset < 0) ? "-" : "+") + StringUtils.leftPad("" + Math.abs(offset), 2, '0') + "00";

		String json = gson.toJson(date);
		Assert.assertEquals("\"1979-03-09T10:30:25" + offsetZ + "\"", json);

		java.sql.Date deser = gson.fromJson(json, java.sql.Date.class);
		Assert.assertEquals(date, deser);

		c = Calendar.getInstance();
		c.set(1979, 2, 9, 0, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		final java.sql.Date date2 = new java.sql.Date(c.getTimeInMillis());

		try {
			CurrentSerializationTemporalType.setCurrentTemporalType(TemporalType.DATE);

			json = gson.toJson(date2);
			Assert.assertEquals("\"1979-03-09\"", json);

			deser = gson.fromJson(json, java.sql.Date.class);
			Assert.assertEquals(date2, deser);

		} finally {
			CurrentSerializationTemporalType.removeCurrentTemporalType();
		}

	}

	@Test
	public void testLocalDate() {

		Gson gson = GsonConfiguration.builder().create();

		final LocalDate date = LocalDate.of(1979, Month.MARCH, 9);

		String json = gson.toJson(date);
		Assert.assertEquals("\"1979-03-09\"", json);

		LocalDate deser = gson.fromJson(json, LocalDate.class);
		Assert.assertEquals(date, deser);

	}

	@Test
	public void testLocalDateTime() {

		Gson gson = GsonConfiguration.builder().create();

		final LocalDateTime date = LocalDateTime.of(1979, Month.MARCH, 9, 10, 30, 25);

		String json = gson.toJson(date);
		Assert.assertEquals("\"1979-03-09T10:30:25\"", json);

		LocalDateTime deser = gson.fromJson(json, LocalDateTime.class);
		Assert.assertEquals(date, deser);

	}

	@Test
	public void testLocalTime() {

		Gson gson = GsonConfiguration.builder().create();

		final LocalTime time = LocalTime.of(10, 30, 25);

		String json = gson.toJson(time);
		Assert.assertEquals("\"10:30:25\"", json);

		LocalTime deser = gson.fromJson(json, LocalTime.class);
		Assert.assertEquals(time, deser);

	}

}
