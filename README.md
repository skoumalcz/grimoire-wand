<p align="center">
  <img src="art/logo.svg" width="128px" />
</p>
<p align="center">
    <a href="https://bintray.com/diareuse/grimoire/wand/"><img src="https://api.bintray.com/packages/diareuse/grimoire/wand/images/download.svg?version=latest" /></a>
</p>
<h1 align="center">Grimoire<sup>Wand</sup></h1>

Why Wand?

* **Consistency**

    Adds consistency to your code when working with your app's resources or common APIs.

* **Ease of Use**

    Many utilities in Wand serve as an extension of system APIs and improving upon them. No long
    setups or boilerplate needed.


### `ColorWand` + [`extensions`](wand/src/main/java/com/skoumal/grimoire/wand/ColorExtensions.kt)

`ColorWand` resolves colors only after calling `.getColor(context)`. In some cases it's only used
as a pass-through to resolve colors immediately. For instance when calling color extension methods
directly on context, it will use `ColorWand` however immediately resolves the value.

```kotlin
// requires app context, optionally works with themed context
context.colorStateList(R.color.primary_color) // returns entire state list
context.color(R.color.primary_color) // returns default color

// requires themed context
context.attribute(R.attr.colorPrimary) // returns default color for attribute
```

In cases in which you need to _save_ the value and _resolve_ later, you can use `as` methods.

```kotlin
var wand: ColorWand

wand = asColor(0x000000)
wand = asColorRes(R.color.primary_color)
wand = asColorStateList(R.color.primary_color)
wand = asColorAttr(R.attr.colorPrimary)

// … some time later

wand.getColor(context)
```

### `TextWand` + [`extensions`](wand/src/main/java/com/skoumal/grimoire/wand/TextExtensions.kt)

`TextWand` too works as a static holder up until the point you want to resolve the values

```kotlin
context.text(R.string.my_string, username)
context.quantityText(R.plurals.days, dayCount, dayCount)
```

Or _save_ and _resolve_ later

```kotlin
var wand: TextWand

wand = asText("My string")
wand = asText(R.string.my_string)
wand = asText(R.plurals.my_string, dayCount, dayCount)
```

### `InsetsWand`

Working with insets was never easy on Android, however if you do figure them out, apps look infinitely better and more modern. Why not giving it a try?

_In Activity_

```kotlin
override fun onCreate(/**/) {
    val wand = InsetsWand(this)
    lifecycleScope.launch {
        wand.collect { //it: InsetsWand
            // todo save / apply insets
        }
    }
}
```

_In Fragment_

```kotlin
override fun onViewCreated(/**/) {
    val wand = InsetsWand(this)
    lifecycleScope.launch {
        wand.collect { //it: InsetsWand
            // todo save / apply insets
        }
    }
}
```

_In View_

```kotlin
override fun onAttachedToWindow() {
    val wand = InsetsWand(this)
    viewScope.launch {
        wand.collect { //it: InsetsWand
            // todo save / apply insets
        }
    }
}
```

### WindowAppearance

Unfortunately Android framework doesn't provide clear and concise way to automatically adjust
status and navigation bar colors based on system version and theme colors. One simple extension
method comes to the rescue. _It works wonderfully with InsetsWand!_

_In Activity_

```kotlin
override fun onCreate(/**/) {
    applyWindowAppearance()
}
```

> This declaration alone will not do any wonders. You need to implement themes, which might be
somewhat difficult too, so here's a helpful guide.

_values/styles.xml_

```xml
<style name="Foundation" parent="Theme.MaterialComponents.DayNight.NoActionBar">
    <item name="android:windowTranslucentStatus">@bool/windowTranslucentStatus</item>
    <item name="android:windowTranslucentNavigation">@bool/windowTranslucentNavigation</item>
    <item name="android:navigationBarColor">@color/colorNavigationBar</item>
    <item name="android:statusBarColor">@color/colorStatusBar</item>
</style>

<style name="Foundation.Extras" />

<style name="AppTheme" parent="Foundation.Extras">
    <!-- This is really important as it sets the window background without which the WindowAppearance tool won't work -->
    <item name="colorSurface">@color/colorSurface</item>
    <!-- Declare other colors here -->
</style>
```

