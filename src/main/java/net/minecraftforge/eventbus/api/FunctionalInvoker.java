package net.minecraftforge.eventbus.api;

public interface FunctionalInvoker<E extends IFunctionalEvent<?>> {
    E get();

    void requestRebuildNext();

    interface Builder<E extends IFunctionalEvent<?>> {
        E build(FunctionalListener<E>[] listeners);
    }
}
