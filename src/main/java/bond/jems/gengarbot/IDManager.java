package bond.jems.gengarbot;

public class IDManager {

    static double lastPokemonID;
    static double lastTrainerID;

    public static double getLastPokemonID() {
        return lastPokemonID;
    }

    public static double getNewTrainerID() {
        lastTrainerID++;
        return lastTrainerID;
    }

    public static double getNewPokemonID() {
        lastPokemonID++;
        return lastPokemonID;
    }
}
