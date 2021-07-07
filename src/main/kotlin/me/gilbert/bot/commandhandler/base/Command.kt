package me.gilbert.bot.commandhandler.base

import me.gilbert.bot.commandhandler.sub.SubCommand
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

abstract class Command {
    abstract val subCommandsList: MutableList<SubCommand>

    val commandHandler: CommandHandler = javaClass.getDeclaredAnnotation(CommandHandler::class.java)

    abstract fun execute(event: GuildMessageReceivedEvent, args: List<String>)

    fun getSubCommand(name: String): SubCommand? {
        return subCommandsList.stream().filter { it.subCommandHandler.name == name }.findFirst().orElse(null)
    }
}