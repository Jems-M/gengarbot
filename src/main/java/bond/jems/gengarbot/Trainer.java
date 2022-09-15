package bond.jems.gengarbot;

import java.util.ArrayList;

public class Trainer {
    private double discordID;

    private double balance; //how much cash money u got

    private double currentBuddy; // pokemonID of the pokemon you're currently walking

    private boolean isEvTraining;

    private ArrayList<CaughtPokemon> pokemonBox;

    public double getDiscordID() {
        return discordID;
    }

    public Trainer(double discordID) {
        //this.trainerID = IDManager.getNewTrainerID();
        this.discordID = discordID;
    }

    public CaughtPokemon getHighestLevelPokemon() {
        int highestLevel = 0; // lowest possible level is 1
        CaughtPokemon highestPokemon = null;
        for (int i = 0; i < pokemonBox.size(); i++) {
            if (pokemonBox.get(i).getLevel() > highestLevel) {
                highestPokemon = pokemonBox.get(i);
            }
        }
        return highestPokemon;
    }
}
