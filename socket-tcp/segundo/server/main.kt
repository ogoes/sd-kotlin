// implementação das funções para enviar para o client
package segundo.server

import segundo.SocketConnection
import java.util.*

fun createByteArray(messageType: Int, commandIdentifier: Int): ByteArray {

  val response: ByteArray = ByteArray(3)

  response.set(0, messageType.toByte())
  response.set(1, commandIdentifier.toByte())

  return response
}

fun addFile(request: ByteArray, client: SocketConnection, localStorage: String = "./.client/") {
  try {
    request.set(2, 1.toByte())
    client.sendMessage(request)
  } catch (t: Throwable) {
    request.set(2, 2.toByte())
    client.sendMessage(request)
  }
}

fun deleteFile() {
}

fun getFileList() {
} 

fun getFile() {
}

fun clientHandler(client: SocketConnection) {

  try {
    var message = client.receiveMessage()
    var command = message.get(1).toInt()

    while () {
      if (command == 1) {
        addFile(message, client)
      } else if (command == 2) {
        // comando 0x02
        deleteFile() 
      } else if (command == 3) {
        // comando 0x03
        getFileList()
      } else if (command == 4) {
        // comando 0x04
        getFile()
      }

      message = client.receiveMessage()
      command = message.get(1).toInt()
    }
  } catch (t: Throwable) {
    println("____________________________________")
  }

  client.finish()
}

fun main(args: Array<String>) {

  println(args[0])
  println(args[1])

  var server: ServerSocket = ServerSocket(args[1].toInt())

  while (true) {

    var client: SocketConnection = server.acceptConnection()

    var handlerThread = Thread {
      clientHandler(client)
    }
    handlerThread.start()
  }
}

