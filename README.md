分布式大作业

## 环境要求：

系统需要配备基础的java环境（这里使用的是jdk17）和maven



## 准备工作

这里已经将Client、Master、Region打包好了，可以进入到JarFile目录，使用`java -jar <对应的包>`直接运行。
也可执行脚本打包：

- Windows用户在根目录执行：

  ```bash
  $./script.cmd
  ```

- Linux用户在根目录执行：

  ```bash
  $./script.sh
  ```

随后就会自动检测java版本（这里检测的是jdk17版本）和maven是否安装，并自动导出到JarFile目录中去。

也可自行编译打包：

1. 在安装maven后进入`.\src`目录下执行该语句：

   ```bash
   $cd .\src
   $mvn clean install
   ```

2. 进入到对应的目录执行打包，打包完成后会在对应的target目录见到jar文件

   ```bash
   #以Clent为例
   $cd ./src/Client
   $mvn package
   ```
   
   Master与Region也是同理,不过要注意的是，Master和Region打包出的文件有两个,我们选用with-dependencies的那项导出，才能正常运行。



## 运行注意事项：

需要启动ZooKeeper服务器与Master后，才能够开始运行Region和Client
ZooKeeper使用默认配置即可，端口需要用2181
