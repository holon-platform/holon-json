/*
 * Copyright 2016-2018 Axioma srl.
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
package com.holonplatform.json.model;

import java.util.List;
import java.util.Optional;

import com.holonplatform.core.property.Property;
import com.holonplatform.json.internal.model.DefaultPropertySetSerializationNode;

/**
 * A {@link PropertySetSerializationTree} node.
 *
 * @since 5.2.0
 */
public interface PropertySetSerializationNode {

	/**
	 * Get the node serialization name.
	 * @return the node serialization name
	 */
	String getName();

	/**
	 * Get the property bound to this node, if it is a leaf node.
	 * @return Optional node property
	 */
	Optional<Property<?>> getProperty();

	/**
	 * Get the children nodes, if it isn't a leaf node.
	 * @return the children nodes, empty if a leaf node
	 */
	List<PropertySetSerializationNode> getChildren();

	/**
	 * Create a non-leaf {@link PropertySetSerializationNode}.
	 * @param name Serialization name (not null)
	 * @return A new {@link PropertySetSerializationNode} instance
	 */
	static PropertySetSerializationNode create(String name) {
		return new DefaultPropertySetSerializationNode(name);
	}

	/**
	 * Create a leaf {@link PropertySetSerializationNode}.
	 * @param name Serialization name (not null)
	 * @param property The {@link Property} bound to the node (not null)
	 * @return A new {@link PropertySetSerializationNode} instance
	 */
	static PropertySetSerializationNode create(String name, Property<?> property) {
		return new DefaultPropertySetSerializationNode(name, property);
	}

}
