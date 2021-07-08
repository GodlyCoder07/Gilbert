package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.base.CommandHandler
import me.gilbert.bot.commandhandler.sub.SubCommand
import me.gilbert.bot.database.command.CommandInformation
import me.gilbert.bot.getCommandRepository
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@CommandHandler("access", "modifies on which channel the command can be used", "access <add | remove | clear | get> <command name>", [])
class AccessCommand(vararg subCommand: SubCommand) : Command(*subCommand) {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
        if (args.size == 1) {
            embedBuilder.setColor(Color.GREEN)
            embedBuilder.setTitle("Commands Access")
            getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandsList()?.sortedBy { it.name }?.forEach{ embedBuilder.addField(it.name, availableChannels(event.jda, it), false) }
            event.message.reply(embedBuilder.build()).queue()
            return
        } else if (args.size != 3) {
            embedBuilder.setColor(Color.RED)
            embedBuilder.setTitle("❌ Error")
            embedBuilder.addField("Invalid Usage", "Usage: ${getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix + commandHandler.usage}", false)
            event.message.reply(embedBuilder.build()).queue { msg ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    msg.delete().queue()
                    event.message.delete().queue()
                }, 3, TimeUnit.SECONDS)
            }
            return
        }

        val command: Command? = getCommandRepository().getCommand(args[2])
        if (command == null) {
            embedBuilder.setColor(Color.RED)
            embedBuilder.setTitle("❌ Error")
            embedBuilder.addField("Invalid Usage", "Usage: ${getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix + commandHandler.usage}", false)
            event.message.reply(embedBuilder.build()).queue { msg ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    msg.delete().queue()
                    event.message.delete().queue()
                }, 3, TimeUnit.SECONDS)
            }
            return
        }

        if (command.commandHandler.name == commandHandler.name) {
            embedBuilder.setColor(Color.RED)
            embedBuilder.setTitle("❌ Denied")
            embedBuilder.addField("Command Error", "This command has to be allowed to every channel!", false)
            event.message.reply(embedBuilder.build()).queue { msg ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    msg.delete().queue()
                    event.message.delete().queue()
                }, 3, TimeUnit.SECONDS)
            }
            return
        }

        val subCommand: SubCommand? = getSubCommand(args[1])
        if (subCommand != null) {
            subCommand.execute(event, args.subList(1, args.size))
        }else {
            embedBuilder.setColor(Color.RED)
            embedBuilder.setTitle("❌ Error")
            embedBuilder.addField("Invalid Usage", "Usage: ${getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix + commandHandler.usage}", false)
            event.message.reply(embedBuilder.build()).queue { msg ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    msg.delete().queue()
                    event.message.delete().queue()
                }, 3, TimeUnit.SECONDS)
            }
            return
        }

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