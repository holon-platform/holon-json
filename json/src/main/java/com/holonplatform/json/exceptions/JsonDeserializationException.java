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
package com.holonplatform.json.exceptions;

/**
 * Exception related to JSON deserialization errors.
 *
 * @since 5.1.0
 */
public class JsonDeserializationException extends RuntimeException {

	private static final long serialVersionUID = 8737886228579742957L;

	/**
	 * Constructor with error message.
	 * @param message Error message
	 */
	public JsonDeserializationException(String message) {
		super(message);
	}

	/**
	 * Constructor with error message and cause.
	 * @param message Error message
	 * @param cause Cause
	 */
	public JsonDeserializationException(String message, Throwable cause) {
		super(message, cause);
	}

}
