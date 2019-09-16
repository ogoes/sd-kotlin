package primeiro.peer

import primeiro.peer.Peer

fun main(args: Array<String>) {
  try {
    print("\u001B[H")

    println("Bem vindo ao Chat UDP")

    print("Informe o endereço do servidor de nicknames: ")
    val serverIP: String = readLine()!!
    print("Informe a porta do servidor: ")
    val serverPort: Int = (readLine()!!).toInt()

    val peer: Peer = Peer(serverIP, serverPort)

    peer.showConnectedHosts()
    Thread {
      peer.receiveMessages()
    }.start()

    while (true) {
      print("informe o usuário: ")
      val user: String = readLine()!!

      print("a mensagem: ")
      val message: String = readLine()!!


      peer.sendMessage(user, message)
    }
  } catch (t: Throwable) {
    println(t)
  }
}

