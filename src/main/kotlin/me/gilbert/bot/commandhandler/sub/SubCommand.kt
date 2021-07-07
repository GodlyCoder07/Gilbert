package me.gilbert.bot.commandhandler.sub

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

abstract class SubCommand {
    val subCommandHandler: SubCommandHandler = javaClass.getDeclaredAnnotation(SubCommandHandler::class.java)

    abstract fun execute(event: GuildMessageReceivedEvent, args: List<String>)
}