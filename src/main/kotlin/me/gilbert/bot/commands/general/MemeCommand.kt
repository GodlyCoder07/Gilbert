package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.base.CommandHandler
import me.gilbert.bot.commandhandler.sub.SubCommand
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.awt.Color
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@CommandHandler("meme", "view memes from online", "meme", [])
class MemeCommand(vararg subCommand: SubCommand) : Command(*subCommand) {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setColor(Color.CYAN)
        embedBuilder.setFooter("Memes").setTimestamp(Date().toInstant())
        if (args.size == 1) {
            val json = JSONObject(IOUtils.toString(URL("https://meme-api.herokuapp.com/gimme"), Charset.forName("UTF-8")))
            embedBuilder.setAuthor(json.getString("author"), null, "https://data.apksum.com/cb/com.jetfuel.colormeme/10.0/icon.png")
            embedBuilder.setTitle(json.getString("title"))
            embedBuilder.setImage(json.getString("url"))
            event.message.reply(embedBuilder.build()).queue()
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
}