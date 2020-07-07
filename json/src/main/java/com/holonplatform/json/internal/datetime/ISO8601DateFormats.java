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
package com.holonplatform.json.internal.datetime;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.holonplatform.core.internal.utils.CalendarUtils;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.json.datetime.CurrentSerializationTemporalType;

/**
 * ISO-8601 date formats used by JSON {@link Date} serializers and
 * deserializers.
 *
 * @since 5.1.0
 */
public final class ISO8601DateFormats {

	static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	static final String ISO_DATETIME_FORMAT_NO_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss";
	static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
	static final String ISO_TIME_FORMAT = "HH:mm:ss";

	private ISO8601DateFormats() {
	}

	/**
	 * Format given <code>date</code> using a suitable format pattern according to
	 * current {@link TemporalType}, or {@link TemporalType#DATE_TIME} if none
	 * available.
	 * @param date Date to format
	 * @return Formatted date, <code>null</code> if given date was null
	 */
	public static String format(Date date) {
		return format(date, null);
	}

	/**
	 * Format given <code>date</code> using a suitable format pattern according to
	 * given {@link TemporalType}, or {@link TemporalType#DATE_TIME} if none
	 * available.
	 * @param date         Date to format
	 * @param temporalType TemporalType to use, <code>null</code> to use current
	 *                     TemporalType
	 * @return Formatted date, <code>null</code> if given date was null
	 */
	public static String format(Date date, TemporalType temporalType) {
		if (date != null) {
			return getDateFormat(getTemporalType(temporalType), true).format(date);
		}
		return null;
	}

	/**
	 * Parse given date/time string, using a suitable date pattern.
	 * @param str String to parse
	 * @return Parsed Date
	 * @throws ParseException If a parsing error occurred
	 */
	public static Date parse(String str) throws ParseException {
		return parse(str, null);
	}

	/**
	 * Parse given date/time string, using a suitable date pattern and given
	 * {@link TemporalType} to provide a consistent Date instance.
	 * @param str          String to parse
	 * @param temporalType Temporal type
	 * @return Parsed Date
	 * @throws ParseException If a parsing error occurred
	 */
	public static Date parse(String str, TemporalType temporalType) throws ParseException {
		if (str != null && !str.trim().isEmpty()) {
			final TemporalType tt = getTemporalType(temporalType);

			final Date date;
			boolean noTime = TemporalType.DATE == tt;

			long asLong = NumberUtils.toLong(str);
			if (asLong > 0) {
				// parse as an instant
				date = new Date(asLong);
			} else {

				if (str.indexOf('T') > -1) {
					// with time
					if (hasTimeZone(str)) {
						// with time zone
						date = FastDateFormat.getInstance(ISO_DATETIME_FORMAT).parse(str);
					} else {
						// without time zone
						date = FastDateFormat.getInstance(ISO_DATETIME_FORMAT_NO_TIMEZONE).parse(str);
					}
				} else {
					// no time
					noTime = true;
					date = FastDateFormat.getInstance(ISO_DATE_FORMAT).parse(str);
				}

			}

			if (date != null && noTime) {
				return CalendarUtils.floorTime(date);
			}
			return date;
		}
		return null;
	}

	/**
	 * Checks if given string includes a time zone
	 * @param str String to check
	 * @return <code>true</code> if given string includes a time zone
	 */
	private static boolean hasTimeZone(String str) {
		int tidx = str.lastIndexOf('T');
		int pidx = str.lastIndexOf('+');
		int midx = str.lastIndexOf('-');
		return (pidx > 0 && pidx > tidx) || (midx > 0 && midx > tidx);
	}

	/**
	 * Get the date format to use according to given {@link TemporalType}.
	 * @param temporalType TemporalType to use, <code>null</code> to use current
	 *                     TemporalType
	 * @param withTimeZone Whether to take into account the time zone part or not
	 * @return Date format
	 */
	private static FastDateFormat getDateFormat(TemporalType temporalType, boolean withTimeZone) {
		switch (temporalType) {
		case DATE:
			return FastDateFormat.getInstance(ISO_DATE_FORMAT);
		case TIME:
			return FastDateFormat.getInstance(ISO_TIME_FORMAT);
		case DATE_TIME:
		default:
			return withTimeZone ? FastDateFormat.getInstance(ISO_DATETIME_FORMAT)
					: FastDateFormat.getInstance(ISO_DATETIME_FORMAT_NO_TIMEZONE);
		}
	}

	/**
	 * Get the {@link TemporalType} to use to format dates, according to given
	 * {@link TemporalType}. If <code>null</code>, the
	 * {@link #CURRENT_TEMPORAL_TYPE} ThreadLocal is checked. If no
	 * {@link TemporalType} is available, the DATE_TIME format is used by default.
	 * @param temporalType Optional {@link TemporalType} to use to select the date
	 *                     format
	 * @return The temporal type
	 */
	private static TemporalType getTemporalType(TemporalType temporalType) {
		TemporalType tt = (temporalType != null) ? temporalType
				: CurrentSerializationTemporalType.getCurrentTemporalType();
		if (tt == null) {
			tt = TemporalType.DATE_TIME;
		}
		return tt;
	}

}
