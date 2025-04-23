package dev.drtheo.autojson.schema.base;

public interface SchemaType {
    byte PRIMITIVE = 0x00;
    byte ARRAY = 0x01;
    byte OBJECT = 0x02;
    byte WRAPPER = 0x03;
}
