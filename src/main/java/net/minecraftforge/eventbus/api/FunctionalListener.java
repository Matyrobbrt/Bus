package net.minecraftforge.eventbus.api;

public record FunctionalListener<T extends IFunctionalEvent<?>>(T listener, EventPriority priority, boolean receiveCancelled) {
    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == FunctionalListener.class && listener == ((FunctionalListener) obj).listener;
    }

    @Override
    public int hashCode() {
        return listener.hashCode();
    }
}
