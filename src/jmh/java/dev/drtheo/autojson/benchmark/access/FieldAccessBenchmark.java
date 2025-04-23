package dev.drtheo.autojson.benchmark.access;

import org.openjdk.jmh.annotations.*;
import sun.misc.Unsafe;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 10, time = 1)
public class FieldAccessBenchmark {

    static class Target {
        private int value = 42;
        // Synthetic getter (needed for LambdaMetafactory)
        public int getValue() { return value; }
    }

    private static Target obj = new Target();
    private static Unsafe unsafe;
    private static long offset;
    private static Field field;
    private static VarHandle varHandle;
    private static IntSupplier lambdaGetter;

    static {
        try {
            // Unsafe setup
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
            offset = unsafe.objectFieldOffset(Target.class.getDeclaredField("value"));

            // Reflection setup
            field = Target.class.getDeclaredField("value");
            field.setAccessible(true);

            // VarHandle setup
            varHandle = MethodHandles
                    .privateLookupIn(Target.class, MethodHandles.lookup())
                    .unreflectVarHandle(field);

            // LambdaMetafactory setup (using getValue() method)
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle getter = lookup.findVirtual(Target.class, "getValue", MethodType.methodType(int.class));
            CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "getAsInt",
                    MethodType.methodType(IntSupplier.class, Target.class),
                    MethodType.methodType(int.class),
                    getter,
                    MethodType.methodType(int.class));
            lambdaGetter = (IntSupplier) site.getTarget().invokeExact(obj);
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    @Benchmark
    public int baseline_direct() {
        return obj.getValue(); // Baseline (non-dynamic)
    }

    @Benchmark
    public int unsafe() {
        return unsafe.getInt(obj, offset);
    }

    @Benchmark
    public int varHandle() {
        return (int) varHandle.get(obj);
    }

    @Benchmark
    public int reflection() throws Exception {
        return (int) field.get(obj);
    }

    @Benchmark
    public int lambdaMetafactory() {
        return lambdaGetter.getAsInt();
    }
}