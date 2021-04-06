# AutoBindings

![build](https://github.com/KaustubhPatange/AutoBindings/workflows/build/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.kaustubhpatange/autobindings)

**AutoBindings** is a set of annotations which will make Android development easier by eliminating boilerplate codes.

## Usage

Library currently supports

- [Generation of RecyclerView adapters](https://github.com/KaustubhPatange/AutoBindings/wiki/Adapter-Generation)
- [Generation of `TypeConverters` for Room](https://github.com/KaustubhPatange/AutoBindings/wiki/TypeConverter-Generation)
- [Generation of `ColumnAdapters` for custom types in SQLDelight](https://github.com/KaustubhPatange/AutoBindings/wiki/ColumnAdapter-Generation)

You can find some extra compiler options list [here](https://github.com/KaustubhPatange/AutoBindings/wiki/Compiler-Options).

## Download

```groovy
implementation 'io.github.kaustubhpatange:autobindings:<version>'

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
