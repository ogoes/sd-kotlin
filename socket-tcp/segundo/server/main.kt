// implementação das funções para enviar para o client
package segundo.server

import java.io.File
import java.util.*
import segundo.SocketConnection

fun getFileName(message: ByteArray): String {
  val fileNameSize: Int = message.get(3).toInt()

  val fileNameInByte: ByteArray = ByteArray(fileNameSize)

  for (i in 1..fileNameSize) {
    fileNameInByte.set(i, message.get(3 + i))
  }

  val charset = Charsets.UTF_8
  val fileName: String = fileNameInByte.toString(charset)
  println(fileName)
  return fileName
}

fun addFile(request: ByteArray, client: SocketConnection, localStorage: String = "./.shared/") {
  try {
    val fileSize = request.get(2 + request.get(3).toInt() + 1).toInt()
    val filename: String = getFileName(request)

    if (fileSize > 0) {
      val file = File(localStorage + filename)
      val fileOutputStream = file.outputStream()

      for (i in 1..fileSize) {
        val byte = client.receiveMessage()

        fileOutputStream.write(byte)
      }

      fileOutputStream.close()
    }

    request.set(2, 1.toByte())
    client.sendMessage(request)
  } catch (t: Throwable) {
    request.set(2, 2.toByte())
    client.sendMessage(request)
  } finally {
    client.finish()
  }
}

fun deleteFile(request: ByteArray, response: MutableList<Byte>, client: SocketConnection, localStorage: String) {
  try {
    val fileName: String = getFileName(request)
    println(fileName)
    val file = File(localStorage + "/" + fileName)

    file.delete()

    response.add(2, 1.toByte()) // SUCESSO

    client.sendMessage(response.toByteArray())
  } catch (t: Throwable) {
    response.add(2, 2.toByte())
  }
  client.finish()
}

fun getFileList(request: ByteArray, response: MutableList<Byte>, client: SocketConnection, localStorage: String = "./.shared/") {
  val files = File(localStorage).list()
  val numberOfFiles = files.size.toByte()

  response.add(2, numberOfFiles) // adicionando na resposta número de arquivos no diretório

  client.sendMessage(response.toByteArray())

  files.forEach {
    val filenameSize = it.length
    response.set(response.lastIndex + 1, filenameSize.toByte())

    val nameInByte = it.toByteArray(Charsets.UTF_8)

    for (i in 1..filenameSize) {
      response.set(response.lastIndex + 1, nameInByte.get(i))
    }
  }
}

// fun getFile(request: ByteArray, client: SocketConnection, localStorage: String = "./.shared/") {

// }

fun clientHandler(client: SocketConnection, localStorage: String) {

  try {
    var message = client.receiveMessage(1024)
    var command = message.get(1).toInt()
    println(command)
    var response: MutableList <Byte> = ArrayList()

    response.add(0, 2.toByte())

    while (true) {
      if (command == 1) {
        addFile(message, client)
      } else if (command == 2) {

        response.add(1, 2.toByte())
        deleteFile(message, response, client, localStorage)
        break
      }
      // else if (command == 3) {

      //   response.add(1, 3.toByte())
      //   getFileList(message, client)

      // } 
      // else if (command == 4) {

      //   response.add(1, 4.toByte())
      //   getFile(message, client)

      // }

      message = client.receiveMessage()
      command = message.get(1).toInt()
    }
  } catch (t: Throwable) {
    println(t)
    println("____________________________________")
  }

  client.finish()
}

fun main(args: Array<String>) {

  println(args[0])
  println(args[1])

  var server: ServerSocket = ServerSocket(args[1].toInt())

  while (true) {

    var client: SocketConnection = server.acceptConnection()

    var handlerThread = Thread {
      clientHandler(client, args[0])
    }
    handlerThread.start()
  }
}

