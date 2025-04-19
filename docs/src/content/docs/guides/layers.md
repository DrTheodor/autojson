---
title: Layers
description: An extensive guide on AutoJSON's layers.
---

Layers are used in AutoJSON to select what modifications should be applied to what
classes or fields.

For example, the [`@Exclude`](https://theo.is-a.dev/autojson/reference/autojson/dev.drtheo.autojson.annotation/-exclude/index.html)
annotation is using the layers to select which fields should be excluded.

In the built-in AutoJSON implementation the layers are bit flags.

```java
import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.annotation.Exclude;

class YourClass {

    @Exclude(layer = 2)
    final String someField = "123";
    
    @Exclude // by default the exclude layer is -1
    final String anotherField = "456";

    public void test() {
        AutoJSON auto = new AutoJSON();
        auto.addLayer(2);
        // or
        auto.setLayer(2);
        
        auto.shouldExclude(2); // true
        auto.shouldExclude(-1); // true
        auto.shouldExclude(1); // false
    }
}
```

Since the layer is a bit flag, you can also combine them!

```java
import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.annotation.Exclude;

class YourClass {

    public void test() {
        AutoJSON auto = new AutoJSON();
        auto.addLayer(1);
        auto.addLayer(4);
        // or
        auto.setLayer(1 | 4);
        // or
        auto.setLayer(5);
        
        auto.shouldExclude(2); // false
        auto.shouldExclude(-1); // true
        auto.shouldExclude(5); // true
        auto.shouldExclude(1); // true
    }
}
```

## Best practices

My recommendation would be to keep the layers in global access, for example:

```java
import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.annotation.Exclude;

class YourClass {
    public static final int NETWORK = 1; // network serialization
    public static final int FILE = 2; // file serialization

    public static AutoJSON auto = new AutoJSON();
}

class SomeData {
    @Exclude(layer = MyMainClass.NETWORK)
    int secret = 42;
}
```

In the example above, the field `secret` will be excluded 
for the `MyMainClass.NETWORK` layer!

## See Also:
- [@Exclude](https://theo.is-a.dev/autojson/reference/autojson/dev.drtheo.autojson.annotation/-exclude/index.html)
- [AutoJSON#layer](https://theo.is-a.dev/autojson/reference/autojson/dev.drtheo.autojson/-auto-j-s-o-n/index.html#-2056749675%2FProperties%2F-1170581573)
- [AutoJSON#getLayer](https://theo.is-a.dev/autojson/reference/autojson/dev.drtheo.autojson/-auto-j-s-o-n/index.html#-307170855%2FFunctions%2F-1170581573)
- [AutoJSON#addLayer](https://theo.is-a.dev/autojson/reference/autojson/dev.drtheo.autojson/-auto-j-s-o-n/index.html#-544612209%2FFunctions%2F-1170581573)
- [AutoJSON#setLayer](https://theo.is-a.dev/autojson/reference/autojson/dev.drtheo.autojson/-auto-j-s-o-n/index.html#1154537710%2FFunctions%2F-1170581573)