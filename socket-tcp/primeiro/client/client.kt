package client

import java.net.Socket
import java.net.SocketException
import java.util.*

fun receiveTimeDate(server: Client, type: String) {
  server.sendMessage(type)

  var response = server.receiveTextMessage()

  println(response)
}

// fun showFiles(server: Client) {
// }

// fun receiveFile(server: Client) {
// }

fun interation(socket: Client) {

  val timeRegex = Regex("time", RegexOption.IGNORE_CASE)
  val dateRegex = Regex("date", RegexOption.IGNORE_CASE)
  val filesRegex = Regex("files", RegexOption.IGNORE_CASE)
  val downRegex = Regex("down\\s([\\w\\d\\.])*", RegexOption.IGNORE_CASE)
  val exitRegex = Regex("exit", RegexOption.IGNORE_CASE)

  print("PROMPT:\n\n\\> ")
  var message = readLine()!!

  while (exitRegex.matches(message) != true) {

    if (timeRegex.matches(message)) {
      receiveTimeDate(socket, "TIME")
    } else if (dateRegex.matches(message)) {
      receiveTimeDate(socket, "DATE")
    } else if (filesRegex.matches(message)) {
      println("files")
    } else if (downRegex.matches(message)) {
      println("download")
    }

    print("\\> ")
    message = readLine()!!
  }

  socket.sendMessage("EXIT")
}

fun main(args: ArrayList<String>) {

  println("Connecting $args[1]:$args[2]")

    try {
      val socket = Socket(args[1], args[2].toInt())

      val server = Client(0, socket)

      interation(server)
    } catch (e: SocketException) {
      println("Erro")
    }
}

