---
title: Annotations
---

## [`@Exclude`](/autojson/reference/autojson/dev.drtheo.autojson.annotation/-exclude/index.html)
Used to exclude a certain field. Use the `layer` parameter to exclude based
on the layer of the `AutoJSON` instance.

Read [Layers](/autojson/guides/layers/) for more info.

## [`@Instantiate`](/autojson/reference/autojson/dev.drtheo.autojson.annotation/-instantiate/index.html)
Used to set the instantiation parameters for a certain field.
The `safe` parameter dictates whether to use safe instantiation or an unsafe one.

The safe instantiation is done by calling the class' constructor via reflections.
While the unsafe way is by using `sun.misc.Unsafe#allocateInstance`, which doesn't
run the default initialization, which means that every single field will get
its default value for the type. For numbers that would be `0`, for `boolean`s - false,
for `Object`s - null.

By default, `AutoJSON` prefers unsafe initialization over a safe one, because
of the performance benefits.

## [`@TypeHint`]()
As of `0.1.0-dev.1` its not supported. _Yet_.