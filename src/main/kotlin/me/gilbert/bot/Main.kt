package me.gilbert.bot

import me.gilbert.bot.commandhandler.CommandListener
import me.gilbert.bot.commandhandler.CommandRepository
import me.gilbert.bot.commands.general.*
import me.gilbert.bot.commands.points.PointsCommand
import me.gilbert.bot.database.ServerData
import me.gilbert.bot.listener.bot.GuildJoinListener
import me.gilbert.bot.listener.bot.GuildLeaveListener
import me.gilbert.bot.listener.direct.PrivateChatListener
import me.gilbert.bot.listener.guild.*
import me.gilbert.bot.utility.Utility
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import java.util.stream.Collectors

private lateinit var jda: JDA
private val serverDataList: MutableMap<String, ServerData> = mutableMapOf()
private lateinit var commandRepository: CommandRepository

fun getServerData(guildId: String): ServerData? {
    return serverDataList[guildId]
}

fun getServerDataList(): MutableMap<String, ServerData> {
    return serverDataList
}

fun addServerData(guildId: String) {
    if (!serverDataList.containsKey(guildId))
        serverDataList[guildId] = ServerData(guildId)
}

fun main() {
    jda = JDABuilder.createDefault(Utility.TOKEN).build()
    jda.addEventListener(
        CommandListener(),
        GuildChatListener(),
        PrivateChatListener(),
        GuildJoinListener(),
        GuildLeaveListener(),
        GuildMemberJoinListener(),
        GuildLeaveListener(),
        TextChannelRemoveListener(),
    )
    jda.awaitReady()
    jda.guildCache.applyStream { guild ->
        guild.map(Guild::getId).collect(Collectors.toList()) }?.forEach { id ->
        addServerData(id)
    }
    commandRepository = CommandRepository()
    commandRepository.addCommand(
        AccessCommand(),
        AnnouncementCommand(),
        HelpCommand(),
        PingCommand(),
        PrefixCommand(),
        PurgeCommand(),
        PointsCommand(),
    )
}