package net.minecraftforge.eventbus.test;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.FunctionalInvoker;
import net.minecraftforge.eventbus.api.FunctionalListener;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IFunctionalEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class FIBasedEvents {
    private static final IEventBus BUS = BusBuilder.builder()
            .build();

    interface NotifyThingyEvent extends IFunctionalEvent<Void> {
        FunctionalInvoker<NotifyThingyEvent> INVOKER = BUS.createInvoker(NotifyThingyEvent.class, listeners -> (param1) -> {
            for (FunctionalListener<NotifyThingyEvent> listener : listeners) {
                listener.listener().notify(param1);
            }
        });
        void notify(String param1);
    }

    void doThings() {
        BUS.addListener(EventPriority.NORMAL, NotifyThingyEvent.class, (param1) -> System.out.println(param1));
        NotifyThingyEvent.INVOKER.get().notify("Hello World");
    }

    @SubscribeEvent(BenchmarkEvent.class)
    public static void helloWorldListener() {
        System.out.println("Benchmark event called!");
    }

    @SubscribeEvent
    public static void helloWorldObj(BenchmarkEventObj obj) {
        System.out.println("obj event called");
    }

    @Test
    void compareTwo() {
        final IEventBus bus = BusBuilder.builder()
                .build();
        final var ivk = bus.createInvoker(BenchmarkEvent.class, listeners -> () -> {
            for (FunctionalListener<BenchmarkEvent> listener : listeners) {
                listener.listener().run();
            }
        });

        record Sub(String param) {
            @SubscribeEvent(BenchmarkEvent.class)
            public void helloWorld() {
                System.out.println("Benchmark event called. We has param: " + Sub.this.param);
            }
        }
//        bus.register(new Sub("tsk"));
//        bus.register(FIBasedEvents.class);
        bus.addListener(EventPriority.NORMAL, BenchmarkEvent.class, () -> {});
        bus.addListener((final BenchmarkEventObj obj) -> {});

        {
            final long startFi = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                ivk.get().run();
            }
            System.err.println("FI took: " + (System.currentTimeMillis() - startFi) + "ms");
        }

        {
            final long startFi = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                bus.post(new BenchmarkEventObj());
            }
            System.err.println("normal took: " + (System.currentTimeMillis() - startFi) + "ms");
        }
    }

    @Test
    void helloWorld() {
        final MyEvent normalPrio = value -> System.out.println("normal: " + value);
        BUS.addListener(EventPriority.LOW, MyEvent.class, value -> System.out.println("low: " + value));
        BUS.addListener(EventPriority.NORMAL, MyEvent.class, normalPrio);
        BUS.addListener(EventPriority.HIGH, MyEvent.class, value -> System.out.println("high: " + value));
        MyEvent.INVOKER.get().accept("Huh...");

        System.out.println("-------------");
        BUS.unregister(normalPrio);
        MyEvent.INVOKER.get().accept("Huh... second try");

        System.out.println("------------");
        BUS.grabInvoker(MyEvent.class).get().accept("3rd time's the charm");

        BUS.addListener(EventPriority.HIGH, WithResult.class, (time) -> {
            System.out.println("Hi! that was called at " + time);
            return null;
        });
        BUS.addListener(EventPriority.LOW, WithResult.class, time -> "kkk.");
        System.out.println(BUS.grabInvokerOrDynamic(WithResult.class, Objects::nonNull).get().compute(System.nanoTime()));

        BUS.addListener(EventPriority.LOW, OnInteract.class, targets -> {
            if (targets.length == 0) {
                return Result.YES;
            }
            return Result.CONTINUE;
        });
        BUS.addListener(EventPriority.HIGH, OnInteract.class, targets -> {
            if (targets.length == 0) {
                return Result.MAYBE;
            }
            return Result.DEFAULT;
        });
        final FunctionalInvoker<OnInteract> interactInvoker = BUS.grabInvokerOrDynamic(OnInteract.class, result -> result != Result.DEFAULT);
        System.out.println(interactInvoker.get().interact(new String[0]));
        System.out.println(interactInvoker.get().interact(new String[] { "Hmm.." }));
    }

    public interface MyEvent extends IFunctionalEvent<Void> {
        FunctionalInvoker<MyEvent> INVOKER = BUS.createInvoker(MyEvent.class, listeners -> (value) -> {
            for (final FunctionalListener<MyEvent> listener : listeners) {
                listener.listener().accept(value);
            }
        });

        void accept(String value);
    }

    public interface WithResult extends IFunctionalEvent<String> {
        @Nullable
        String compute(long time);
    }

    public interface OnInteract extends IFunctionalEvent<Result> {
        Result interact(String[] targets);
    }

    enum Result {
        YES,
        CONTINUE,
        MAYBE,
        DEFAULT
    }

    public interface BenchmarkEvent extends IFunctionalEvent<Void> {
        void run();
    }
    public static final class BenchmarkEventObj extends Event {}
}
