package bond.jems.gengarbot;

import com.github.oscar0812.pokeapi.models.pokemon.Pokemon;
import com.github.oscar0812.pokeapi.models.pokemon.PokemonAbility;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.*;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DBHandler {

    public static Connection getDatabaseConnection() throws SQLException {
        String url = GengarBot.getDbUrl();
        String username = GengarBot.getDbUsername();
        String password = GengarBot.getDbPassword();
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Inserts a new trainer into the database.
     *
     * @param discordID the discord ID of the new user
     * @return true if successful, false if not
     * @throws SQLException if it can't connect to the database
     */
    public static boolean newTrainer(String discordID) throws SQLException {

        Connection connection = getDatabaseConnection();

        String query1 = "SELECT COUNT(*) FROM Trainer WHERE discordID = " + discordID;
        Statement statement1 = connection.createStatement();
        ResultSet count = statement1.executeQuery(query1);
        count.next();
        int existingTrainers = count.getInt("COUNT(*)");
        statement1.close();

        if (existingTrainers == 0) {
            String query2 = "INSERT INTO Trainer (discordID, balance, isEvTraining) VALUES ('" + discordID + "', 0, 0)";
            Statement statement2 = connection.createStatement();
            statement2.executeUpdate(query2);
            statement2.close();
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    /**
     * Inserts a new pokemon into the database.
     *
     * @param trainerDiscordID discord ID of the person who owns the pokemon
     * @param dexNumber        dex number of the pokemon
     * @param level            level of the pokemon
     * @param shiny            whether the pokemon is shiny
     * @return true if successful, false if not
     */
    public static boolean newPokemon(String trainerDiscordID, int dexNumber, int level, boolean shiny) throws SQLIntegrityConstraintViolationException {
        Pokemon newPokemon = Pokemon.getById(dexNumber);

        String originalTrainerID = trainerDiscordID; //useless right now, will be useful when trading is added

        int timeCaught = (int) Instant.now().getEpochSecond();

        int shinyInt;
        if (shiny) {
            shinyInt = 1;
        } else {
            shinyInt = 0;
        }

        // maybe just swap natures from enums to strings and get rid of all this shit
        // not like being an enum gives a significant advantage here anyway
        Nature enumNature = Nature.values()[new Random().nextInt(Nature.values().length)];
        String nature = enumNature.name();
        nature = nature.substring(0, 1).toUpperCase() + nature.substring(1).toLowerCase();

        int female = -1;
        int genderRate = newPokemon.getSpecies().getGenderRate();
        Random rand = new Random();
        if (genderRate != -1) {
            if (rand.nextInt(8) + 1 <= genderRate) {
                female = 1;
            } else {
                female = 0;
            }
        }

        PokemonAbility ability;
        ArrayList<PokemonAbility> possibleAbilities = newPokemon.getAbilities();

        possibleAbilities.removeIf(PokemonAbility::getIsHidden);

        ability = possibleAbilities.get(rand.nextInt(possibleAbilities.size()));

        String abilityName = ability.getAbility().getName();

        String terraType = "Normal";

        int happiness = newPokemon.getSpecies().getBaseHappiness();

        String characteristic;

        int hpIV = rand.nextInt(32);
        int attackIV = rand.nextInt(32);
        int defenseIV = rand.nextInt(32);
        int spAtkIV = rand.nextInt(32);
        int spDefIV = rand.nextInt(32);
        int speedIV = rand.nextInt(32);

        characteristic = PokemonInfoCalculator.determineCharacteristic(hpIV, attackIV, defenseIV, spAtkIV, spDefIV, speedIV);

        try {
            Connection connection = getDatabaseConnection();


            String query = "INSERT INTO Pokemon (trainerDiscordID, originalTrainerID, timeCaught, dexNumber, " +
                    "level, shiny, nature, sex, ability, terraType, happiness, characteristic, " +
                    "hpIV, attackIV, defenseIV, spAtkIV, spDefIV, speedIV) VALUES " +
                    "('" + trainerDiscordID + "', '" + originalTrainerID + "', '" + timeCaught + "', '" + dexNumber +
                    "', '" + level + "', '" + shinyInt + "', '" + nature + "', '" + female + "', '" + abilityName +
                    "', '" + terraType + "', '" + happiness + "', '" + characteristic + "', '" + hpIV + "', '" +
                    attackIV + "', '" + defenseIV + "', '" + spAtkIV + "', '" + spDefIV + "', '" + speedIV + "')";

            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            connection.close();


            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }


    public static ArrayList<PokemonListEntry> getPokemonList(String discordID, HashMap<Integer, String> languageHash) throws SQLException {
        ArrayList<PokemonListEntry> pokemonList = new ArrayList<>();

        Connection connection = getDatabaseConnection();
        Statement statement = connection.createStatement();
        String query = "SELECT uniqueID, level, dexNumber, nickname, shiny, " +
                "hpIV, attackIV, defenseIV, spAtkIV, spDefIV, speedIV " +
                "FROM Pokemon WHERE trainerDiscordID = " + discordID + " ORDER BY uniqueID DESC";

        ResultSet rs = statement.executeQuery(query);


        while (rs.next()) {
            double ivPercentage = (rs.getInt("hpIV") + rs.getInt("attackIV") + rs.getInt("defenseIV") +
                    rs.getInt("spAtkIV") + rs.getInt("spDefIV") + rs.getInt("speedIV")) / 186.0 * 100;
            DecimalFormat df = new DecimalFormat("###.##");
            String ivPercentageString = df.format(ivPercentage);
            String displayedName;
            if (rs.getString("nickname") == null) {
                displayedName = GengarBot.getPokemonNameByDexNumber(languageHash, rs.getInt("dexNumber"));
            } else {
                displayedName = rs.getString("nickname");
            }
            PokemonListEntry newEntry = new PokemonListEntry(rs.getInt("uniqueID"), rs.getInt("level"),
                    displayedName, rs.getBoolean("shiny"), ivPercentageString);

            pokemonList.add(newEntry);
        }

        return pokemonList;
    }


    // Will probably go unused. Now am just going to get a small cache whenever the user asks for it, and
    // get detailed info on specific pokemon when requested.
    public static boolean updatePokemonCache(String discordID) {
        try {
            Connection connection = getDatabaseConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Pokemon WHERE trainerDiscordID = " + discordID + " ORDER BY uniqueID DESC";

            ResultSet rs = statement.executeQuery(query);

            //int[] dexNumbers = resultSet.getArray("dexNumber");

            // while resultset has next object;
            // build a new CaughtPokemon object and add it to a trainer's pokemonCache.
            // remember to do updatePokemonCache on anything that changes info on any pokemon.

            CaughtPokemon newPokemon;

            while (rs.next()) {

                boolean shiny;
                int storedShinyValue = rs.getInt("shiny");

                int storedSexValue = rs.getInt("sex");
                Sex newPokemonSex;
                if (storedSexValue == 1) {
                    newPokemonSex = Sex.FEMALE;
                } else if (storedSexValue == 0) {
                    newPokemonSex = Sex.MALE;
                } else {
                    newPokemonSex = Sex.UNKNOWN;
                }

                newPokemon = new CaughtPokemon(rs.getInt("uniqueID"), rs.getString("name"),
                        rs.getString("trainerDiscordID"), rs.getString("originalTrainerID"),
                        rs.getInt("timeCaught"), rs.getInt("dexNumber"), rs.getString("specialForm"),
                        rs.getString("nickname"), rs.getInt("level"), rs.getInt("xp"),
                        rs.getBoolean("shiny"), rs.getString("nature"), newPokemonSex,
                        rs.getString("ability"), rs.getString("holding"),
                        rs.getBoolean("gMax"), rs.getString("terraType"),
                        rs.getInt("happiness"), rs.getString("characteristic"),
                        rs.getString("move1"), rs.getString("move2"), rs.getString("move3"),
                        rs.getString("move4"), rs.getInt("HPEV"), rs.getInt("AttackEV"),
                        rs.getInt("DefenseEV"), rs.getInt("SpAtkEV"), rs.getInt("SpDefEV"),
                        rs.getInt("SpeedEV"), rs.getInt("HPIV"),rs.getInt("AttackIV"),
                        rs.getInt("DefenseIV"),rs.getInt("SpAtkIV"),rs.getInt("SpDefIV"),
                        rs.getInt("SpeedIV"), rs.getBoolean("bottleCappedHP"), rs.getBoolean("bottleCappedAttack"),
                        rs.getBoolean("bottleCappedDefense"), rs.getBoolean("bottleCappedSpAtk"),
                        rs.getBoolean("bottleCappedSpDef"), rs.getBoolean("bottleCappedSpeed"), rs.getString("mintedNature"));
            }



            GengarBot.pokemonCacheUpdated(discordID, true);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }




    public static boolean setSpawnChannel(String guildID, String channelID) {
        try {
            Connection connection = getDatabaseConnection();
            Statement statement = connection.createStatement();
            String query = "UPDATE `Guild` SET `spawnChannelID` = '" + channelID + "' WHERE `Guild`.`guildID` = '" + guildID + "'";
            statement.executeUpdate(query);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean removeSpawnChannel(String guildID) {
        try {
            Connection connection = getDatabaseConnection();
            Statement statement = connection.createStatement();
            String query = "UPDATE `Guild` SET `spawnChannelID` = NULL WHERE `Guild`.`guildID` = '" + guildID + "'";
            statement.executeUpdate(query);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static boolean newGuild(String guildID) {
        try {
            Connection connection = getDatabaseConnection();
            Statement statement = connection.createStatement();
            String query = "INSERT INTO Guild (guildID) VALUES (" + guildID + ")";
            statement.executeUpdate(query);
            System.out.println("Joined new guild: " + guildID);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean buildSpawnChannelCache() {

        try {
            Connection connection = getDatabaseConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT guildID, spawnChannelID FROM `Guild`";
            ResultSet resultSet = statement.executeQuery(query);

            ArrayList<String> guildIDList = new ArrayList<>();
            ArrayList<String> spawnChannelIDList = new ArrayList<>();

            while (resultSet.next()) {
                guildIDList.add(resultSet.getString("guildID"));
                spawnChannelIDList.add(resultSet.getString("spawnChannelID"));
            }

            for (int i = 0; i < guildIDList.size(); i++) {
                if (spawnChannelIDList.get(i) != null) {
                    TextChannel channel = GengarBot.getJda().getTextChannelById(spawnChannelIDList.get(i));
                    GengarBot.setSpawnChannel(guildIDList.get(i), channel);
                }

            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    public static boolean setBuddy(double pokemonID, String discordID) {
        try {
            Connection connection = getDatabaseConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(1) FROM `Pokemon` WHERE `uniqueID` = '" + pokemonID + "' AND `trainerDiscordID` = '" + discordID + "'";
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();

            if (resultSet.getInt(1) == 1) {
                // pokemon exists, belongs to the current user
                statement = connection.createStatement();
                query = "UPDATE `Trainer` SET `currentBuddyID` = '" + pokemonID + "' WHERE `Trainer`.`discordID` = '" + discordID + "'";
                statement.executeUpdate(query);

            } else {
                //pokemon doesnt exist, or doesn't belong to current user
                return false;
            }

            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static CaughtPokemon getPokemonInfo(double pokemonID, String discordID) {
        try {
            Connection connection = getDatabaseConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM `Pokemon` WHERE `uniqueID` = '" + pokemonID + "' AND `trainerDiscordID` = '" + discordID + "'";
            ResultSet rs = statement.executeQuery(query);

            CaughtPokemon pokemon;
            if (rs.next()) {
                int uniqueID = rs.getInt("uniqueID");
                String name = GengarBot.getPokemonNameByDexNumber(GengarBot.getPokemonNamesEn(), rs.getInt("dexNumber"));
                String trainerDiscordID = rs.getString("trainerDiscordID");
                String originalTrainerID = rs.getString("originalTrainerID");
                int timeCaught = rs.getInt("timeCaught");
                int dexNumber = rs.getInt("dexNumber");
                String specialForm = rs.getString("specialForm");
                String nickname = rs.getString("nickname");
                int level = rs.getInt("level");
                int xp = rs.getInt("xp");
                boolean shiny = rs.getInt("shiny") == 1;
                String nature = rs.getString("nature");
                Sex sex;
                if (rs.getInt("sex") == 1) {
                    sex = Sex.FEMALE;
                } else {
                    sex = Sex.MALE;
                }
                String ability = rs.getString("ability");
                String holding = rs.getString("holding");
                boolean gMax = rs.getInt("gMax") == 1;
                String terraType = rs.getString("terraType");
                int happiness = rs.getInt("happiness");
                String characteristic = rs.getString("characteristic");
                String move1 = rs.getString("move1");
                String move2 = rs.getString("move2");
                String move3 = rs.getString("move3");
                String move4 = rs.getString("move4");
                int hpEV = rs.getInt("hpEV");
                int attackEV = rs.getInt("attackEV");
                int defenseEV = rs.getInt("defenseEV");
                int spAtkEV = rs.getInt("spAtkEV");
                int spDefEV = rs.getInt("spDefEV");
                int speedEV = rs.getInt("speedEV");
                int hpIV = rs.getInt("hpIV");
                int attackIV = rs.getInt("attackIV");
                int defenseIV = rs.getInt("defenseIV");
                int spAtkIV = rs.getInt("spAtkIV");
                int spDefIV = rs.getInt("spDefIV");
                int speedIV = rs.getInt("speedIV");
                boolean bottleCappedHP = rs.getInt("bottleCappedHP") == 1;
                boolean bottleCappedAttack = rs.getInt("bottleCappedAttack") == 1;
                boolean bottleCappedDefense = rs.getInt("bottleCappedDefense") == 1;
                boolean bottleCappedSpAtk = rs.getInt("bottleCappedSpAtk") == 1;
                boolean bottleCappedSpDef = rs.getInt("bottleCappedSpDef") == 1;
                boolean bottleCappedSpeed = rs.getInt("bottleCappedSpeed") == 1;
                String mintedNature = rs.getString("mintedNature");


                pokemon = new CaughtPokemon(uniqueID, name, trainerDiscordID, originalTrainerID, timeCaught, dexNumber,
                        specialForm, nickname, level, xp, shiny, nature, sex, ability, holding, gMax, terraType, happiness,
                        characteristic, move1, move2, move3, move4, hpEV, attackEV, defenseEV, spAtkEV, spDefEV, speedEV,
                        hpIV, attackIV, defenseIV, spAtkIV, spDefIV, speedIV, bottleCappedHP, bottleCappedAttack,
                        bottleCappedDefense, bottleCappedSpAtk, bottleCappedSpDef, bottleCappedSpeed, mintedNature);
                return pokemon;
            } else {
                // couldn't find the pokemon
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
