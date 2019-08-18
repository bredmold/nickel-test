![Build Status](https://travis-ci.org/bredmold/nickel-test.svg?branch=master)

# nickel-test
Testing Tools for Java &amp; JUnit

A simple tool for managing unit tests data. An issue I've run into over and over in developing tests is how to
manage the data that my tests inevitably need. This is the answer I've come up with. Name the data for the test
case, so it's obvious which data is needed. This library helps keep an understandable link between test data
and test case.

It doesn't help at all with the problem of keeping your test data up to date.

# How to Build
```bash
mvn clean install
```

# How to Use It

There are two main ways to use NickelTest
1. Using static methods (assumes JUnit 4)
1. As a JUnit Rule

## Using NickelTest as static methods

This tool is built to be used with JUnit v4.x (currently tested with JUnit 4.12).

Here's a simple example:

```java
import static nickel.test.NickelTestResource.testResource;

public class ThingTest {
    @Test
    public void aTest() {
        /*
        Load a resource from the classpath: /ThingTest/aTest
         */
        String testString = testResource()
            .forTestMethod()
            .asString("UTF-8");
    }
}
```

That's that. Now you've pulled in a test resource from a path that's easy to know based on the name of your test
method.

But what if you're going to re-use the same file lots of times? Well, you can just copy it and rely on Git to notice
that you have the same file in your repo several times. But, you can pull your resource based on class name only,
and not method name.

```java
@Test
public void bTest() {
    /*
    Load a resource from the classpath: /ThingTest/reusable.txt
     */
    String testString = testResource()
        .forTestClass()
        .resourceName("reusable.txt")
        .asString("UTF-8");
}
```

### Properties Example

```java
@Test
public void propertiesTest() {
    /*
    Load a resource from the classpath: /ThingTest/propertiesTest.properties
     */
    Properties props = testResource()
        .forTestMethod()
        .asProperties();
}
```

### JSON Example
To use JSON, you need to have the Jackson library in your classpath. It looks slightly different, overall.

```java
import static nickel.test.jackson.JacksonTestResource.jacksonTestResource;

public class JsonTest {
    @Test
    public void test() {
        /*
        Load a resource from the classpath: /JsonTest/test.json
         */
        BeanClass bean = jacksonTestResource()
            .forTestMethod()
            .asJson(BeanClass.class);
    }
}
```

### XML Example
Similar to JSON, above, we need a special XML custom test resource. This will let us use the JVM's JAXB implementation
to parse XML.

```java
import static nickel.test.jaxb.JaxbTestResource.jaxbTestResource;

public class JaxbTest {
    @Test
    public void test() {
        /*
        Load a resource from the classpath: /JaxbTest/test.xml
         */
        BeanClass bean = jaxbTestResource()
            .forTestMethod()
            .asXml(BeanClass.class);
    }
}
```

### YAML Example
YAML processing is handled with the help of the SnakeYAML library.

```java
import static nickel.test.yaml.YamlTestResource.yamlTestResource;

public class YamlTest {
    @Test
    public void test() {
        /*
        Load a resource from the classpath: /YamlTest/test.yaml
         */
        BeanClass bean = yamlTestResource()
            .forTestMethod()
            .asYaml(BeanClass.class);
    }
}
```

## Using NickelTest as a JUnit 5 Extension
```java
import nickel.test.annotations.NickelTestResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@ExtendWith(NickelTestExtension.class)
class JUnit5Test {
    @NickelTestResource
    private String stringValue;

    @Test
    void test() {
        /*
        You can use stringValue here, because it will have been initialized.
         */
    }
}
```

Nice and simple. You annotate your field, and it gets loaded for you.

### Object Binding in JUnit 5
```java
import nickel.test.annotations.NickelTestResource;
import nickel.test.annotations.NickelTestResource.Binding;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@ExtendWith(NickelTestExtension.class)
class JacksonBindingTest {
    @NickelTestResource(with = Binding.jackson)
    private BeanClass bean;

    @Test
    void test() {
        /*
        Test with your instance of BeanClass, which has now been initialized using Jackson
         */
    }
}
```

Well, that was easy. All you have to do is tell the Framework to use Jackson. But what if you need to control the binding process? Well, you supply your own `ObjectMapper`, of course!

```java
import nickel.test.annotations.BindingContext;
import nickel.test.annotations.NickelTestResource;
import nickel.test.annotations.NickelTestResource.Binding;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@ExtendWith(NickelTestExtension.class)
class JacksonBindingTest {
    @NickelTestResource(with = Binding.jackson)
    private BeanClass bean;

    @BindingContext
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        /*
        Do special stuff here to initialize your ObjectMapper
         */
    }

    @Test
    void test() {
        /*
        Test with your instance of BeanClass, which has now been initialized using Jackson
         */
    }
}
```

Use the `@BindingContext` annotation to mark your framework-specific binding configuration:

Binding Value | Binding Context Type
------------- | --------------------
jackson       | `com.fasterxml.jackson.binding.ObjectMapper`
jaxb          | `javax.xml.bind.JAXBContext`
yaml          | `org.yaml.snakeyaml.Yaml`

## Using NickelTest as a JUnit 4 Rule
NickelTest can be incorporated into your test as a Rule.

```java
import nickel.test.junit4.NickelTestRule;
import org.junit.Rule;
import org.junit.Test;

public class RuleTest {
    @Rule
    public NickelTestRule nickelTestRule = new NickelTestRule()

    @Test
    public void test() {
        BeanClass bean = nickelTestRule.jacksonTestResource()
            .forTestMethod()
            .asJson(BeanClass.class);
    }
}
```

There are two advantages to using NickelTest in this mode
1. The rule infrastructure allows NickelTest to resolve the test method during the setup phase of a test.
2. Most IDEs will be able to auto-complete the methods on the rule, providing improved API discoverability.
