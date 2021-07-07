package me.gilbert.bot.commandhandler.base

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class CommandHandler(val name: String,
                                val description: String,
                                val usage: String,
                                val channelId: Array<String>)
