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
package com.holonplatform.json;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.holonplatform.json.Json.JsonWriteException;

/**
 * Represents a JSON serialization result, providing methods to obtain the JSON data in different formats.
 * 
 * @since 5.1.0
 * 
 * @see Json
 */
public interface JsonWriter {

	/**
	 * Get the JSON data as a {@link String}.
	 * @return the JSON data as a String (may be null)
	 * @throws JsonWriteException If a JSON serialization error occured
	 */
	String asString();

	/**
	 * Get the JSON data as a byte array.
	 * <p>
	 * By default, the UTF-8 charset is used for encoding.
	 * </p>
	 * @return the JSON data as a byte array (may be null)
	 * @throws JsonWriteException If a JSON serialization error occured
	 */
	byte[] asBytes();

	/**
	 * Write the JSON data to given {@link Appendable} writer.
	 * @param writer the writer into which to write the JSON data (not null)
	 * @throws JsonWriteException If a JSON serialization error occured
	 */
	void write(Appendable writer);

	/**
	 * Write the JSON data to given {@link OutputStream}, using the UTF-8 charset for encoding.
	 * @param stream the stream into which to write the JSON data (not null)
	 * @throws JsonWriteException If a JSON serialization error occured
	 */
	default void write(OutputStream stream) {
		write(stream, StandardCharsets.UTF_8);
	}

	/**
	 * Write the JSON data to given {@link OutputStream}, using the given charset name for encoding.
	 * @param stream the stream into which to write the JSON data (not null)
	 * @param charsetName charset name to use for encoding
	 * @throws JsonWriteException If a JSON serialization error occured
	 */
	default void write(OutputStream stream, String charsetName) {
		write(stream, Charset.forName(charsetName));
	}

	/**
	 * Write the JSON data to given {@link OutputStream}, using the given {@link Charset} for encoding.
	 * @param stream the stream into which to write the JSON data (not null)
	 * @param charset charset to use for encoding
	 * @throws JsonWriteException If a JSON serialization error occured
	 */
	void write(OutputStream stream, Charset charset);

}
