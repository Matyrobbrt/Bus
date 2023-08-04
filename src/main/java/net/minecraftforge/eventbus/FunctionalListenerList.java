package net.minecraftforge.eventbus;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.FunctionalInvoker;
import net.minecraftforge.eventbus.api.FunctionalListener;
import net.minecraftforge.eventbus.api.IFunctionalEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class FunctionalListenerList<T extends IFunctionalEvent<?>> {

    private final EventBus bus;
    private final Class<T> eventClass;
    private final List<FunctionalListener<T>> listeners = Collections.synchronizedList(new ArrayList<>());
    private final List<Runnable> updateNotifications = new CopyOnWriteArrayList<>();
    private final FunctionalInvoker<T> invoker = new FunctionalInvoker<>() {
        @Nullable
        T listener;
        boolean requestedRebuild = false;
        @Override
        public T get() {
            if (listener == null || requestedRebuild) {
                listener = invokerBuilder.build(buildSortedListeners());
                requestedRebuild = false;
            }
            return listener;
        }

        @Override
        public void requestRebuildNext() {
            requestedRebuild = true;
        }
    };
    private FunctionalInvoker.Builder<T> invokerBuilder = new DefaultBuilder<>();

    public FunctionalListenerList(EventBus bus, Class<T> eventClass) {
        this.bus = bus;
        this.eventClass = eventClass;
        updateNotifications.add(this::notifyUpdate);
    }

    public void addListener(EventPriority priority, T listener, boolean receiveCancelled) {
        this.listeners.add(new FunctionalListener<>(listener, priority, receiveCancelled));
        updateNotifications.forEach(Runnable::run);
    }

    public void unregister(T listener) {
        this.listeners.remove(new FunctionalListener<>(listener, EventPriority.LOW, false));
        updateNotifications.forEach(Runnable::run);
    }

    private void notifyUpdate() {
        synchronized (invoker) {
            invoker.requestRebuildNext();
        }
    }

    public FunctionalInvoker<T> grabInvoker(FunctionalInvoker.Builder<T> builder) {
        if (builder != this.invokerBuilder) {
            if (this.invokerBuilder.getClass() != DefaultBuilder.class) {
                throw new IllegalArgumentException("Attempted to change non-default invoker builder for event: " + eventClass);
            }
            this.invokerBuilder = builder;
            invoker.requestRebuildNext();
        }
        return invoker;
    }

    public FunctionalInvoker<T> orIfDefault(Supplier<FunctionalInvoker.Builder<T>> factory) {
        if (invokerBuilder.getClass() == DefaultBuilder.class) {
            return grabInvoker(factory.get());
        }
        return invoker;
    }

    public FunctionalInvoker<T> grabInvokerOrDefault() {
        if (invokerBuilder.getClass() == DefaultBuilder.class) {
            return grabInvoker(bus.buildDynamicInvokerB((Class)eventClass, Objects::nonNull));
        }
        return invoker;
    }

    private FunctionalListener<T>[] buildSortedListeners() {
        if (listeners.isEmpty()) {
            return new FunctionalListener[0];
        }
        return listeners.stream()
                .sorted(Comparator.comparing(w -> w.priority().ordinal()))
                .toArray(length -> (FunctionalListener<T>[]) Array.newInstance(FunctionalListener.class, length));
    }

    public final class DefaultBuilder<T extends IFunctionalEvent<?>> implements FunctionalInvoker.Builder<T> {

        @Override
        public T build(FunctionalListener<T>[] listeners) {
            return listeners[0].listener(); // TODO - fix
        }
    }
}
