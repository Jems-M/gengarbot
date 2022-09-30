package bond.jems.commands;

import bond.jems.gengarbot.DBHandler;
import bond.jems.gengarbot.GengarBot;
import bond.jems.gengarbot.PokemonInfoCalculator;
import bond.jems.gengarbot.PokemonListEntry;
import com.github.oscar0812.pokeapi.utils.Client;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
                DBHandler.setSpawnChannel(guildID, event.getTextChannel().getId());
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
            // SQLIntegrityConstraintViolationException if not registered - add a check for this
            // TODO: Find out what kind of error is produced when an unregistered user runs /catch, and put a try-catch for an error message
            // "You haven't registered yet! Do /start to begin!"

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
                //String actualName = Client.getPokemonById(pokemonID).getSpecies().getName();
                //actualName = Normalizer.normalize(actualName, Normalizer.Form.NFD);
                //actualName = actualName.replaceAll("[^\\p{ASCII}]", "");

                if (GengarBot.pokemonNameMatch(pokemonID, nameGuess)) {
                    event.getUser().getId();
                    String actualName = nameGuess.substring(0,1).toUpperCase() + nameGuess.substring(1);
                    try {
                        event.reply("Congratulations! You caught " + actualName + "!").queue();
                        String discordID = event.getUser().getId();
                        DBHandler.newPokemon(discordID, pokemonID, 10, shiny);
                    } catch (SQLIntegrityConstraintViolationException e) {
                        event.reply("You haven't registered yet! Do /start to start catching pokemon!").queue();
                    }

                    GengarBot.clearLatestEncounter(event.getChannel().getId());
                } else {
                    event.reply("That's not the right name!").queue();
                }
            } else {
                event.reply("I can't find a pokemon in this channel :(").queue();
            }
            // Congratulations! You caught a level ?? <name>! (Added to Pokedex.)
            // TODO: calculate levels properly on-catch, and display them in the catch message.

        } else if (event.getName().equals("info")) {
            /*
            Eventually, this will be "info latest", to show the latest one, or "info <number>" to get a specific one
            Or just "info" to get your buddy pokemon. For now, it just gets the latest.
             */

        } else if (event.getName().equals("pokemon")) {
            OptionMapping page = event.getOption("page");
            int pageNumber;
            if (page == null) {
                pageNumber = 0;
            } else {
                 pageNumber = Math.max(page.getAsInt() - 1, 0);
            }

            OptionMapping language = event.getOption("language");
            String languageString;
            if (language == null || PokemonInfoCalculator.getLanguageStringToLanguageHash().get(language.getAsString()) == null) {
                languageString = "en";
            } else {
                languageString = language.getAsString().toLowerCase();
                //String[] availableLanguages = {"de", "en", "es", "fr", "ja", "ko", "ru", "th", "zh-hans", "zh-hant"};
                //if (availableLanguages.)
            }
            HashMap<Integer, String> languageHash = PokemonInfoCalculator.getLanguageStringToLanguageHash().get(languageString);
            event.deferReply().queue();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.MAGENTA.darker());
            embedBuilder.setTitle("**Your pokemon;**");
            //embedBuilder.setFooter("Test footer, please ignore");
            StringBuilder allPokemon = new StringBuilder();
            ArrayList<PokemonListEntry> pokemonArrayList;
            String backtick = "`";
            try {
                pokemonArrayList = DBHandler.getPokemonList(event.getUser().getId(), languageHash);

                int start = pageNumber * 20;
                int finish = Math.min(start + 20, pokemonArrayList.size() - 1);

                //for (int i = start; i < finish; i++) {
                for (int i = start; i < finish; i++) {
                    allPokemon.append(pokemonArrayList.get(i).toString());
                    //allPokemon.append(backtick).append(i).append(backtick).append(pokemonArrayList.get(i).toString());
                }
            } catch (SQLException e) {
                allPokemon.append("I couldn't connect to the database :(");
            }

            embedBuilder.setDescription("*Page " + (pageNumber + 1) + "*\n" + allPokemon);
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

        }
    }
}
