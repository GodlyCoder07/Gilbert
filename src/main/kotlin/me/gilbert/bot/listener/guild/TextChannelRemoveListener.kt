package me.gilbert.bot.listener.guild

import me.gilbert.bot.getServerData
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class TextChannelRemoveListener: ListenerAdapter() {
    override fun onTextChannelDelete(event: TextChannelDeleteEvent) {
        getServerData(event.guild.id)?.getCommandInformationRepository()?.getCommandsList()?.forEach { commandInformation ->
            commandInformation.channelId.forEach { cmdId ->
                if (event.jda.getGuildChannelById(cmdId) == null) {
                    val commandList: MutableList<String> = commandInformation.channelId.toMutableList()
                    commandList.remove(cmdId)
                    commandInformation.channelId = commandList.toTypedArray()
                    getServerData(event.guild.id)?.getCommandInformationRepository()?.save()
                }
            }
        }
    }
}