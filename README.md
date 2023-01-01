[![Releases](https://img.shields.io/github/release/HTTP-RPC/Sierra.svg)](https://github.com/HTTP-RPC/Sierra/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.httprpc/sierra.svg)](https://repo1.maven.org/maven2/org/httprpc/sierra/)
[![javadoc](https://javadoc.io/badge2/org.httprpc/sierra/javadoc.svg)](https://javadoc.io/doc/org.httprpc/sierra)

# Introduction
Sierra is an open-source framework for simplifying development of Java Swing applications. It provides a convenient DSL for declaratively instantiating Swing component hierarchies. The framework is extremely lightweight (less than 40KB) and has no external dependencies. 

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
Sierra provides the `UIBuilder` class, whose methods can be used to declaratively establish a hierarchy of user interface elements. The methods defined by this class form a DSL, or "domain-specific language", that makes it easy to visualize the resulting output:

* `column()` - produces an instance of `ColumnPanel`, a container that automatically arranges sub-components along the y-axis
* `row()` - produces an instance of `RowPanel`, a container that automatically arranges sub-components along the x-axis
* `stack()` - produces an instance of `StackPanel`, a container that automatically arranges sub-components by z-order

These components offer an alternative to the standard Java layout managers, which can often be limiting or difficult to use in practice. `ColumnPanel` optionally aligns sub-components to a grid, similar to an HTML table or `GridBagLayout`. `RowPanel` optionally aligns sub-components to baseline, similar to `FlowLayout`. 

Additionally, `UIBuilder` provides this method for declaring a panel's contents:

```java
public static <C extends Component> Cell<C> cell(C component) { ... }
```

The returned `Cell` instance can be used to further customize the layout or configuration of the provided component:

* `weightBy()` - specifies the amount of excess space in a container that should be allocated to the component, relative to other weighted components
* `with()` - accepts a callback that can be used to set properties or invoke methods on the component

Finally, these `UIBuilder` methods can be used to declare spacer cells in column and row panels, similar to `BoxLayout`:

* `strut()` - declares a fixed-size spacer cell
* `glue()` - declares a flexible spacer cell

Sierra also includes the `TextPane` and `ImagePane` components, which provide an alternative to `JLabel` for displaying basic text or image content, respectively: `TextPane` supports wrapping text without requiring HTML, and `ImagePane` supports scaling without requiring an intermediate `BufferedImage`.

For example, the following code declares a column panel containing a graphic and a simple greeting:

```java
setContentPane(column(4, false,
    cell(new ImagePane(image, true)),
    cell(new TextPane("Hello, World!", false)).with(textPane -> textPane.setHorizontalAlignment(HorizontalAlignment.CENTER))
).with(columnPanel -> {
    columnPanel.setBackground(Color.WHITE);
    columnPanel.setOpaque(true);
    columnPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
}).getComponent());
```

The resulting output is shown below:

<img src="README/greeting.png" width="432px"/>

The complete source code can be found [here](https://github.com/HTTP-RPC/Sierra/blob/master/sierra-test/src/main/java/org/httprpc/sierra/GreetingTest.java).

# Examples
This section includes examples demonstrating usage of `UIBuilder` with the [Flat](https://github.com/JFormDesigner/FlatLaf) look-and-feel.

## Border Layout
Inspired by the [border layout](https://docs.oracle.com/javase/tutorial/uiswing/layout/border.html) tutorial example.

[BorderTest.java](https://github.com/HTTP-RPC/Sierra/blob/master/sierra-test/src/main/java/org/httprpc/sierra/BorderTest.java)

<img src="README/border.png" width="630px"/>

## Component Orientation
Inspired by the [flow layout](https://docs.oracle.com/javase/tutorial/uiswing/layout/flow.html) tutorial example.

[OrientationTest.java](https://github.com/HTTP-RPC/Sierra/blob/master/sierra-test/src/main/java/org/httprpc/sierra/OrientationTest.java)

<img src="README/orientation.png" width="451px"/>

## Baseline Alignment
Demonstrates baseline alignment.

[BaselineTest.java](https://github.com/HTTP-RPC/Sierra/blob/master/sierra-test/src/main/java/org/httprpc/sierra/BaselineTest.java)

<img src="README/baseline.png" width="324px"/>

## Box Alignment
Demonstrates box alignment.

[BoxTest.java](https://github.com/HTTP-RPC/Sierra/blob/master/sierra-test/src/main/java/org/httprpc/sierra/BoxTest.java)

<img src="README/box.png" width="547px"/>

## Grid Alignment
Demonstrates grid alignment.

[FormTest.java](https://github.com/HTTP-RPC/Sierra/blob/master/sierra-test/src/main/java/org/httprpc/sierra/FormTest.java)

<img src="README/form.png" width="592px"/>

[GridTest.java](https://github.com/HTTP-RPC/Sierra/blob/master/sierra-test/src/main/java/org/httprpc/sierra/GridTest.java)

<img src="README/grid.png" width="432px"/>

[AlignmentTest.java](https://github.com/HTTP-RPC/Sierra/blob/master/sierra-test/src/main/java/org/httprpc/sierra/AlignmentTest.java)

<img src="README/alignment.png" width="332px"/>

## Periodic Table
Inspired by Wikipedia's [classification of the elements](https://en.wikipedia.org/wiki/Periodic_table#Classification_of_elements).

[PeriodicTableTest.java](https://github.com/HTTP-RPC/Sierra/blob/master/sierra-test/src/main/java/org/httprpc/sierra/PeriodicTableTest.java)

<img src="README/periodic-table.png" width="1060px"/>

# Focus Management
Sierra also provides the `ScrollingKeyboardFocusManager` class, which can be used to ensure that components are automatically scrolled into view when focused:

```java
KeyboardFocusManager.setCurrentKeyboardFocusManager(new ScrollingKeyboardFocusManager());
```

# Additional Information
This guide introduced the Sierra framework and provided an overview of its key features. For additional information, see the [source code](https://github.com/HTTP-RPC/Sierra/tree/master/sierra/src/main/java/org/httprpc/sierra).
