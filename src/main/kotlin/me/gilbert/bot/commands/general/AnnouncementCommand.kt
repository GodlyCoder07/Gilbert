package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.base.CommandHandler
import me.gilbert.bot.commandhandler.sub.SubCommand
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@CommandHandler("announce", "announce in your server", "announce", [])
class AnnouncementCommand(vararg subCommand: SubCommand) : Command(*subCommand) {
    companion object {
        val cache: MutableMap<String, ScheduledFuture<*>> = mutableMapOf()
        val questionLevel: MutableMap<String, Int> = mutableMapOf()
        val contentInformation: MutableList<String> = mutableListOf(
            "Which channel do you want this to announce? (Channel ID)",
            "What role to ping (Role ID)",
            "Title of the announcement",
            "Content of the announcement"
        )
    }
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        event.message.delete().queue()
        if (!cache.contains(event.author.id)) {
            questionLevel[event.author.id] = 0
            cache[event.author.id] =
                Executors.newSingleThreadScheduledExecutor().schedule({
                    cache.remove(event.author.id)
                    event.author.openPrivateChannel().queue { dm -> dm.sendMessage("No respond for 60 seconds. Announcement request is now declined").queue() }
                }, 60, TimeUnit.SECONDS)
            event.author.openPrivateChannel().queue { dm -> dm.sendMessage(contentInformation[questionLevel[event.author.id]!!]).queue() }
        }
    }
}