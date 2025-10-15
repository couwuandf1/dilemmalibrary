package space.commandf1.dilemmalibrary;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DilemmaLibrary {
    private static final Map<DilemmaLibraryHandler, DilemmaLibrary> INSTANCES = new ConcurrentHashMap<>();

    @Getter
    private final DilemmaLibraryHandler handler;

    private DilemmaLibrary(DilemmaLibraryHandler handler) {
        this.handler = handler;
    }

    public static DilemmaLibrary getInstance(DilemmaLibraryHandler handler) {
        return INSTANCES.computeIfAbsent(handler, DilemmaLibrary::new);
    }

}
