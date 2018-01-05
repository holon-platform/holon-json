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
package com.holonplatform.json.datetime;

import com.holonplatform.core.temporal.TemporalType;

/**
 * Utility class to set the current {@link TemporalType} which has to be used for <code>java.util.Date</code> values
 * JSON serialization using a {@link ThreadLocal}.
 * 
 * @since 5.1.0
 */
public final class CurrentSerializationTemporalType {

	/**
	 * Current {@link TemporalType}
	 */
	private final static ThreadLocal<TemporalType> CURRENT_TEMPORAL_TYPE = new ThreadLocal<>();

	private CurrentSerializationTemporalType() {
	}

	/**
	 * Gets the current {@link TemporalType} to use for <code>java.util.Date</code> values JSON serialization.
	 * @return The current {@link TemporalType}
	 */
	public static TemporalType getCurrentTemporalType() {
		return CURRENT_TEMPORAL_TYPE.get();
	}

	/**
	 * Sets the current {@link TemporalType} to use for <code>java.util.Date</code> values JSON serialization.
	 * @param temporalType The {@link TemporalType} to set
	 */
	public static void setCurrentTemporalType(TemporalType temporalType) {
		CURRENT_TEMPORAL_TYPE.set(temporalType);
	}

	/**
	 * Removes the current {@link TemporalType} to use for <code>java.util.Date</code> values JSON serialization.
	 */
	public static void removeCurrentTemporalType() {
		CURRENT_TEMPORAL_TYPE.remove();
	}

}
