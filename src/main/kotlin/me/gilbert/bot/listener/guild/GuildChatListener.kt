package me.gilbert.bot.listener.guild

import me.gilbert.bot.database.player.UserData
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.util.*

class GuildChatListener: ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return

        if (getServerData(event.guild.id)?.getPlayerDataRepository()?.get(event.author.id) == null) {
            val playerData = UserData()
            playerData.id = event.author.id
            getServerData(event.guild.id)?.getPlayerDataRepository()?.add(playerData)
        }

        val prefix: String =
            getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix
                ?: return

        if (getServerData(event.guild.id)?.getServerInformationRepository()?.getServerInformationModel()?.announcementChannel?.isNotBlank() == true) {
            if (!event.message.contentRaw.startsWith(prefix) && event.channel.id != getServerData(event.guild.id)?.getServerInformationRepository()
                    ?.getServerInformationModel()?.announcementChannel
            ) {
                var points: Double =
                    getServerData(event.guild.id)?.getPlayerDataRepository()?.get(event.author.id)?.points!!
                points += event.message.contentRaw.length / 2500.0
                getServerData(event.guild.id)?.getPlayerDataRepository()?.get(event.author.id)?.points = points
                getServerData(event.guild.id)?.getPlayerDataRepository()?.save()
            }
        }

        if (getServerData(event.guild.id)?.getServerInformationRepository()?.getServerInformationModel()?.announcementChannel?.isNotBlank() == true) {
            if (event.channel.id == getServerData(event.guild.id)?.getServerInformationRepository()?.getServerInformationModel()?.announcementChannel) {
                event.message.delete().queue()
                val content: MutableList<String> = event.message.contentRaw.split("\n").toMutableList()
                val embedBuilder = EmbedBuilder()
                embedBuilder.setAuthor(event.author.name, null, event.author.avatarUrl)
                embedBuilder.setFooter("By: ${event.author.name}", "https://image.flaticon.com/icons/png/512/630/630757.png").setTimestamp(Date().toInstant())
                if (content.size > 1) {
                    val title = content[0]
                    content.removeAt(0)
                    val body: String = content.joinToString("\n")
                    embedBuilder.setColor(Color.YELLOW)
                    embedBuilder.setTitle(title)
                    embedBuilder.setDescription("\n$body")
                    event.channel.sendMessage(MessageBuilder().setEmbed(embedBuilder.build()).setContent("@everyone").build()).queue()
                } else {

                }
            }
        }
    }
}