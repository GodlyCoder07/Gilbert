package me.gilbert.bot.commands.subcommands.access

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.sub.SubCommand
import me.gilbert.bot.commandhandler.sub.SubCommandHandler
import me.gilbert.bot.database.command.CommandInformation
import me.gilbert.bot.getCommandRepository
import me.gilbert.bot.getServerData
import me.gilbert.bot.utility.EmbedUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SubCommandHandler("clear", "adds all channel access to a command", "clear <command name>")
class Clear: SubCommand() {
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
        val embedBuilder = EmbedBuilder()
        embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
        val command: Command? = getCommandRepository().getCommand(args[1])
        if (command == null) {
            event.message.reply(
                EmbedUtil.COMMAND_ERROR(
                    "Invalid Usage", "Usage: ${
                        getServerData(event.guild.id)?.getCommandInformationRepository()
                            ?.getCommandInformationModel()?.prefix + "access <add | remove | clear | get> <command name>"
                    }", true
                )
            ).queue { msg ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    msg.delete().queue()
                    event.message.delete().queue()
                }, 3, TimeUnit.SECONDS)
            }
            return
        }
        if (!command.commandHandler.isAccessible) {
            event.message.reply(EmbedUtil.COMMAND_ERROR("Command Not Accessible", "You cannot modify the access of ${command.commandHandler.name}", true))
        }
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