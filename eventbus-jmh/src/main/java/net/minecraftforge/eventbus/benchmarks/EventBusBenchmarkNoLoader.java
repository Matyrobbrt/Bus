package net.minecraftforge.eventbus.benchmarks;

import net.minecraftforge.eventbus.benchmarks.compiled.BenchmarkArmsLength;

import net.minecraftforge.eventbus.benchmarks.compiled.FIEvent;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class EventBusBenchmarkNoLoader {
    private Object invoker;

    @Setup
    public void setup() {
        invoker = BenchmarkArmsLength.NoLoader.fiSubscriberBus().grabInvoker(FIEvent.class);
    }

    // No Runtime Patching
    @Benchmark
    public int testNoLoaderDynamic() {
        BenchmarkArmsLength.postDynamic(BenchmarkArmsLength.NoLoader);
        return 0;
    }

    @Benchmark
    public int testNoLoaderLambda() {
        BenchmarkArmsLength.postLambda(BenchmarkArmsLength.NoLoader);
        return 0;
    }

    @Benchmark
    public int testNoLoaderStatic() {
        BenchmarkArmsLength.postStatic(BenchmarkArmsLength.NoLoader);
        return 0;
    }

    @Benchmark
    public int testNoLoaderCombined() {
        BenchmarkArmsLength.postCombined(BenchmarkArmsLength.NoLoader);
        return 0;
    }

    @Benchmark
    public int testNoLoaderFI() {
        BenchmarkArmsLength.postFI(BenchmarkArmsLength.NoLoader);
        return 0;
    }

    @Benchmark
    public int testNoLoaderFIWithInvoker() {
        BenchmarkArmsLength.postFIWithInvoker(invoker);
        return 0;
    }
}
