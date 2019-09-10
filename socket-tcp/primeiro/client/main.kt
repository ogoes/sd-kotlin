package client

import java.net.Socket
import java.net.SocketException
import java.util.*

import java.io.File
import java.nio.charset.Charset

import java.nio.file.Files
import java.nio.file.StandardOpenOption

fun receiveTimeDate(server: Client, type: String) {
  server.sendMessage(type)

  var response: String = server.receiveTextMessage()



  println(response)
  print("\u001B[${response.length}D")
  print(response)
  print("\u001B[${response.length}D")
  println(response)
  println(response)

}

fun showFiles(server: Client) {
  val filesCount: Int = server.receiveIntegerMessage()

  server.sendMessage("ACK")
  println(filesCount)

  var file: String = ""

  for (i in 1..filesCount) {
    file = server.receiveTextMessage()
    server.sendMessage("ACK")
    println(file)
  }

}

fun receiveFile(server: Client, filename: String) {
}

fun interation(socket: Client) {

  val timeRegex = Regex("time", RegexOption.IGNORE_CASE)
  val dateRegex = Regex("date", RegexOption.IGNORE_CASE)
  val filesRegex = Regex("files", RegexOption.IGNORE_CASE)
  val downRegex = Regex("down", RegexOption.IGNORE_CASE)
  val exitRegex = Regex("exit", RegexOption.IGNORE_CASE)

  print("PROMPT:\n\n\\> ")
  var message = readLine()!!

  while (exitRegex.matches(message) != true) {

    if (timeRegex.matches(message)) {
      receiveTimeDate(socket, "TIME")
    } else if (dateRegex.matches(message)) {
      receiveTimeDate(socket, "DATE")
    } else if (filesRegex.matches(message)) {

      socket.sendMessage("FILES")
      showFiles(socket)

    } else if (downRegex.containsMatchIn(message)) {

      val fileRegex = Regex("\\s[\\w\\d\\.]+")

      val file = fileRegex.find(message)!!
      
      try {
        receiveFile(socket, file.value.substring(1))
      } catch (t: Throwable) {
        println(t)
      }
    }

    print("\\> ")
    message = readLine()!!
  }

  socket.sendMessage("EXIT")
}

fun main(args: Array<String>) {

  println("Connecting ${args[0]}:${args[1]}")

    try {
      val socket = Socket(args[0], args[1].toInt())

      val server = Client(0, socket)

      interation(server)

      server.finish()
    } catch (e: SocketException) {
      println("Erro")
    } catch (t: Throwable) {
      println("Finalizando")
    }
}

