package segundo

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.*

class SocketConnection : Interface {
  private var _socket: Socket
  private var _inputStream: InputStream
  private var _outputStream: OutputStream
  private var _id: Int
  private var _connected: Boolean

  constructor (socket: Socket) {
    _id = 0
    _socket = socket
    _inputStream = _socket.getInputStream()
    _outputStream = _socket.getOutputStream()
    _connected = true
  }

  constructor (id: Int, socket: Socket) {
    _id = id
    _socket = socket
    _inputStream = _socket.getInputStream()
    _outputStream = _socket.getOutputStream()
    _connected = true
  }

  fun sendMessage(message: ByteArray) {
    super.sendMessage(_outputStream, message)
  }

  fun receiveMessage(bufferSize: Int = (1024 * 1024)): ByteArray {
    return super.receiveMessage(_inputStream, bufferSize)
  }

  fun finish() {
    _connected = false
    _socket.close()
    _inputStream.close()
    _outputStream.close()
  }
}

