package space.commandf1.dilemmalibrary.processer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import space.commandf1.dilemmalibrary.provider.logger.LoggerProvider;

import java.io.InputStream;

public class YamlConfigProcesser<T> extends AbstractProcesser<InputStream, T> {
    private final Class<T> clazz;
    private final LoggerProvider<?> loggerProvider;

    public YamlConfigProcesser(@NotNull Class<T> clazz, @NotNull LoggerProvider<?> loggerProvider) {
        super("YAML Config Processor");
        this.clazz = clazz;
        this.loggerProvider = loggerProvider;
    }

    @Override
    public @Nullable T process(@NotNull InputStream input) throws Exception {
        this.loggerProvider.debug("Processing YAML config for class: " + this.clazz.getSimpleName());
        try {
            LoaderOptions options = new LoaderOptions();
            Yaml yaml = new Yaml(new CustomClassLoaderConstructor(this.clazz, this.clazz.getClassLoader(), options));
            T result = yaml.loadAs(input, this.clazz);
            this.loggerProvider.debug("YAML config processed successfully for class: " + this.clazz.getSimpleName());
            return result;
        } catch (Exception e) {
            this.loggerProvider.error("Failed to process YAML config for class: " + this.clazz.getSimpleName(), e);
            throw e;
        }
    }
}