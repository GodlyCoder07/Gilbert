package me.gilbert.bot.database.serverinfo

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.gilbert.bot.database.Data
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ServerInformationRepository(guildId: String, path: File): Data {
    /*
     *  Today I created @PixelCord. Right now, he does nothing. What I am planning to do is to connect Discord and Minecraft chat.
     *  Every message you send in a particular channel will be received by the bot, @PixelCord and sends it to the Minecraft server
     *  that has been assigned to it. I will also upload the plugin in spigot for others to use in their Minecraft server. Today I
     *  created @PixelCord. Right now, he does nothing. What I am planning to do is to connect Discord and Minecraft chat. Every message
     *  you send in a particular channel will be received by the bot, @PixelCord and sends it to the Minecraft server that has been
     *  assigned to it. I will also upload the plugin in spigot for others to use in their Minecraft server.
     */
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