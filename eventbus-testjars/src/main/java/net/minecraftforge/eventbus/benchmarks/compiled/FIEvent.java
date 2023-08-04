package net.minecraftforge.eventbus.benchmarks.compiled;

import net.minecraftforge.eventbus.api.IFunctionalEvent;

public interface FIEvent extends IFunctionalEvent<Void> {
    void run();
}
