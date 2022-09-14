package bond.jems.commands;

import bond.jems.gengarbot.DBHandler;
import bond.jems.gengarbot.GengarBot;
import com.github.oscar0812.pokeapi.utils.Client;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.text.Normalizer;
import java.util.Objects;

public class BotCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("start")) {
            //TODO: check if trainer already exists

            try {
                boolean success = DBHandler.newTrainer(event.getUser().getId());
                if (success) {
                    event.reply("New trainer registered!").queue();
                } else {
                    event.reply("You're already registered!").queue();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }




        } else if (event.getName().equals("spawnchannel")){

            if (event.getGuild() != null) {
                String guildID = event.getGuild().getId();
                GengarBot.setSpawnChannel(guildID, event.getTextChannel());
                event.reply("Redirected this server's pokemon spawns to this channel!").queue();

            } else {
                event.reply("An error occurred. Error code: NUTS").queue();
            }


        } else if (event.getName().equals("removespawnchannel")) {

            if (event.getGuild().getId() != null) {
                GengarBot.removeSpawnChannel(event.getGuild().getId());
                event.reply("Removed this server's spawn channel.").queue();
            } else {
                event.reply("This server doesn't have a spawn channel!").queue();
            }


        } else if (event.getName().equals("catch")) {

            OptionMapping option = event.getOption("name");
            if (option == null) {
                event.reply("An error occurred. Error code: BALLS").queue();
                return;
            }

            String nameGuess = option.getAsString().toLowerCase();
            String channelID = GengarBot.getPreferredChannel(event.getGuild().getId(),event.getTextChannel()).getId();

            if (GengarBot.getLatestEncounter(channelID) != null) {
                int pokemonID = GengarBot.getLatestEncounter(channelID).getDexNumber();
                boolean shiny = GengarBot.getLatestEncounter(channelID).isShiny();
                String actualName = Client.getPokemonById(pokemonID).getSpecies().getName();
                actualName = Normalizer.normalize(actualName, Normalizer.Form.NFD);
                actualName = actualName.replaceAll("[^\\p{ASCII}]", "");

                if (nameGuess.equals(actualName)) {
                    event.getUser().getId();
                    String actualNameCap = actualName.substring(0,1).toUpperCase() + actualName.substring(1);
                    event.reply("Congratulations! You caught " + actualNameCap + "!").queue();

                    String trainerID = DBHandler.trainerIDFromDiscordID(event.getUser().getId());

                    if (!Objects.equals(trainerID, "")) {
                        DBHandler.newPokemon(trainerID, pokemonID, 10, shiny);
                    } else {
                        event.reply("Something went wrong. Error code: CRIPES");
                    }



                    GengarBot.clearLatestEncounter(event.getChannel().getId());
                } else {
                    event.reply("That's not the right name!").queue();
                }
            } else {
                event.reply("I can't find a pokemon in this channel :(").queue();
            }
            // Congratulations! You caught a level ?? <name>! (Added to Pokedex.)

        }
    }
}