
/**
 * *@description neste código se encontra a função que envia um arquivo via
 * * pacotes datagrama com tamanho máximo de 1024 bytes *
 * @author Dennis Urtubia
 * @author Otávio Goes
 */

package segundo.client

import java.io.File
import java.math.BigInteger
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.security.MessageDigest
import kotlin.math.ceil

/**
 * Função para criar a hash md5
 */
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

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
fun uploadFile(dgramSocket: DatagramSocket, filePath: String, serverIP: InetAddress, serverPort: Int) {
  try {

    val file = File(filePath)

    var last: Int = 0

    for (i in 1..filePath.length) {

      if (filePath.get(i - 1) == '/') last = i - 1
    }
    val filename: String = filePath.substring(last + 1)

    val firstBuffer: MutableList<Byte> = mutableListOf()

    val sizeFile: Int = file.length().toInt()
    val filenameInBytes = filename.toByteArray(Charsets.UTF_8)
    val sizeFilename = filenameInBytes.size

    // Tamanho do nome do arquivo
    firstBuffer.add(sizeFilename.toByte())

    // Nome do arquivo
    for (i in 1..sizeFilename) {
      firstBuffer.add(filenameInBytes.get(i - 1))
    }

    // Converte o tamanho do arquivo (int) para 4 bytes
    val HEXport3: Int = sizeFile shr 24
    val HEXport2: Int = (sizeFile and 0x00ff0000) shr 16
    val HEXport1: Int = (sizeFile and 0x0000ff00) shr 8
    val HEXport0: Int = sizeFile and 0x000000ff

    firstBuffer.add(HEXport3.toByte())
    firstBuffer.add(HEXport2.toByte())
    firstBuffer.add(HEXport1.toByte())
    firstBuffer.add(HEXport0.toByte())

    val firstBytes = firstBuffer.toByteArray()

    val firstPkt: DatagramPacket = DatagramPacket(firstBytes, firstBytes.size, serverIP, serverPort)
    dgramSocket.send(firstPkt)

    val ackBuffer: ByteArray = ByteArray(4)
    val dgramPacket: DatagramPacket = DatagramPacket(ackBuffer, ackBuffer.size)
    dgramSocket.receive(dgramPacket) // ACK

    // ----------------------------------------------------- //

    val numberOfPackets: Int = ceil(sizeFile / 1024.0).toInt()

    val fileInputStream = file.inputStream()
    val bytes = ByteArray(1024)

    for (i in 1..numberOfPackets) {
      fileInputStream.read(bytes)
      val senderPacket: DatagramPacket = DatagramPacket(bytes, bytes.size, serverIP, serverPort)
      dgramSocket.send(senderPacket)

      dgramSocket.receive(dgramPacket)
    }

    
    // ----------------------------------------------------- //
    
    val fileBytes = ByteArray(sizeFile)
    fileInputStream.read(fileBytes)
    
    val md5String: String = fileBytes.toString().md5()
    val md5Size = md5String.length
    
    val md5Bytes: MutableList<Byte> = mutableListOf<Byte>()
    
    md5Bytes.add(md5Size.toByte())
    
    for (i in 1..md5Size) {
      md5Bytes.add(md5String.get(i - 1).toByte())
    }
    
    val md5Buffer = md5Bytes.toByteArray()
    val md5Packet: DatagramPacket = DatagramPacket(md5Buffer, md5Buffer.size, serverIP, serverPort)
    dgramSocket.send(md5Packet)
    // dgramSocket.receive(dgramPacket)


    fileInputStream.close()
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

  var dgramSocket: DatagramSocket = DatagramSocket()

  uploadFile(dgramSocket, filename, InetAddress.getByName(serverIP), serverPort)
}

