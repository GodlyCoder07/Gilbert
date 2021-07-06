package me.gilbert.bot.listener.guild

import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class GuildMemberLeaveListener: ListenerAdapter() {
    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        getServerData(event.guild.id)?.getPlayerDataRepository()?.remove(event.user.id)
    }
}