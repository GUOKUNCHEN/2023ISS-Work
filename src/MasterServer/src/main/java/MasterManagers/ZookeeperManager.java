package MasterManagers;

import MasterManagers.utils.CuratorHolder;
import MasterManagers.utils.ServiceMonitor;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.*;

@Slf4j
public class ZookeeperManager implements Runnable{
    private TableManger tableManger;
    //ZooKeeper集群访问的端口
    public static String ZK_HOST = "192.168.43.125:2181";
    //ZooKeeper会话超时时间
    public static final Integer ZK_SESSION_TIMEOUT = 3000;
    //ZooKeeper连接超时时间
    public static final Integer ZK_CONNECTION_TIMEOUT = 3000;
    //ZooKeeper集群内各个服务器注册的节点路径
    public static final String ZNODE = "/db";
    //ZooKeeper集群内各个服务器注册自身信息的节点名前缀
    public static final String HOST_NAME_PREFIX = "Region_";

    public ZookeeperManager(TableManger tableManger) {
        this.tableManger = tableManger;
		System.out.println("Zk服务启动,当前连接的Zk服务器ip:port为:"+ZookeeperManager.ZK_HOST+"\n需要更换服务器吗?(y/n)");
		try (Scanner scanner = new Scanner(System.in)) {
			String Choose = scanner.next();
			if(Choose.equals("y")){
				scanner.nextLine();
				System.out.println("请输入更换的相同格式服务器ip:port");
				ZookeeperManager.ZK_HOST=scanner.nextLine();
				System.out.println(ZookeeperManager.ZK_HOST);
			}
		}
    }

    @Override
    public void run() {
        this.startMonitor();
    }

    // 一些zookeeper的测试代码
    public void startMonitor(){
        try {
            // 开启一个连接
            CuratorHolder curatorClientHolder = new CuratorHolder(ZK_HOST);
            // 创建服务器主目录
            if (!curatorClientHolder.checkNodeExist(ZNODE)){
                curatorClientHolder.createNode(ZNODE,"服务器主目录");
            }

            // 开始监听服务器目录，如果有节点的变化，则处理相应事件
            curatorClientHolder.monitorChildrenNodes(ZNODE, new ServiceMonitor(curatorClientHolder,tableManger));
			System.out.println("已启动监听");
		} catch (Exception e) {
            log.warn(e.getMessage(),e);
        }
    }
}
