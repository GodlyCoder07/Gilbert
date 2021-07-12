package me.gilbert.bot.database.player

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.gilbert.bot.database.Data
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class UserDataRepository(guildId: String, path: File): Data {
    private val gson: Gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()
    private lateinit var userDataModel: UserDataModel
    private val file = File(path, "data_player_$guildId.json")

    init {
        createOrLoad()
    }

    override fun create() {
        userDataModel = UserDataModel()
        save()
    }

    override fun load() {
        val reader = FileReader(file)
        userDataModel = gson.fromJson(reader, UserDataModel::class.java)
        reader.close()
    }

    override fun save() {
        val writer = FileWriter(file)
        gson.toJson(userDataModel, writer)
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

    fun add(data: UserData) {
        if (!userDataModel.playersData.contains(data))
            userDataModel.playersData.add(data)
        save()
    }

    fun get(id: String): UserData? {
        return userDataModel.playersData
            .stream()
            .filter { data -> data.id == id }
            .findFirst()
            .orElse(null)
    }

    fun remove(id: String) {
        if (userDataModel.playersData.contains(get(id)))
            userDataModel.playersData.remove(get(id))
    }
}