package net.minecraftforge.eventbus.benchmarks.compiled;

import java.util.function.Consumer;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.FunctionalInvoker;
import net.minecraftforge.eventbus.api.FunctionalListener;
import net.minecraftforge.eventbus.api.IEventBus;

public class BenchmarkArmsLength
{
    public record Bus(
        IEventBus staticSubscriberBus,
        IEventBus dynamicSubscriberBus,
        IEventBus lambdaSubscriberBus,
        IEventBus combinedSubscriberBus,
        IEventBus fiSubscriberBus
    ) {
        public Bus register() {
            staticSubscriberBus.register(SubscriberStatic.class);
            combinedSubscriberBus.register(SubscriberStatic.class);
            dynamicSubscriberBus.register(new SubscriberDynamic());
            combinedSubscriberBus.register(new SubscriberDynamic());
            SubscriberLambda.register(lambdaSubscriberBus);
            fiSubscriberBus.addListener(EventPriority.NORMAL, FIEvent.class, () -> {});
            fiSubscriberBus.createInvoker(FIEvent.class, listeners -> () -> {
                for (FunctionalListener<FIEvent> listener : listeners) {
                    listener.listener().run();
                }
            });
            SubscriberLambda.register(combinedSubscriberBus);
            return this;
        }
    };

    public static Runnable supplier() {
        return () -> {
            try {
                CancelableEvent.class.getDeclaredField("LISTENER_LIST");
            } catch (Exception e) {
                throw new RuntimeException("Transformer did not apply!", e);
            }

            ModLauncher = new Bus(
                BusBuilder.builder().useModLauncher().build(),
                BusBuilder.builder().useModLauncher().build(),
                BusBuilder.builder().useModLauncher().build(),
                BusBuilder.builder().useModLauncher().build(),
                BusBuilder.builder().useModLauncher().build()
            ).register();
            ClassLoader = new Bus(
                BusBuilder.builder().build(),
                BusBuilder.builder().build(),
                BusBuilder.builder().build(),
                BusBuilder.builder().build(),
                BusBuilder.builder().build()
            ).register();
        };
    }

    public static Bus ModLauncher;
    public static Bus ClassLoader;
    public static Bus NoLoader = new Bus(
        BusBuilder.builder().build(),
        BusBuilder.builder().build(),
        BusBuilder.builder().build(),
        BusBuilder.builder().build(),
        BusBuilder.builder().build()
    ).register();

    public static final Consumer<Object> postStatic = BenchmarkArmsLength::postStatic;
    public static final Consumer<Object> postDynamic = BenchmarkArmsLength::postDynamic;
    public static final Consumer<Object> postLambda = BenchmarkArmsLength::postLambda;
    public static final Consumer<Object> postCombined = BenchmarkArmsLength::postCombined;
    public static final Consumer<Object> postFI = BenchmarkArmsLength::postFI;
    public static final Consumer<Object> postFIWithInvoker = BenchmarkArmsLength::postFIWithInvoker;

    public static void postStatic(Object bus)
    {
        postAll(((Bus)bus).staticSubscriberBus);
    }

    public static void postDynamic(Object bus)
    {
        postAll(((Bus)bus).dynamicSubscriberBus);
    }

    public static void postLambda(Object bus)
    {
        postAll(((Bus)bus).lambdaSubscriberBus);
    }

    public static void postCombined(Object bus)
    {
        postAll(((Bus)bus).combinedSubscriberBus);
    }

    public static void postFI(Object bus)
    {
        ((Bus) bus).fiSubscriberBus.grabInvoker(FIEvent.class).get().run();
    }

    public static void postFIWithInvoker(Object invoker)
    {
        ((FunctionalInvoker<FIEvent>) invoker).get().run();
    }

    private static void postAll(IEventBus bus)
    {
        bus.post(new CancelableEvent());
        bus.post(new ResultEvent());
        bus.post(new EventWithData("Foo", 5, true)); //Some example data
    }
}
