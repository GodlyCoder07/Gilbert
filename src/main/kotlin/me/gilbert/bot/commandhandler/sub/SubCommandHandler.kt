package me.gilbert.bot.commandhandler.sub

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubCommandHandler(val name: String, val description: String, val usage: String)