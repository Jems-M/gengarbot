package bond.jems.gengarbot;

import bond.jems.commands.BotCommands;
import bond.jems.listeners.BotListeners;
import com.github.oscar0812.pokeapi.models.pokemon.Pokemon;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


public class GengarBot {
    static Random rand = new Random();

    static JDA jda;

    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;

    static Integer[] rareDexNumbers = new Integer[]{137,147,148,149,233,246,247,248,349,350,371,372,373,374,375,376,
            442,443,444,445,447,448,474,479,485,570,571,610,611,612,633,634,635,636,637,
            704,705,706,714,715,782,783,784,785,885,886,887};
    static Integer[] ultraBeastDexNumbers = new Integer[]{793,794,795,796,797,798,799,803,804,805,806};
    static Integer[] legendaryDexNumbers = new Integer[]{144,145,146,150,243,244,245,249,250,377,378,379,380,381,382,383,384,
            480,481,482,483,484,486,487,488,638,639,640,641,642,643,644,645,646,716,717,718,773,785,786,787,788,789,790,791,792,
            800,888,889,890,891,892,894,895,896,897,898,905};
    static Integer[] mythicalDexNumbers = new Integer[]{151,251,385,386,489,490,491,492,493,494,647,648,649,719,720,721,801,802,807,808,809,893};

    static Integer[] allSpecialDexNumbers = new Integer[]{137,147,148,149,233,246,247,248,349,350,371,372,373,
            374,375,376,442,443,444,445,447,448,474,479,485,570,571,610,611,612,633,634,635,636,637,704,705,
            706,714,715,782,783,784,773,885,886,887,793,794,795,796,797,798,799,803,804,805,806,144,145,146,
            150,243,244,245,249,250,377,378,379,380,381,382,383,384,480,481,482,483,484,486,487,488,638,639,
            640,641,642,643,644,645,646,716,717,718,785,786,787,788,789,790,791,792,800,888,889,890,891,892,
            894,895,896,897,898,905,151,251,385,386,489,490,491,492,493,494,647,648,649,719,720,721,801,802,
            807,808,809,893};

    //unown = 61, maybe need to do something with its forms
    //rotom = 479

    private static HashMap<String, ChatEncounter> latestEncounterInChannel = new HashMap<>();

    private static HashMap<String, TextChannel> spawnChannel = new HashMap<>();

    private static HashMap<Integer, String> pokemonNamesDe = new HashMap<>();
    private static HashMap<Integer, String> pokemonNamesEn = new HashMap<>();
    private static HashMap<Integer, String> pokemonNamesEs = new HashMap<>();
    private static HashMap<Integer, String> pokemonNamesFr = new HashMap<>();
    private static HashMap<Integer, String> pokemonNamesJa = new HashMap<>();
    private static HashMap<Integer, String> pokemonNamesKo = new HashMap<>();
    private static HashMap<Integer, String> pokemonNamesRu = new HashMap<>();
    private static HashMap<Integer, String> pokemonNamesTh = new HashMap<>();
    private static HashMap<Integer, String> pokemonNamesZhHans = new HashMap<>();
    private static HashMap<Integer, String> pokemonNamesZhHant = new HashMap<>();

    private static ArrayList<HashMap<Integer, String>> pokemonNamesFromDexNumber = new ArrayList<>();


    private static HashMap<String, Boolean> pokemonCacheUpToDate = new HashMap<>();

