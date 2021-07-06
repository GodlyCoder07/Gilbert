package me.gilbert.bot.listener.bot

import me.gilbert.bot.addServerData
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class GuildJoinListener: ListenerAdapter() {
    override fun onGuildJoin(event: GuildJoinEvent) {
        addServerData(event.guild.id)
    }
}