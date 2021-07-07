package me.gilbert.bot.commands

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.base.CommandHandler
import me.gilbert.bot.commandhandler.sub.SubCommand
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@CommandHandler("ping", "checks your ping", "ping", [])
class PingCommand(override val subCommandsList: MutableList<SubCommand>) : Command() {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        if (args.size == 1) {
            val user: User = event.author
            val latency: Int = OffsetDateTime.now().minusSeconds(event.message.timeCreated.second.toLong()).toInstant().nano / 1000000
            val embedBuilder = EmbedBuilder()
            when {
                latency in 1..60 -> {
                    embedBuilder.setColor(Color.GREEN)
                }
                latency in 61..120 -> {
                    embedBuilder.setColor(Color.YELLOW)
                }
                latency in 121..200 -> {
                    embedBuilder.setColor(Color.ORANGE)
                }
                latency > 201 -> {
                    embedBuilder.setColor(Color.RED)
                }
            }
            embedBuilder.setAuthor("Pong!", null, "https://image.flaticon.com/icons/png/512/250/250500.png")
            embedBuilder.setTitle(user.name + "'s Ping")
            embedBuilder.addField(
                "Calculating Latency...",
                "🏓 Latency is $latency ms",
                false
            )
            embedBuilder.setFooter("Ping").setTimestamp(Date().toInstant())
            event.message.reply(embedBuilder.build()).queue()
        }else {
            val embedBuilder = EmbedBuilder()
            embedBuilder.setColor(Color.RED)
            embedBuilder.setAuthor("Commands")
            embedBuilder.setTitle("❌ Error")
            embedBuilder.addField("Invalid Usage", "Usage: ${getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix + commandHandler.usage}", false)
            embedBuilder.setFooter("Commands").setTimestamp(Date().toInstant())
            event.message.reply(embedBuilder.build()).queue { msg ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    msg.delete().queue()
                    event.message.delete().queue()
                }, 3, TimeUnit.SECONDS)
            }
        }
    }
}