package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.base.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.awt.Color
import java.net.URL
import java.nio.charset.Charset
import java.util.*

@CommandHandler("meme", "view memes from online", "meme", [])
class MemeCommand : Command() {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setColor(Color.CYAN)
        embedBuilder.setFooter("Memes").setTimestamp(Date().toInstant())
        val json = JSONObject(IOUtils.toString(URL("https://meme-api.herokuapp.com/gimme"), Charset.forName("UTF-8")))
        embedBuilder.setAuthor(
            json.getString("author"),
            null,
            "https://data.apksum.com/cb/com.jetfuel.colormeme/10.0/icon.png"
        )
        embedBuilder.setTitle(json.getString("title"))
        embedBuilder.setImage(json.getString("url"))
        event.message.reply(embedBuilder.build()).queue()
    }
}