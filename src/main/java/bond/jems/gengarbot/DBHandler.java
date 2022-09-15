package bond.jems.gengarbot;

import com.github.oscar0812.pokeapi.models.pokemon.Pokemon;
import com.github.oscar0812.pokeapi.models.pokemon.PokemonAbility;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nonnull;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
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

    /*
    public static boolean getLatestPokemon(String discordID) {
        try {
            Connection connection = getDatabaseConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT MAX(timeCaught), nickname, shiny, mega, megaY, " +
                    "gMax, level, nature, originalTrainerID, holding, " +
                    "hpEV, hpIV, attackEV, attackIV, defenseEV, defenseIV, speedEV, speedIV, " +
                    "move1, move2, move3, move4 FROM Pokemon WHERE trainerDiscordID = 219861850089717770";

            statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
     */


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

            /*
            for (int i = 0; i < spawnChannelIDList.size(); i++) {
                if (spawnChannelIDList.get(i) == null) {
                    spawnChannelIDList.set(i, "None");
                }
            }
            */

            for (int i = 0; i < guildIDList.size(); i++) {
                if (spawnChannelIDList.get(i) != null) {
                    TextChannel channel = GengarBot.getJda().getTextChannelById(spawnChannelIDList.get(i));
                    GengarBot.setSpawnChannel(guildIDList.get(i), channel);
                }

            }
            connection.close();
            //System.out.println("");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

}
