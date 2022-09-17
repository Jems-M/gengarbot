package bond.jems.gengarbot;


import java.util.ArrayList;

public class Trainer {
    private double discordID;

    private double balance; //how much cash money u got

    private double currentBuddy; // pokemonID of the pokemon you're currently walking

    private boolean isEvTraining;

    private ArrayList<CaughtPokemon> pokemonCache;

    public double getDiscordID() {
        return discordID;
    }

    public Trainer(double discordID) {
        this.discordID = discordID;
    }

    public void addToPokemonCache(CaughtPokemon pokemon) {
        pokemonCache.add(pokemon);
    }

    public void clearPokemonCache() {
        pokemonCache.clear();
    }

}
