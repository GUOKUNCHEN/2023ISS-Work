package RegionManagers;

import RegionManagers.SocketManager.ClientSocketManager;
import RegionManagers.SocketManager.MasterSocketManager;
import miniSQL.API;
import miniSQL.Interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.swing.plaf.synth.Region;

// 整个Region Server的manager
public class RegionManager {
	public static String FTP = "192.168.43.125";
	public static int FTP_Port=21;
	public static String ZK_HOST="192.168.43.125:2181";
	public static String MasterUrl="192.168.43.125";
	public static int MasterPort=12345;
	private DataBaseManager dataBaseManager;
	private ClientSocketManager clientSocketManager;
	private MasterSocketManager masterSocketManager;
	private zkServiceManager zkServiceManager;

	private final int PORT = 22222;

	public RegionManager() throws IOException, InterruptedException {
		try {
			HostCheck();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataBaseManager = new DataBaseManager();
		zkServiceManager = new zkServiceManager();
		masterSocketManager = new MasterSocketManager(MasterUrl,MasterPort);
		masterSocketManager.sendTableInfoToMaster(dataBaseManager.getMetaInfo());
		clientSocketManager = new ClientSocketManager(PORT, masterSocketManager);
		Thread centerThread = new Thread(clientSocketManager);
		centerThread.start();
		// 测试代码，测试region和master的沟通情况
		// masterSocketManager.sendToMaster(dataBaseManager.getMetaInfo());
		// Thread masterThread = new Thread(masterSocketManager);
		// masterThread.start();
	}

	public void run() throws Exception {
		// 线程1：在应用启动的时候自动将本机的Host信息注册到ZooKeeper，然后阻塞，直到应用退出的时候也同时退出

		API.initial();
		Thread zkServiceThread = new Thread(zkServiceManager);
		zkServiceThread.start();
		Thread MasterSocketThread = new Thread(masterSocketManager);
		MasterSocketThread.start();

		System.out.println("从节点开始运行！");

	}

	private void HostCheck() throws Exception {
		String Choose = "y";
		Scanner scanner = new Scanner(System.in);
		while (Choose.equals("y")) {
			System.out.println("当前即将连接到的ZK服务器为:" + RegionManager.ZK_HOST + " 需要更换吗？(y/n)");
			try {
				while (!scanner.hasNext()) {
					// 暂停一段时间，避免过度占用 CPU 资源
					try {
						Thread.sleep(100); // 100 毫秒
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Choose = scanner.next();
				if (Choose.equals("y")) {
					scanner.nextLine();
					System.out.println("请输入更换的相同格式服务器ip:port");
					RegionManager.ZK_HOST = scanner.nextLine();
					System.out.println(RegionManager.ZK_HOST);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Choose = "y";
		while (Choose.equals("y")) {
			System.out.println("当前即将连接到的Master服务器为:" + RegionManager.MasterUrl + " 需要更换吗？(y/n)");

			try {
				while (!scanner.hasNext()) {
					// 暂停一段时间，避免过度占用 CPU 资源
					try {
						Thread.sleep(100); // 100 毫秒
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Choose = scanner.next();
				if (Choose.equals("y")) {
					scanner.nextLine();
					System.out.println("请输入更换的相同格式服务器ip");
					RegionManager.MasterUrl = scanner.nextLine();
					System.out.println(RegionManager.MasterUrl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Choose = "y";
		while (Choose.equals("y")) {
			System.out.println("当前即将连接到的FTP服务器为(ip:port):" + RegionManager.FTP +":"+RegionManager.FTP_Port+ " 需要更换吗？(y/n)");

			try {
				while (!scanner.hasNext()) {
					// 暂停一段时间，避免过度占用 CPU 资源
					try {
						Thread.sleep(100); // 100 毫秒
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Choose = scanner.next();
				if (Choose.equals("y")) {
					scanner.nextLine();
					System.out.println("请输入更换的相同格式服务器ip");
					RegionManager.FTP = scanner.nextLine();
					System.out.println(RegionManager.FTP);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		scanner.close();
	}
}
