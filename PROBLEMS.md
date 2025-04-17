## 17.04.25

```java
class JsonSerializationContext

Array primitive$value(Object o);
```

- shouldn't return an array!

```java
class JsonSerializationContext

AutoJSON auto();
```

- shouldnt be used!
UPD: nvm, it should be used for settings.

also should allow for generic schemas

## 16.04.25
Ok so some thinking about it

If we take into account generics...

```java
import java.lang.reflect.Type;
import java.util.function.Supplier;

public interface JsonDeserializationContext extends JsonContext {
    <T> T decode(@NonNull Class<T> clazz, @Nullable Type type);
    <T> T decode(@NonNull Class<T> clazz, @Nullable Type type, Schema<T> schema);
}
```

If the schema is supplied, then it should be also taking `PrimitiveSchema`s into account.

---

Actually, yk what? `Type`s are also `Class<?>`s!

---

Nevermind. (?) or not? fuck

## 15.04.25
AAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHH
ok so everything works. quite fast compared to gson too
ish.
slower than it was before.
BUT the primitives serialization support is there! the arrays/collections too.
the api is absolute garbage in my opinion though.
i havent accounted for generic types, so as soon as ive added those it all caught up on fire
besides, i wasnt able to implement my ideal array deserialization:
the parser doesn't support 

besides, another problem ive found is that when the buffer of the parser overflows, 
it might end up cutting off a token's parsing, which is not nice. at all.

another thing thats not working is type hints, since, again, schemas kinda dont 
have any mechanism to support this unfortunately

---
## 14.04.25

AAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHH
everything works perfectly.
except for arrays! fuck that shit
i hate arrays
not only unsafe requires additional handling for arrays, 
but also the whole infrastructure just cant support them properly
im so mad


alr so the idea i have right now is to split the schema into 
`Schema`:
```java
<To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> auto, JsonSerializationContext c, T t);
```

`ObjectSchema`:
```java
<To> void deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, T t, String field);
```

`ArraySchema`:
```java
<To> void deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, T t, int index);
```

and then.......... i dont know
maybe `AutoJSON` will have a `arraySchema` and `objectSchema` methods

`BakedAutoSchema` -> `BakedAutoObjectSchema`