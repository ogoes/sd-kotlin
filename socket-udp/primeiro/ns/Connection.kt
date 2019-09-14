package primeiro.ns


import primeiro.Interface

import java.net.InetAddress
import java.net.Socket

import java.io.InputStream
import java.io.OutputStream


class Connection: Interface {
	private var _id: Int
	private var _socket: Socket
	private var _port: Int
	private lateinit var _nickname: String
	private lateinit var _iattr: InetAddress

	private var _inputStream: InputStream
  private var _outputStream: OutputStream
	
	constructor (id: Int = 0, socket: Socket) {
		_id = id
		_socket = socket
		_port = -1

		_inputStream = socket.getInputStream()
		_outputStream = socket.getOutputStream()
	}

	fun sendMessage (message: ByteArray) {
		super.sendBinaryMessage(_outputStream, message)
	}

	fun receiveMessage (): ByteArray {
		return super.receiveBinaryMessage(_inputStream, 1024 * 1024)
	}
	
	fun setPort (port: Int) {
		_port = port
	}
	fun getPort (): Int = _port

	fun setNickname (nick: String) {
		_nickname = nick
	}
	fun getNickname (): String = _nickname

	fun setAddress (addr: InetAddress) {
		_iattr = addr
	}
  fun getAddress (): String = _iattr.toString()

}
