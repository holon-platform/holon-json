# Holon platform JSON module

> Latest release: [5.1.2](#obtain-the-artifacts)

This is the __JSON__ module of the [Holon Platform](https://holon-platform.com), which provides support for the [JSON](http://www.json.org) data-interchange format using the must popular serialization and deserialization libraries:

* [Jackson](https://github.com/FasterXML/jackson)
* [Gson](https://github.com/google/gson)

The module main features are:

* A simple __JSON objects serialization and deserialization API__, to use concrete JSON parsers implementation in a easier and abstract way, providing also useful helper methods for Collections and `PropertyBox` types serialization and deserialization.
* __JSON__ serialization and deserialization support for the `PropertyBox` platform foundation data container class.
* __JAX-RS__ configuration and auto-configuration support to enable [Gson](https://github.com/google/gson) as default JSON type messages serialization and deserialization engine with `PropertyBox` support and to enable  `PropertyBox` serialization and deserialization when using [Jackson](http://wiki.fasterxml.com/JacksonHome) as default provider.
* Full support of the __Java 8 date and time API__: the `java.time.*` data types serialization and deserialization is supported and enabled out-of-the-box.
* The __ISO-8601__ format is used by default to serialize the `java.util.Date` data types.
* __Spring__ support to configure `PropertyBox` serialization and deserialization in `RestTemplate` JSON message converters.
* __Spring Boot__ support to auto-configure `Gson` and `ObjectMapper` instances with `PropertyBox` serialization and deserialization capabilities.

See the module [documentation](https://docs.holon-platform.com/current/reference/holon-json.html) for details.

Just like any other platform module, this artifact is part of the [Holon Platform](https://holon-platform.com) ecosystem, but can be also used as a _stand-alone_ library.

See [Getting started](#getting-started) and the [platform documentation](https://docs.holon-platform.com/current/reference) for further details.

## At-a-glance overview

_JSON API - serialization:_
```java
Json json = Json.require();

JsonWriter result = json.toJson(myObject);
String asString = result.asString(); 
byte[] asBytes = result.asBytes(); 
result.write(new StringWriter()); 
		
asString = json.toJsonString(myObject);
```

_JSON API - deserialization:_
```java
Json json = Json.require();

MyObject result = json.fromJson("[JSON string]", MyObject.class);
		
result = json.fromJson(JsonReader.from(new StringReader("JSON string")), MyObject.class);
```

_JSON API - Property model:_
```java
StringProperty NAME = StringProperty.create("name");
StringProperty SURNAME = StringProperty.create("surname");
PropertySet<?> PROPERTY_SET = PropertySet.of(NAME, SURNAME);
		
PropertyBox propertyBox = PropertyBox.builder(PROPERTY_SET).set(NAME, "John").set(SURNAME, "Doe").build();
		
Json json = Json.require();
		
String jsonValue = json.toJson(propertyBox).asString();
PropertyBox result = json.fromJson(jsonValue, PROPERTY_SET);
```

_JSON API - Provider:_
```java
// Using Jackson
Json jsonApi = JacksonJson.create();
		
// Using Gson
Json jsonApi = GsonJson.create();
```

See the [module documentation](https://docs.holon-platform.com/current/reference/holon-json.html) for the user guide and a full set of examples.

## Code structure

See [Holon Platform code structure and conventions](https://github.com/holon-platform/platform/blob/master/CODING.md) to learn about the _"real Java API"_ philosophy with which the project codebase is developed and organized.

## Getting started

### System requirements

The Holon Platform is built using __Java 8__, so you need a JRE/JDK version 8 or above to use the platform artifacts.

### Releases

See [releases](https://github.com/holon-platform/holon-json/releases) for the available releases. Each release tag provides a link to the closed issues.

#### 5.1.x release notes

See [What's new in version 5.1.x](https://docs.holon-platform.com/current/reference/holon-json.html#WhatsNew51x) to learn about the new features and API operations of the 5.1 minor release.

### Obtain the artifacts

The [Holon Platform](https://holon-platform.com) is open source and licensed under the [Apache 2.0 license](LICENSE.md). All the artifacts (including binaries, sources and javadocs) are available from the [Maven Central](https://mvnrepository.com/repos/central) repository.

The Maven __group id__ for this module is `com.holon-platform.json` and a _BOM (Bill of Materials)_ is provided to obtain the module artifacts:

_Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform.json</groupId>
        <artifactId>holon-json-bom</artifactId>
        <version>5.1.2</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Using the Platform BOM

The [Holon Platform](https://holon-platform.com) provides an overall Maven _BOM (Bill of Materials)_ to easily obtain all the available platform artifacts:

_Platform Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform</groupId>
        <artifactId>bom</artifactId>
        <version>${platform-version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Build from sources

You can build the sources using Maven (version 3.3.x or above is recommended) like this: 

`mvn clean install`

## Getting help

* Check the [platform documentation](https://docs.holon-platform.com/current/reference) or the specific [module documentation](https://docs.holon-platform.com/current/reference/holon-json.html).

* Ask a question on [Stack Overflow](http://stackoverflow.com). We monitor the [`holon-platform`](http://stackoverflow.com/tags/holon-platform) tag.

* Report an [issue](https://github.com/holon-platform/holon-json/issues).

* A [commercial support](https://holon-platform.com/services) is available too.

## Examples

See the [Holon Platform examples](https://github.com/holon-platform/holon-examples) repository for a set of example projects.

## Contribute

See [Contributing to the Holon Platform](https://github.com/holon-platform/platform/blob/master/CONTRIBUTING.md).

[![Gitter chat](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/holon-platform/contribute?utm_source=share-link&utm_medium=link&utm_campaign=share-link) 
Join the __contribute__ Gitter room for any question and to contact us.

## License

All the [Holon Platform](https://holon-platform.com) modules are _Open Source_ software released under the [Apache 2.0 license](LICENSE).

## Artifacts list

Maven _group id_: `com.holon-platform.json`

Artifact id | Description
----------- | -----------
`holon-json` | Holon JSON objects serialization and deserialization core API
`holon-gson` | Base [Gson](https://github.com/google/gson) configuration for `PropertyBox` serialization and deserialization support
`holon-gson-jaxrs` | __JAX-RS__ configuration support to use [Gson](https://github.com/google/gson) as JSON type messages serializer/deserializer
`holon-gson-spring` | Spring `RestTemplate` configuration and `Gson` Spring Boot auto-configuration
`holon-jackson` | Base [Jackson](http://wiki.fasterxml.com/JacksonHome) configuration for `PropertyBox` serialization and deserialization support
`holon-jackson-jaxrs` | __JAX-RS__ configuration support for [Jackson](http://wiki.fasterxml.com/JacksonHome) to enable `PropertyBox` serialization and deserialization in JSON format
`holon-jackson-spring` | Spring `RestTemplate` configuration and `ObjectMapper` Spring Boot auto-configuration
`holon-json-bom` | Bill Of Materials
`holon-json-bom-platform` | Bill Of Materials with external dependencies
`documentation-json` | Documentation
