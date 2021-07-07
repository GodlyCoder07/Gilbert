package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.base.CommandHandler
import me.gilbert.bot.commandhandler.sub.SubCommand
import me.gilbert.bot.database.command.CommandInformation
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@CommandHandler("help", "shows list of commands", "help", [])
class HelpCommand(override val subCommandsList: MutableList<SubCommand>) : Command() {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setAuthor("Commands")
        embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
        if (args.size == 1) {
            embedBuilder.setColor(Color.GREEN)
            embedBuilder.setTitle("Commands Information")
            embedBuilder.setDescription("${event.author.asMention} list of commands has been sent to you privately")
            event.message.reply(embedBuilder.build()).queue{ event.author.openPrivateChannel().queue { pc -> pc.sendMessage(commands(event)).queue() } }
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
        }
    }

    fun commands(event: GuildMessageReceivedEvent): MessageEmbed {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setColor(Color.GREEN)
        embedBuilder.setAuthor("Help", null, "https://img.icons8.com/nolan/64/help--v1.png")
        embedBuilder.setTitle("Commands: ")
        val commandInformationList: MutableSet<CommandInformation>? = getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.commands
        for (index in getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.commands?.indices!!) {
            val commandInformation: CommandInformation? = commandInformationList?.toMutableList()?.get(index)
            embedBuilder.addField(
                commandInformation?.name?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                "`Usage: ${getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix + commandInformation?.usage}`\n`Function: ${commandInformation?.description}`"
                , false)
        }
        embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
        return embedBuilder.build()
    }
}