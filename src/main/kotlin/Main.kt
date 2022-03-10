package com.github.kotlintelegrambot.dispatcher

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Update
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

var TOKEN = "2851303311:AAEfumy4fWIMfapwJc6Tt3s_Vz1_ayt17wQ"

fun main(argsMain: Array<String>) {
    if(argsMain.size != 1)
        println("java -jar TelegramTerminal [TOKEN]")
    else
        TOKEN = argsMain[0]

    val bot = bot {
        token = TOKEN
//        timeout = 30
        dispatch {
            text("help") {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id), text =
                        "Ping uses ICMP to send out echo requests\n /ping\n\n" +
                        "Show chat ID\n /chatId\n\n" +
                        "Internet speed test\n /speedTest\n\n" +
                        "Start FTP server\n /startFtp\n\n" +
                        "Stop FTP server\n /stopFtp\n\n" +
                        "Start SSH server\n /startSsh\n\n" +
                        "Stop SSH server\n /stopSsh\n\n" +
                        "Executing commands in the shell\n /c\n\n")
            }
            text("ping") {
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "pong")
            }
            command("chatId") {
                val chat = "${ChatId.fromId(message.chat.id)}"
                val chatId = chat.drop(6).dropLast(1)
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = chatId)
            }
            command("speedTest") {
                exec(bot, update, listOf("speedtest --simple"))
            }
            command("startFtp") {
                exec(bot, update, listOf("/etc/init.d/vsftpd start"))
                exec(bot, update, listOf("echo \"ifconfig.me   `curl ifconfig.me`\""))
                exec(bot, update, listOf("echo \"icanhazip.com   `curl icanhazip.com`\""))
                exec(bot, update, listOf("echo \"ipecho.net/plain   `curl ipecho.net/plain`\""))
                exec(bot, update, listOf("echo \"ifconfig.co   `curl ifconfig.co`\""))
            }
            command("stopFtp") {
                exec(bot, update, listOf("/etc/init.d/vsftpd stop"))
            }

            command("startSsh") {
                exec(bot, update, listOf("/etc/init.d/ssh start"))
                exec(bot, update, listOf("echo \"ifconfig.me   `curl ifconfig.me`\""))
                exec(bot, update, listOf("echo \"icanhazip.com   `curl icanhazip.com`\""))
                exec(bot, update, listOf("echo \"ipecho.net/plain   `curl ipecho.net/plain`\""))
                exec(bot, update, listOf("echo \"ifconfig.co   `curl ifconfig.co`\""))
            }
            command("stopSsh") {
                exec(bot, update, listOf("/etc/init.d/ssh stop"))
            }

            command("c") {
                exec(bot, update, args)
            }
        }
    }
    bot.startPolling()
}

fun exec(bot:Bot, update:Update, args:List<String>) {
    val joinedArgs = args.joinToString(separator = " ")
    val processBuilder = ProcessBuilder()
    processBuilder.command("bash", "-c", joinedArgs)
    try {
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null)
            bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = "$line")
//        val exitCode = process.waitFor()
//        bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = "\nExited with error code : $exitCode")
    } catch (e: IOException) {
        bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = "${e.printStackTrace()}")
    } catch (e: InterruptedException) {
        bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = "${e.printStackTrace()}")
        e.printStackTrace()
    }
}