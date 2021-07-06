package me.gilbert.bot.database

import me.gilbert.bot.database.command.CommandInformationRepository
import me.gilbert.bot.database.player.UserDataRepository
import java.io.File

class ServerData(id: String) {
    private var path: File = File("C:\\bot_data\\server_data_$id")

    private val userDataRepository: UserDataRepository
    private val commandInformationRepository: CommandInformationRepository

    init {
        if (!path.exists()) {
            path.mkdirs()
        }
        userDataRepository = UserDataRepository(id, path)
        commandInformationRepository = CommandInformationRepository(id, path)
    }

    fun getCommandInformationRepository(): CommandInformationRepository {
        return commandInformationRepository
    }

    fun getPlayerDataRepository(): UserDataRepository {
        return userDataRepository
    }

    fun delete() {
        path.delete()
    }

}