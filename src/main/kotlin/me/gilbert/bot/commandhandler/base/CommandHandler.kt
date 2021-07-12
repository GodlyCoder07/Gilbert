package me.gilbert.bot.commandhandler.base

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)

/**
 * @param name the name of the command
 * @param description the description of the command
 * @param usage the usage of the command
 * @param channelId the channels that the command can get executed
 * @param executeWithArgument execute the command if there is argument
 */
annotation class CommandHandler(val name: String,
                                val description: String,
                                val usage: String,
                                val channelId: Array<String>,
                                val executeWithArgument: Boolean = false,
                                val isAccessible: Boolean = true)
