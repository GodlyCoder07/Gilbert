package me.gilbert.bot.listener.direct

import me.gilbert.bot.commands.AnnouncementCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PrivateChatListener: ListenerAdapter() {
    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if (event.author.isBot) return

        if (AnnouncementCommand.cache.contains(event.author.id)) {
            var channel = ""
            var role = ""
            var title = ""
            val content: String
            val embedBuilder = EmbedBuilder()
            embedBuilder.setAuthor("Announcement", null, "https://image.flaticon.com/icons/png/512/630/630757.png")
            AnnouncementCommand.cache[event.author.id]?.cancel(false)
            when (AnnouncementCommand.questionLevel[event.author.id]) {
                0 -> { channel = event.message.contentRaw }
                1 -> { role = event.message.contentRaw }
                2 -> { title = event.message.contentRaw }
                3 -> {
                    content = event.message.contentRaw
                    AnnouncementCommand.cache[event.author.id]?.cancel(false)
                    AnnouncementCommand.questionLevel.remove(event.author.id)
                    if (event.jda.getRoleById(role) == null || event.jda.getTextChannelById(channel) != null) {
                        embedBuilder.setColor(Color.GREEN)
                        embedBuilder.addField("❌ Announcement Failed", "Announcement has been failed to deliver because the role or the channel does not exist", true)
                        embedBuilder.setFooter("Announcement By: ${event.author.name}")
                        event.author.openPrivateChannel().queue { dm ->
                            dm.sendMessage(embedBuilder.build()).queue()
                        }
                        return
                    }

                    embedBuilder.setColor(Color.GREEN)
                    embedBuilder.addField("Confirmation", "Please react with ✅ to confirm and ❌ to cancel", true) // add them to a map with executor of 60 seconds
                    embedBuilder.addField(title, content, true)
                    embedBuilder.setFooter("Announcement By: ${event.author.name}")
                    event.author.openPrivateChannel().queue { dm ->
                        dm.sendMessage(embedBuilder.build()).queue { message ->
                            message.addReaction("✅").queue()
                            message.addReaction("❌").queue()
                        }
                    }
                    return
                }
            }

            AnnouncementCommand.questionLevel[event.author.id] = AnnouncementCommand.questionLevel[event.author.id]?.plus(1)!!
            AnnouncementCommand.cache[event.author.id] =
                Executors.newSingleThreadScheduledExecutor().schedule( {
                    AnnouncementCommand.cache.remove(event.author.id)
                    event.author.openPrivateChannel().queue { dm ->
                        dm.sendMessage("No respond for 60 seconds. Announcement request is now declined").queue()
                    }
                }, 60, TimeUnit.SECONDS)
            event.author.openPrivateChannel().queue { dm ->
                dm.sendMessage(AnnouncementCommand.contentInformation[AnnouncementCommand.questionLevel[event.author.id]!!]).queue()
            }
        }
    }
}