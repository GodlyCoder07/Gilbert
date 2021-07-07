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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SubCommandHandler("", "adds channel access to a command", "add <command name>")
class Add: SubCommand() {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
        val command: Command = getCommandRepository().getCommand(args[1]) ?: return
        val commandInformation: CommandInformation = getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformation(command) ?: return
        val channelId: MutableList<String> = commandInformation.channelId.toMutableList()
        if (!channelId.contains(event.channel.id)) {
            channelId.add(event.channel.id)
            commandInformation.channelId = channelId.toTypedArray()
            getServerData(event.guild.id)?.getCommandInformationRepository()?.save()
            embedBuilder.setColor(Color.GREEN)
            embedBuilder.setTitle("✅ Successful")
            embedBuilder.addField(
                "Access Granted",
                "Command `${commandInformation.name}` had successfully gained permission to ${event.channel.asMention} (${event.channel.id})",
                false
            )
            event.message.reply(embedBuilder.build()).queue()
        } else {
            embedBuilder.setColor(Color.RED)
            embedBuilder.setTitle("❌ Denied")
            embedBuilder.addField(
                "Command Error",
                "`${commandInformation.name}` already has permission in this channel",
                false
            )
            event.message.reply(embedBuilder.build()).queue { msg ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    msg.delete().queue()
                    event.message.delete().queue()
                }, 3, TimeUnit.SECONDS)
            }
        }
    }
}