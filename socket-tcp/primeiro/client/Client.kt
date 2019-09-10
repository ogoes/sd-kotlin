package client

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.*
import primeiro.Interface

class Client : Interface {

  private var _socket: Socket
  private var _inputStream: InputStream
  private var _outputStream: OutputStream
  private var _id: Int
  private var _connected: Boolean

  constructor (id: Int, socket: Socket) {
    _id = id
    _socket = socket
    _inputStream = _socket.getInputStream()
    _outputStream = _socket.getOutputStream()
    _connected = true
  }

  fun sendMessage(message: String) {
    sendTextMessage(_outputStream, message)
  }

  fun sendMessage(message: ByteArray) {
    sendBinaryMessage(_outputStream, message)
  }

  fun sendMessage(message: Int) {
    sendIntegerMessage(_outputStream, message)
  }

  fun receiveTextMessage(bufferSize: Int = 4096): String {
    return super.receiveTextMessage(_inputStream, bufferSize)
  }

  fun receiveBinaryMessage(bufferSize: Int = 4096): ByteArray {
    return super.receiveBinaryMessage(_inputStream, bufferSize)
  }

  fun receiveByteMessage(): Byte {
    return super.receiveBinaryMessage(_inputStream)
  }

  fun receiveIntegerMessage(): Int {
    return super.receiveIntegerMessage(_inputStream)
  }

  fun finish() {
    _connected = false
    _socket.close()
    _inputStream.close()
    _outputStream.close()
  }
}
