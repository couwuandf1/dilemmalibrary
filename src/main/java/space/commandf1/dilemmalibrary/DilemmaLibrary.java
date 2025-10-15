package space.commandf1.dilemmalibrary;

public class DilemmaLibrary {
    private static DilemmaLibrary instance;

    public static DilemmaLibrary getInstance() {
        if (instance == null) {
            instance = new DilemmaLibrary()
        }

        return instance;
    }

}
