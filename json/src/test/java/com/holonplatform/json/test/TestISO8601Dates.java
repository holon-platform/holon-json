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
package com.holonplatform.json.test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.holonplatform.core.internal.utils.CalendarUtils;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.datetime.CurrentSerializationTemporalType;
import com.holonplatform.json.internal.datetime.ISO8601DateFormats;

public class TestISO8601Dates {

	@Test
	public void testFormat() throws ParseException {

		final Calendar c = Calendar.getInstance();
		c.set(1979, 2, 9, 10, 30);
		c.set(Calendar.SECOND, 25);
		c.set(Calendar.MILLISECOND, 0);
		
		String str = ISO8601DateFormats.format(c.getTime(), TemporalType.DATE);
		Assert.assertEquals("1979-03-09", str);
		
		try {
			CurrentSerializationTemporalType.setCurrentTemporalType(TemporalType.DATE);
			str = ISO8601DateFormats.format(c.getTime());
			Assert.assertEquals("1979-03-09", str);
		} finally {
			CurrentSerializationTemporalType.removeCurrentTemporalType();
		}
		
		final int offset = (c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000);
		final String offsetZ = ((offset < 0) ? "-" : "+") + StringUtils.leftPad("" + Math.abs(offset), 2, '0') + "00";
		
		str = ISO8601DateFormats.format(c.getTime());
		Assert.assertEquals("1979-03-09T10:30:25" + offsetZ, str);
		
		Date date = ISO8601DateFormats.parse(str);
		Assert.assertEquals(c.getTime(), date);
		
		date = ISO8601DateFormats.parse("1979-03-09", TemporalType.DATE);
		Assert.assertEquals(CalendarUtils.floorTime(c.getTime()), date);
		
		date = ISO8601DateFormats.parse("1979-03-09");
		Assert.assertEquals(CalendarUtils.floorTime(c.getTime()), date);
		
		date = ISO8601DateFormats.parse("1979-03-09T10:30:25");
		Assert.assertEquals(c.getTime(), date);
		
	}
	
	@Test
	public void testTimestamp() throws ParseException {
		
		final Calendar c = Calendar.getInstance();
		c.set(1979, 2, 9, 10, 30);
		c.set(Calendar.SECOND, 25);
		c.set(Calendar.MILLISECOND, 0);
		
		String str = "" + c.getTimeInMillis();
		
		Date date = ISO8601DateFormats.parse(str);
		Assert.assertEquals(c.getTime(), date);
		
		date = ISO8601DateFormats.parse(str, TemporalType.DATE);
		Assert.assertEquals(CalendarUtils.floorTime(c.getTime()), date);
		
	}

}
