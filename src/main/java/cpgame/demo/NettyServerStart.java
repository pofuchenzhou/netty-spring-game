package cpgame.demo;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cpgame.demo.netty.ServerInitializer;
/** 
* @project:		demo
* @Title:		NettyServerStart.java
* @Package:		
  @author: 		chenpeng
* @email: 		46731706@qq.com
* @date:		2015年8月20日 下午2:36:20 
* @description:
* @version:
*/
public class NettyServerStart
{
  private static int port;
  public static ApplicationContext factory;

  public static void main(String[] args)
    throws Exception
  {
    DOMConfigurator.configureAndWatch("config/log4j.xml");
    if (args.length > 0)
      port = Integer.parseInt(args[0]);
    else {
      port = 8080;
    }
    run();
  }

  private static void run()
    throws Exception
  {
    factory = new FileSystemXmlApplicationContext("config/propholder.xml");
    ServerInitializer initializer = (ServerInitializer)factory.getBean(ServerInitializer.class);

    NettyServer server = new NettyServer(port);
    server.setInitializer(initializer);
    server.run();
    System.out.println("server is running……");
  }
}