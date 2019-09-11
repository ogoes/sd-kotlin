package primeiro

import java.io.InputStream
import java.io.OutputStream
import java.io.DataOutputStream
import java.io.DataInputStream

import java.nio.charset.Charset


interface Interface {
  
  fun sendTextMessage (outputStream: OutputStream, message: String) {

    val bufferedMessage: ByteArray = message.toByteArray(Charsets.UTF_8)
    outputStream.write(bufferedMessage)
    outputStream.flush()
  } 

  fun receiveTextMessage (inputStream: InputStream, bufferSize: Int = 4096): String {

    val buffer: ByteArray = ByteArray(bufferSize)
    val size: Int = inputStream.read(buffer)

    return buffer.copyOf(size).toString(Charsets.UTF_8)
  }


  fun sendBinaryMessage (outputStream: OutputStream, message: ByteArray) {
    outputStream.write(message)
    outputStream.flush()
  }

  fun receiveBinaryMessage (inputStream: InputStream, bufferSize: Int = 32): ByteArray {
    
    val buffer: ByteArray = ByteArray(bufferSize)
    val size: Int = inputStream.read(buffer)

    return buffer.copyOf(size)
  }

  fun sendBinaryMessage (outputStream: OutputStream, message: Byte) {
    val list = listOf <Byte> (message)

    outputStream.write(list.toByteArray())
    outputStream.flush()
  }


  fun sendIntegerMessage (outputStream: OutputStream, message: Int) {

    val output = DataOutputStream(outputStream)
    output.writeInt(message)
    output.flush()
  }

  fun receiveIntegerMessage (inputStream: InputStream): Int {
    return DataInputStream(inputStream).readInt()
  }

}