    public static void main(String[] args) throws LoginException, InterruptedException {
        String token = args[0];
        dbUrl = args[1];
        dbUsername = args[2];
        dbPassword = args[3];
        jda = JDABuilder.createDefault(token)
                .setActivity(Activity.playing("with your (poke)balls"))
                .addEventListeners(new BotCommands(), new BotListeners())
                .build().awaitReady();

        jda.upsertCommand("start", "Starts your pokemon-catching career").queue();

        jda.upsertCommand("catch", "Catches the latest pokemon in the channel.")
                .addOption(OptionType.STRING, "name", "The name of the pokemon you're trying to catch.", true)
                .queue();

        jda.upsertCommand("spawnchannel", "Designates this channel as the one where pokemon will spawn.")
                .queue();

        jda.upsertCommand("removespawnchannel", "Lets pokemon spawn anywhere.")
                .queue();

        jda.upsertCommand("pokemon", "Show a list of all the pokemon you've caught.")
                .addOption(OptionType.STRING, "language", "Pick a language (just two letters - FR, DE, ES, JA, etc)")
                .queue();


        LevelHandler.buildXpLookupTable();
        PokemonInfoCalculator.buildCharacteristicLookup();
        DBHandler.buildSpawnChannelCache();
        PokemonInfoCalculator.buildPokemonNameLookup();
        pokemonNamesFromDexNumber.add(pokemonNamesDe);
        pokemonNamesFromDexNumber.add(pokemonNamesEn);
        pokemonNamesFromDexNumber.add(pokemonNamesEs);
        pokemonNamesFromDexNumber.add(pokemonNamesFr);
        pokemonNamesFromDexNumber.add(pokemonNamesJa);
        pokemonNamesFromDexNumber.add(pokemonNamesKo);
        pokemonNamesFromDexNumber.add(pokemonNamesRu);
        pokemonNamesFromDexNumber.add(pokemonNamesTh);
        pokemonNamesFromDexNumber.add(pokemonNamesZhHans);
        pokemonNamesFromDexNumber.add(pokemonNamesZhHant);

        System.out.println("Actually done loading, for real this time");
    }

    public static JDA getJda() {
        return jda;
    }

    public static void updateLatestEncounter(String channelID, ChatEncounter encounter) {
        latestEncounterInChannel.put(channelID, encounter);

    }

    public static ChatEncounter getLatestEncounter(String channelID) {
        return latestEncounterInChannel.get(channelID);
    }

    public static void clearLatestEncounter(String channelID) {
        latestEncounterInChannel.remove(channelID);
    }


    public static TextChannel getPreferredChannel(String guildID, TextChannel channel) {
        if (spawnChannel.get(guildID) != null) {
            return spawnChannel.get(guildID);
        } else {
            return channel;
        }
    }

    public static TextChannel getSpawnChannel(String guildID) {
        return spawnChannel.get(guildID);
    }

    public static void setSpawnChannel(String guildID, TextChannel channel) {
        spawnChannel.put(guildID, channel);
    }

    public static void removeSpawnChannel(String guildID) {
        spawnChannel.remove(guildID);
    }