_values-v23/styles.xml_

```xml
<style name="Foundation.Extras">
    <item name="android:windowLightStatusBar">@bool/light_mode</item>
</style>
```

_values-v27/styles.xml_

```xml
<style name="Foundation.Extras">
    <item name="android:windowLightStatusBar">@bool/light_mode</item>
    <item name="android:windowLightNavigationBar">@bool/light_mode</item>
</style>
```

### AutoClean

Having to deal with properties such as RecyclerView's Adapter is a hassle, which may or may not
cause memory leaks, depending on how are you disposing the adapter. If you ever cleared the adapter
from the view there are essentially two ways.

1) Set the adapter on RecyclerView to `null`
    - this is not always preferred since your viewHolders can be still attached or bound to the
      local context for whatever reason

2) Set the adapter in your view (activity, fragment, …) to `null`
    - this helps the GC to understand that you don't want to use it anymore, bindings (views) get
      automatically disposed as well and so the reference can be cleared without any issues

Since the latter option is more helpful, we created `AutoClean`. The usage is pretty straightforward
and you can start using it now in _any_ `LifecycleOwner`. There are essentially two ways to use it.

#### Immutable, lazy

The lambda evaluates every time the internal value is empty. Which is essentially every time the
fragment initializes (_with some caveats_).

```kotlin
class MyFragment : Fragment(R.layout.fragment_my) {

    private val myAdapter by autoClean {
        MyAdapter()
    }

}
```

#### Mutable, with default value

If accessed _before_ it's been assigned value it either throws or returns default value. Again with
lifecycle's death, the reference is cleared.

```kotlin
class MyFragment : Fragment(R.layout.fragment_my) {

    private var myAdapter: MyAdapter by autoClean(/*optionally default*/)

    override fun onViewCreated(…) {
        myAdapter = MyAdapter()
    }

}
```

### Extensions

You might find a healthy amount of helpful extensions here:

* [Color](wand/src/main/java/com/skoumal/grimoire/wand/ColorExtensions.kt)
* [Context](wand/src/main/java/com/skoumal/grimoire/wand/ContextExtensions.kt)
* [Dimensions](wand/src/main/java/com/skoumal/grimoire/wand/DimenExtensions.kt)
* [Text](wand/src/main/java/com/skoumal/grimoire/wand/TextExtensions.kt)
* [View](wand/src/main/java/com/skoumal/grimoire/wand/ViewExtensions.kt)

## Recycler View

Often than not we all find ourselves using RecyclerViews with the pesky adapters and view holders.
We aim to solve this with some easy boilerplate reducers.

### `AsyncBindingAdapter`

It merges principles of
[AdapterListDiffer](wand-recyclerview/src/main/java/com/skoumal/grimoire/wand/recyclerview/diff/AdapterListDiffer.kt) and
[BindingViewHolder](wand-recyclerview/src/main/java/com/skoumal/grimoire/wand/recyclerview/viewholder/BindingViewHolder.kt).
Only thing you really need to do is provide layout resources through `getItemViewType`. Fetching
specific items can be done through
[`getItemAt(position)`](wand-recyclerview/src/main/java/com/skoumal/grimoire/wand/recyclerview/diff/AdapterListDiffer.kt).

```kotlin
class MyAdapter(
    differ: DiffUtil.ItemCallback<MyData>,
    extras: ExtrasBinder? = null
) : AsyncBindingAdapter<MyData>(differ, extras) {

    override fun getItemViewType(position: Int) {
        val item = getItemAt(position)
        return when(item) {
            null -> R.layout.placeholder
            else -> R.layout.item_my_data
        }
    }

}
```

> You can also bind extras (additional data, callbacks, etc…) to your items through
[ExtrasBinder](wand-recyclerview/src/main/java/com/skoumal/grimoire/wand/recyclerview/ExtrasBinder.kt)

## Wizard

Wizard is here to help you with the annoyance of declaring accounts and managing them. It comes with
a bonus of easy declaration of sync services and so forth. Here's how to set it up.

