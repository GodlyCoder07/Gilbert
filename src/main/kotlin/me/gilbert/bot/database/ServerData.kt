package me.gilbert.bot.database

import me.gilbert.bot.database.command.CommandInformationRepository
import me.gilbert.bot.database.player.UserDataRepository
import me.gilbert.bot.database.serverinfo.ServerInformationRepository
import java.io.File

class ServerData(id: String) {
    private var path: File = File("C:\\bot_data\\server_data_$id")

    private val userDataRepository: UserDataRepository
    private val commandInformationRepository: CommandInformationRepository
    private val serverInformationRepository: ServerInformationRepository

    init {
        if (!path.exists()) {
            path.mkdirs()
        }
        userDataRepository = UserDataRepository(id, path)
        commandInformationRepository = CommandInformationRepository(id, path)
        serverInformationRepository = ServerInformationRepository(id, path)
    }

    fun getCommandInformationRepository(): CommandInformationRepository {
        return commandInformationRepository
    }

    fun getPlayerDataRepository(): UserDataRepository {
        return userDataRepository
    }

    fun getServerInformationRepository(): ServerInformationRepository {
        return serverInformationRepository
    }

    fun delete() {
        path.delete()
    }

}