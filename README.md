# AutoBindings

![build](https://github.com/KaustubhPatange/AutoBindings/workflows/build/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.kaustubhpatange/autobindings)

**AutoBindings** is a set of annotations which will make Android development easier by eliminating boilerplate codes.

> _Currently it only supports recyclerView adapter generation._

> _The library is still in alpha (not recommend for production). Although you can try it out. I'll be adding more features soon._

## Download

```groovy
implementation 'io.github.kaustubhpatange:autobindings:tag'

// Kotlin
apply plugin: 'kotlin-kapt' // at top of your module build.gradle file
kapt 'io.github.kaustubhpatange:autobindings-compiler:tag'
// Java
annotationProcessor 'io.github.kaustubhpatange:autobindings-compiler:tag'
```

## Usage

- [Adapter Generation]()
  - [RecyclerViewAdpater](#recyclerview-adapter)
  - [ListAdapter](#list-adapter)
  - [Notes](#notes)

### RecyclerView Adapter

> _The working and implementation might change!_

- A basic adapter consiting of a `dataSet` & are updated through `adapter.notify*` methods, usually used for normal purpose.

```kotlin
@RecyclerViewAdapter(R.layout.recyclerview_item_layout, Data::class)
class TestAdapter {
    @Bind
    fun bind(view: View, item: Data, position: Int) {
        // Set your views.
    }

    @OnClick(R.id.item_id)
    fun onClick(context: Context, item: Data, position: Int) {
        // ...
    }

    @OnLongClick(R.id.item_id)
    fun onLongClick(context: Context, item: Data, position: Int) {
        // ...
    }
}
```

- Compile the project and you'll have `BindTestAdapter` class generated (ready for use).
- You can then use the `BindTestAdapter` class with `recyclerView`.

```kotlin
// Kotlin
recyclerView.adapter = BindTestAdapter(TestAdapter(), List<Data>)

//Java
recyclerView.setAdapter(new BindTestAdapter(new TestAdapter(), List<Data>));
```

- Notice we're passing a new instance of `TestAdapter` as parameter because we don't want to allocate objects for every instance of `BindTestAdapter` even if we are not using it.

- Check [sample-ktx](sample-ktx/) project for it's usage.

### List Adapter

- An adapter which is typically used with `viewModel` for submitting data through `livedata`

```kotlin
@RecyclerViewListAdapter(R.layout.recyclerview_item_layout, Data::class)
class TestAdapter {
    @DiffItemSame
    fun itemSame(oldItem: Data, newItem: Data): Boolean {
        // DiffUtil.areItemsTheSame callback
    }

    @DiffContentSame
    fun contentSame(oldItem: Data, newItem: Data): Boolean {
        // DiffUtil.areContentsTheSame callback
    }

    @Bind
    fun bind(view: View, item: Data, position: Int) { }

    @OnClick(R.id.item_id)
    fun onClick(context: Context, item: Data, position: Int) { }

    @OnLongClick(R.id.item_id)
    fun onLongClick(context: Context, item: Data, position: Int) { }
}
```

- Compile the project and you'll have `BindTestAdapter` class generated (ready for use).
- You can then use the `BindTestAdapter` class with `recyclerView`.

### Notes

- If you pass `setInViewHolder = false` in `@OnClick` or `@OnLongClick`, then the clicks will be generated in `onBindViewHolder` instead. This will be used if your listeners are dynamic.

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
