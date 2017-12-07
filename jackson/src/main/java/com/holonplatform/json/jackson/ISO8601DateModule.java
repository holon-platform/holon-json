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
package com.holonplatform.json.jackson;

import java.util.Date;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.holonplatform.json.jackson.internal.datetime.JacksonISO8601DateSerializer;

/**
 * Jackson module to register ISO-8601 {@link Date} serializers.
 *
 * @since 5.1.0
 */
public class ISO8601DateModule extends SimpleModule {

	private static final long serialVersionUID = 2813464926516518217L;

	public ISO8601DateModule() {
		super(ISO8601DateModule.class.getName(), new Version(5, 0, 0, null, null, null));
		addSerializer(Date.class, new JacksonISO8601DateSerializer());
	}

}
