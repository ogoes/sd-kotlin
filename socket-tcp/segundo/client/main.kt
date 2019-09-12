package segundo

import java.io.File
import java.net.Socket
import java.net.SocketException
import java.util.*

fun createByteArray(messageType: Int, commandIdentifier: Int, filePath: String): ByteArray {

  val fileNameMatcher = Regex("/[\\w\\d]+\\.[\\w]+").find(filePath)!!
  val fileName = fileNameMatcher.value
  val fileNameSize = fileName.length
  val fileNameByte = fileName.toByteArray(Charsets.UTF_8)

  val request: ByteArray = ByteArray(3 + fileNameSize)

  request.set(0, messageType.toByte())
  request.set(1, commandIdentifier.toByte())
  request.set(2, fileNameSize.toByte())

  for (i in 1..fileNameSize) {
    request.set(i + 2, fileNameByte.get(i))
  }

  return request
}

/**
 * @param
 */
fun addFile(host: String, port: Int, request: ByteArray, pathFile: String) {
  var socket: Socket
  var server: SocketConnection

  try {

    socket = Socket(host, port)
    server = SocketConnection(0, socket)

    val file: File = File(pathFile)

    val fileSize: Int = file.length().toInt()

    val fileInputStream = file.inputStream()
    val bytes = ByteArray(1)

    server.sendMessage(request)

    for (i in 1..fileSize) {
      fileInputStream.read(bytes)
      server.sendMessage(bytes)
    }
  } catch (t: Throwable) {
    println(t)
  } finally {
  }
}

/**
 * @param
 *
 */
fun deleteFile(host: String, port: Int, request: ByteArray) {
  val socket: Socket
  val server: SocketConnection
  try {
    socket = Socket(host, port)
    server = SocketConnection(0, socket)

    server.sendMessage(request)
  } catch (t: Throwable) {
    println(t)
  } finally {
  }
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
  } finally {
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
  } finally {
  }
}

fun interation(host: String, port: Int) {
  print("PROMPT:\n\n\\> ")
  var message = readLine()!!

  val addFileRegex = Regex("addfile", RegexOption.IGNORE_CASE)
  val deleteRegex = Regex("delete", RegexOption.IGNORE_CASE)
  val getFileListRegex = Regex("getfilelist", RegexOption.IGNORE_CASE)
  val getFileRegex = Regex("getfile", RegexOption.IGNORE_CASE)

  while (true) {
    val fileRegex = Regex("\\s[\\w\\d\\.]+")
    val pathFile = fileRegex.find(message)!!

    if (addFileRegex.matches(message)) {

      addFile(host, port, createByteArray(1, 1, pathFile.value), pathFile.value)
    } else if (deleteRegex.matches(message)) {
      deleteFile(host, port, createByteArray(1, 2, pathFile.value))
    } else if (getFileListRegex.matches(message)) {
      getFileList(host, port, createByteArray(1, 3, pathFile.value))
    } else if (getFileRegex.matches(message)) {
      getFile(host, port, createByteArray(1, 4, pathFile.value))
    }
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

