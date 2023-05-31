package MasterManagers.SocketManager;

import MasterManagers.TableManger;

import java.net.Socket;

/**
 * 1.如果消息为<client>[1]tablename，表示该请求为客户端的table查询信息,返回<master>[1]ip(对应的Region) tablename
 * 2.如果消息为<client>[2]name，则在做负载均衡处理后返回<master>[2]ip name
 */
public class ClientProcessor {

    private TableManger tableManger;
    private Socket socket;

    public ClientProcessor(TableManger tableManger, Socket socket){
        this.tableManger = tableManger;
        this.socket = socket;
    }
    public String processClientCommand(String cmd) {
        String result = "";
        String tablename = cmd.substring(3);
        if (cmd.startsWith("[1]")) {
            result = "[1]"+tableManger.getInetAddress(tablename) +" "+ tablename;
        } else if (cmd.startsWith("[2]")) {
            result = "[2]"+tableManger.getBestServer() + " " +tablename;
        }
        return result;
    }

}
