[![Releases](https://img.shields.io/github/release/HTTP-RPC/Sierra.svg)](https://github.com/HTTP-RPC/Sierra/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.httprpc/sierra.svg)](https://repo1.maven.org/maven2/org/httprpc/sierra/)
[![javadoc](https://javadoc.io/badge2/org.httprpc/sierra/javadoc.svg)](https://javadoc.io/doc/org.httprpc/sierra)

# Introduction
Sierra is an open-source framework for simplifying development of Java Swing applications. It provides a convenient DSL for declaratively instantiating Swing component hierarchies. The framework is extremely lightweight and has no external dependencies. 

The project's name comes from the nautical _S_ or _Sierra_ flag, representing the first letter in "Swing":

![](sierra.png)

This guide introduces the Sierra framework and provides an overview of its key features.

# Contents
* [Getting Sierra](#getting-sierra)
* [Sierra Classes](#sierra-classes)
* [Examples](#examples)
* [Additional Information](#additional-information)

# Getting Sierra
Sierra is distributed via Maven Central at [org.httprpc:sierra](https://repo1.maven.org/maven2/org/httprpc/sierra/). Java 11 or later is required.

# Sierra Classes
Sierra provides the `UIBuilder` class, whose methods can be used to declaratively establish a hierarchy of user interface elements. The methods provided by this class form a DSL, or "domain-specific language", that makes it easy to visualize the resulting output. 

TODO

```java
public static <C extends Component> Cell<C> cell(C component) { ... }
```

The returned `Cell` instance can be used to further customize the layout or configuration of the provided component:

* `weightBy()` - applies a weight to a cell
* `with()` - accepts a callback that can be used to set properties or invoke methods on the cell's component

The following methods also return a `Cell` instance and can be used to declare fixed-width or flexible spacer cells, respectively:

* `strut()`
* `glue()`

TODO

## ScrollingKeyboardFocusManager
Sierra additionally provides the `ScrollingKeyboardFocusManager` class, which can be used to ensure that components are automatically scrolled into view when focused:

```java
KeyboardFocusManager.setCurrentKeyboardFocusManager(new ScrollingKeyboardFocusManager());
```

# Examples
This section includes examples demonstrating usage of `UIBuilder` with the [Flat](https://github.com/JFormDesigner/FlatLaf) look-and-feel.

TODO

# Additional Information
This guide introduced the Sierra framework and provided an overview of its key features. For additional information, see the [source code](https://github.com/HTTP-RPC/Sierra/tree/master/sierra/src/main/java/org/httprpc/sierra).
