package me.gilbert.bot.listener.guild

import me.gilbert.bot.database.player.UserData
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class GuildMemberJoinListener: ListenerAdapter() {
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val playerData = UserData()
        playerData.id = event.user.id
        getServerData(event.guild.id)?.getPlayerDataRepository()?.add(playerData)
    }
}