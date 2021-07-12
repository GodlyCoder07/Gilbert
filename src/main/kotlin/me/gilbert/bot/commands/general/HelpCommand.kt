package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.base.CommandHandler
import me.gilbert.bot.database.command.CommandInformation
import me.gilbert.bot.getServerData
import me.gilbert.bot.utility.EmbedUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.util.*

@CommandHandler("help", "shows list of commands", "help", [])
class HelpCommand : Command() {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setColor(Color.GREEN)
        embedBuilder.setAuthor("Help", null, "https://img.icons8.com/nolan/64/help--v1.png")
        embedBuilder.setTitle("Commands: ")
        val commandInformationList: MutableSet<CommandInformation>? =
            getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.commands
        for (index in getServerData(event.guild.id)?.getCommandInformationRepository()
            ?.getCommandInformationModel()?.commands?.indices!!) {
            val commandInformation: CommandInformation? = commandInformationList?.toMutableList()?.get(index)
            embedBuilder.addField(
                commandInformation?.name?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                "`Usage: ${
                    getServerData(event.guild.id)?.getCommandInformationRepository()
                        ?.getCommandInformationModel()?.prefix + commandInformation?.usage
                }`\n`Function: ${commandInformation?.description}`", false
            )
        }
        embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
        event.message.reply(
            EmbedUtil.COMMAND_SUCCESSFUL(
                "Commands",
                "https://www.kindpng.com/picc/m/82-821483_bangimportant-a-podcast-about-green-exclamation-mark-icon.png",
                "Commands Information",
                "${event.author.asMention} list of commands has been sent to you privately",
                "Commands",
                true
            )
        ).queue { event.author.openPrivateChannel().queue { pc -> pc.sendMessage(embedBuilder.build()).queue() } }
    }
}