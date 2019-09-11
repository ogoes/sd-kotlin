package segundo

import java.io.InputStream
import java.io.OutputStream

class Interface {
  fun sendBinaryMessage(outputStream: OutputStream, message: ByteArray) {
    outputStream.write(message)
    outputStream.flush()
  }

  fun receiveBinaryMessage(inputStream: InputStream, bufferSize: Int = 32): ByteArray {

    val buffer: ByteArray = ByteArray(bufferSize)
    val size: Int = inputStream.read(buffer)

    return buffer.copyOf(size)
  }
}

