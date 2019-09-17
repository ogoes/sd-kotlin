package segundo.server

import java.io.File
import java.math.BigInteger
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.security.MessageDigest
import kotlin.math.ceil

/**
 * Função para criar a hash md5
 */
fun String.md5(): String {
  val md = MessageDigest.getInstance("MD5")
  return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

/**
 * Função que recebe o arquivo e salva em um diretório padrão do servidor
 *
 * @param dgramSocket: Socket responsável por enviar o arquivo
 * @param localStorage: Local onde o arquivo será salvo
 */
fun receiveFile(dgramSocket: DatagramSocket, localStorage: String = "./.shared") {

  try {

    val readBuffer: ByteArray = ByteArray(1024)
    val readPacket: DatagramPacket = DatagramPacket(readBuffer, readBuffer.size)

    dgramSocket.receive(readPacket)

    val writeBuffer: ByteArray = "ACK".toByteArray(Charsets.UTF_8)
    val writePacket: DatagramPacket = DatagramPacket(writeBuffer, writeBuffer.size, readPacket.getSocketAddress())

    dgramSocket.send(writePacket) // ACK

    val receivedData = readPacket.getData()

    val filenameSize = receivedData.get(0).toInt()
    val filenameBytes: MutableList<Byte> = mutableListOf()

    for (i in 1..filenameSize) {
      filenameBytes.add(receivedData.get(i))
    }

    val filename: String = filenameBytes.toByteArray().toString(Charsets.UTF_8)

    val fileSizeInit = filenameSize + 1

    val HEXfile3: Int = receivedData.get(fileSizeInit + 0).toInt() shl 24
    val HEXfile2: Int = receivedData.get(fileSizeInit + 1).toInt() shl 16
    val HEXfile1: Int = receivedData.get(fileSizeInit + 2).toInt() shl 8
    val HEXfile0: Int = receivedData.get(fileSizeInit + 3).toInt() shl 0

    val fileSize = HEXfile3 or HEXfile2 or HEXfile1 or HEXfile0

    val file = File(localStorage + "/" + filename)

    val fileOutputStream = file.outputStream()
    val fileInputStream = file.inputStream()

    val numberOfPackets: Int = ceil(fileSize / 1024.0).toInt()

    for (i in 1..numberOfPackets) {
      dgramSocket.receive(readPacket)
      fileOutputStream.write(readPacket.getData())
      dgramSocket.send(writePacket) // ACK
    }

    fileOutputStream.close()

    // val md5Buffer = ByteArray(1024)
    // val md5Packet = DatagramPacket(md5Buffer, md5Buffer.size)

    dgramSocket.receive(readPacket)

    val received = readPacket.getData()
    val md5Size = received.get(0).toInt()

    val md5Bytes: MutableList<Byte> = mutableListOf()

    for (i in 1..md5Size) {
      md5Bytes.add(received.get(i).toByte())
    }

    dgramSocket.send(writePacket) // ACK

    val fileBytes = ByteArray(fileSize)
    fileInputStream.read(fileBytes)

    fileInputStream.close()
  } catch (t: Throwable) {
    println(t)
  }
}

fun main() {

  try {
    val socket: DatagramSocket = DatagramSocket(7890)

    Thread {
      receiveFile(socket)
    }.start()
  } catch (t: Throwable) {
    println(t)
  }
}

