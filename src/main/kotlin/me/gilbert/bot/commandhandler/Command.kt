package me.gilbert.bot.commandhandler

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

abstract class Command {
    val commandHandler: CommandHandler = javaClass.getDeclaredAnnotation(CommandHandler::class.java)

    abstract fun execute(event: GuildMessageReceivedEvent, args: List<String>)
}