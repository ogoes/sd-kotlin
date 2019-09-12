package segundo

import java.io.File
import java.net.Socket
import java.net.SocketException
import java.util.*

fun connect(host: String, port: Int): SocketConnection {
  val socket: Socket = Socket(host, port)
  return SocketConnection(socket)
}

fun createByteArray(messageType: Int, commandIdentifier: Int, fileName: String): ByteArray {

  val fileNameSize = fileName.length
  val fileNameByte = fileName.toByteArray(Charsets.UTF_8)

  val request: ByteArray = ByteArray(3 + fileNameSize)

  request.set(0, messageType.toByte())
  request.set(1, commandIdentifier.toByte())
  request.set(2, fileNameSize.toByte())

  for (i in 1..fileNameSize) {
    request.set(i + 2, fileNameByte.get(i - 1))
  }

  return request
}

/**
 * @param
 */
fun addFile(server: SocketConnection, request: ByteArray, pathFile: String) {
  try {
    val file: File = File(pathFile)

    val fileSize: Int = file.length().toInt()

    val fileInputStream = file.inputStream()
    val bytes = ByteArray(1)

    request.set(request.lastIndex, fileSize.toByte())

    server.sendMessage(request)

    for (i in 1..fileSize) {
      fileInputStream.read(bytes)
      server.sendMessage(bytes)
    }
  } catch (t: Throwable) {
    println(t)
  }
  server.finish()
}

/**
 * @param
 *
 */
fun deleteFile(server: SocketConnection, request: ByteArray) {
  try {
    server.sendMessage(request)

    val response: ByteArray = server.receiveMessage()
    val codeResponse: Int = response.get(2).toInt()
    println(codeResponse)
  } catch (t: Throwable) {
    println(t)
  }
  server.finish()
}

/**
 * @param
 */
fun getFileList(host: String, port: Int, request: ByteArray) {
  val socket: Socket
  val server: SocketConnection
  try {
    socket = Socket(host, port)
    server = SocketConnection(0, socket)

    server.sendMessage(request)
  } catch (t: Throwable) {
    println(t)
  }
}

/**
 * @param
 * @
 */
fun getFile(host: String, port: Int, request: ByteArray) {
  val socket: Socket
  val server: SocketConnection
  try {
    socket = Socket(host, port)
    server = SocketConnection(0, socket)

    server.sendMessage(request)
  } catch (t: Throwable) {
    println(t)
  }
}

fun interation(host: String, port: Int) {
  try {
    print("PROMPT:\n\n\\> ")
    var message = readLine()!!

    val addFileRegex = Regex("addfile", RegexOption.IGNORE_CASE)
    val deleteRegex = Regex("delete\\s[\\w\\d\\.]+", RegexOption.IGNORE_CASE)
    val getFileListRegex = Regex("getfilelist", RegexOption.IGNORE_CASE)
    val getFileRegex = Regex("getfile", RegexOption.IGNORE_CASE)

    val server: SocketConnection = connect(host, port)

    while (true) {
      val fileRegex = Regex("\\s[\\w\\d\\.]+")
      var fileName = fileRegex.find(message)!!.value
      fileName = fileName.substring(1)

      if (addFileRegex.matches(message)) {

        addFile(server, createByteArray(1, 1, fileName), fileName)
      } else if (deleteRegex.matches(message)) {
        deleteFile(server, createByteArray(1, 2, fileName))
      } else if (getFileListRegex.matches(message)) {
        getFileList(host, port, createByteArray(1, 3, fileName))
      } else if (getFileRegex.matches(message)) {
        getFile(host, port, createByteArray(1, 4, fileName))
      }

      print("\\> ")
      message = readLine()!!
    }
  } catch (t: Throwable) {
    println(t)
  }
}

fun main(args: Array<String>) {

  println("Connecting ${args[0]}:${args[1]}")

    try {

      interation(args[0], args[1].toInt())
    } catch (e: SocketException) {
      println("Erro")
    } catch (t: Throwable) {
      println("Finalizando")
    }
}