    public static int generatePokemonByDexNumber() {
        boolean validEncounter = false;
        int rarity = rand.nextInt(10000); //higher rarity = possibility for rarer pokemon
        int newDexNumber;
        newDexNumber = rand.nextInt(905) + 1;

        while (Arrays.asList(allSpecialDexNumbers).contains(newDexNumber) && rarity < 9800) {
            newDexNumber = rand.nextInt(905) + 1;
        }

        /*
        while (!validEncounter) {
            newDexNumber = rand.nextInt(905) + 1;
            // check if legendary/mythical AND rarity = 0, if so, legendary encounter. if not, reroll.
            boolean rare = Arrays.asList(rareDexNumbers).contains(newDexNumber);
            boolean ultraBeast = Arrays.asList(ultraBeastDexNumbers).contains(newDexNumber);
            boolean legendary = Arrays.asList(legendaryDexNumbers).contains(newDexNumber);
            boolean mythical = Arrays.asList(mythicalDexNumbers).contains(newDexNumber);

            if (rarity > 6666 && rare) {
                validEncounter = true;
            }
            if (rarity > 7500 && ultraBeast) {
                validEncounter = true;
            }
            if (rarity > 9000 && legendary) {
                validEncounter = true;
            }
            if (rarity > 9500 && mythical) {
                validEncounter = true;
            }
        }
         */

        return newDexNumber;
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static String getDbUsername() {
        return dbUsername;
    }

    public static String getDbPassword() {
        return dbPassword;
    }

    public static void addPokemonNameToLookup(String language, int dexNumber, String name) {
        if (language.equals("de")) {
            pokemonNamesDe.put(dexNumber, name);
        } else if (language.equals("en")) {
            pokemonNamesEn.put(dexNumber, name);
        } else if (language.equals("es")) {
            pokemonNamesEs.put(dexNumber, name);
        } else if (language.equals("fr")) {
            pokemonNamesFr.put(dexNumber, name);
        } else if (language.equals("ja")) {
            pokemonNamesJa.put(dexNumber, name);
        } else if (language.equals("ko")) {
            pokemonNamesKo.put(dexNumber, name);
        } else if (language.equals("ru")) {
            pokemonNamesRu.put(dexNumber, name);
        } else if (language.equals("th")) {
            pokemonNamesTh.put(dexNumber, name);
        } else if (language.equals("zh-hans")) {
            pokemonNamesZhHans.put(dexNumber, name);
        } else if (language.equals("zh-hant")) {
            pokemonNamesZhHant.put(dexNumber, name);
        }
    }

    public static String getPokemonNameByDexNumber(HashMap<Integer, String> languageHash, int dexNumber) {
        return languageHash.get(dexNumber);
    }

    public static boolean pokemonNameMatch(int dexNumber, String name) {
        System.out.println(pokemonNamesEn.get(dexNumber));
        if (name.equalsIgnoreCase(pokemonNamesDe.get(dexNumber))) {
            return true;
        } else if (name.equalsIgnoreCase(pokemonNamesEn.get(dexNumber))) {
            return true;
        } else if (name.equalsIgnoreCase(pokemonNamesEs.get(dexNumber))) {
            return true;
        } else if (name.equalsIgnoreCase(pokemonNamesFr.get(dexNumber))) {
            return true;
        } else if (name.equalsIgnoreCase(pokemonNamesJa.get(dexNumber))) {
            return true;
        } else if (name.equalsIgnoreCase(pokemonNamesKo.get(dexNumber))) {
            return true;
        } else if (name.equalsIgnoreCase(pokemonNamesRu.get(dexNumber))) {
            return true;
        } else if (name.equalsIgnoreCase(pokemonNamesTh.get(dexNumber))) {
            return true;
        } else if (name.equalsIgnoreCase(pokemonNamesZhHans.get(dexNumber))) {
            return true;
        } else if (name.equalsIgnoreCase(pokemonNamesZhHant.get(dexNumber))) {
            return true;
        } else {
            return false;
        }
    }

    public static void pokemonCacheUpdated(String discordID, boolean upToDate) {
        pokemonCacheUpToDate.put(discordID, upToDate);
    }

    public static boolean isPokemonCacheUpToDate(String discordID) {
        return pokemonCacheUpToDate.get(discordID);
    }

    public static HashMap<Integer, String> getPokemonNamesDe() {
        return pokemonNamesDe;
    }

    public static HashMap<Integer, String> getPokemonNamesEn() {
        return pokemonNamesEn;
    }

    public static HashMap<Integer, String> getPokemonNamesEs() {
        return pokemonNamesEs;
    }

    public static HashMap<Integer, String> getPokemonNamesFr() {
        return pokemonNamesFr;
    }

    public static HashMap<Integer, String> getPokemonNamesJa() {
        return pokemonNamesJa;
    }

    public static HashMap<Integer, String> getPokemonNamesKo() {
        return pokemonNamesKo;
    }

    public static HashMap<Integer, String> getPokemonNamesRu() {
        return pokemonNamesRu;
    }

    public static HashMap<Integer, String> getPokemonNamesTh() {
        return pokemonNamesTh;
    }

    public static HashMap<Integer, String> getPokemonNamesZhHans() {
        return pokemonNamesZhHans;
    }

    public static HashMap<Integer, String> getPokemonNamesZhHant() {
        return pokemonNamesZhHant;
    }
}
