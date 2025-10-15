package space.commandf1.dilemmalibrary.provider;

import org.jetbrains.annotations.NotNull;

public interface IProvider<T> {
    @NotNull T get();
}
