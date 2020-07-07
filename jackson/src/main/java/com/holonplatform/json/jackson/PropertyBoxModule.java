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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.internal.JacksonPropertyBoxDeserializer;
import com.holonplatform.json.jackson.internal.JacksonPropertyBoxSerializer;

/**
 * Jackson module with {@link PropertyBox} serialization and deserialization
 * capabilities.
 * 
 * @since 5.1.0
 */
public class PropertyBoxModule extends Module {

	private final SimpleSerializers serializers = new SimpleSerializers();
	private final SimpleDeserializers deserializers = new SimpleDeserializers();

	public PropertyBoxModule() {
		super();
		serializers.addSerializer(PropertyBox.class, new JacksonPropertyBoxSerializer());
		deserializers.addDeserializer(PropertyBox.class, new JacksonPropertyBoxDeserializer());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fasterxml.jackson.databind.Module#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return PropertyBoxModule.class.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fasterxml.jackson.databind.Module#version()
	 */
	@Override
	public Version version() {
		return new Version(5, 0, 0, null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fasterxml.jackson.databind.Module#setupModule(com.fasterxml.jackson.
	 * databind.Module.SetupContext)
	 */
	@Override
	public void setupModule(SetupContext context) {
		context.addSerializers(serializers);
		context.addDeserializers(deserializers);
	}

}
