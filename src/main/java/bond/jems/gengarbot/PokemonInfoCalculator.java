package bond.jems.gengarbot;

import com.github.oscar0812.pokeapi.models.pokemon.Nature;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PokemonInfoCalculator {


    private static HashMap<String, HashMap<Integer, String>> languageStringToLanguageHash = new HashMap<>();

    private static HashMap<Integer, String> hpCharacteristics = new HashMap<>();
    private static HashMap<Integer, String> attackCharacteristics = new HashMap<>();
    private static HashMap<Integer, String> defenseCharacteristics = new HashMap<>();
    private static HashMap<Integer, String> spAtkCharacteristics = new HashMap<>();
    private static HashMap<Integer, String> spDefCharacteristics = new HashMap<>();
    private static HashMap<Integer, String> speedCharacteristics = new HashMap<>();

    public static int calculateHPStat(int base, int iv, int ev, int level) {
        int quarterEv = (int) Math.floor((float) ev / 4);
        double stat = ((2 * base) + iv + quarterEv) * level;
        stat = Math.floor(stat / 100) + level + 10;

        return (int) stat;
    }

    public static int calculateStat(int base, int iv, int ev, int level, boolean natureBoosted, boolean natureReduced) {
        int quarterEv = (int) Math.floor((float) ev / 4);
        double stat = ((2 * base) + iv + quarterEv) * level;
        if (natureBoosted && !natureReduced) {
            stat = Math.floor((Math.floor(stat / 100) + 5) * 1.1);
        } else if (natureReduced && !natureBoosted) {
            stat = Math.floor((Math.floor(stat / 100) + 5) * 0.9);
        } else {
            stat = Math.floor(stat / 100);
        }


        return (int) stat;
    }

    public static void buildPokemonNameLookup() {
        languageStringToLanguageHash.put("de", GengarBot.getPokemonNamesDe());
        languageStringToLanguageHash.put("en", GengarBot.getPokemonNamesEn());
        languageStringToLanguageHash.put("es", GengarBot.getPokemonNamesEs());
        languageStringToLanguageHash.put("fr", GengarBot.getPokemonNamesFr());
        languageStringToLanguageHash.put("ja", GengarBot.getPokemonNamesJa());
        languageStringToLanguageHash.put("ko", GengarBot.getPokemonNamesKo());
        languageStringToLanguageHash.put("ru", GengarBot.getPokemonNamesRu());
        languageStringToLanguageHash.put("th", GengarBot.getPokemonNamesTh());
        languageStringToLanguageHash.put("zh-hans", GengarBot.getPokemonNamesZhHans());
        languageStringToLanguageHash.put("zh-hant", GengarBot.getPokemonNamesZhHant());

        ArrayList<String> languageFiles = new ArrayList<>();
        languageFiles.add("src/main/resources/languages/de");
        languageFiles.add("src/main/resources/languages/en");
        languageFiles.add("src/main/resources/languages/es");
        languageFiles.add("src/main/resources/languages/fr");
        languageFiles.add("src/main/resources/languages/ja");
        languageFiles.add("src/main/resources/languages/ko");
        languageFiles.add("src/main/resources/languages/ru");
        languageFiles.add("src/main/resources/languages/th");
        languageFiles.add("src/main/resources/languages/zh-hans");
        languageFiles.add("src/main/resources/languages/zh-hant");

        try {
            for (String languageFilePath : languageFiles) {
                String shortLangString = languageFilePath.substring(29);
                File languageFile = new File(languageFilePath);
                Scanner scanner = new Scanner(languageFile);
                int dexNumber = 1;
                while (scanner.hasNextLine()) {
                    GengarBot.addPokemonNameToLookup(shortLangString, dexNumber, scanner.nextLine());
                    dexNumber++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Unable to build pokemon name lookup; FileNotFoundException.");
        }


    }

    public static void buildCharacteristicLookup() {
        hpCharacteristics.put(0, "Loves to eat");
        hpCharacteristics.put(1, "Takes plenty of siestas");
        hpCharacteristics.put(2, "Nods off a lot");
        hpCharacteristics.put(3, "Scatters things often");
        hpCharacteristics.put(4, "Likes to relax");

        attackCharacteristics.put(0, "Proud of its power");
        attackCharacteristics.put(1, "Likes to thrash about");
        attackCharacteristics.put(2, "A little quick tempered");
        attackCharacteristics.put(3, "Likes to fight");
        attackCharacteristics.put(4, "Quick tempered");

        defenseCharacteristics.put(0, "Sturdy body");
        defenseCharacteristics.put(1, "Capable of taking hits");
        defenseCharacteristics.put(2, "Highly persistent");
        defenseCharacteristics.put(3, "Good endurance");
        defenseCharacteristics.put(4, "Good perseverance");

        spAtkCharacteristics.put(0, "Highly curious");
        spAtkCharacteristics.put(1, "Mischievous");
        spAtkCharacteristics.put(2, "Thoroughly cunning");
        spAtkCharacteristics.put(3, "Often lost in thought");
        spAtkCharacteristics.put(4, "Very finicky");

        spDefCharacteristics.put(0, "Strong willed");
        spDefCharacteristics.put(1, "Somewhat vain");
        spDefCharacteristics.put(2, "Strongly defiant");
        spDefCharacteristics.put(3, "Hates to lose");
        spDefCharacteristics.put(4, "Somewhat stubborn");

        speedCharacteristics.put(0, "Likes to run");
        speedCharacteristics.put(1, "Alert to sounds");
        speedCharacteristics.put(2, "Impetuous and silly");
        speedCharacteristics.put(3, "Somewhat of a clown");
        speedCharacteristics.put(4, "Quick to flee");
    }

    /**
     *
     * No. I'm not happy with this either.
     * @param hpIV
     * @param attackIV
     * @param defenseIV
     * @param spAtkIV
     * @param spDefIV
     * @param speedIV
     * @return
     */
    public static String determineCharacteristic(int hpIV, int attackIV, int defenseIV,
                                                 int spAtkIV, int spDefIV, int speedIV) {
        if (hpIV >= attackIV && hpIV >= defenseIV && hpIV >= spAtkIV && hpIV >= spDefIV && hpIV >= speedIV) {
            return hpCharacteristics.get(hpIV % 5);

        } else if (attackIV >= hpIV && attackIV >= defenseIV && attackIV >= spAtkIV && attackIV >= spDefIV && attackIV >= speedIV) {
            return attackCharacteristics.get(attackIV % 5);

        } else if (defenseIV >= attackIV && defenseIV >= hpIV && defenseIV >= spAtkIV && defenseIV >= spDefIV && defenseIV >= speedIV) {
            return defenseCharacteristics.get(defenseIV % 5);

        } else if (spAtkIV >= attackIV && spAtkIV >= defenseIV && spAtkIV >= hpIV && spAtkIV >= spDefIV && spAtkIV >= speedIV) {
            return spAtkCharacteristics.get(spAtkIV % 5);

        } else if (spDefIV >= attackIV && spDefIV >= defenseIV && spDefIV >= spAtkIV && spDefIV >= hpIV && spDefIV >= speedIV) {
            return spDefCharacteristics.get(spDefIV % 5);

        } else if (speedIV >= attackIV && speedIV >= defenseIV && speedIV >= spAtkIV && speedIV >= spDefIV && speedIV >= hpIV) {
            return speedCharacteristics.get(speedIV % 5);
        } else {
            // we shouldn't ever get here?
            return "Loves finding bugs";
        }
    }

    public static HashMap<String, HashMap<Integer, String>> getLanguageStringToLanguageHash() {
        return languageStringToLanguageHash;
    }
}
