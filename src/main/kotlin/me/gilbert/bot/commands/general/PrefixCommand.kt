package me.gilbert.bot.commands.general

import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.commandhandler.base.CommandHandler
import me.gilbert.bot.commandhandler.sub.SubCommand
import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@CommandHandler("prefix", "modify command prefix", "prefix <prefix>", [])
class PrefixCommand(vararg subCommand: SubCommand) : Command(*subCommand) {
    override fun execute(event: GuildMessageReceivedEvent, args: List<String>) {
        val embedBuilder = EmbedBuilder()
        if (args.size == 2) {
            val prefix: String = args[1]
            getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix = prefix
            getServerData(event.guild.id)?.getCommandInformationRepository()?.save()
            embedBuilder.setColor(Color.GREEN)
            embedBuilder.setTitle("✅ Successful")
            embedBuilder.addField("Prefix Changed",
                "Command prefix had successfully changed to `${prefix}`",
                false)
            event.message.reply(embedBuilder.build()).queue()
        }else {
            embedBuilder.setColor(Color.RED)
            embedBuilder.setTitle("❌ Error")
            embedBuilder.addField(
                "Invalid Usage",
                "Usage: ${getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandInformationModel()?.prefix + commandHandler.usage}",
                false
            )
            event.message.reply(embedBuilder.build()).queue { msg ->
                Executors.newSingleThreadScheduledExecutor().schedule({
                    msg.delete().queue()
                    event.message.delete().queue()
                }, 3, TimeUnit.SECONDS)
            }
        }
    }
}