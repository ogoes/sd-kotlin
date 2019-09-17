package ns


import primeiro.Interface

import java.net.InetAddress
import java.net.Socket
import java.net.SocketException

import java.io.InputStream
import java.io.OutputStream


open class Connection: Interface {
	protected var _id: Int = 0
	protected lateinit var _socket: Socket
	protected var _port: Int = 0
	protected var _nickname: String
	protected lateinit var _iattr: InetAddress


	private var _connected: Boolean
	
	constructor (id: Int) {
		_id = id


		_nickname = ""
		_connected = false
	}

	constructor (id: Int, socket: Socket) {
		_id = id
		_socket = socket
		_port = -1

    _nickname = _id.toString()
    _connected = false
	}

	fun sendMessage (message: ByteArray) {
		super.sendBinaryMessage(_socket.getOutputStream(), message)
	}

	fun receiveMessage (): ByteArray {
		return super.receiveBinaryMessage(_socket.getInputStream(), 1024 * 1024)
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
  fun getAddress (): String = _iattr.getHostAddress()

}
