package ns

import java.net.SocketException
import java.net.Socket


fun main (args: Array<String>) {

  val server = NameServer(args[0].toInt())

  var ids: Int = 0

  try {
    while (true) {
      var clientSocket = server.acceptConnection()

      var handlerThread = Thread {
        server.clientHandler(Connection(ids, clientSocket))
      }
      handlerThread.start()
      ids += 1
    }

  } catch (ex: SocketException) {
    println("Error")
  }
}