DISCLAIMER:
> This info doesn't need to be 100% up to date for the Android Framework. For the most part refer
> to official docs.

### `DefaultAuthenticatorService`

Extendable service that already contains all the necessary default stubs. For the most part you only
need to include tags to your app's manifest and then add xml resource.

```xml

<service android:name="com.skoumal.grimoire.wand.wizard.auth.DefaultAuthenticatorService">
    <intent-filter>
        <action android:name="android.accounts.AccountAuthenticator" />
    </intent-filter>
    <meta-data 
        android:name="android.accounts.AccountAuthenticator"
        android:resource="@xml/authenticator" />
</service>
```

And don't forget to create `@xml/authenticator`.

```xml

<account-authenticator xmlns:android="http://schemas.android.com/apk/res/android"
    android:accountType="@string/default_account_type" 
    android:icon="@mipmap/ic_launcher"
    android:smallIcon="@mipmap/ic_launcher" 
    android:label="@string/app_name" />
```

Now you're set up to use `Wizard`! Yey!

### `SyncService`

At this point you might already want to add a background sync service. A little bit more agency is
needed in order to create a sync adapter, but it's not something terribly hard.

```kotlin
class MySyncAdapter @JvmOverloads constructor(
    context: Context,
    autoInitialize: Boolean,
    allowParallelSyncs: Boolean = false,
) : AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs) {

    // todo add your implementation

}
```

```kotlin
class MySyncService : SyncService() {
    override fun onCreateSyncAdapter() = MySyncAdapter(this, true)
}
```

Moreover you need to register your newly created service in your manifest.

```xml

<service 
    android:name=".MySyncService" 
    android:exported="true" 
    android:process=":sync">
    <intent-filter>
        <action android:name="android.content.SyncAdapter" />
    </intent-filter>
    <meta-data 
        android:name="android.content.SyncAdapter" 
        android:resource="@xml/sync_adapter" />
</service>
```

Now that's done, you need to create a `@xml/sync_adapter` definition.

```xml

<sync-adapter xmlns:android="http://schemas.android.com/apk/res/android"
    android:accountType="@string/default_account_type" 
    android:allowParallelSyncs="false"
    android:contentAuthority="@string/default_content_authority" 
    android:supportsUploading="true"
    android:isAlwaysSyncable="true" 
    android:userVisible="true" />
```

You might want to create new content authority, if you already don't have one. You can also use a
simple stub created for you. Worry not though, you can build upon empty implementation.

```kotlin
class MyContentProvider : DefaultContentProvider()
```

And finally you can add you provider to the manifest.

```xml
<provider
    android:authorities="@string/default_content_authority"
    android:syncable="true"
    android:exported="false"
    android:label="@string/default_provider_label"
    android:name=".MyContentProvider" />
```

### WizardRegistry

To use this registry you need to either have authentication implemented before, or follow the 
tutorial above. Only then you can proceed.

First of all you need an instance of your registry. You need exactly one registry per account type.

```kotlin
val registry = WizardRegistry.Builder(context)
    .setAccountType(context.getString(R.string.default_account_type))
    .setAccountResolver { _,_ -> true } // optional
    .build()
```

The difference between this and native API is that you do not need to create account before 
fetching its info, or even worse iterating over existing accounts, which can be a mess at times.

Therefore all you need to do is to *create*, *fetch* or *remove* accounts.

```kotlin
val extras = mapOf(
    UserExtra.COLOR, Color.BLUE.toString()
)
val wizard = Wizard(
    name = "John Doe", // will be visible in system settings
    password = "backendtoken",
    extra = extras
)
registry.putWizard(wizard) // aaand done!
```

Just like that you're signed in to your app. Wonder how to check whether you're signed in?

```kotlin
if (!registry.exists()) {
    // show login
}
```

Sign out? Just as easy.

```kotlin
registry.removeWizard(null) // null signs out all existing uses with this account type
```


Logo by <a href="https://www.flaticon.com/authors/smalllikeart" title="smalllikeart">
smalllikeart</a>
