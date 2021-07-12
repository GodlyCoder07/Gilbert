package me.gilbert.bot.commands.subcommands.access

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.sub.SubCommand
import me.gilbert.bot.commandhandler.sub.SubCommandHandler
import me.gilbert.bot.database.command.CommandInformation
import me.gilbert.bot.getCommandRepository
import me.gilbert.bot.getServerData
import me.gilbert.bot.utility.EmbedUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.lang.StringBuilder
import java.util.*

@SubCommandHandler("get", "gets the channel where the command can be used", "get <command name>")
class Get: SubCommand() {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        if (args.size == 1) {
            EmbedUtil.COMMAND_ERROR(
                "Invalid Usage",
                "${
                    getServerData(event.guild.id)?.getCommandInformationRepository()
                        ?.getCommandInformationModel()?.prefix
                } + prefix ${subCommandHandler.usage}",
                true
            )
            return
        }
        val command: Command = getCommandRepository().getCommand(args[1]) ?: return
        val commandInformation: CommandInformation =
            getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformation(command) ?: return
        val embedBuilder = EmbedBuilder()
        embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
        embedBuilder.setColor(Color.GREEN)
        embedBuilder.setTitle("Access")
        if (commandInformation.channelId.isEmpty()) {
            embedBuilder.addField(
                "Channels that has access to `${commandInformation.name}`:",
                "`${commandInformation.name}` has permission to every channel",
                false
            )
            event.message.reply(embedBuilder.build()).queue()
            return
        }
        embedBuilder.addField(
            "Channels that has access to `${commandInformation.name}`:",
            availableChannels(event.jda, commandInformation),
            false
        )
        event.message.reply(embedBuilder.build()).queue()
    }

    private fun availableChannels(jda: JDA, commandInformation: CommandInformation): String {
        val channels = StringBuilder()
        commandInformation.channelId.forEach { cmd ->
            channels.append(jda.getGuildChannelById(cmd)?.asMention).append("\n")
        }
        return channels.toString()
    }
}