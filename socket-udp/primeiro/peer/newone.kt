package peer

import java.net.BindException
import java.net.Socket
import java.net.SocketException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.Random
import ns.Connection

class Peer0 : Connection {

  private lateinit var _dgramSocket: DatagramSocket
  private val _connected: Boolean

  private lateinit var _connectedPeers: MutableList<Pair<String, Pair<String, Int>>>
  private lateinit var _messages: MutableList<String>

  constructor () : super(0) {

    var port: Int

    while (true) {
      try {
        port = Random().nextInt(65535 - 10000) + 10000
        _dgramSocket = DatagramSocket(port)
        break
      } catch (e: BindException) {
      }
    }

    _connected = true
    print("informe o nickname: ")
    val nickname = readLine()!!

    setPort(port)
    setAddress(InetAddress.getLocalHost())
    setNickname(nickname)
  }

  fun connectServer(serverIP: String, serverPort: Int) {
    val nicknameSize: Int = _nickname.length

    val addressAsString: String = _iattr.getHostAddress()
    val addressSize: Int = addressAsString.length

    val HEXport3: Int = _port shl 24
    val HEXport2: Int = (_port and 0x00ff0000) shl 16
    val HEXport1: Int = (_port and 0x0000ff00) shl 8
    val HEXport0: Int = _port and 0x000000ff

    val requestType: Int = 1

    val requestBytes: MutableList<Byte> = mutableListOf()

    requestBytes.add(requestType.toByte())
    requestBytes.add(nicknameSize.toByte())

    for (i in 1..nicknameSize) {
      requestBytes.add(_nickname.get(i - 1).toByte())
    }

    requestBytes.add(addressSize.toByte())

    for (i in 1..addressSize) {
      requestBytes.add(addressAsString.get(i - 1).toByte())
    }

    requestBytes.add(HEXport3.toByte())
    requestBytes.add(HEXport2.toByte())
    requestBytes.add(HEXport1.toByte())
    requestBytes.add(HEXport0.toByte())


    var server: Connection
    try {
      val socket = Socket(serverIP, serverPort)

      server = Connection(0, socket)
    } catch (e: SocketException) {
      println("Erro ao conectar com o servidor")
      return
    }

    server.sendMessage(requestBytes.toByteArray())

    val response = server.receiveMessage()


    if (response.get(1).toInt() == 2) {
      throw Throwable("Erro ao conectar com o servidor")
    }
    receiveConnectedPeers(server)
  }

  fun sendMessage(host: InetAddress, port: Int) {
  }
  fun receiveMessages() {
    while (_connected) {
      val buffer: ByteArray = ByteArray(514)
      val packet: DatagramPacket = DatagramPacket(buffer, buffer.size)

      _dgramSocket.receive(packet)

      Thread {
        messageHandler(buffer)
      }.start()
    }
  }

  private fun messageHandler(message: ByteArray) {
  }

  private fun receiveConnectedPeers(server: Connection) {

    
  }
}

