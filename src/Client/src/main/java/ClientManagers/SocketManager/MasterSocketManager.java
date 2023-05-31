package ClientManagers.SocketManager;

import ClientManagers.ClientManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MasterSocketManager {

    private Socket socket = null;
    private BufferedReader input = null;
    private PrintWriter output = null;
    private boolean isRunning = false;
    private Thread infoListener;

    private ClientManager clientManager;

    // 服务器的IP和端口号
    private String master = "10.192.190.200";
    private int PORT = 12345;

    // 使用map来存储需要处理的表名-sql语句的对应关系
    Map<String, String> commandMap = new HashMap<>();

    public MasterSocketManager(ClientManager clientManager) throws IOException {
		master=ClientManager.MasterUrl;
		PORT = ClientManager.MasterPort;
		Scanner scanner = new Scanner(System.in);
		System.out.println("即将连接的Master服务器IP为:"+this.master+" port:"+this.PORT+ "需要更换请在ClientManager中修改");
		// String Choose = "y";
		// while (Choose.equals("y")) {
		// 	System.out.println("即将连接的Master服务器IP为:"+this.master+" port:"+this.PORT+ "需要更换吗(y/n)?");

		// 	try {
		// 		while (!scanner.hasNext()) {
		// 			// 暂停一段时间，避免过度占用 CPU 资源
		// 			try {
		// 				Thread.sleep(100); // 100 毫秒
		// 			} catch (InterruptedException e) {
		// 				e.printStackTrace();
		// 			}
		// 		}
		// 		Choose = scanner.next();
		// 		if (Choose.equals("y")) {
		// 			scanner.nextLine();
		// 			System.out.println("请输入更换的相同格式服务器ip");
		// 			this.master = scanner.nextLine();
		// 			System.out.println(this.master);
		// 		}
		// 	} catch (Exception e) {
		// 		e.printStackTrace();
		// 	}
		// }
        // scanner.close();
		socket = new Socket(master, PORT);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
        isRunning = true;

        this.clientManager = clientManager;

        this.listenToMaster(); // 开启监听线程
    }

    // 像主服务器发送信息的api
    // 要加上client标签，可以被主服务器识别
	// <client>[1]name 对应查询，返回<master>[1]ip
    public void sendToMaster(String info) {
        output.println("<client>[1]" + info);
    }
	// <client>[2]name 对应创建，返回<master>[2]ip
    public void sendToMasterCreate(String info) {
        output.println("<client>[2]" + info);
    }

    // 接收来自master server的信息并显示
    // 新增代码，查询主服务器中存储的表名和对应的端口号
    // 主服务器返回的内容的格式应该是"<table>table port"，因此args[0]和[1]分别代表了表名和对应的端口号
    public void receiveFromMaster() throws IOException, InterruptedException {
        String line = null;
        if (socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown()) {
            System.out.println("新消息>>>Socket已经关闭!");
        } else {
            line = input.readLine();
        }
        if (line != null) {
            System.out.println("新消息>>>从服务器收到的信息是：" + line);
            // 主节点通信协议的解析方案
            if (line.startsWith("<master>[1]") || line.startsWith("<master>[2]")) {
                // 截取ip地址
                String[] args = line.substring(11).split(" ");
                String ip = args[0], table = args[1];
				try{
					
					this.clientManager.connectToRegion(ip, commandMap.get(table));
					this.clientManager.cacheManager.setCache(table, ip);
					System.out.println("table与ip已写入缓存");
				}catch(Exception e){
					if(ip.equals("null"))//配置时ip分配出了点问题
						System.out.println("服务器IP分配出现错误,服务器登记列表中不存在该表单");
					else{
						e.printStackTrace();
					}
				}
                
            }
        }

    }

    // 开启一个监听线程
    public void listenToMaster() {
        infoListener = new InfoListener();
        infoListener.start();
    }

    // 将sql语句发送到主服务器进一步处理，这里还有待进一步开发，目前仅供实验
    // 进一步开发在这个方法里面扩展

    public void process(String sql, String table) {
        // 来处理sql语句
        this.commandMap.put(table, sql);
        // 用<table>前缀表示要查某个表名对应的端口号
        this.sendToMaster(table);
    }

    public void processCreate(String sql, String table) {
        this.commandMap.put(table, sql);
        // 用<table>前缀表示要查某个表名对应的端口号
        System.out.println("存入table的是" + table + " " + sql);
        this.sendToMasterCreate(table);
    }

    // 关闭socket的方法，在输入quit的时候直接调用
    public void closeMasterSocket() throws IOException {
        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
        infoListener.stop();
    }


    // 用一个内部类来实现客户端的监听
    // 这里其实参考了Java应用技术里的聊天室的设计
    class InfoListener extends Thread {
        @Override
        public void run() {
            System.out.println("新消息>>>客户端的主服务器监听线程启动！");
            while (isRunning) {
                if (socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown()) {
                    isRunning = false;
                    break;
                }

                try {
                    receiveFromMaster();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    sleep(100);
                } catch (InterruptedException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
