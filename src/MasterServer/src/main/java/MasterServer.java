import MasterManagers.MasterManager;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * DB Master Server
 * DB Master Server 实际上是一个检索服务器，负责管理和提供数据服务器元数据。主要工作包括以下几个方面：
 * 
 * 启动与加载数据服务器列表：与 ZooKeeper 集群进行通信，加载对应节点数据，获取当前所有能提供服务的数据服务器列表，并加载到内存中。
 * 获取表格元数据：与每个 RegionServer 进行通信，获取到每个服务器上保存的表格元数据，并存储到本地。
 * 监听节点变化：持续监听节点，当节点内容发生变化（例如某 Server 上线或下线），获得通知，重新加载节点数据，刷新内存中数据服务器列表。
 * 处理客户端请求：接收客户端的请求，返回需要的元数据。
 * 执行策略（可选）：在节点变化时与相应的服务器进行联系，执行容错容灾、副本复制等策略。
 * 解决方案设计
 * 为实现以上功能，这里设计了两个线程：
 * 
 * 线程一：负责启动时向 ZooKeeper 发送请求，获取 ZNODE目录下的信息并持续监控。当发生目录变化时，执行回调函数，处理相应策略。策略主要包括步骤 3 和 5。
 * 线程二：负责监听和处理客户端请求，返回需要的元数据。
 *
 */
public class MasterServer {
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		MasterManager masterManager = new MasterManager();
		masterManager.initialize();
	}
}
