package MasterManagers.utils;

import MasterManagers.SocketManager.SocketThread;
import MasterManagers.TableManger;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 从节点数据传输：从节点在每次执行创表、插入、删除操作后，将所修改的表和索引传输到 FTP 服务器。
 * 主节点监测与备份：当主节点监测到某从节点失效时，寻找缺少表的从节点，向该从节点发送备份指令（包括失效从节点所存储的所有表）。目标从节点从 FTP
 * 服务器读取表和索引。读取完成后，发送消息给主节点。主节点接收到消息后，更新 table-server 映射。
 * 从节点恢复策略：当失效的从节点重新连接，主节点向从节点发送恢复指令。从节点收到指令后，删除本节点的所有表。删除完成后，发送消息给主节点。主节点接收到消息后，将该从节点状态变更为有效的从节点，恢复正常使用。
 */
@Slf4j
public class ServiceStrategyExecutor {

	private TableManger tableManger;

	public ServiceStrategyExecutor(TableManger tableManger) {
		this.tableManger = tableManger;
	}

	public boolean existServer(String hostUrl) {
		return tableManger.existServer(hostUrl);
	}

	public void execStrategy(String hostUrl, StrategyTypeEnum type) {
		try {
			switch (type) {
				case RECOVER:
					execRecoverStrategy(hostUrl);
					break;
				case INVALID:
					execInvalidStrategy(hostUrl);
					break;
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	private void execInvalidStrategy(String hostUrl) {
		StringBuffer allTable = new StringBuffer();
		List<String> tableList = tableManger.getTableList(hostUrl);
		// <master>[3]ip#name@name@
		String bestInet = tableManger.getBestServer(hostUrl);
		log.warn("bestInet:" + bestInet);
		allTable.append(hostUrl + "#");
		int i = 0;
		for (String s : tableList) {
			if (i == 0) {
				allTable.append(s);
			} else {
				allTable.append("@");
				allTable.append(s);
			}
			i++;
		}
		tableManger.exchangeTable(bestInet, hostUrl);
		SocketThread socketThread = tableManger.getSocketThread(bestInet);
		socketThread.sendToRegion("[3]" + allTable);
	}

	private void execRecoverStrategy(String hostUrl) {
		tableManger.recoverServer(hostUrl);
		SocketThread socketThread = tableManger.getSocketThread(hostUrl);
		socketThread.sendToRegion("[4]recover");
	}

}
