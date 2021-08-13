package me.gilbert.bot.database.serverinfo

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.gilbert.bot.database.Data
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ServerInformationRepository(guildId: String, path: File): Data {
    private val gson: Gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()
    private lateinit var serverInformationModel: ServerInformationModel
    private val file = File(path, "data_server-info_$guildId.json")

    init {
        createOrLoad()
    }

    override fun create() {
        serverInformationModel = ServerInformationModel()
        save()
    }

    override fun load() {
        val reader = FileReader(file)
        serverInformationModel = gson.fromJson(reader, ServerInformationModel::class.java)
        reader.close()
    }

    override fun save() {
        val writer = FileWriter(file)
        gson.toJson(serverInformationModel, writer)
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

    fun getServerInformationModel(): ServerInformationModel {
        return serverInformationModel
    }
}
