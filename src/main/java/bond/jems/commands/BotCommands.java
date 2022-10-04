package bond.jems.commands;

import bond.jems.gengarbot.*;
import com.github.oscar0812.pokeapi.models.pokemon.Pokemon;
import com.github.oscar0812.pokeapi.utils.Client;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.internal.utils.PermissionUtil;
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
            //boolean canManageChannels = PermissionUtil.checkPermission(event.getMember(), Permission.MANAGE_CHANNEL);
            //boolean canManageChannels = PermissionUtil.checkPermission((Member) event.getUser(), Permission.MANAGE_CHANNEL);
            boolean canManageChannels = true; //temporary

            if (event.getGuild() != null) {
                if (canManageChannels) {
                    String guildID = event.getGuild().getId();
                    GengarBot.setSpawnChannel(guildID, event.getTextChannel());
                    DBHandler.setSpawnChannel(guildID, event.getTextChannel().getId());
                    event.reply("Redirected this server's pokemon spawns to this channel!").queue();
                } else {
                    event.reply("You don't have permission to manage channels :(");
                }


            } else {
                event.reply("An error occurred. Error code: NUTS").queue();
            }


        } else if (event.getName().equals("removespawnchannel")) {
            //boolean canManageChannels = PermissionUtil.checkPermission(event.getMember(), Permission.MANAGE_CHANNEL);
            //boolean canManageChannels = PermissionUtil.checkPermission((Member) event.getUser(), Permission.MANAGE_CHANNEL);
            //TODO: find out how to check if a user can manage channels. same for /spawnchannel.
            boolean canManageChannels = true; //temporary

            if (canManageChannels) {
                String guildID = event.getGuild().getId();
                GengarBot.removeSpawnChannel(event.getGuild().getId());
                DBHandler.removeSpawnChannel(guildID);
                event.reply("Removed this server's spawn channel.").queue();
            } else {
                event.reply("You don't have permission to manage channels :(");
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

        } else if (event.getName().equals("setbuddy")) {
            OptionMapping pokemonID = event.getOption("id");
            double id = pokemonID.getAsDouble();
            String discordID = event.getUser().getId();

            boolean success = DBHandler.setBuddy(id, discordID);

            if (success) {
                event.reply("Set your buddy successfully!").queue();
            } else {
                event.reply("Couldn't set your buddy :(").queue();
            }
        } else if (event.getName().equals("info")) {
            OptionMapping pokemonID = event.getOption("id");
            double id = pokemonID.getAsDouble();
            String discordID = event.getUser().getId();

            CaughtPokemon pokemon = DBHandler.getPokemonInfo(id, discordID);

            if (pokemon == null) {
                event.reply("That pokemon isn't yours! (Or it doesn't exist)").queue();
                return;
            }

            event.deferReply().queue();

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.MAGENTA.darker());
            String shinyString;
            if (pokemon.isShiny()) {
                shinyString = "✨ Level ";
            } else {
                shinyString = "Level ";
            }

            if (pokemon.getNickname() == null) {
                embedBuilder.setTitle(shinyString + pokemon.getLevel() + " " + pokemon.getName());
            } else {
                embedBuilder.setTitle(shinyString + pokemon.getLevel() + " " + pokemon.getNickname());
            }

            Pokemon pokemonInfo = Pokemon.getById(pokemon.getDexNumber());
            String typeString;
            try {
                String type1 = pokemonInfo.getTypes().get(0).getType().getName();
                type1 = type1.substring(0, 1).toUpperCase() + type1.substring(1);

                String type2 = pokemonInfo.getTypes().get(1).getType().getName();
                type2 = type2.substring(0, 1).toUpperCase() + type2.substring(1);
                typeString = type1 + " | " + type2;

            } catch (IndexOutOfBoundsException e) {
                String type = pokemonInfo.getTypes().get(0).getType().getName();
                typeString = type.substring(0, 1).toUpperCase() + type.substring(1);
            }

            String natureString;
            if (pokemon.getMintedNature() != null) {
                natureString = pokemon.getMintedNature() + "(originally " + pokemon.getNature() + ")";
            } else {
                natureString = pokemon.getNature();
            }


            // TODO: make this take nature into account for stat calculation right here
            // TODO: show total IV % here
            String description = "**XP:** " + pokemon.getXp() + "\n" +
                    "**Types:** " + typeString + "\n" +
                    "**Nature:** " + natureString + "\n" +
                    "**HP:** " + PokemonInfoCalculator.calculateHPStat(pokemonInfo.getStats().get(0).getBaseStat(),
                    pokemon.getHpIV(), pokemon.getHpEV(), pokemon.getLevel()) + " - IV: " + pokemon.getHpIV() + "/31\n" +
                    "**Attack:** " + PokemonInfoCalculator.calculateStat(pokemonInfo.getStats().get(1).getBaseStat(),
                    pokemon.getAttackIV(), pokemon.getAttackEV(), pokemon.getLevel(), false, false) +
                    " - IV: " + pokemon.getAttackIV() + "/31\n" +
                    "**Defense:** " + PokemonInfoCalculator.calculateStat(pokemonInfo.getStats().get(1).getBaseStat(),
                    pokemon.getDefenseIV(), pokemon.getDefenseEV(), pokemon.getLevel(), false, false) +
                    " - IV: " + pokemon.getDefenseIV() + "/31\n" +
                    "**Sp.Atk:** " + PokemonInfoCalculator.calculateStat(pokemonInfo.getStats().get(1).getBaseStat(),
                    pokemon.getSpAtkIV(), pokemon.getSpAtkEV(), pokemon.getLevel(), false, false) +
                    " - IV: " + pokemon.getSpAtkIV() + "/31\n" +
                    "**Sp.Def:** " + PokemonInfoCalculator.calculateStat(pokemonInfo.getStats().get(1).getBaseStat(),
                    pokemon.getSpDefIV(), pokemon.getSpDefEV(), pokemon.getLevel(), false, false) +
                    " - IV: " + pokemon.getSpDefIV() + "/31\n" +
                    "**Speed:** " + PokemonInfoCalculator.calculateStat(pokemonInfo.getStats().get(1).getBaseStat(),
                    pokemon.getSpeedIV(), pokemon.getSpeedEV(), pokemon.getLevel(), false, false) +
                    " - IV: " + pokemon.getSpeedIV() + "/31\n";

                    String paddedDexNumber = String.format("%03d", pokemon.getDexNumber());
            String imageLink = "https://raw.githubusercontent.com/HybridShivam/Pokemon/master/assets/images/" + paddedDexNumber + ".png";
            embedBuilder.setImage(imageLink);
            embedBuilder.setDescription(description);
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

        }
    }
}
