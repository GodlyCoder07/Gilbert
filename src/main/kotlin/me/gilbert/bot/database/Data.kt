package me.gilbert.bot.database

interface Data {
    fun create()
    fun load()
    fun save()
    fun createOrLoad()
}