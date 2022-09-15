package bond.jems.gengarbot;

public class IDManager {

    static double lastPokemonID;

    public static double getLastPokemonID() {
        return lastPokemonID;
    }


    public static double getNewPokemonID() {
        lastPokemonID++;
        return lastPokemonID;
    }
}
