![Build Status](https://travis-ci.org/bredmold/nickel-test.svg?branch=master)

# nickel-test
Testing Tools for Java &amp; JUnit

I've written tools like this many times over, so I thought I'd just write it one last time and put it on GitHub.

# How to Build
```bash
mvn clean install
```

# How to Use It

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

## Properties Example

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

## JSON Example
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

## XML Example
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

## YAML Example
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
