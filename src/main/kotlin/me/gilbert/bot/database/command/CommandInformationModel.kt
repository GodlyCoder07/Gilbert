package me.gilbert.bot.database.command

class CommandInformationModel {
    var prefix: String = "$"
    var commands: MutableSet<CommandInformation> = mutableSetOf()
}