package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.base.CommandHandler
import me.gilbert.bot.getServerData
import me.gilbert.bot.utility.EmbedUtil
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@CommandHandler("announcement", "announce in your server", "announce", [], true)
class AnnouncementCommand : Command() {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        event.message.delete().queue()
        if (event.guild.getCategoriesByName(args[1], true).isNotEmpty()) {
            event.guild.getCategoriesByName(args[1], false)[0].createTextChannel(args[2])
                .queue {
                    getServerData(event.guild.id)?.getServerInformationRepository()
                        ?.getServerInformationModel()?.announcementChannel = it.id
                    getServerData(event.guild.id)?.getServerInformationRepository()?.save()
                }
        } else {
            event.guild.createCategory(args[1]).queue {
                it.createTextChannel(args[2])
                    .queue { channel ->
                        getServerData(event.guild.id)?.getServerInformationRepository()
                            ?.getServerInformationModel()?.announcementChannel = channel.id
                        getServerData(event.guild.id)?.getServerInformationRepository()?.save()
                    }
            }
        }

        event.message.reply(
            EmbedUtil.COMMAND_SUCCESSFUL(
                "Announcement",
                "https://image.flaticon.com/icons/png/512/630/630757.png",
                "Announcement Channel Assigned",
                "Announcement Channel has been created in `category: ${args[1]}, channel: ${args[2]}`",
                "Announcements",
                true
            )
        ).queue {
            Executors.newSingleThreadScheduledExecutor().schedule({
                it.delete().queue()
            }, 5, TimeUnit.SECONDS)
        }
    }
}