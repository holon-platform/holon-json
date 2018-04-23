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
package com.holonplatform.json.gson.internal;

import javax.annotation.Priority;

import com.holonplatform.json.Json;
import com.holonplatform.json.JsonProvider;
import com.holonplatform.json.gson.GsonJson;

/**
 * Default {@link JsonProvider} using Gson.
 *
 * @since 5.1.0
 */
@Priority(Integer.MAX_VALUE - 500)
public class DefaultGsonJsonProvider implements JsonProvider {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.JsonProvider#provide()
	 */
	@Override
	public Json provide() {
		return GsonJson.create();
	}

}
