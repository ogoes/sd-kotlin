package primeiro

import primeiro.Client


import java.net.Socket
import java.net.SocketException
import java.nio.charset.Charset

import java.util.*



fun receiveTimeDate (server: Client, type: String) {
  server.sendMessage(type)

  var response = server.receiveTextMessage()

  println(response)
}

fun showFiles (server: Client) {

}

fun receiveFile (server: Client) {

}


fun interation (socket: Client) {
  val reader = socket.getInputStream()
  val writer = socket.getOutputStream()

  val timeRegex = Regex("time", RegexOption.IGNORE_CASE)
  val dateRegex = Regex("date", RegexOption.IGNORE_CASE)
  val filesRegex = Regex("files", RegexOption.IGNORE_CASE)
  val downRegex = Regex("down\\s([\\w\\d\\.])*", RegexOption.IGNORE_CASE)
  val exitRegex = Regex("exit", RegexOption.IGNORE_CASE)


  print("PROMPT:\n\n\> ")
  var message = readline()!!

  while (exitRegex.matches(message) != true) {

    if (timeRegex.matches(message)) {
      receiveTime(socket, "TIME")
    } else if (dateRegex.matches(message)) {
      receiveDate(socket, "DATE")
    } else if (filesRegex.matches(message)) {
      println("files")
    } else if (downRegex.matches(message)) {
      println("download")
    }

    print("\> ")
    message = readline()!!
  }

  socket.sendMessage("EXIT")

}



fun main (args: ArrayList<String>) {

  println("Connecting $args[1]:$args[2]")

    try {
      val socket = Socket(args[1], args[2].toInt())

      val server = Client(0, socket)
      
      interation(server)
    } catch (e: SocketException) {
      println("\nHouveram erros na conexão com o servidor.\n\nVerifique o endereço ou a porta especificada\n\n\tEndereço IP: ${serverIP}\n\tPorta: ${serverPort}")
      return 1
    }
}
