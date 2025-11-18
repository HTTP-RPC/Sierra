# Sierra DTD Encoder
Generates a Sierra DTD file.

## Usage
```
dtd-encoder [bindings [classpath]]
```

The first argument represents the path to a properties file containing the custom bindings. Keys represent markup tags, and values are the fully qualified class names to which the tags will be bound. For example:

```properties
chart-panel = org.jfree.chart.ChartPanel
```

The second argument is the path to a directory containing the dependencies (typically the JAR files containing the types to be bound).

Both arguments are relative to the current directory.

If no arguments are provided, the standard Sierra DTD will be generated.

## Example
```
$ dtd-encoder bindings.properties build/libs
```
