---
title: Logger
description: A guide on AutoJSON's logger.
---

AutoJSON has a [Logger](https://theo.is-a.dev/autojson/reference/autojson/dev.drtheo.autojson.logger/-logger/index.html) interface.

By default, `AutoJSON` implements a [`DelegateLogger`](https://theo.is-a.dev/autojson/reference/autojson/dev.drtheo.autojson.logger/-delegate-logger/index.html),
meaning it delegates all the logger calls to the logger field.

The `logger` field is protected, so to change it you would need to create your own
`AutoJSON` implementation:

```java
import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.logger.Logger;

class MyOtherLogger implements Logger {
    // implemented methods
}

class MyAutoJSON extends AutoJSON {

    @Override
    protected Logger setupLogger() {
        return new MyOtherLogger();
    }
}
```