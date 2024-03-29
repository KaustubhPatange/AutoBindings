# AutoBindings

![build](https://github.com/KaustubhPatange/AutoBindings/workflows/build/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.kaustubhpatange/autobindings-compiler)

### This project is _Discontinued_ as such logic for creating `RecyclerView` adapters, `TypeConverter`s for Room or SQLDelight can easily be abstracted into an abstract class. No need to use code generation.

___

**AutoBindings** is a set of annotations which will make Android development easier by eliminating boilerplate codes.

## Usage

Library currently supports

- [Generation of RecyclerView adapters](https://github.com/KaustubhPatange/AutoBindings/wiki/Adapter-Generation)
- [Generation of `TypeConverters` for Room](https://github.com/KaustubhPatange/AutoBindings/wiki/TypeConverter-Generation)
- [Generation of `ColumnAdapters` for custom types in SQLDelight](https://github.com/KaustubhPatange/AutoBindings/wiki/ColumnAdapter-Generation)
- [Use of `TypeConverters` for general purpose](https://github.com/KaustubhPatange/AutoBindings/wiki/Generic-use-of-TypeConverters)

You can find some set of extra compiler options [here](https://github.com/KaustubhPatange/AutoBindings/wiki/Compiler-Options).

## Download

```groovy
implementation 'io.github.kaustubhpatange:autobindings-recyclerview:<version>' // For Recyclerview bindings
implementation 'io.github.kaustubhpatange:autobindings-room:<version>' // For Room bindings
implementation 'io.github.kaustubhpatange:autobindings-sqldelight:<version>' // For SQLDelight bindings

implementation "io.github.kaustubhpatange:autobindings-room-noop:<version>" // For general use of typeconverters if you don't depend on Room

// Kotlin
apply plugin: 'kotlin-kapt' // at top of your module build.gradle file
kapt 'io.github.kaustubhpatange:autobindings-compiler:<version>'
// Java
annotationProcessor 'io.github.kaustubhpatange:autobindings-compiler:<version>'
```

## Resources

- **Medium article(s)**
  - [Auto-Generate TypeConverters for entity classes: Room](https://medium.com/@developerkp16/auto-generate-typeconverters-for-entity-classes-room-1b40a793c146)
  - [“Custom column types” in SQLDelight: Android](https://developerkp16.medium.com/custom-column-types-in-sqldelight-android-a6f166635464)

## License

- [The Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

```
Copyright 2020 Kaustubh Patange

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
