package primeiro2

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

val mapa: MutableMap<String, Int> = mutableMapOf(
  Pair<String, Int>("ada", 6789),
  Pair<String, Int>("carlos", 9876),
  Pair<String, Int>("jao", 5555)
)

fun main(args: Array<String>) {

  print("Informe o nickname: ")
  val nickname: String = readLine()!!

  if (mapa.containsKey(nickname) == false) {
    mapa.put(nickname, 6543)
  }

  var user: String = ""
  while (mapa.containsKey(user) != true) {
    print("Informe o usuário que receberá as mensagens: ")
    user = readLine()!!
  }

  val port: Int = mapa.get(user)!!

  
  var dgramSocket: DatagramSocket = DatagramSocket(mapa.get(nickname)!!)

  try {

    Thread {
      print("mensagem: ")
      var message: String = readLine()!!
      while (message != "SAIR") {

        val buffer: ByteArray = message.toByteArray(Charsets.UTF_8)

        var dgramPacket: DatagramPacket = DatagramPacket(buffer, buffer.size, InetAddress.getLocalHost(), port)
        dgramSocket.send(dgramPacket)

        print("mensagem: ")
        message = readLine()!!
      }
    }.start()

    val buffer: ByteArray = ByteArray(1024)

    val packet: DatagramPacket = DatagramPacket(buffer, buffer.size)
    dgramSocket.receive(packet)

    val receivedMessage: String = buffer.toString(Charsets.UTF_8)
    while(receivedMessage != "SAIR") {
      println("mensagem recebida: ${buffer.toString(Charsets.UTF_8)}")
      dgramSocket.receive(packet)
    }

  } catch (e: Throwable) {
    println("cu de anu")
  }
}

