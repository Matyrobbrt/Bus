package net.minecraftforge.eventbus.benchmarks;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.FunctionalInvoker;
import net.minecraftforge.eventbus.api.FunctionalListener;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IFunctionalEvent;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class BenchmarkFIVsObj {
    private static IEventBus bus;
    private static FunctionalInvoker<BenchmarkEvent> invoker;

    @Setup
    public void setup() {
        bus = BusBuilder.builder().useModLauncher().build();
        invoker = bus.createInvoker(BenchmarkEvent.class, listeners -> () -> {
            for (FunctionalListener<BenchmarkEvent> listener : listeners) {
                listener.listener().run();
            }
        });
        bus.addListener((final BenchmarkEventObj eventObj) -> {});
        bus.addListener((final BenchmarkEventObj eventObj) -> {});
        bus.addListener(EventPriority.NORMAL, BenchmarkEvent.class, () -> {});
        bus.addListener(EventPriority.NORMAL, BenchmarkEvent.class, () -> {});
        invoker.get();
    }

    @Benchmark
    public void handleObj() {
        bus.post(new BenchmarkEventObj());
    }

    @Benchmark()
    public void handleFI() {
        invoker.get().run();
    }

    @Benchmark
    public void handleFiWithLookup() {
        bus.grabInvoker(BenchmarkEvent.class).get().run();
    }

    public interface BenchmarkEvent extends IFunctionalEvent<Void> {
        void run();
    }
    public static final class BenchmarkEventObj extends Event {}
}
