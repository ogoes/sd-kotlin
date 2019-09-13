/**
 * * @description: neste código se encontram as implementações das funções que enviam as requisições para o servidor
 * @author Otávio Goes
 * @author Dennis Urtubia
 */

package client

import java.io.File
import java.net.Socket
import java.net.SocketException
import java.util.*
import primeiro.SocketConnection

/**
 * @param loaded este parâmetro informa quanto o arquivo já foi baixado
 * @param size tamanho total do arquivo da transferência
 * @return
 */
fun reloadFileDownloadProgressBar(loaded: Int, size: Int) {
  // println(" [          ] 99%")

    val porcent: Int = ((loaded.toDouble() / size.toDouble()) * 100.0).toInt()

    val spaces = porcent / 10

    print("\u001B[s")

    print(" |\u001B[47m")
    for (i in 1..(spaces)) {
      print(" ")
    }
    print("\u001B[0m")
    for (i in 1..(10 - (spaces))) {
      print(" ")
    }
    print("| ")

    print(porcent)
    print("%")
    print("\u001B[u")
}

/**
 * *@description efetua a requisição para receber a data/hora do sistema
 * @param server objeto utilizado para efetuar o envio e recebimento de mensagens
 */
fun receiveTimeDate(server: SocketConnection, type: String) {
  server.sendMessage(type)

  var response: String = server.receiveTextMessage()

  println(response)
}

/**
 * *@description efetua a requisição para a exibição de arquivos da pasta definida por padrão
 * @param server objeto utilizado para efetuar o envio e recebimento de mensagens
 */
fun showFiles(server: SocketConnection) {
  val filesCount: Int = server.receiveIntegerMessage()

  server.sendMessage("ACK")

  var file: String

  for (i in 1..filesCount) {
    file = server.receiveTextMessage()
    server.sendMessage("ACK")
    println(file)
  }
}

/**
 * *@description efetua a requisição de download do arquivo solicitado pelo cliente
 * @param server objeto utilizado para efetuar a troca de mensagens
 * @param filename parâmetro utilizado para enviar ao servidor qual arquivo deseja fazer download
 * @param localStorage diretório para armazenamento do arquivo baixado
 */
fun receiveFile(server: SocketConnection, filename: String, localStorage: String = "./.client/") {

  server.sendMessage("DOWN $filename")

  val fileSize = server.receiveIntegerMessage()

  server.sendMessage("ACK")

  if (fileSize > 0) {
    val file = File(localStorage + filename)
    val fileOutputStream = file.outputStream()

    for (i in 1..fileSize) {
      val byte = server.receiveBinaryMessage()

      reloadFileDownloadProgressBar(i, fileSize)

      fileOutputStream.write(byte)

      server.sendMessage("ACK")
    }
    fileOutputStream.close()
    print("\n")
  } else {
    println("Arquivo não existe")
  }
}

/**
 * *@description esta função é responsável pela identificação do comando solicitado pelo usuário e
 * * a chamada da respectiva função *
 */
fun interation(socket: SocketConnection) {

  val timeRegex = Regex("time", RegexOption.IGNORE_CASE)
  val dateRegex = Regex("date", RegexOption.IGNORE_CASE)
  val filesRegex = Regex("files", RegexOption.IGNORE_CASE)
  val downRegex = Regex("down", RegexOption.IGNORE_CASE)
  val exitRegex = Regex("exit", RegexOption.IGNORE_CASE)

  print("PROMPT:\n\n\\> ")
  var message = readLine()!!

  while (exitRegex.matches(message) != true) {

    if (timeRegex.matches(message)) {
      receiveTimeDate(socket, "TIME")
    } else if (dateRegex.matches(message)) {
      receiveTimeDate(socket, "DATE")
    } else if (filesRegex.matches(message)) {

      socket.sendMessage("FILES")
      showFiles(socket)
    } else if (downRegex.containsMatchIn(message)) {

      val fileRegex = Regex("\\s[\\w\\d\\.]+")

      val file = fileRegex.find(message)!!

      try {
        receiveFile(socket, file.value.substring(1))
      } catch (t: Throwable) {
        println(t)
      }
    }

    print("\\> ")
    message = readLine()!!
  }

  socket.sendMessage("EXIT")
}

/**
 * *@description inicia a conexão do client com o socket e executa o método para a troca
 * * de mensagens a partir do comando solicitado pelo cliente *
 * @param args[0] host pelo qual o client é executado
 * @param args[1] porta pelo qual o client é executado
 */
fun main(args: Array<String>) {

  println("Connecting ${args[0]}:${args[1]}")

    try {
      val socket = Socket(args[0], args[1].toInt())

      val server = SocketConnection(0, socket)

      interation(server)

      server.finish()
    } catch (e: SocketException) {
      println("Erro")
    } catch (t: Throwable) {
      println("Finalizando")
    }
}

