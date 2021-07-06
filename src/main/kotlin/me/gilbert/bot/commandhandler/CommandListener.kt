package me.gilbert.bot.commandhandler

import me.gilbert.bot.database.command.CommandInformation
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CommandListener: ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return

        val prefix: String = getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix ?: return

        if (event.message.contentRaw.startsWith(prefix)) {
            val args: List<String> = event.message.contentRaw.split(" ")
            val command: Command? = CommandRepository.getCommand(args[0].replace(prefix, ""))
            if (command == null) {
                val embedBuilder = EmbedBuilder()
                embedBuilder.setColor(Color.RED)
                embedBuilder.setAuthor("Commands")
                embedBuilder.setTitle("❌ Unknown Command")
                embedBuilder.addField("Command doesn't exists", "The command that you've entered doesn't exist", false)
                embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
                event.channel.sendMessage(embedBuilder.build()).queue { msg ->
                    Executors.newSingleThreadScheduledExecutor().schedule({
                        msg.delete().queue()
                        event.message.delete().queue()
                    }, 3, TimeUnit.SECONDS)
                }
                return
            }
            val commandInformation: CommandInformation? = getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformation(command)
            if (commandInformation?.channelId?.isNotEmpty() == true) {
                if (commandInformation.channelId.contains(event.channel.id)) {
                    command.execute(event, args)
                }else {
                    val embedBuilder = EmbedBuilder()
                    embedBuilder.setColor(Color.RED)
                    embedBuilder.setAuthor("Commands")
                    embedBuilder.setTitle("❌ Access Denied")
                    embedBuilder.addField("Command does not have access",
                        "You cannot use the command in this channel, to use this command go to: \n ${availableChannels(event.jda, commandInformation)}",
                        false)
                    embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
                    event.channel.sendMessage(embedBuilder.build()).queue { msg ->
                        Executors.newSingleThreadScheduledExecutor().schedule({
                            msg.delete().queue()
                            event.message.delete().queue()
                        }, 3, TimeUnit.SECONDS)
                    }
                }
            }else if (commandInformation?.channelId?.isEmpty() == true && commandInformation.channelId.isEmpty()) {
                command.execute(event, args)
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