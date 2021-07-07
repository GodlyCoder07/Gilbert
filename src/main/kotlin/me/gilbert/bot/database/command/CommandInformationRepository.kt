package me.gilbert.bot.database.command

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.gilbert.bot.commandhandler.base.Command
import me.gilbert.bot.database.Data
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import java.util.stream.Collectors

class CommandInformationRepository(guildId: String, path: File): Data {
    private val gson: Gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()
    private lateinit var commandInformationModel: CommandInformationModel
    private var commandsList: MutableSet<CommandInformation> = mutableSetOf()
    private val file = File(path, "info_command_$guildId.json")

    init {
        createOrLoad()
    }

    override fun create() {
        commandInformationModel = CommandInformationModel()
        save()
    }

    override fun load() {
        val reader = FileReader(file)
        commandInformationModel = gson.fromJson(reader, CommandInformationModel::class.java)
        reader.close()
    }

    override fun save() {
        val writer = FileWriter(file)
        commandInformationModel.commands = commandsList
        commandInformationModel.commands.sortedBy { cmd -> cmd.name }
        gson.toJson(commandInformationModel, writer)
        writer.close()
    }

    override fun createOrLoad() {
        if (file.parentFile != null && !file.parentFile.exists()) {
            file.parentFile.mkdir()
        }
        if (!file.exists()) {
            file.createNewFile()
            create()
        }
        load()
    }

    fun getCommandInformation(command: Command): CommandInformation? {
        return commandInformationModel.commands
            .stream()
            .filter { cmd -> cmd.name == command.commandHandler.name }
            .findFirst()
            .orElse(null)
    }

    fun addCommandInformation(vararg command: Command) {
        val commandsListCache: MutableList<CommandInformation> = mutableListOf()
        Arrays.stream(command)
            .forEach { cmd ->
                val commandInformation = CommandInformation()
                commandInformation.name = cmd.commandHandler.name
                commandInformation.description = cmd.commandHandler.description
                commandInformation.usage = cmd.commandHandler.usage
                commandInformation.subCommands = cmd.subCommandsList.stream().map { it.subCommandHandler.name }.collect(Collectors.toList()).toTypedArray()
                commandInformation.channelId = cmd.commandHandler.channelId
                if (commandInformationModel.commands.contains(getCommandInformation(cmd))) {
                    commandInformation.channelId = getCommandInformation(cmd)?.channelId!!
                }
                commandsListCache.add(commandInformation)
            }
        commandsList.addAll(commandsListCache)
    }

    fun getCommandInformationModel(): CommandInformationModel {
        return commandInformationModel
    }

    fun getCommandsList(): MutableSet<CommandInformation> {
        return commandsList
    }
}

