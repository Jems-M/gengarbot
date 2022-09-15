package bond.jems.listeners;

import bond.jems.gengarbot.ChatEncounter;
import bond.jems.gengarbot.DBHandler;
import bond.jems.gengarbot.GengarBot;
import com.github.oscar0812.pokeapi.models.pokemon.Pokemon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import com.github.oscar0812.pokeapi.utils.*;

import java.awt.*;
import java.util.Random;

public class BotListeners extends ListenerAdapter {
    Random rand = new Random();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            if (rand.nextInt(5) == 0) { // 50?
                /*
                TextChannel channelToSpawnIn;
                if (GengarBot.getSpawnChannel(event.getGuild().getId()) != null) {
                    channelToSpawnIn = GengarBot.getSpawnChannel(event.getGuild().getId());
                } else {
                    channelToSpawnIn = event.getTextChannel();
                }
                 */

                TextChannel channelToSpawnIn = GengarBot.getPreferredChannel(event.getGuild().getId(),event.getTextChannel());

                boolean shiny = false;
                if (rand.nextInt(4096) == 0) {
                    shiny = true;
                }
                int newDexNumber = GengarBot.generatePokemonByDexNumber();
                ChatEncounter encounter = new ChatEncounter(newDexNumber, shiny);

                GengarBot.updateLatestEncounter(GengarBot.getPreferredChannel(event.getGuild().getId(),event.getTextChannel()).getId(), encounter);

                String paddedDexNumber = String.format("%03d", newDexNumber);
                String imageLink = "https://raw.githubusercontent.com/HybridShivam/Pokemon/master/assets/images/" + paddedDexNumber + ".png";

                EmbedBuilder embedBuilder = new EmbedBuilder();
                if (shiny) {
                    embedBuilder.setTitle("A wild pokemon appeared! ✨");
                } else {
                    embedBuilder.setTitle("A wild pokemon appeared!");
                }
                embedBuilder.setDescription("Guess the name of the pokemon and do /catch <name> to catch it!");
                embedBuilder.setImage(imageLink);

                // TODO: make the colour dependent on the pokemon's primary type
                // TODO: make gender ratio use actual gender ratio
                embedBuilder.setColor(Color.MAGENTA.darker());



                channelToSpawnIn.sendMessageEmbeds(embedBuilder.build()).queue();
            }
        }

    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        DBHandler.newGuild(event.getGuild().getId());
    }
}
