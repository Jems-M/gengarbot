package bond.jems.gengarbot;

public class PokemonListEntry {

    private int uniqueID;
    private int level;
    private String displayedName;
    private boolean shiny;
    private String ivPercentage;

    public PokemonListEntry(int uniqueID, int level, String displayedName, boolean shiny, String ivPercentage) {
        this.uniqueID = uniqueID;
        this.level = level;
        this.displayedName = displayedName;
        this.shiny = shiny;
        this.ivPercentage = ivPercentage;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public boolean isShiny() {
        return shiny;
    }

    @Override
    public String toString() {
        String shinyEmoji;
        if (shiny) {
            shinyEmoji = "✨";
        } else {
            shinyEmoji = "";
        }
        return "**" + displayedName + "** | Level: " + level + " | ID: " + uniqueID + " | IV: " + ivPercentage + "% " + shinyEmoji + "\n";
    }
}
