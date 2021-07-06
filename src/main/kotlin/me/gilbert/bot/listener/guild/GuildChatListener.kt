package me.gilbert.bot.listener.guild

import me.gilbert.bot.database.player.UserData
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class GuildChatListener: ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return

        if (getServerData(event.guild.id)?.getPlayerDataRepository()?.get(event.author.id) == null) {
            val playerData = UserData()
            playerData.id = event.author.id
            getServerData(event.guild.id)?.getPlayerDataRepository()?.add(playerData)
        }

        val prefix: String = getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix ?: return

        if (!event.message.contentRaw.startsWith(prefix)) {
            var points: Double = getServerData(event.guild.id)?.getPlayerDataRepository()?.get(event.author.id)?.points!!
            points += event.message.contentRaw.length / 2500.0
            getServerData(event.guild.id)?.getPlayerDataRepository()?.get(event.author.id)?.points = points
            getServerData(event.guild.id)?.getPlayerDataRepository()?.save()
        }

    }
}