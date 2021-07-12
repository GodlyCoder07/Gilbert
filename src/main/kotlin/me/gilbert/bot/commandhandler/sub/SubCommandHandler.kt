package me.gilbert.bot.commandhandler.sub

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
/**
 * @param name the name of the sub command
 * @param description the description of the sub command
 * @param usage the usage of the description
 */
annotation class SubCommandHandler(val name: String, val description: String, val usage: String)