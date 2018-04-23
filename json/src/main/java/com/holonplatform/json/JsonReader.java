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

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import com.holonplatform.json.internal.DefaultJsonReader;

/**
 * Represents a JSON encoded data source.
 * 
 * @since 5.1.0
 * 
 * @see Json
 */
public interface JsonReader {

	/**
	 * Get the {@link Reader} to read the JSON data.
	 * @return the JSON data reader
	 */
	Reader getReader();

	/**
	 * Build a {@link JsonReader} using given {@link Reader} as JSON data source.
	 * @param reader JSON data reader
	 * @return a new {@link JsonReader}
	 */
	static JsonReader from(Reader reader) {
		return new DefaultJsonReader(reader);
	}

	/**
	 * Build a {@link JsonReader} using given String as JSON data source.
	 * @param string JSON string
	 * @return a new {@link JsonReader}
	 */
	static JsonReader from(String string) {
		return new DefaultJsonReader(string);
	}

	/**
	 * Build a {@link JsonReader} using given byte array as JSON data source.
	 * @param bytes JSON data bytes (UTF-8 encoding is assumed by default)
	 * @return a new {@link JsonReader}
	 */
	static JsonReader from(byte[] bytes) {
		return new DefaultJsonReader(bytes);
	}

	/**
	 * Build a {@link JsonReader} using given {@link InputStream} as JSON data source and UTF-8 as charset.
	 * @param stream JSON data input stream
	 * @return a new {@link JsonReader}
	 */
	static JsonReader from(InputStream stream) {
		return new DefaultJsonReader(stream);
	}

	/**
	 * Build a {@link JsonReader} using given {@link InputStream} as JSON data source and given charset name.
	 * @param stream JSON data input stream
	 * @param charsetName name of the charset with which the stream is encoded
	 * @return a new {@link JsonReader}
	 */
	static JsonReader from(InputStream stream, String charsetName) {
		return new DefaultJsonReader(stream, charsetName);
	}

	/**
	 * Build a {@link JsonReader} using given {@link InputStream} as JSON data source and given charset.
	 * @param stream JSON data input stream
	 * @param charset charset with which the stream is encoded
	 * @return a new {@link JsonReader}
	 */
	static JsonReader from(InputStream stream, Charset charset) {
		return new DefaultJsonReader(stream, charset);
	}

}
