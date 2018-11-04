package com.youxu.rpc;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import com.youxu.service.HalloService;
import com.youxu.service.HalloServiceImpl;

public class Provider {
	@SuppressWarnings("resource")
	public static void export(final Object service, int port) throws Exception{
		//服务校验
		if(service == null) {
			throw new IllegalArgumentException("service must not be null");
		}
		//端口校验
		if(port <= 0 || port >= 65535) {
			throw new IllegalArgumentException("Invalid port:" + port + "a valid port must between 0 and 65535");
		}
		//向操作系统注册服务
		ServerSocket serverSocket = new ServerSocket(port);
		//循环启动监听
		for(;;) {			
			Socket socket = serverSocket.accept();
			SocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
			System.out.println("客户端：" + remoteSocketAddress + "连接服务器");
			//开启单独的线程处理服务的调用
			new ServerThread(socket, service).start();
		}
	}
    public static void main(String[] args) throws Exception {
        HalloService halloService = new HalloServiceImpl();
        export(halloService, 1234);
    }

}
