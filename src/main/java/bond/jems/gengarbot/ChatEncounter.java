package bond.jems.gengarbot;

public class ChatEncounter {
    boolean shiny;
    int dexNumber;

    public ChatEncounter(int dexNumber, boolean shiny) {
        this.dexNumber = dexNumber;
        this.shiny = shiny;
    }

    public boolean isShiny() {
        return shiny;
    }

    public int getDexNumber() {
        return dexNumber;
    }
}
