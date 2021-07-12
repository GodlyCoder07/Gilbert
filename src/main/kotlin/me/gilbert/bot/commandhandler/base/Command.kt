package me.gilbert.bot.commandhandler.base

import me.gilbert.bot.commandhandler.sub.SubCommand
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*

/**
 * @param subCommands the Sub Commands of the class
 */
abstract class Command(vararg var subCommands: SubCommand) {
    val commandHandler: CommandHandler = javaClass.getDeclaredAnnotation(CommandHandler::class.java)

    abstract fun execute(event: GuildMessageReceivedEvent, args: List<String>)

    fun getSubCommand(name: String): SubCommand? {
        return Arrays.stream(subCommands).filter { it.subCommandHandler.name == name }.findFirst().orElse(null)
    }
}