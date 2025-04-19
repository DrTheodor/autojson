---
title: Tips & Tricks
---

## Excluding a field dynamically
Usually, to exclude a field you'd use an [`@Exclude`](/autojson/guides/annotations/#exclude)
annotation, or, alternatively, write a [custom schema](/autojson/guides/schemas/),
but in some situations the custom schema would get too cluttered or complex and
you can't add the annotation directly.

In this case, you should override the default AutoJSON instance:

```java
import dev.drtheo.autojson.AutoJSON;

import java.lang.reflect.Field;

class MyAutoJSON extends AutoJSON {

    @Override
    public boolean shouldExclude(Field field, int layer) {
        if (field.getName().equals("token"))
            return true;
        
        return super.shouldExclude(field, layer);
    }
}
```

## Override instantiation parameters dynamically
Again, sometimes, you can't add the required annotation.
For example, the [`@Instantiate`](/autojson/guides/annotations/#instantiate)
annotation.

In this case you'd override the `safeInstancing` method:

```java
import dev.drtheo.autojson.AutoJSON;

class MyAutoJSON extends AutoJSON {

    @Override
    public boolean safeInstancing(Class<?> type) {
        if (type.getName().equals("java.util.ArrayList"))
            return true;
        
        return super.safeInstancing(type);
    }
}
```

## Increasing performance
If you use custom schemas, make sure you're caching the schemas of non-built-in
types you're serializing.

All (de)serialization contexts support passing the type and the schema. 