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