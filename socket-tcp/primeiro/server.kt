package primeiro
import primeiro.Server

import java.io.InputStream
import java.io.OutputStream
import java.util.*


import java.time.LocalTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import java.io.File


fun getTime (): String {
  val time: LocalTime = LocalTime.now()

  return time.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString()
}
fun getDate (): String {
  val date: LocalDateTime = LocalDateTime.now()

  return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
}
fun showFiles (client: Client, folder: String = "default") {

  if (folder.isEmpty()) {

  } else {
    val files = File(folder).list()

    client.sendMessage(files.size)
    files.forEach {
      client.sendMessage(it)
    }
  }
}
fun sendFile (client: Client, filename: String) {
}
fun clientHandler (client: Client, folder: String) {

  var message = client.receiveTextMessage()

  val timeRegex = Regex("time", RegexOption.IGNORE_CASE)
  val dateRegex = Regex("date", RegexOption.IGNORE_CASE)
  val filesRegex = Regex("files", RegexOption.IGNORE_CASE)
  val downRegex = Regex("down\\s([\\w\\d\\.])*", RegexOption.IGNORE_CASE)
  val exitRegex = Regex("exit", RegexOption.IGNORE_CASE)

  while (exitRegex.matches(message) != true) {
    if (timeRegex.matches(message)) {
      val response: String = getTime()
    } else if (dateRegex.matches(message)) {
      val response: String = getDate()

      client.sendMessage(response)

    } else if (filesRegex.matches(message)) {
      showFiles(client, folder)
    } else if (downRegex.matches(message)) {
      val fileRegex = Regex("\\s[\\w\\d\\.]+")

      val file = fileRegex.find(message)
      try {
        sendFile(client, file.value.substring(1))
      } finally {
        println("foi")
      }
    }

    message = client.receiveTextMessage()
  }

  client.finish()
}



fun main (args: ArrayList<String>) {

  println("Running at port ${args[2]}")

  var server: ServerSocket = ServerSocket(args[2].toInt())
  
  while (true) {
    var client: Client = server.acceptConnection()

    var handlerThread = Thread {
      clientHandler(client, args[1])
    }
    handlerThread.start()
  }

}
