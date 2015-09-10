package cpgame.demo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/** 
* @project:		demo
* @Title:		WebSocketServerIndexPage.java
* @Package:		cpgame.demo.netty
  @author: 		chenpeng
* @email: 		46731706@qq.com
* @date:		2015年8月20日 下午2:32:36 
* @description:
* @version:
*/
public class WebSocketServerIndexPage {

	  public static ByteBuf getContent(String webSocketLocation)
	  {
	    return Unpooled.copiedBuffer("<html><head><title>Web Socket Test</title></head>\r\n<body>\r\n<script type=\"text/javascript\">\r\nvar socket;\r\nif (!window.WebSocket) {\r\n  window.WebSocket = window.MozWebSocket;\r\n}\r\nif (window.WebSocket) {\r\n  socket = new WebSocket(\"" + webSocketLocation + "\");" + "\r\n" + "  socket.onmessage = function(event) {" + "\r\n" + "    var ta = document.getElementById('responseText');" + "\r\n" + "    ta.value = event.data + '\\n' + ta.value" + "\r\n" + "  };" + "\r\n" + "  socket.onopen = function(event) {" + "\r\n" + "    var ta = document.getElementById('responseText');" + "\r\n" + "    ta.value = \"Web Socket opened!\";" + "\r\n" + "  };" + "\r\n" + "  socket.onclose = function(event) {" + "\r\n" + "    var ta = document.getElementById('responseText');" + "\r\n" + "    ta.value = \"Web Socket closed\" + '\\n'+ ta.value; " + "\r\n" + "  };" + "\r\n" + "} else {" + "\r\n" + "  alert(\"Your browser does not support Web Socket.\");" + "\r\n" + '}' + "\r\n" + "\r\n" + "function send(message) {" + "\r\n" + "  if (!window.WebSocket) { return; }" + "\r\n" + "  if (socket.readyState == WebSocket.OPEN) {" + "\r\n" + "    socket.send(message);" + "\r\n" + "  } else {" + "\r\n" + "    alert(\"The socket is not open.\");" + "\r\n" + "  }" + "\r\n" + '}' + "\r\n" + "</script>" + "\r\n" + "<form onsubmit=\"return false;\">" + "\r\n" + "<input type=\"text\" style=\"width:100%;height:22px;\" name=\"message\" value=\"999,are you ok?\"/>" + "<input type=\"button\" value=\"Send Web Socket Data\"" + "\r\n" + "       onclick=\"send(this.form.message.value)\" />" + "\r\n" + "<h3>Output</h3>" + "\r\n" + "<textarea id=\"responseText\" style=\"width: 1348px; height:599px;\"></textarea>" + "\r\n" + "</form>" + "\r\n" + "</body>" + "\r\n" + "</html>" + "\r\n", CharsetUtil.US_ASCII);
	  }
}
