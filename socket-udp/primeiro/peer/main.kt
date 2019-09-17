package primeiro.peer

import primeiro.peer.Peer

fun main(args: Array<String>) {
  try {

    println("Bem vindo ao Chat UDP")

    print("Informe o endereço do servidor de nicknames: ")
    // val serverIP: String = readLine()!!
    val serverIP: String = "localhost"
    print("Informe a porta do servidor: ")
    // val serverPort: Int = (readLine()!!).toInt()
    val serverPort: Int = 9999

    val peer: Peer = Peer()

    peer.connectToNameServer(serverIP, serverPort)

    Thread {
      peer.receiveMessages()
    }.start()

    Thread {
      peer.sendMessages()
    }.start()

  } catch (t: Throwable) {
    println(t)
  }
}

