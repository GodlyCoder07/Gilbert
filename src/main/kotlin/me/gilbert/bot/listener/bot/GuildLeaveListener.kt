package me.gilbert.bot.listener.bot

import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class GuildLeaveListener: ListenerAdapter() {
    override fun onGuildLeave(event: GuildLeaveEvent) {
        getServerData(event.guild.id)?.delete() ?: return
    }
}