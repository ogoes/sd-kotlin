/**
 * * @description: neste código se encontram as implementações das funções solicitadas pelo client
 * @author Otávio Goes
 * @author Dennis Urtubia
 */

package segundo.server

import java.io.File
import java.util.*
import segundo.SocketConnection

/**
 * *@description cria o header de resposta com os campos padrões definidos pelo protocolo
 */
fun createResponseHeader(messageType: Int, commandType: Int, statusCode: Int): ByteArray {
  val byteArray: ByteArray = ByteArray(3)

  byteArray.set(0, messageType.toByte())
  byteArray.set(1, commandType.toByte())
  byteArray.set(2, statusCode.toByte())

  return byteArray
}

fun getFilename(message: ByteArray): String {
  val fileNameSize: Int = message.get(2).toInt()

  val fileNameInByte: ByteArray = ByteArray(fileNameSize)

  for (i in 1..fileNameSize) {
    fileNameInByte.set(i - 1, message.get(2 + i))
  }

  val fileName: String = fileNameInByte.toString(Charsets.UTF_8)

  return fileName
}

fun addFile(client: SocketConnection, request: ByteArray, localStorage: String = "./.shared") {
  try {

    val filename: String = getFilename(request)
    val filenameSize: Int = request.get(2).toInt()

    var requestInit: Int = filenameSize + 3

    val HEXbigEndianFileSize3: Int = (request.get(requestInit + 0).toInt() shl 24) // 0xff000000
    val HEXbigEndianFileSize2: Int = (request.get(requestInit + 1).toInt() shl 16) // 0x00ff0000
    val HEXbigEndianFileSize1: Int = (request.get(requestInit + 2).toInt() shl 8) // 0x0000ff00
    val HEXbigEndianFileSize0: Int = (request.get(requestInit + 3).toInt() shl 0) // 0x000000ff

    val fileSize: Int = HEXbigEndianFileSize3 or HEXbigEndianFileSize2 or HEXbigEndianFileSize1 or HEXbigEndianFileSize0

    val writer: ByteArray = ByteArray(fileSize)

    for (i in 1..fileSize) {
      writer.set(i - 1, request.get(requestInit + 2 + i))
    }

    val file = File(localStorage + "/" + filename)

    val fileOutputStream = file.outputStream()

    fileOutputStream.write(writer)

    fileOutputStream.close()

    client.sendMessage(createResponseHeader(2, 1, 1)) // SUCCESS
  } catch (t: Throwable) {
    client.sendMessage(createResponseHeader(2, 1, 2)) // ERROR
  }

  client.finish()
}

fun deleteFile(client: SocketConnection, filename: String, localStorage: String = "./.shared") {
  try {
    val file = File(localStorage + "/" + filename)

    if (file.exists()) {
      file.delete()

      client.sendMessage(createResponseHeader(2, 2, 1)) // SUCCESS
    } else {
      client.sendMessage(createResponseHeader(2, 2, 2)) // ERROR
    }
  } catch (t: Throwable) {
    client.sendMessage(createResponseHeader(2, 2, 2)) // ERROR
  }
  client.finish()
}

fun getFileList(client: SocketConnection, localStorage: String = "./.shared") {

  try {
    val files = File(localStorage).list()
    val fileCounter = files.size

    val HEXbigEndianFileCounter1: Int = fileCounter shr 8 // 0xff00
    val HEXbigEndianFileCounter0: Int = fileCounter and 0x00ff

    val response = createResponseHeader(2, 3, 1) // SUCCESS

    val filesBytes: MutableList<Byte> = mutableListOf<Byte>(
      response.get(0),
      response.get(1),
      response.get(2),
      HEXbigEndianFileCounter1.toByte(),
      HEXbigEndianFileCounter0.toByte()
    )

    files.forEach {
      val filenameSize = it.length
      filesBytes.add(filenameSize.toByte())

      val filenameByteArray = it.toByteArray()

      for (i in 1..filenameSize) {
        filesBytes.add(filenameByteArray.get(i - 1))
      }
    }

    client.sendMessage(filesBytes.toByteArray())
  } catch (t: Throwable) {
    client.sendMessage(createResponseHeader(2, 3, 2)) // ERROR
  }

  client.finish()
}

fun getFile(client: SocketConnection, filename: String, localStorage: String = "./.shared") {
  try {

    val file = File(localStorage + "/" + filename)

    if (file.exists()) {
      val fileSize = file.length().toInt()

      val response = createResponseHeader(2, 4, 1)

      val writer: ByteArray = ByteArray(fileSize + 4 + response.size)

      val HEXbigEndianFileSize3: Int = fileSize shr 24 // 0xff000000
      val HEXbigEndianFileSize2: Int = (fileSize and 0x00ff0000) shr 16 // 0x00ff0000
      val HEXbigEndianFileSize1: Int = (fileSize and 0x0000ff00) shr 8 // 0x0000ff00
      val HEXbigEndianFileSize0: Int = fileSize and 0x000000ff // 0x000000ff

      writer.set(0, response.get(0))
      writer.set(1, response.get(1))
      writer.set(2, response.get(2))

      writer.set(3, HEXbigEndianFileSize3.toByte())
      writer.set(4, HEXbigEndianFileSize2.toByte())
      writer.set(5, HEXbigEndianFileSize1.toByte())
      writer.set(6, HEXbigEndianFileSize0.toByte())

      val writerInit = 7

      val fileInputStream = file.inputStream()
      val byte = ByteArray(fileSize)
      fileInputStream.read(byte)

      for (i in 1..fileSize) {
        writer.set(writerInit + i - 1, byte.get(i - 1))
      }

      fileInputStream.close()

      client.sendMessage(writer)
    } else {
      client.sendMessage(createResponseHeader(2, 4, 2)) // ERROR
    }

    client.sendMessage(createResponseHeader(2, 4, 1)) // SUCCESS
  } catch (t: Throwable) {
    client.sendMessage(createResponseHeader(2, 4, 2)) // ERROR
  }

  client.finish()
}

fun clientHandler(client: SocketConnection, localStorage: String, requestSize: Int = 1024 * 1024) {

  try {
    val request = client.receiveMessage(requestSize)
    val command = request.get(1).toInt()

    if (command == 1) { // addFile
      addFile(client, request, localStorage)
    } else if (command == 2) { // deleteFile
      deleteFile(client, getFilename(request), localStorage)
    } else if (command == 3) { // getFileList
      getFileList(client, localStorage)
    } else if (command == 4) { // getFile
      getFile(client, getFilename(request), localStorage)
    } else { // ERROR
      client.sendMessage(createResponseHeader(0, 2, 2))
    }
  } catch (t: Throwable) {
    println(t)
    println("____________________________________")
  }

  client.finish()
}

fun main(args: Array<String>) {

  var server: ServerSocket = ServerSocket(args[1].toInt())

  while (true) {

    var client: SocketConnection = server.acceptConnection()

    var handlerThread = Thread {
      clientHandler(client, args[0])
    }
    handlerThread.start()
  }
}

