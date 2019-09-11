package client

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.*
import segundo.Interface

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

  fun sendMessage(message: ByteArray) {
    sendBinaryMessage(_outputStream, message)
  }

  fun receiveBinaryMessage(bufferSize: Int = 4096): ByteArray {
    return super.receiveBinaryMessage(_inputStream, bufferSize)
  }

  fun receiveByteMessage(): Byte {
    return super.receiveBinaryMessage(_inputStream)
  }

  fun finish() {
    _connected = false
    _socket.close()
    _inputStream.close()
    _outputStream.close()
  }
}

