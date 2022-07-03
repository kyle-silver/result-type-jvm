# A Result Type for the Rest of Us

This package contains a small, composable Result type heavily inspired by Rust. It provides ergonomic and robust error handling and leverages Java's type system to make uncaught runtime exceptions a thing of the past.

## Installation

This package can be found on [Maven Central](https://search.maven.org/artifact/dev.kylesilver/result-type-jvm). For POM files, add the following:

```xml
<dependency>
  <groupId>dev.kylesilver</groupId>
  <artifactId>result-type-jvm</artifactId>
  <version>0.1.2</version>
</dependency>
```

For Gradle projects, use:

```groovy
implementation 'dev.kylesilver:result-type-jvm:0.1.2'
```


## Usage

A `Result` type indicates that a value is either a success `Ok` or an error `Err`. Before accessing the value inside, you must inspect the `Result` to determine which of the two possible outcomes have occurred.

Instead of throwing an exception, you can return an error value.

```java
// indicates the value was successful
Result<Integer, String> ok = Result.ok(1);

// indicates there was an error
Result<Integer, String> err = Result.err("something went wrong!");
```

If you want to get the value out and throw an exception if there's an error, you can do so with `unwrap()`

```java
int value = result.unwrap();
```

You can also substitute a default value in the event of an error

```java
int value = result.err().orElse(0);
```

And you can also apply stream-style maps which are only applied to the value if it is `Ok`

```java
// the map is applied to Ok values
Result<Integer, String> result1 = Result.ok(5);
String result = result1.map(x -> Integer.toString(x)).unwrap();

// but not to errors
Result<Integer, String> result2 = Result.err("whoops!");
result2.map(x -> Integer.toString(x)).isErr(); // true
```

You can use `match` to apply more complex manipulations to the result of an operation.

```java
Result<Integer, String> result = Result.ok(5);
int computed = result.match(
    ok -> ok + 1,
    err -> {
        log.warn("there was an error!");
        return 0;
    }
);
```

There's a lot more that you can do with Result types, check out the [docs](https://kyle-silver.github.io/result-type-jvm/dev/kylesilver/result/Result.html) for more details.
