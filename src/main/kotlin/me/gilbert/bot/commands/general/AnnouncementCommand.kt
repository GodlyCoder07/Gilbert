package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.base.CommandHandler
import me.gilbert.bot.commandhandler.sub.SubCommand
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@CommandHandler("announcement", "announce in your server", "announce", [])
class AnnouncementCommand(vararg subCommand: SubCommand) : Command(*subCommand) {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setAuthor(
            "Announcement",
            null,
            "https://image.flaticon.com/icons/png/512/630/630757.png"
        )
        embedBuilder.setFooter("Announcements").setTimestamp(Date().toInstant())
        if (args.size == 3) {
            event.message.delete().queue()
            embedBuilder.setColor(Color.GREEN)
            embedBuilder.addField(
                "Successful",
                "Announcement Channel has been created in `category: ${args[1]}, channel: ${args[2]}`",
                false
            )

            if (event.guild.getCategoriesByName(args[1], true).isNotEmpty()) {
                event.guild.getCategoriesByName(args[1], false)[0].createTextChannel(args[2])
                    .queue {
                        getServerData(event.guild.id)?.getServerInformationRepository()?.getServerInformationModel()?.announcementChannel = it.id
                        getServerData(event.guild.id)?.getServerInformationRepository()?.save()
                    }
            } else {
                event.guild.createCategory(args[1]).queue {
                    it.createTextChannel(args[2])
                        .queue { channel ->
                            getServerData(event.guild.id)?.getServerInformationRepository()?.getServerInformationModel()?.announcementChannel = channel.id
                            getServerData(event.guild.id)?.getServerInformationRepository()?.save()
                        }
                }
            }

            event.message.reply(embedBuilder.build()).queue {
                Executors.newSingleThreadScheduledExecutor().schedule({
                    it.delete().queue()
                }, 5, TimeUnit.SECONDS)
            }
        } else {
            // usage
        }
    }
}