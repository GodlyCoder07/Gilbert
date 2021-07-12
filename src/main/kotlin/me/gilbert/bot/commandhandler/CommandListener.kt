package me.gilbert.bot.commandhandler

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.database.command.CommandInformation
import me.gilbert.bot.getCommandRepository
import me.gilbert.bot.getServerData
import me.gilbert.bot.utility.EmbedUtil
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.lang.StringBuilder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CommandListener: ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return

        val prefix: String =
            getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix
                ?: return

        if (event.message.contentRaw.startsWith(prefix)) {
            val args: List<String> = event.message.contentRaw.split(" ")
            val command: Command? = getCommandRepository().getCommand(args[0].replace(prefix, ""))
            if (command == null) {
                event.channel.sendMessage(
                    EmbedUtil.COMMAND_ERROR(
                        "`${args[0]}` doesn't exists",
                        "The command that you've entered doesn't exist",
                        true
                    )
                ).queue { msg ->
                    Executors.newSingleThreadScheduledExecutor().schedule({
                        msg.delete().queue()
                        event.message.delete().queue()
                    }, 3, TimeUnit.SECONDS)
                }
                return
            }
            val commandInformation: CommandInformation? =
                getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformation(command)
            if (commandInformation?.channelId?.isNotEmpty() == true) {
                if (commandInformation.channelId.contains(event.channel.id)) {
                    execute(event, command, args)
                } else {
                    event.channel.sendMessage(
                        EmbedUtil.COMMAND_ERROR(
                            "`${command.commandHandler.name}` does not have access to `${event.channel}`",
                            "You cannot use the command in this channel, to use this command go to: \n ${
                                availableChannels(
                                    event.jda,
                                    commandInformation
                                )
                            }",
                            true
                        )
                    ).queue { msg ->
                        Executors.newSingleThreadScheduledExecutor().schedule({
                            msg.delete().queue()
                            event.message.delete().queue()
                        }, 10, TimeUnit.SECONDS)
                    }
                }
            } else if (commandInformation?.channelId?.isEmpty() == true) {
                execute(event, command, args)
            }
        }
    }

    private fun availableChannels(jda: JDA, commandInformation: CommandInformation): String {
        val channels = StringBuilder()
        commandInformation.channelId.forEach { cmd ->
            channels.append(jda.getGuildChannelById(cmd)?.asMention).append("\n")
        }
        return channels.toString()
    }

    private fun execute(event: GuildMessageReceivedEvent, command: Command, args: List<String>) {
        if (!command.commandHandler.executeWithArgument) {
            if (args.size < 2) {
                command.execute(event, args)
                return
            }
            if (command.subCommands.isEmpty()) {
                event.message.reply(
                    EmbedUtil.COMMAND_ERROR(
                        "Invalid Usage",
                        getServerData(event.guild.id)?.getCommandInformationRepository()
                            ?.getCommandInformationModel()?.prefix + command.commandHandler.usage,
                        true
                    )
                ).queue {
                    Executors.newSingleThreadScheduledExecutor()
                        .schedule({
                            it.delete().queue()
                            event.message.delete().queue()
                        }, 3, TimeUnit.SECONDS)
                }
                return
            }
            command.getSubCommand(args[1])?.execute(event, args.subList(1, args.size))
                ?: event.message.reply(
                    EmbedUtil.COMMAND_ERROR(
                        "Invalid Usage",
                        getServerData(event.guild.id)?.getCommandInformationRepository()
                            ?.getCommandInformationModel()?.prefix + command.commandHandler.usage,
                        true
                    )
                ).queue {
                    Executors.newSingleThreadScheduledExecutor()
                        .schedule({
                            it.delete().queue()
                            event.message.delete().queue()
                        }, 3, TimeUnit.SECONDS)
                }
            return
        }
        if (args.size > 1 && command.subCommands.isEmpty()) {
            event.message.reply(
                EmbedUtil.COMMAND_ERROR(
                    "Invalid Usage",
                    getServerData(event.guild.id)?.getCommandInformationRepository()
                        ?.getCommandInformationModel()?.prefix + command.commandHandler.usage,
                    true
                )
            ).queue {
                Executors.newSingleThreadScheduledExecutor()
                    .schedule({
                        it.delete().queue()
                        event.message.delete().queue()
                    }, 3, TimeUnit.SECONDS)
            }
        }
        command.getSubCommand(args[1])?.execute(event, args.subList(1, args.size)) ?: event.message.reply(
            EmbedUtil.COMMAND_ERROR(
                "Invalid Usage",
                getServerData(event.guild.id)?.getCommandInformationRepository()
                    ?.getCommandInformationModel()?.prefix + command.commandHandler.usage,
                true
            )
        ).queue {
            Executors.newSingleThreadScheduledExecutor()
                .schedule({
                    it.delete().queue()
                    event.message.delete().queue()
                }, 3, TimeUnit.SECONDS)
        }
    }
}