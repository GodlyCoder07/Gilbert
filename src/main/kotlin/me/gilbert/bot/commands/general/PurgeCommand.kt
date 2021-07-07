package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.base.CommandHandler
import me.gilbert.bot.commandhandler.sub.SubCommand
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@CommandHandler("purge", "delete previous messages", "purge <amount>", [])
class PurgeCommand(override val subCommandsList: MutableList<SubCommand>) : Command() {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        if (args.size == 2) {
            val amount: Int = Integer.parseInt(args[1])
            embedBuilder.setColor(Color.GREEN)
            embedBuilder.setAuthor("Purge", null, "https://image.flaticon.com/icons/png/512/875/875550.png")
            embedBuilder.setTitle("Purging messages in ${event.channel.name}")
            embedBuilder.addField("Purging",
                "Deleting $amount messages...",
                false)
            embedBuilder.setFooter("Purge").setTimestamp(Date().toInstant())
            event.message.reply(embedBuilder.build()).queue()
            deleteMessages(event, amount + 2)
        }else {
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

    private fun deleteMessages(event:GuildMessageReceivedEvent, amount: Int) {
        if (amount < 100) {
            Executors.newSingleThreadScheduledExecutor().schedule({
                event.channel.deleteMessages(event.channel.history.retrievePast(amount).complete()).queue()
            }, 5, TimeUnit.SECONDS)
            return
        }

        Executors.newSingleThreadScheduledExecutor().schedule({
            event.channel.deleteMessages(event.channel.history.retrievePast(100).complete()).queue()
        }, 5, TimeUnit.SECONDS)
        deleteMessages(event, amount - 100)
    }
}