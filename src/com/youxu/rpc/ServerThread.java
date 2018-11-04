package com.youxu.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class ServerThread extends Thread{
	
	private Socket socket;
	private Object service;
	
	public ServerThread(Socket socket, Object service) {
		super();
		this.socket = socket;
		this.service = service;
	}
	
	@Override
	public void run() {
		try {
			//从客户端获取它需要调用的方法
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			//获取方法名称
			String methodName = ois.readUTF();
			//获取方法的参数类型
			Class<?>[] parameterTypes  = (Class<?>[]) ois.readObject();
			//获取方法的参数值
			Object[] arguments = (Object[]) ois.readObject();
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			try {
				//通过反射获取方法
				Method method = service.getClass().getMethod(methodName, parameterTypes);
				//调用方法，传入参数，并获取返回值
				Object result = method.invoke(service, arguments);
				//返回调用的结果
				oos.writeObject(result);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
}
