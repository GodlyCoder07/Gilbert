package me.gilbert.bot.commands.subcommands.access

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.sub.SubCommand
import me.gilbert.bot.commandhandler.sub.SubCommandHandler
import me.gilbert.bot.database.command.CommandInformation
import me.gilbert.bot.getCommandRepository
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.util.*

@SubCommandHandler("clear", "adds all channel access to a command", "clear <command name>")
class Clear: SubCommand() {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
        val command: Command = getCommandRepository().getCommand(args[1]) ?: return
        val commandInformation: CommandInformation = getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformation(command) ?: return
        val channelId: MutableList<String> = commandInformation.channelId.toMutableList()
        channelId.clear()
        commandInformation.channelId = channelId.toTypedArray()
        getServerData(event.guild.id)?.getCommandInformationRepository()?.save()
        embedBuilder.setColor(Color.GREEN)
        embedBuilder.setTitle("✅ Successful")
        embedBuilder.addField(
            "Access Cleared",
            "Command `${commandInformation.name}` has now access to every channel",
            false
        )
        event.message.reply(embedBuilder.build()).queue()
    }

}