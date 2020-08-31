# AutoBindings

![build](https://github.com/KaustubhPatange/AutoBindings/workflows/build/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.kaustubhpatange/autobindings)

**AutoBindings** is a set of annotations which will make Android development easier by eliminating boilerplate codes.

> _Currently auto-generate RecyclerView adapters._

## Features

- [x] Supports standard `RecyclerView.Adapter` & `ListAdapter` generation.
- [x] Support for `OnClick` & `OnLongClick` methods.
- [x] Supports `DiffUtil.ItemCallback` generation method.
- [x] Support to load images through `Glide`.
- [x] Support for multiple view types, check [sample-ktx](sample-ktx/).
- [x] Custom Linter to check incorrect implementation & auto fix it.
- [ ] More?

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
  - [ListAdapter](#list-adapter) (recommended, check [sample-ktx](sample-ktx/))
  - [Notes](#notes)

### RecyclerView Adapter

> _The working and implementation might change!_

- A basic adapter consiting of a `dataSet` & are updated through `adapter.notify*` methods, usually used for normal purpose.

```kotlin
@RecyclerViewAdapter(Data::class)
class TestAdapter {

    @GlideLoadArray(
        GlideLoad(R.id.image_id, "parameter-name", ...)
    )
    @OnBind(R.layout.item_layout_first)
    fun bind1(view: View, item: Data, position: Int) {
        // Set your views for first layout.
    }

     @OnBind(R.layout.item_layout_second, viewType = 2)
    fun bind2(view: View, item: Data, position: Int) {
        // Set your views for second layout.
    }

    @OnClick(R.id.item_id, viewType = 2)
    fun onClick(context: Context, item: Data, position: Int) {
        // OnClick for only second layout.
    }

    @OnLongClick(R.id.item_id)
    fun onLongClick(context: Context, item: Data, position: Int) {
        // OnClick for only first layout.
    }

    @ItemViewType
    fun viewType(position: Int): Int = 0
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

### List Adapter

- A modern way of writing adapter supporting `diffutil` callbacks.

```kotlin
@RecyclerViewListAdapter(Data::class)
class TestAdapter {

    @DiffItemSame
    fun itemSame(oldItem: Data, newItem: Data): Boolean {
        // DiffUtil.areItemsTheSame callback
    }

    @DiffContentSame
    fun contentSame(oldItem: Data, newItem: Data): Boolean {
        // DiffUtil.areContentsTheSame callback
    }

    ...
}
```

- Compile the project and you'll have `BindTestAdapter` class generated (ready for use).
- You can then use the `BindTestAdapter` class with `recyclerView`.

### Notes

- If you pass `setInViewHolder = false` in `@OnClick` or `@OnLongClick`, then the clicks will be generated in `onBindViewHolder` instead. This will be used if your listeners are dynamic.
- You can set click listener for specific `viewType`, default is first `OnBind()`.

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
