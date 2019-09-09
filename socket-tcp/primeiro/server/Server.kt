package server

import client.Client
import java.net.ServerSocket

class ServerSocket {
  private var _socket: ServerSocket
  private var _connectedClients: MutableList <Client> = ArrayList()

  constructor (port: Int) {
    _socket = ServerSocket(port)

    println("Server running at $port")
  }

  fun acceptConnection(): Client {
    val socket = _socket.accept()
    val client = Client(_connectedClients.size, socket)

    _connectedClients.add(client)

    return client
  }
}

