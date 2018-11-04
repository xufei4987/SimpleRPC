package com.youxu.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

import com.youxu.service.HalloService;

public class Consumer {
	/**
	 * 
	 * @param interfaceClass 需要调用的接口
	 * @param host 服务提供者的IP
	 * @param port 服务提供者的端口号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T refer(final Class<T> interfaceClass,
			final String host, final int port) {
		if (interfaceClass == null)
            throw new IllegalArgumentException("Interface class == null");
        if (!interfaceClass.isInterface())
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");
        if (host == null || host.length() == 0)
            throw new IllegalArgumentException("Host == null!");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException("Invalid port " + port);
        System.out.println("Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Socket socket = new Socket(host, port);
				try {
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					try {
						oos.writeUTF(method.getName());
						oos.writeObject(method.getParameterTypes());
						oos.writeObject(args);
						ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
						try {
							Object resultObj = ois.readObject();
							if(resultObj instanceof Throwable) {
								throw (Throwable) resultObj;
							}
							return resultObj;
						}finally {
							ois.close();
						}
					} finally {
						oos.close();
					}			
				}finally {
					socket.close();
				}
			}
		});
	}
	
	public static void main(String[] args)throws Exception {
		HalloService halloService = refer(HalloService.class, "127.0.0.1", 1234);
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			String hello = halloService.hallo("world" + i);
			System.out.println(hello);
			Thread.sleep(1000);
		}
	}
}
