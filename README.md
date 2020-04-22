# ForgeToolkit

This repository contains a small toolkit that helps us to work with the main JourneyMap mod project in a development 
environment.

Please bear in mind that this was built specifically for the JourneyMap project, and will need customising if you 
want to use it in your project. Simply make your changes and `gradlew build` to rebuild.

## Usage

Note: This project currently operates only on Java files, and not Kotlin files.

1. Install Java 8 or later.
1. Download the latest version of ForgeToolkit from the [releases page](https://github.com/TeamJM/ForgeToolkit/releases/latest)
1. On the command-line, execute the JAR file - eg, `java -jar ForgeToolkit.jar` - for command help.

## Configuration

Some toolkit commands allow you to ignore sets of lang keys that may not be detected within your Java source code.
By default, the toolkit ignores keys with the following prefixes:

* `_`
* `jm.common.location_`
* `jm.webmap.`

You can configure this behaviour for use with your own project by creating a file named `.forge-toolkit.json`, which
should be placed in the current working directory (most likely the root of your project). It supports two options:

* `ignored_prefixes` should be a list of strings, representing prefixes to ignore.
* `ignored_patterns` should be a list of strings containing regular expressions - all matched keys will be ignored.

## Commands

For all examples below, `java -jar ForgeToolkit.jar` is replaced with `ForgeToolkit` for the sake of brevity.

### Clean

The `clean` command removes all unused translation keys from a given set of JSON-format lang files.

```bash
ForgeToolkit clean src/main/java src/main/resources/assets/journeymap/lang/*.json
```

This will modify the lang files in place, and output all the keys that have been removed. The keys will be printed
in JSON format, allowing you to copy them back into the file if needed.

Please note that if you are constructing language key strings dynamically, this tool will not detect them. If you're
sure your keys are present in your source code, you can ignore them by following the configuration instructions above.

### Finalise

The `finalise` (or `finalize`) command cleans up a lang file in preparation for submission to our 
[lang files repository](https://github.com/TeamJM/journeymap-lang).

```bash
ForgeToolkit finalise src/main/resources/assets/journeymap/lang/sv_se.json
```

This will modify the lang file in-place, performing the equivalent of a `flatten` operation, followed by a `sort`
operation.

### Flatten

When you use the `update` command, all untranslated keys in the given lang files will be placed within an
`untranslated` object at the bottom of the file. Once you've translated those keys, the `flatten` command will remove 
the `untranslated` object from those files, placing the key in the root object in the lang file.

```bash
ForgeToolkit flatten src/main/resources/assets/journeymap/lang/*.json
```

This will modify the lang files in-place.

### Sort

The `sort` command will simply sort the keys within a given set of lang files, in alphabetical order.

```bash
ForgeToolkit sort src/main/resources/assets/journeymap/lang/*.json
```

This will modify the lang files in-place.

### Update

The `update` command will use a primary lang file (the lang file the developer updates with the project's code) to
populate other lang files with the keys that are present in the primary file, if they're missing. An `untranslated` 
object will be created, and will contain those keys.

Once the keys have been translated, you can use the `flatten` command to merge them into the bottom of the translated
set of keys in the file, or the `finalise` command to both `flatten` and `sort` them into the rest of the file.

```bash
ForgeToolkit update src/main/resources/assets/journeymap/lang/en_us.json src/main/resources/assets/journeymap/lang/*.json
```

This will modify the lang files in-place. Additionally, it will ignore the primary lang file if it's specified as part
of the target files.

### Validate

The `validate` command will compare the keys in a given lang file with a directory containing the project's Java
source files, and output a list of keys that are present in the lang file, but not found within the Java source
files.

```bash
ForgeToolkit validate src/main/resources/assets/journeymap/lang/en_us.json src/main/java
```

Please note that if you are constructing language key strings dynamically, this tool will not detect them. If you're
sure your keys are present in your source code, you can ignore them by following the configuration instructions above.
