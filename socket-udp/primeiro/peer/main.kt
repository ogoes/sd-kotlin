package primeiro

import java.net.*
import primeiro.ns.Connection

// fun connect(host: String, port: Int): Connection {
//   val socket: Socket = Socket(host, port)
//   return Connection(socket)
// }

fun makeMessageRequest(message: String, nickname: String): ByteArray {
  val messageSize: Int = message.length
  val nicknameSize: Int = nickname.length

  val request: MutableList<Byte> = mutableListOf()

  val nicknameInBytes: ByteArray = nickname.toByteArray(Charsets.UTF_8)
  val messageInBytes: ByteArray = message.toByteArray(Charsets.UTF_8)

  // adiciona o tamanho do nickname: 1 byte
  request.add(nicknameSize.toByte())

  // adiciona o nick do usuário em bytes: [0 - 255] bytes
  for (i in 1..nicknameSize) {
    request.add(nicknameInBytes.get(i - 1))
  }

  // adiciona o tamanho da mensagem: 1 byte
  request.add(messageSize.toByte())

  // adiciona a mensagem do usuário em bytes: [0 - 255] bytes
  for (i in 1..messageSize) {
    request.add(messageInBytes.get(i - 1))
  }

  return request.toByteArray()
}

fun sendMessage(dgramSocket: DatagramSocket, message: ByteArray, dstIP: String, port: Int) {
  val ipAddress: InetAddress = InetAddress.getByName(dstIP)
  val request: DatagramPacket = DatagramPacket(message, message.size, ipAddress, port)

  dgramSocket.send(request)
}

fun connectAndRegister(server: Connection, nickname: String, address: String, port: Int) {
  try {
    val request: MutableList<Byte> = mutableListOf()

    // request type(1)
    request.add(1.toByte())

    // nickname size
    val nicknameSize = nickname.length
    request.add(nicknameSize.toByte())

    // nickname
    for (i in 1..nicknameSize) {
      request.add(nickname.get(i - 1).toByte())
    }

    // address size
    val addressSize = address.length
    request.add(addressSize.toByte())

    // address
    for (i in 1..addressSize) {
      request.add(address.get(i - 1).toByte())
    }

    // port
    val HEXport3: Int = (port) shr 24
    val HEXport2: Int = (port and 0x00ff0000) shr 16
    val HEXport1: Int = (port and 0x0000ff00) shr 8
    val HEXport0: Int = (port and 0x000000ff)
    request.add(HEXport3.toByte())
    request.add(HEXport2.toByte())
    request.add(HEXport1.toByte())
    request.add(HEXport0.toByte())

    // envia requisição para o servidor
    server.sendMessage(request.toByteArray())

    // response
    // response = server.receiveMessage()
  } catch (t: Throwable) {
    println(t)
  }
}

fun getAllUsers(server: Connection) {
  val request: ByteArray = ByteArray(1)
  request.set(0, 1.toByte())
  server.sendMessage(request)

  val response = server.receiveMessage()
  val numberOfUsers = response.get(2)

  for (i in 1..numberOfUsers) {}
}

/**
 * @TODO Falta converter os bytes recebidos na resposta do servidor para inteiro
 */
fun getByNickname(server: Connection, nickname: String): String {
  try {
    val nicknameSize: Int = nickname.length

    val request: MutableList<Byte> = mutableListOf()
    val charset = Charsets.UTF_8

    // request type(3)
    request.add(3.toByte())

    // host nickname size
    request.add(nicknameSize.toByte())

    // nickname 
    val nicknameInBytes: ByteArray = nickname.toByteArray(charset)
    for (i in 1..nicknameSize) {
      request.add(nicknameInBytes.get(i - 1))
    }

    server.sendMessage(request.toByteArray())

    val response = server.receiveMessage()

    val hostAddressSize: Int = response.get(2).toInt()
    val hostnameInBytes: ByteArray = ByteArray(hostAddressSize)
    val portInBytes: ByteArray = ByteArray(4)

    for (i in 1..hostAddressSize) {
      hostnameInBytes.set(i - 1, response.get(i + 2))
    }

    for (i in (response.size - 1) downTo (response.size - 1 - 3)) {
      portInBytes.set(i, response.get(i))
    }

    val hostName: String = hostnameInBytes.toString(charset)
    // val port =

    // return hostName + port
  } catch (t: Throwable) {
    println(t)
  }
}

fun exit(server: Connection) {
  val request: ByteArray = ByteArray(1)
  request.set(0, 4.toByte())
  server.sendMessage(request)
}

/**
 * @TODO Onde criar o objeto Connection e DatagramSocket
 */
fun interation(nickname: String, host: String, port: Int) {
  try {
    val dgramSocket = DatagramSocket(port)

    print("Mensagem: ")
    var message = readLine()!!
  } catch (t: Throwable) {
    println(t)
  }
}

fun main(args: Array<String>) {
  try {
    println("Bem vindo ao Chat UDP")

    println("Nos informe seu nickname: ")
    var userNickname = readLine()!!

    val host = args[0]
    val port = args[1].toInt()

    interation(userNickname, host, port)
  } catch (t: Throwable) {
    println(t)
  }
}

