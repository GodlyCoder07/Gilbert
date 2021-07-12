package me.gilbert.bot.utility

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color
import java.util.*

class EmbedUtil {
    companion object {
        private val embedBuilder = EmbedBuilder()

        fun COMMAND_ERROR(error: String, content: String, timeStamp: Boolean): MessageEmbed {
            embedBuilder.clear()
            embedBuilder.setColor(Color.RED)
            embedBuilder.setAuthor("⚠️ Error", null, "https://image.flaticon.com/icons/png/512/1500/1500374.png")
            embedBuilder.addField(error, content,false)
            embedBuilder.setFooter("Commands")
            if (timeStamp) embedBuilder.setTimestamp(Date().toInstant())
            return embedBuilder.build()
        }

        fun COMMAND_SUCCESSFUL(author: String, iconUrl: String, title: String, content: String, footer: String, timeStamp: Boolean): MessageEmbed {
            embedBuilder.clear()
            embedBuilder.setColor(Color.GREEN)
            embedBuilder.setAuthor(author, null, iconUrl)
            embedBuilder.addField(title, content, false)
            embedBuilder.setFooter(footer)
            if (timeStamp) embedBuilder.setTimestamp(Date().toInstant())
            return embedBuilder.build()
        }
    }
}