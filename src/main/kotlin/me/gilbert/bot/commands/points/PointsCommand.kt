package me.gilbert.bot.commands.points

import me.gilbert.bot.commandhandler.Command
import me.gilbert.bot.commandhandler.CommandHandler
import me.gilbert.bot.database.player.UserData
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import kotlin.math.ceil

@CommandHandler("points", "your points in the server!", "points <user>", [])
class PointsCommand: Command() {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setColor(Color.YELLOW)
        embedBuilder.setAuthor("Points", null, "https://image.flaticon.com/icons/png/512/4291/4291373.png")
        if (event.message.mentionedMembers.isNotEmpty()) {
            val member: Member = event.message.mentionedMembers[0]
            val data: UserData = getServerData(event.guild.id)?.getPlayerDataRepository()?.get(member.id) ?: return
            embedBuilder.setColor(Color.GREEN)
            embedBuilder.setTitle("Points System")
            embedBuilder.addField("${member.effectiveName}'s Points",
                "${member.effectiveName} has ${ceil(data.points).toInt()} points!",
                false)
            event.channel.sendMessage(embedBuilder.build()).queue()
            return
        }
        val data: UserData = getServerData(event.guild.id)?.getPlayerDataRepository()?.get(event.author.id) ?: return
        embedBuilder.setColor(Color.GREEN)
        embedBuilder.setTitle("Points System")
        embedBuilder.addField("${event.author.name}'s Points",
            "${event.author.name} has ${ceil(data.points).toInt()} points!",
            false)
        event.channel.sendMessage(embedBuilder.build()).queue()
    }
}