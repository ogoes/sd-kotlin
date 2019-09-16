package segundo.client

import java.io.File
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.math.*

fun uploadFile(dgramSocket: DatagramSocket, filename: String, serverIP: String, serverPort: Int) {
  try {
    val hostAddress: InetAddress = InetAddress.getByName(serverIP)
    val pathName: String = "./.client"

    val request: MutableList<Byte> = mutableListOf()

    val file = File(pathName + filename)
    val sizeFile = file.length()

    val numberOfPackets: Double = ceil((sizeFile / 1024).toDouble())

    // file size -- 1 byte
    request.add(sizeFile.toByte())

    // file in bytes
    val fileInBytes = file.readBytes()

    // falta criar os pentagrama

    // var packetDatagram = DatagramPacket(buf: ByteArray!, length: Int, address: InetAddress!, port: Int)
    // dgramSocket.send(packetDatagram)
  } catch (t: Throwable) {
    println(t)
  }
}

fun main() {

  println("Bem vindo ao Servidor de Arquivos UDP")

  print("Informe o endereço do servidor de arquivos: ")
  val serverIP: String = readLine()!!

  print("Informe a porta do servidor: ")
  val serverPort: Int = (readLine()!!).toInt()

  print("Informe o nome do arquivo que você deseja fazer upload: ")
  val filename: String = readLine()!!

  var dgramSocket: DatagramSocket = DatagramSocket(serverPort)

  uploadFile(dgramSocket, filename, serverIP, serverPort)
}

