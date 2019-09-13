/**
 * * @description: neste código se encontram as implementações das funções solicitadas pelo client
 * @author Otávio Goes
 * @author Dennis Urtubia
 */

package server

import java.io.File
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import primeiro.SocketConnection

/**
 * @return hora com formato especificado na descrição do trabalho
 */
fun getTime(): String {
  val time: LocalTime = LocalTime.now()

  return time.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString()
}

/**
 * @return data com formato especificado na descrição do trabalho
 */
fun getDate(): String {
  val date: LocalDateTime = LocalDateTime.now()

  return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
}

/**
 * *@description envia para o client os arquivos da pasta definida por padrão
 */
fun showFiles(client: SocketConnection, folder: String = "default") {

  if (folder.isEmpty()) {
  } else {
    val files = File(folder).list()

    client.sendMessage(files.size)
    client.receiveTextMessage()

    files.forEach {
      client.sendMessage(it)
      client.receiveTextMessage()
    }
  }
}

/**
 * *@description envia o arquivo solicitado pelo client
 */
fun sendFile(client: SocketConnection, filename: String) {

  val file: File = File(filename)

  if (file.exists()) {

    val fileSize: Int = file.length().toInt()

    client.sendMessage(fileSize)
    client.receiveTextMessage()

    val fileInputStream = file.inputStream()
    val bytes = ByteArray(1)

    for (i in 1..fileSize) {
      fileInputStream.read(bytes)

      client.sendMessage(bytes)
      client.receiveTextMessage()
    }

    fileInputStream.close()
  } else {
    client.sendMessage(0)
    client.receiveTextMessage()
  }
}
/**
 * *@description identifica o comando solicitado pelo client e executa a função do respectivo comando
 */
fun clientHandler(client: SocketConnection, folder: String) {

  try {
    var message = client.receiveTextMessage()

    while (message != "EXIT") {

      if (message == "TIME") {

        val response: String = getTime()
        client.sendMessage(response)
      } else if (message == "DATE") {

        val response: String = getDate()
        client.sendMessage(response)
      } else if (message == "FILES") {

      showFiles(client, folder)
      } else if (message.substring(0, 4) == "DOWN") {
        // println(message)

        val fileRegex = Regex("\\s[\\w\\d\\.]+")

        val file = fileRegex.find(message)!!
        try {
          sendFile(client, folder + "/" + file.value.substring(1))
        } catch (t: Throwable) {
          println(t)
        }
      }

      message = client.receiveTextMessage()
    }
  } catch (t: Throwable) {
    println("____________________________________")
  }

  client.finish()
}

fun main(args: Array<String>) {

  var server: ServerSocket = ServerSocket(args[1].toInt())

  while (true) {

    var client: SocketConnection = server.acceptConnection()

    var handlerThread = Thread {
      clientHandler(client, args[0])
    }
    handlerThread.start()
  }
}

