package segundo.server

import java.net.ServerSocket
import segundo.SocketConnection

class ServerSocket {
  private var _socket: ServerSocket
  private var _connectedClients: MutableList <SocketConnection> = ArrayList()

  constructor (port: Int) {
    _socket = ServerSocket(port)

    println("Server running at $port")
  }

  fun acceptConnection(): SocketConnection {
    val socket = _socket.accept()
    val client = SocketConnection(_connectedClients.size, socket)

    _connectedClients.add(client)

    return client
  }
}

