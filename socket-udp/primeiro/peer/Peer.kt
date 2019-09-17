package primeiro.peer

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket
import java.net.SocketException
import ns.Connection



import java.util.Random

class Peer : Connection {

  private lateinit var _connectedPeers: MutableList<String>
  private var _messages: MutableList<String>
  private lateinit var _nameServer: Connection

  private val _dgramSocket: DatagramSocket
  private var _connected: Boolean


  constructor () : super(0) {
    val port: Int = Random().nextInt(65535 - 7000) + 7000

    _dgramSocket = DatagramSocket(port)

    setPort(port)
    setAddress(InetAddress.getLocalHost())

    _connected = false
    _messages = mutableListOf()
  }

  fun connectToNameServer(serverIp: String, serverPort: Int) {

    try {
      _socket = Socket(serverIp, serverPort)
    } catch (t: SocketException) {
      println("Erro ao conectar com o servidor")
      return
    }

    println(getPort())

    setNickname()

    register()

    _connected = true
  }

  fun showConnectedHosts() {
    if (_connected) {
      getNicknameList()

      _connectedPeers.forEach {
        println(it)
      }
    }
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

  fun sendMessages() {
    showConnectedHosts()
    while (_connected) {
      print("informe o usuário: ")
      val user: String = readLine()!!

      print("a mensagem: ")
      val message: String = readLine()!!

      sendMessage(user, message)
    }
  }

  private fun sendMessage(nickname: String, message: String) {
    getNicknameList()
    if (_connectedPeers.contains(nickname) == false) {
      println("Usuário não conectado")
      return
    }

    val (hostIP, hostPort) = getHostInfos(nickname)

    val hostAddress: InetAddress = InetAddress.getByName(hostIP)

    val messageBytes: MutableList<Byte> = mutableListOf()

    val senderNickname: String = getNickname()
    val senderNicknameSize: Int = senderNickname.length

    val messageSize: Int = message.length

    messageBytes.add(senderNicknameSize.toByte())
    for (i in 1..senderNicknameSize) {
      messageBytes.add(senderNickname.get(i - 1).toByte())
    }

    messageBytes.add(messageSize.toByte())
    for (i in 1..messageSize) {
      messageBytes.add(message.get(i - 1).toByte())
    }

    val packet: DatagramPacket = DatagramPacket(messageBytes.toByteArray(), messageBytes.size, hostAddress, hostPort)

    _dgramSocket.send(packet)

    showMessages()
  }

  private fun showMessages() {
    val initIndex: Int = _messages.size - 10

    print("\u001B[H")

    if (initIndex < 0) {
      _messages.forEach {
        println(it)
      }
      for (i in 1..(_messages.size - initIndex)) {
        println("")
      }
    } else {
      for (i in initIndex..(_messages.size)) {
        println(_messages.get(i))
      }
    }

    for (i in 1..19) {
      print("")
    }
    println("")
  }

  private fun messageHandler(message: ByteArray) {

    try {

      var init: Int = 0
      val nicknameSize: Int = message.get(init).toInt()
      init += 1
      val nicknameBytes: MutableList<Byte> = mutableListOf()

      for (i in 1..nicknameSize) {
        nicknameBytes.add(message.get(init))
        init += 1
      }

      val nickname: String = nicknameBytes.toByteArray().toString(Charsets.UTF_8)

      val messageSize: Int = message.get(init).toInt()
      init += 1
      val msgBytes: MutableList<Byte> = mutableListOf()

      for (i in 1..messageSize) {
        msgBytes.add(message.get(init))
        init += 1
      }
      val msg: String = msgBytes.toByteArray().toString(Charsets.UTF_8)

      _messages.add("[$nickname]: $msg")
    } catch (t: Throwable) {
    }
  }

  private fun getNicknameList() {
    _connectedPeers = mutableListOf()
    try {
      val requestNicknameList = 2
      sendMessage(byteArrayOf(requestNicknameList.toByte()))

      val response = receiveMessage()
      val status: Int = response.get(1).toInt()

      if (status == 1) {

        val connectedPeers: Int = response.get(2).toInt()
        val newList: MutableList<String> = mutableListOf()

        var init: Int = 3

        for (i in 1..connectedPeers) {
          val nickSize = response.get(init).toInt()
          init += 1
          val nicksByte: MutableList<Byte> = mutableListOf()

          for (j in 1..nickSize) {
            nicksByte.add(response.get(init))
            init += 1
          }

          newList.add(nicksByte.toByteArray().toString(Charsets.UTF_8))
        }

        _connectedPeers = newList
      }
    } catch (t: Throwable) {
      println("Não foi possível receber a lista de peers conectados")
    }
  }

  private fun setNickname() {
    getNicknameList()

    var invalid: Boolean = true
    var nickname: String = ""

    var novamente: String = ""
    while (invalid) {
      print("Informe o seu nickname$novamente: ")
      nickname = readLine()!!

      invalid = _connectedPeers.contains(nickname)

      if (invalid) {
        novamente = " novamente"
        println("Nickname invalido")
      }
    }

    setNickname(nickname)
  }

  private fun getHostInfos(nickname: String): Pair<String, Int> {
    if (_connectedPeers.contains(nickname)) {

      val requestType: Int = 3
      val nicknameSize: Int = nickname.length
      val requestBytes: MutableList<Byte> = mutableListOf()

      requestBytes.add(requestType.toByte())
      requestBytes.add(nicknameSize.toByte())

      for (i in 1..nicknameSize) {
        requestBytes.add(nickname.get(i - 1).toByte())
      }

      sendMessage(requestBytes.toByteArray())
      val response = receiveMessage()
      val responseStatus: Int = response.get(1).toInt()

      if (responseStatus == 1) {
        var init: Int = 2
        val addressSize: Int = response.get(init).toInt()
        init += 1

        val addressBytes: MutableList<Byte> = mutableListOf()
        for (i in 1..addressSize) {
          addressBytes.add(response.get(init))
          init += 1
        }

        val address: String = addressBytes.toByteArray().toString(Charsets.UTF_8)

        val HEXport3: Int = (response.get(init + 0).toInt()) shl 24 // 0xff000000
        val HEXport2: Int = (response.get(init + 1).toInt()) shl 16 // 0x00ff0000
        val HEXport1: Int = (response.get(init + 2).toInt()) shl 8  // 0x0000ff00
        val HEXport0: Int = (response.get(init + 3).toInt()) shl 0  // 0x000000ff

        val port: Int = HEXport3 or HEXport2 or HEXport1 or HEXport0


        println(address)
        println(port)

        return Pair<String, Int>(address, port)
      }
    }
    return Pair<String, Int>("invalid", -1)
  }

  private fun register() {
    val requestType: Int = 1

    val nickname = getNickname()
    val nicknameBytes: ByteArray = nickname.toByteArray(Charsets.UTF_8)
    val nicknameSize = nicknameBytes.size

    val address = getAddress()
    val addressBytes: ByteArray = address.toByteArray(Charsets.UTF_8)
    val addressSize = addressBytes.size

    val port = getPort()

    val requestBytes: MutableList<Byte> = mutableListOf()

    requestBytes.add(requestType.toByte())
    requestBytes.add(nicknameSize.toByte())

    for (i in 1..nicknameSize) {
      requestBytes.add(nicknameBytes.get(i - 1))
    }

    requestBytes.add(addressSize.toByte())

    for (i in 1..addressSize) {
      requestBytes.add(addressBytes.get(i - 1))
    }

    val HEXport3: Int = port shr 24
    val HEXport2: Int = (port and 0x00ff0000) shr 16
    val HEXport1: Int = (port and 0x0000ff00) shr 8
    val HEXport0: Int = port and 0x000000ff

    requestBytes.add(HEXport3.toByte())
    requestBytes.add(HEXport2.toByte())
    requestBytes.add(HEXport1.toByte())
    requestBytes.add(HEXport0.toByte())

    sendMessage(requestBytes.toByteArray())

    val response = receiveMessage()
    val responseStatus = response.get(1).toInt()
    _connected = if (responseStatus == 1) true else false
  }
}

