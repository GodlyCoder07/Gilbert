package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.Command
import me.gilbert.bot.commandhandler.CommandHandler
import me.gilbert.bot.commandhandler.CommandRepository
import me.gilbert.bot.database.command.CommandInformation
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@CommandHandler("access", "modifies on which channel the command can be used", "access <add | remove> <command name>", [])
class AccessCommand: Command() {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
        if (args.size != 3) {
            embedBuilder.setColor(Color.RED)
            embedBuilder.setTitle("❌ Error")
            embedBuilder.addField("Invalid Usage", "Usage: ${getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix + commandHandler.usage}", false)
            event.channel.sendMessage(embedBuilder.build()).queue { msg ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    msg.delete().queue()
                    event.message.delete().queue()
                }, 3, TimeUnit.SECONDS)
            }
            return
        }
        val command: Command? = CommandRepository.getCommand(args[2])
        if (command == null) {
            embedBuilder.setColor(Color.RED)
            embedBuilder.setTitle("❌ Error")
            embedBuilder.addField("Invalid Usage", "Usage: ${getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix + commandHandler.usage}", false)
            event.channel.sendMessage(embedBuilder.build()).queue { msg ->
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
            event.channel.sendMessage(embedBuilder.build()).queue { msg ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    msg.delete().queue()
                    event.message.delete().queue()
                }, 3, TimeUnit.SECONDS)
            }
            return
        }
        val commandInformation: CommandInformation? = getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformation(command)
        if (commandInformation != null) {
            val channelId: MutableList<String> = commandInformation.channelId.toMutableList()
            when (args[1]) {
                "add" -> {
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
                        event.channel.sendMessage(embedBuilder.build()).queue()
                    } else {
                        embedBuilder.setColor(Color.RED)
                        embedBuilder.setTitle("❌ Denied")
                        embedBuilder.addField(
                            "Command Error",
                            "`${commandInformation.name}` already has permission in this channel",
                            false
                        )
                        event.channel.sendMessage(embedBuilder.build()).queue { msg ->
                            Executors.newSingleThreadScheduledExecutor().schedule({
                                msg.delete().queue()
                                event.message.delete().queue()
                            }, 3, TimeUnit.SECONDS)
                        }
                    }
                }
                "remove" -> {
                    if (channelId.contains(event.channel.id)) {
                        channelId.remove(event.channel.id)
                        commandInformation.channelId = channelId.toTypedArray()
                        getServerData(event.guild.id)?.getCommandInformationRepository()?.save()
                        embedBuilder.setColor(Color.GREEN)
                        embedBuilder.setTitle("✅ Successful")
                        embedBuilder.addField(
                            "Access Removed",
                            "Command `${commandInformation.name}` has permission now no permission to ${event.channel.name} (${event.channel.id})",
                            false
                        )
                        event.channel.sendMessage(embedBuilder.build()).queue()
                    } else {
                        embedBuilder.setColor(Color.RED)
                        embedBuilder.setTitle("❌ Denied")
                        embedBuilder.addField(
                            "Command Error",
                            "`${commandInformation.name}` doesn't have permission in this channel",
                            false
                        )
                        event.channel.sendMessage(embedBuilder.build()).queue { msg ->
                            Executors.newSingleThreadScheduledExecutor().schedule({
                                msg.delete().queue()
                                event.message.delete().queue()
                            }, 3, TimeUnit.SECONDS)
                        }
                    }
                }
                "clear" -> {
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
                    event.channel.sendMessage(embedBuilder.build()).queue()
                    // TOODO check if the list is empty or not... if empty, send message that it already is cleared else clear
                }
                "get" -> {
                    embedBuilder.setColor(Color.GREEN)
                    embedBuilder.setTitle("Access")
                    if (commandInformation.channelId.isEmpty()) {
                        embedBuilder.addField(
                            "Channels that has access to `${commandInformation.name}`:",
                            "`${commandInformation.name}` has permission to every channel",
                            false
                        )
                        event.channel.sendMessage(embedBuilder.build()).queue()
                        return
                    }
                    embedBuilder.addField(
                        "Channels that has access to `${commandInformation.name}`:",
                        availableChannels(event.jda, commandInformation),
                        false
                    )
                    event.channel.sendMessage(embedBuilder.build()).queue()
                }
                else -> {
                    embedBuilder.setColor(Color.RED)
                    embedBuilder.setTitle("❌ Error")
                    embedBuilder.addField("Invalid Usage", "Usage: ${getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix + commandHandler.usage}", false)
                    event.channel.sendMessage(embedBuilder.build()).queue { msg ->
                        Executors.newSingleThreadScheduledExecutor().schedule({
                            msg.delete().queue()
                            event.message.delete().queue()
                        }, 3, TimeUnit.SECONDS)
                    }
                }
            }
        }
    }

    private fun availableChannels(jda: JDA, commandInformation: CommandInformation): String {
        val channels = StringBuilder()
        commandInformation.channelId.forEach {
                cmd -> channels.append(jda.getGuildChannelById(cmd)?.asMention).append("\n")
        }
        return channels.toString()
    }
}