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
package com.holonplatform.json.internal.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.json.model.PropertySetSerializationNode;
import com.holonplatform.json.model.PropertySetSerializationTree;
import com.holonplatform.json.model.PropertySetSerializationTreeResolver;

/**
 * Default {@link PropertySetSerializationTreeResolver} implementation.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
public class DefaultPropertySetSerializationTreeResolver implements PropertySetSerializationTreeResolver {

	@SuppressWarnings("unchecked")
	private Function<PropertySet<?>, Iterable<Property>> preProcessor = ps -> (PropertySet<Property>) ps;

	private Predicate<Property<?>> validator = p -> true;

	/**
	 * Set the property set pre-processor.
	 * @param preProcessor the pre-processor to set (not null)
	 */
	public void setPreProcessor(Function<PropertySet<?>, Iterable<Property>> preProcessor) {
		ObjectUtils.argumentNotNull(preProcessor, "PropertySet pre-processor must be not null");
		this.preProcessor = preProcessor;
	}

	/**
	 * Set the property validator.
	 * @param validator the validator to set (not null)
	 */
	public void setValidator(Predicate<Property<?>> validator) {
		ObjectUtils.argumentNotNull(validator, "Property validator must be not null");
		this.validator = validator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.model.PropertySetSerializationTreeResolver#resolve(com.holonplatform.core.property.
	 * PropertySet)
	 */
	@Override
	public PropertySetSerializationTree resolve(PropertySet<?> propertySet) {
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		return getSerializationTree(propertySet);
	}

	/**
	 * Build the PropertySet serialization tree.
	 * @param propertySet PropertySet to serialize
	 * @return the serialization tree
	 */
	private PropertySetSerializationTree getSerializationTree(PropertySet<?> propertySet) {
		DefaultPropertySetSerializationTree tree = new DefaultPropertySetSerializationTree();

		// pre-process
		Iterable<Property> actualPropertySet = preProcessor.apply(propertySet);

		// progress tracking list
		final List<Property<?>> properties = new LinkedList<>();
		actualPropertySet.forEach(p -> properties.add(p));

		for (Property<?> property : actualPropertySet) {
			getSerializationNode(properties, property).ifPresent(n -> {
				tree.add(n);
			});
		}
		return tree;
	}

	/**
	 * Get the {@link PropertySetSerializationNode} for given property.
	 * @param properties Available properties
	 * @param property Property to parse
	 * @return The optional {@link PropertySetSerializationNode} which corresponds to given property
	 */
	private Optional<PropertySetSerializationNode> getSerializationNode(List<Property<?>> properties,
			Property<?> property) {
		final List<String> pathNames = getPropertySerializationHierarchy(property);
		if (!pathNames.isEmpty()) {
			if (pathNames.size() == 1) {
				properties.remove(property);
				return Optional.of(new DefaultPropertySetSerializationNode(pathNames.get(0), property));
			} else {
				return getSerializationNode(properties, Collections.singletonList(pathNames.get(0)));
			}
		}
		return Optional.empty();
	}

	/**
	 * Get the {@link PropertySetSerializationNode} which corresponds to given parent path names hierarchy.
	 * @param properties Available properties
	 * @param parentPathNames Parent path names hierarchy
	 * @param serializationMode Serialization mode
	 * @return Optional {@link PropertySetSerializationNode} which corresponds to given parent path names hierarchy
	 */
	private Optional<PropertySetSerializationNode> getSerializationNode(List<Property<?>> properties,
			List<String> parentPathNames) {
		// check valid path names
		if (parentPathNames == null || parentPathNames.isEmpty()) {
			return Optional.empty();
		}
		// build node
		DefaultPropertySetSerializationNode node = new DefaultPropertySetSerializationNode(
				parentPathNames.get(parentPathNames.size() - 1));
		for (Property<?> property : properties) {
			final List<String> pathNames = getPropertySerializationHierarchy(property);
			if (pathNames.size() > parentPathNames.size()
					&& pathNamesEquals(pathNames.subList(0, parentPathNames.size()), parentPathNames)) {
				// check hierarchy
				if (pathNames.size() == parentPathNames.size() + 1) {
					getSerializationPropertyName(property).ifPresent(name -> {
						node.addChild(new DefaultPropertySetSerializationNode(name, property));
					});
				} else {
					List<String> parents = new LinkedList<>();
					parents.addAll(parentPathNames);
					parents.add(pathNames.get(parentPathNames.size()));
					// check sub hierarchy
					getSerializationNode(properties, parents).ifPresent(n -> {
						node.addChild(n);
					});
				}
			}
		}
		if (!node.getChildren().isEmpty()) {
			// remove processed properties
			node.getChildren().forEach(c -> c.getProperty().ifPresent(p -> properties.remove(p)));
			return Optional.of(node);
		}
		return Optional.empty();
	}

	/**
	 * Checks if given path names hierarchies are equal.
	 * @param p1 First path name hierarchy
	 * @param p2 Second path name hierarchy
	 * @return <code>true</code> if given path names hierarchies are equal
	 */
	private static boolean pathNamesEquals(List<String> p1, List<String> p2) {
		if (p1 == null || p2 == null) {
			return false;
		}
		for (int i = 0; i < p1.size(); i++) {
			if (p1.get(i) == null || !p1.get(i).equals(p2.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the property serialization names hierarchy, only if the property must be included in serialization according
	 * to current validator.
	 * @param property The property for which to obtain the serialization hierarchy
	 * @return the property serialization names hierarchy
	 */
	private List<String> getPropertySerializationHierarchy(Property<?> property) {
		if (!validator.test(property)) {
			return Collections.emptyList();
		}
		if (Path.class.isAssignableFrom(property.getClass())) {
			return getPathNameHierarchy((Path<?>) property);
		}
		if (property.getName() != null) {
			return Collections.singletonList(property.getName());
		}
		return Collections.emptyList();
	}

	/**
	 * Get the path names hierarchy from given path, ujsing any parent path and splitting the path name if a dot
	 * notation is detected.
	 * @param path Path
	 * @return the path names hierarchy
	 */
	private static List<String> getPathNameHierarchy(Path<?> path) {
		final String pathName = path.relativeName();
		if (pathName == null) {
			return Collections.emptyList();
		}
		if (pathName.indexOf('.') < 1) {
			return Collections.singletonList(pathName);
		}
		return Arrays.asList(pathName.split("\\."));
	}

	/**
	 * Get the property serialization name, if the property serialization names hierarchy is not empty.
	 * @param property Property
	 * @return Optional property serialization name
	 */
	private static Optional<String> getSerializationPropertyName(Property<?> property) {
		List<String> names = (Path.class.isAssignableFrom(property.getClass()))
				? getPathNameHierarchy((Path<?>) property)
				: Collections.singletonList(property.getName());
		if (!names.isEmpty()) {
			return Optional.ofNullable(names.get(names.size() - 1));
		}
		return Optional.empty();
	}

	// ------- Builder

	public static class DefaultBuilder implements Builder {

		private final DefaultPropertySetSerializationTreeResolver instance = new DefaultPropertySetSerializationTreeResolver();

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.json.model.PropertySetSerializationTreeResolver.Builder#preProcessor(java.util.function.
		 * Function)
		 */
		@Override
		public Builder preProcessor(Function<PropertySet<?>, Iterable<Property>> preProcessor) {
			instance.setPreProcessor(preProcessor);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.json.model.PropertySetSerializationTreeResolver.Builder#validator(java.util.function.
		 * Predicate)
		 */
		@Override
		public Builder validator(Predicate<Property<?>> validator) {
			instance.setValidator(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.json.model.PropertySetSerializationTreeResolver.Builder#build()
		 */
		@Override
		public PropertySetSerializationTreeResolver build() {
			return instance;
		}

	}

}
