package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.base.CommandHandler
import me.gilbert.bot.commands.subcommands.access.Add
import me.gilbert.bot.commands.subcommands.access.Clear
import me.gilbert.bot.commands.subcommands.access.Get
import me.gilbert.bot.commands.subcommands.access.Remove
import me.gilbert.bot.database.command.CommandInformation
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.lang.StringBuilder

@CommandHandler("access", "modifies on which channel the command can be used", "access <add | remove | clear | get> <command name>", [], false, false)
class AccessCommand : Command(Add(), Get(), Clear(), Remove()) {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setColor(Color.GREEN)
        embedBuilder.setTitle("Commands Access")
        getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandsList()?.sortedBy { it.name }
            ?.forEach { embedBuilder.addField(it.name, availableChannels(event.jda, it), false) }
        event.message.reply(embedBuilder.build()).queue()
    }

    private fun availableChannels(jda: JDA, commandInformation: CommandInformation): String {
        val channels = StringBuilder()
        if (commandInformation.channelId.isEmpty()) {
            return "`${commandInformation.name}` has access to every channel"
        }
        commandInformation.channelId.forEach {
            channels.append(jda.getGuildChannelById(it)?.asMention).append("\n")
        }
        return channels.toString()
    }
}