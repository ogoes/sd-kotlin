
/**
 * *@description neste código se encontra a função que envia um arquivo via
 * * pacotes datagrama com tamanho máximo de 1024 bytes *
 * @author Dennis Urtubia
 * @author Otávio Goes
 */

package segundo.client

import java.io.File
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.math.ceil

// Primeiro DatagramPacket = 
//  - Tamanho do arquivo (4 bytes) 
//  - Tamanho nome do arquivo (1 byte)
//  - Nome do arquivo (Tamanho nome do arquivo)

// Demais DatagramPacket's = 
// Envia os bytes do arquivo em pacotes de até 1024 bytes

/**
 * @param dgramSocket: objeto utilizado para efetuar o envio e recebimento de pacotes datagrama
 * @param filename: nome do arquivo que será feito upload
 * @param serverIP: IP do servidor de arquivos
 * @param serverPort: porta da conexão do servidor de arquivos
 */
fun uploadFile(dgramSocket: DatagramSocket, filename: String, serverIP: InetAddress, serverPort: Int) {
  try {

    val pathName: String = "./.client"
    val file = File(pathName + "/" + filename)

    val firstBuffer: MutableList<Byte> = mutableListOf()

    val sizeFile: Int = file.length().toInt()
    val sizeFilename = filename.length
    val filenameInBytes = filename.toByteArray(Charsets.UTF_8)

    // Converte o tamanho do arquivo (int) para 4 bytes
    val HEXport3: Int = sizeFile shr 24
    val HEXport2: Int = (sizeFile and 0x00ff0000) shr 16
    val HEXport1: Int = (sizeFile and 0x0000ff00) shr 8
    val HEXport0: Int = sizeFile and 0x000000ff
    firstBuffer.add(HEXport3.toByte())
    firstBuffer.add(HEXport2.toByte())
    firstBuffer.add(HEXport1.toByte())
    firstBuffer.add(HEXport0.toByte())

    // Tamanho do nome do arquivo
    firstBuffer.add(sizeFilename.toByte())

    // Nome do arquivo
    for (i in 1..sizeFilename) {
      firstBuffer.add(filenameInBytes.get(i - 1))
    }

    val firstPkt: DatagramPacket = DatagramPacket(firstBuffer.toByteArray(), firstBuffer.size, serverIP, serverPort)
    dgramSocket.send(firstPkt)

    // ----------------------------------------------------- //

    val numberOfPackets: Int = ceil((sizeFile / 1024).toDouble()).toInt()

    val fileInputStream = file.inputStream()
    val bytes = ByteArray(1024)

    for (i in 1..numberOfPackets) {
      fileInputStream.read(bytes)
      val senderPacket: DatagramPacket = DatagramPacket(bytes, bytes.size, serverIP, serverPort)
      dgramSocket.send(senderPacket)

      val receiverPacket: DatagramPacket = DatagramPacket(bytes, bytes.size, serverIP, serverPort)
      dgramSocket.receive(receiverPacket)
    }
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

