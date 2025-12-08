# Sierra DTD Encoder
Generates a Sierra DTD file.

## Usage
```
dtd-encoder [bindings-file [classpath]]
```

Both arguments are optional. The first represents the path to a properties file containing the custom bindings. Keys represent markup tags, and values are the fully qualified class names to which the tags will be bound. 

The second argument is the path to a directory containing the dependencies (typically the JAR files containing the types to be bound). 

Paths are relative to the current directory.

## Example
```
$ dtd-encoder src/main/resources/bindings.properties build/libs
```
