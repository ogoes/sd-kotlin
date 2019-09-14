package primeiro.ns


import primeiro.Interface

import java.io.IOException
import java.net.InetAddress
import java.io.InputStream
import java.io.OutputStream
import java.util.*

import java.net.Socket


class Connection: Interface {
	private var _id: Int
	private var _socket: Socket
	private var _port: Long
	private var _nickname: String
	private var _iattr: InetAddress

	private var _inputStream: InputStream
  private var _outputStream: OutputStream
	
	constructor (id: Int, nickname: String, socket: Socket, iattr: InetAddress,  port: Long) {
		_id = id
		_socket = socket
		_port = port
		_nickname = nickname
		_iattr = iattr

		_inputStream = socket.getInputStream()
		_outputStream = socket.getOutputStream()
	}

	fun sendMessage (message: ByteArray) {
		super.sendBinaryMessage(_outputStream, message)
	}

	fun receiveMessage (): ByteArray {
		return super.receiveBinaryMessage(_inputStream, )
	}
	
}

class NameServer:  Interface {

	

  private var _peers: MutableList<> = mutableListOf()
}
