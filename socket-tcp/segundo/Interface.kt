package segundo

import java.io.InputStream
import java.io.OutputStream

interface Interface {
  fun sendMessage(outputStream: OutputStream, message: ByteArray) {
    outputStream.write(message)
    outputStream.flush()
  }

  fun receiveMessage(inputStream: InputStream, bufferSize: Int = 258): ByteArray {

    val buffer: ByteArray = ByteArray(bufferSize)
    val size: Int = inputStream.read(buffer)

    return buffer.copyOf(size)
  }
}
