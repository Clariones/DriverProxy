package org.skynet.bgby.listeningserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.UdpData;

public abstract class UdpListenerService {
	protected final String TAG;
	protected String listeningAddress;
	// protected MulticastSocket listeningSocket;
	protected DatagramSocket listeningSocket;
	protected MainLoopThread mainThread;
	protected boolean damon;
	protected boolean needToStop;
	protected Object sendLock;
	protected Object recvLock;
	
	public UdpListenerService() {
		TAG = this.getClass().getName();
		sendLock = new Object();
		recvLock = new Object();
	}

	public boolean isNeedToStop() {
		return needToStop;
	}

	public void setNeedToStop(boolean needToStop) {
		this.needToStop = needToStop;
	}

	public boolean isDamon() {
		return damon;
	}

	public void setDamon(boolean damon) {
		this.damon = damon;
	}

	public String getListeningAddress() {
		return listeningAddress;
	}

	public void setListeningAddress(String listeningAddress) {
		this.listeningAddress = listeningAddress;
	}

	public int getListeningPort() {
		return listeningPort;
	}

	public void setListeningPort(int listeningPort) {
		this.listeningPort = listeningPort;
	}

	public boolean isHasStarted() {
		return hasStarted;
	}

	public void setHasStarted(boolean hasStarted) {
		this.hasStarted = hasStarted;
	}

	protected int listeningPort;
	protected boolean hasStarted = false;
	protected ExecutorService threadPool;
	protected DatagramSocket sendingSocket;

	public void start() throws ListeningServerException {
		if (wasStarted()) {
			throw new ListeningServerException("Already stated");
		}
		// create socket
		try {
			createUdpSocket();
		} catch (Exception e) {
			throw new ListeningServerException("Failed create multicast socket", e);
		}
		// create thread pool to execute tasks
		createThreadPool();
		// then start the work-loop
		startLoop();
	}

	protected abstract void createUdpSocket() throws IOException;

	protected void createThreadPool() {
		this.threadPool = Executors.newFixedThreadPool(5);
	}

	protected void startLoop() {
		mainThread = new MainLoopThread();
		mainThread.setDaemon(isDamon());
		mainThread.setName("UDP Listener");
		mainThread.start();
	}

	

	protected void testFoo(InetAddress ip) {
		try {
			ip = InetAddress.getByName("192.168.2.105");

			System.out.println(ip);
			NetworkInterface ni = NetworkInterface.getByInetAddress(ip);
			List<InterfaceAddress> list = ni.getInterfaceAddresses();
			if (list.size() > 0) {
				int mask = list.get(0).getNetworkPrefixLength(); // 子网掩码的二进制1的个数
				StringBuilder maskStr = new StringBuilder();
				int[] maskIp = new int[4];
				StringBuilder broadIp = new StringBuilder();
				for (int i = 0; i < maskIp.length; i++) {
					maskIp[i] = (mask >= 8) ? 255 : (mask > 0 ? (mask & 0xff) : 0);
					mask -= 8;
					maskStr.append(maskIp[i]);
					broadIp.append((ip.getAddress()[i] | ~maskIp[i]) & 0xFF);
					if (i < maskIp.length - 1) {
						maskStr.append(".");
						broadIp.append(".");
					}
				}
				System.out.println(maskStr);
				System.out.println(broadIp);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean wasStarted() {
		return isHasStarted();
	}

	public boolean isAlive() {
		return wasStarted() && !listeningSocket.isClosed() && mainThread.isAlive();
	}

	public void stop() {
		if (sendingSocket != null) {
			sendingSocket.close();
		}
		if (listeningSocket != null) {
			listeningSocket.close();
		}
		if (!mainThread.isAlive()) {
			return;
		}
		setNeedToStop(true);
		mainThread.interrupt();
		threadPool.shutdownNow();

	}

	public void processDataPacket(DatagramPacket dp) {
		UdpData message = new UdpData();
		message.setFrom(dp.getSocketAddress());
		byte[] data = Arrays.copyOfRange(dp.getData(), dp.getOffset(), dp.getOffset() + dp.getLength());
		message.setData(data);
		threadPool.execute(new UdpMsgProcessTask(message));
	}

	public class UdpMsgProcessTask implements Runnable {
		protected UdpData message;

		public UdpMsgProcessTask(UdpData message) {
			this.message = message;
		}

		@Override
		public void run() {
			UdpData response = serve(message);
			if (response != null) {
				sendUdpMessage(response);
			}
		}

	}

	public class MainLoopThread extends Thread {
		public void run() {
			setHasStarted(true);
			byte[] buffer = new byte[1024];
			DriverUtils.log(Level.INFO, TAG,
					"UDP listening service started at " + listeningSocket.getLocalSocketAddress());
			while (!isNeedToStop() && !listeningSocket.isClosed()) {
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length); // 2.����һ��ָ����������С���鲥��ַ�Ͷ˿ڵ�DatagramPacket�鲥���ݰ�����
				try {
					listeningSocket.receive(dp);
					DriverUtils.log(Level.SEVERE, TAG,
							"Multicast listening service got message: " + dp.getSocketAddress());
					processDataPacket(dp);

				} catch (IOException e) {
					e.printStackTrace();
					DriverUtils.log(Level.SEVERE, TAG, "Multicast listening service exception: " + e.getMessage());
				}
			}
		}
	}

	public UdpData serve(UdpData message) {
		// You must override this method to handle your message.
		// by default, nothing do here
		DriverUtils.log(Level.INFO, TAG, new String(message.getData()));
		return null;
	}

	public void sendUdpMessage(UdpData response) {
		if (this.sendingSocket == null || sendingSocket.isClosed()) {
			DriverUtils.log(Level.SEVERE, TAG, "Send message when socket not ready");
			return;
		}
		byte[] data = response.getData();
		if (data == null || data.length == 0) {
			DriverUtils.log(Level.SEVERE, TAG, "Cannot send empty message");
			return;
		}
		
		if (response.getSocketAddress() == null) {
			DriverUtils.log(Level.SEVERE, TAG, "Cannot send message without address");
			return;
		}

		synchronized (sendLock) {
			try {
				DatagramPacket dp = new DatagramPacket(data, data.length, response.getSocketAddress());

				// listeningSocket.send(dp);
				sendingSocket.send(dp);
				DriverUtils.log(Level.FINE, TAG, "Send UDP to " + dp.getSocketAddress());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				DriverUtils.log(Level.SEVERE, TAG, "Exception when send message");
			}
		}
	}
}
