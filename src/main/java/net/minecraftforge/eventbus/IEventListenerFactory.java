package net.minecraftforge.eventbus;

import net.minecraftforge.eventbus.api.FunctionalInvoker;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.eventbus.api.IFunctionalEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public interface IEventListenerFactory {
    IEventListener create(Method callback, Object target)  throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException;

    <RESULT, T extends IFunctionalEvent<RESULT>> FunctionalInvoker.Builder<T> buildDynamic(Class<T> type, Predicate<RESULT> resultPredicate) throws Exception;

    default String getUniqueName(Method callback) {
        return String.format("%s.__%s_%s_%s",
            callback.getDeclaringClass().getPackageName(),
            callback.getDeclaringClass().getSimpleName(),
            callback.getName(),
            callback.getParameterTypes()[0].getSimpleName()
        );
    }

    default String getUniqueName(Class<? extends IFunctionalEvent<?>> factoryType) {
        return String.format("%s.__%sDynamicInvoker", factoryType.getPackageName(), factoryType.getSimpleName());
    }
}
