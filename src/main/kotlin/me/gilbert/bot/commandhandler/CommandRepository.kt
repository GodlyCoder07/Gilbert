package me.gilbert.bot.commandhandler

import me.gilbert.bot.getServerData
import me.gilbert.bot.getServerDataList

class CommandRepository {
    private val commands: MutableSet<Command> = mutableSetOf()

    private fun getCommands(): MutableSet<Command> {
        return commands
    }

    fun getCommand(name: String): Command? {
        return commands.stream()
            .filter { cmd -> cmd.commandHandler.name.equals(name, false) }
            .findFirst()
            .orElse(null)
    }

    fun addCommand(vararg command: Command) {
        getCommands().addAll(command)
        getServerDataList().forEach { t ->
            getServerData(t.key)?.getCommandInformationRepository()?.addCommandInformation(*command)
            getServerData(t.key)?.getCommandInformationRepository()?.save()
        }
    }
}