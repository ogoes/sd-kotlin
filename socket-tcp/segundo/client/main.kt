/**
 * * @description: neste código se encontram as implementações das funções que enviam as requisições para o servidor
 * @author Otávio Goes
 * @author Dennis Urtubia
 */

package segundo

import java.io.File
import java.net.Socket
import java.net.SocketException
import java.util.*

fun connect(host: String, port: Int): SocketConnection {
  val socket: Socket = Socket(host, port)
  return SocketConnection(socket)
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
fun addFile(server: SocketConnection, request: ByteArray, path: String) {
  try {
    val filename = getFilename(request)

    val file: File = File(path + "/" + filename)
    val fileSize: Int = file.length().toInt()

    val writerBytes: ByteArray = ByteArray(request.size + 4 + fileSize)
    val writerInit = request.size

    // / começa a criar todo o pacote

    for (i in 1..request.size) {
      writerBytes.set(i - 1, request.get(i - 1))
    }

    val bigEndianInt3: Int = fileSize shr 24 // 0xff000000
    val bigEndianInt2: Int = (fileSize and 0x00ff0000) shr 16 // 0x00ff0000
    val bigEndianInt1: Int = (fileSize and 0x0000ff00) shr 8 // 0x0000ff00
    val bigEndianInt0: Int = fileSize and 0x000000ff // 0x000000ff

    writerBytes.set(writerInit + 0, bigEndianInt3.toByte())
    writerBytes.set(writerInit + 1, bigEndianInt2.toByte())
    writerBytes.set(writerInit + 2, bigEndianInt1.toByte())
    writerBytes.set(writerInit + 3, bigEndianInt0.toByte())

    val writerFileInit: Int = writerInit + 4

    val fileInputStream = file.inputStream()
    val byte = ByteArray(fileSize)
    fileInputStream.read(byte)
    fileInputStream.close()

    for (i in 1..fileSize) {
      writerBytes.set(writerFileInit + i - 1, byte.get(i - 1))
    }

    server.sendMessage(writerBytes)

    val response: ByteArray = server.receiveMessage(3)

    val requestStatus: Int = response.get(2).toInt()

    if (requestStatus == 1) {
      println("SUCCESS")
    } else if (requestStatus == 2) {
      println("ERROR")
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
    val requestStatus: Int = response.get(2).toInt()

    if (requestStatus == 1) {
      println("SUCCESS")
    } else if (requestStatus == 2) {
      println("ERROR")
    }
  } catch (t: Throwable) {
    println(t)
  }
  server.finish()
}

/**
 * @param
 */
fun getFileList(server: SocketConnection, request: ByteArray) {
  try {

    server.sendMessage(request)

    val response = server.receiveMessage()

    val requestStatus: Int = response.get(2).toInt()

    if (requestStatus == 1) {

      val filesCounter1HEX: Int = response.get(3).toInt()
      val filesCounter0HEX: Int = response.get(4).toInt()

      val fileCounter: Int = ((filesCounter1HEX shl 8) or filesCounter0HEX) // 0xff00 or 0x00ff == 0xffff

      var responseInit = 5

      for (i in 1..fileCounter) {
        val fileSize: Int = response.get(responseInit).toInt()

        val filenameByteArray: ByteArray = ByteArray(fileSize)

        for (j in 1..fileSize) {
          filenameByteArray.set(j - 1, response.get(responseInit + j))
        }

        val filename: String = filenameByteArray.toString(Charsets.UTF_8)

        println(filename)

        responseInit += (fileSize + 1)
      }
    } else if (requestStatus == 2) {
      println("ERROR")
    }
  } catch (t: Throwable) {
    println(t)
  }

  server.finish()
}

/**
 * @param
 * @
 */
fun getFile(server: SocketConnection, request: ByteArray, path: String, responseSize: Int = 1024 * 1024) {
  try {
    server.sendMessage(request)

    val filename = getFilename(request)


    val response = server.receiveMessage(responseSize)
    val requestStatus: Int = response.get(2).toInt()

    if (requestStatus == 1) {
      val bigEndianInt3: Int = response.get(3).toInt() shl 24 // 0xff000000
      val bigEndianInt2: Int = response.get(4).toInt() shl 16 // 0x00ff0000
      val bigEndianInt1: Int = response.get(5).toInt() shl 8 // 0x0000ff00
      val bigEndianInt0: Int = response.get(6).toInt() shl 0 // 0x000000ff

      val fileSize: Int = bigEndianInt3 or bigEndianInt2 or bigEndianInt1 or bigEndianInt0

      val file: File = File(path + "/" + filename)
      val fileOutputStream = file.outputStream()

      val bytes: ByteArray = ByteArray(fileSize)

      val responseInit = 7

      for (i in 1..fileSize) {
        bytes.set(i - 1, response.get(responseInit + i - 1))
      }

      fileOutputStream.write(bytes)
      fileOutputStream.close()
    } else if (requestStatus == 2) {
      println("ERROR")
    }
  } catch (t: Throwable) {
    println(t)
  }

  server.finish()
}

fun interation(host: String, port: Int, defaultPath: String) {
  try {
    print("PROMPT:\n\\> ")

    val addFileRegex = Regex("addfile\\s[\\w\\d\\.]+", RegexOption.IGNORE_CASE)
    val deleteRegex = Regex("delete\\s[\\w\\d\\.]+", RegexOption.IGNORE_CASE)
    val getFileListRegex = Regex("getfilelist", RegexOption.IGNORE_CASE)
    val getFileRegex = Regex("getfile\\s[\\w\\d\\.]+", RegexOption.IGNORE_CASE)
    val exitRegex = Regex("exit", RegexOption.IGNORE_CASE)
    val fileRegex = Regex("\\s[\\w\\d\\.]+")

    var fileName: String
    var message = readLine()!!
    // val server: SocketConnection = connect(host, port)

    while (exitRegex.matches(message) != true) {

      if (addFileRegex.matches(message)) {
        fileName = fileRegex.find(message)!!.value
        fileName = fileName.substring(1)

        addFile(connect(host, port), createByteArray(1, 1, fileName), defaultPath)
      } else if (deleteRegex.matches(message)) {
        fileName = fileRegex.find(message)!!.value
        fileName = fileName.substring(1)

        deleteFile(connect(host, port), createByteArray(1, 2, fileName))
      } else if (getFileListRegex.matches(message)) {
        getFileList(connect(host, port), createByteArray(1, 3, ""))
      } else if (getFileRegex.matches(message)) {
        fileName = fileRegex.find(message)!!.value
        fileName = fileName.substring(1)

        getFile(connect(host, port), createByteArray(1, 4, fileName), "./.client")
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

      interation(args[0], args[1].toInt(), "./.client")
    } catch (e: SocketException) {
      println("Erro")
    } catch (t: Throwable) {
      println("Finalizando")
    }
}

