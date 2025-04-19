package dev.drtheo.autojson.util.adapter;

import dev.drtheo.autojson.util.ClassAdapter;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class ByteClassAdapter  extends ClassAdapter.Num<Byte, byte[]> {

    public ByteClassAdapter() {
        super(Byte.class, (byte) 0);
    }

    @Override
    public @NotNull Byte get(Unsafe unsafe, Object obj, long address) {
        return unsafe.getByte(obj, address);
    }

    @Override
    public int getLength(byte[] ts) {
        return ts.length;
    }

    @Override
    public Byte get(byte[] ts, int index) {
        return ts[index];
    }

    @Override
    protected void set0(Unsafe unsafe, Object obj, long address, @NotNull Byte value) {
        unsafe.putByte(obj, address, value);
    }

    @Override
    protected void set0(byte[] ts, int index, @NotNull Byte value) {
        ts[index] = value;
    }

    @Override
    protected Byte unwrap(Number number) {
        return number.byteValue();
    }
}
