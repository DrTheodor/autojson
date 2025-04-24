package dev.drtheo.autojson.adapter.string.parser2;

public interface JsonVisitor {
    void beginObject();
    void endObject();

    void key(String key);
    void primitive(Object value);

    void valueEnd(String key);
}
