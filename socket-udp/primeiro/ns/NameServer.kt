package primeiro.ns

import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import primeiro.Interface

class NameServer : Interface {

  private var _socket: ServerSocket
  private var _dgramSocket: DatagramSocket
  private var _port: Int

  constructor (port: Int) {
    _port = port

    _socket = ServerSocket(port)
    _dgramSocket = DatagramSocket()

    println("Server running at $_port")
  }

  fun acceptConnection(): Socket = _socket.accept()

  fun clientHandler(client: Connection) {

    try {

      var request = client.receiveMessage()
      var requestType = request.get(0).toInt()

			while (requestType != 4) { // 4-QUIT


				if (requestType == 1) {
					connectPeer(client, request)
				} else if (requestType == 2) {
					sendConnectedHostsNicknames(client)
        } else if (requestType == 3) {
					hostInformationByNickname(client, request)
				}
				
        request = client.receiveMessage()
        requestType = request.get(0).toInt()
      }
    } catch (e: Throwable) {
		}
		
		_peers.remove(client)
  }

  private fun connectPeer(client: Connection, request: ByteArray): Boolean {
    // val client: Connection = Connection(_peers.size, socket)
    // val request = client.receiveMessage()

    try {

      // nickname
      val nicknameSize = request.get(1).toInt()
      val nickname: String = nicknameByRequest(request)
      var beginValid = (2 + nicknameSize)

      // address
      val addressSize = request.get(beginValid).toInt()
      val address: InetAddress = addressByRequest(request, beginValid)
      beginValid += (1 + addressSize)

      // port
      val HEXport3: Int = (request.get(beginValid + 0).toInt()) shl 24
      val HEXport2: Int = (request.get(beginValid + 1).toInt() and 0x00ff0000) shl 16
      val HEXport1: Int = (request.get(beginValid + 2).toInt() and 0x0000ff00) shl 8
      val HEXport0: Int = (request.get(beginValid + 3).toInt() and 0x000000ff) shl 0

      val port: Int = HEXport3 or HEXport2 or HEXport1 or HEXport0

      client.setNickname(nickname)
      client.setAddress(address)
      client.setPort(port)

      _peers.add(client)

      client.sendMessage(byteArrayOf(1, 1)) // SUCCESS
      return true
    } catch (t: Throwable) {
      client.sendMessage(byteArrayOf(1, 2)) // FAILURE
      println(t)
      return false
    }
  }

  private fun sendConnectedHostsNicknames(peer: Connection) {

    try {

      val response: MutableList<Byte> = mutableListOf()

      val type = 1
      val success = 1
      response.add(type.toByte())
      response.add(success.toByte())

      val numberOfConnectedPeers = _peers.size - 1

      response.add(numberOfConnectedPeers.toByte())

      val peerNickname = peer.getNickname()

      _peers.forEach {
        val nickname = it.getNickname()

        if (nickname != peerNickname) {

          val nicknameSize = nickname.length
          response.add(nicknameSize.toByte())
          for (i in 1..nicknameSize) {
            response.add(nickname.get(i - 1).toByte())
          }
        }
      }

      peer.sendMessage(response.toByteArray())
    } catch (t: Throwable) {
      peer.sendMessage(byteArrayOf(1, 2))
    }
  }

  private fun hostInformationByNickname(peer: Connection, request: ByteArray) {
    try {

      val nickname: String = nicknameByRequest(request)

      val peerNickname = peer.getNickname()

      if (nickname != peerNickname) {

        val response: MutableList<Byte> = mutableListOf()

        val type = 3
        val success = 1
        response.add(type.toByte())
        response.add(success.toByte())

        val address: String = peer.getAddress()
        val addressSize = address.length

        val port = peer.getPort()

        val HEXport3: Int = (port) shr 24
        val HEXport2: Int = (port and 0x00ff0000) shr 16
        val HEXport1: Int = (port and 0x0000ff00) shr 8
        val HEXport0: Int = (port and 0x000000ff)

        response.add(addressSize.toByte())
        for (i in 1..addressSize) {
          response.add(address.get(i - 1).toByte())
        }
        response.add(HEXport3.toByte())
        response.add(HEXport2.toByte())
        response.add(HEXport1.toByte())
        response.add(HEXport0.toByte())

        peer.sendMessage(response.toByteArray()) // SUCCESS
      } else {
        peer.sendMessage(byteArrayOf(1, 2)) // FAILURE
      }
    } catch (t: Throwable) {
      peer.sendMessage(byteArrayOf(1, 2)) // FAILURE
    }
  }

  private fun nicknameByRequest(request: ByteArray): String {
    // nickname
    var beginValid = 1
    val nicknameSize = request.get(beginValid).toInt()
    val nicknameByte: MutableList<Byte> = mutableListOf()

    for (i in 1..nicknameSize) {
      nicknameByte.add(request.get(beginValid))
      beginValid += 1
    }

    return nicknameByte.toByteArray().toString(Charsets.UTF_8)
  }

  private fun addressByRequest(request: ByteArray, beginValid: Int): InetAddress {

    var index = beginValid

    val addressSize = request.get(index).toInt()
    index += 1
    val addressByte: MutableList<Byte> = mutableListOf()

    for (i in 1..addressSize) {
      addressByte.add(request.get(index))
      index += 1
    }

    return InetAddress.getByName(addressByte.toByteArray().toString(Charsets.UTF_8))
  }

  private fun sendConnectedPeers() {
  }

  private var _peers: MutableList<Connection> = mutableListOf()
}

