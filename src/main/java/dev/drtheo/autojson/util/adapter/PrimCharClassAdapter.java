package dev.drtheo.autojson.util.adapter;

import dev.drtheo.autojson.util.ClassAdapter;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class PrimCharClassAdapter extends ClassAdapter.Wrapped<Character, char[], String> {

    public PrimCharClassAdapter() {
        super(Character.class, String.class, (char) 0);
    }

    @Override
    public Character unwrap(String s) {
        return s.charAt(0);
    }

    @Override
    public @NotNull Character get(Unsafe unsafe, Object obj, long address) {
        return unsafe.getChar(obj, address);
    }

    @Override
    public int getLength(char[] ts) {
        return ts.length;
    }

    @Override
    public Character get(char[] ts, int index) {
        return ts[index];
    }

    @Override
    protected void set0(Unsafe unsafe, Object obj, long address, @NotNull Character value) {
        unsafe.putChar(obj, address, value);
    }

    @Override
    protected void set0(char[] ts, int index, @NotNull Character value) {
        ts[index] = value;
    }
}
