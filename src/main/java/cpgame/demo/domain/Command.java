package cpgame.demo.domain;

import java.nio.charset.Charset;
import java.util.List;

import cpgame.demo.utils.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class Command {
	private int id;
	private List<String> commandData;
	private ByteBuf messageData;
	private ERequestType requestType;
	private int readIndex = 0;
	FullHttpRequest request;

	public Command(ERequestType requestType, Object msg) {
		String message = "";
		this.requestType = requestType;
		switch (requestType) {
		case HTTP:
			request = (FullHttpRequest) msg;
			message = request.content().toString(Charset.forName("UTF-8"));
			this.commandData = StringUtils.splitToStringList(message, ",");
			if ((this.commandData != null) && (this.commandData.size() >= 1)) {
				this.id = Integer.valueOf(((String) this.commandData.get(0)).trim()).intValue();
				this.commandData.remove(0);
			}
			break;
		case WEBSOCKET_TEXT:
			message = ((TextWebSocketFrame) msg).text();
			this.commandData = StringUtils.splitToStringList(message, ",");
			if ((this.commandData != null) && (this.commandData.size() >= 1)) {
				this.id = Integer.valueOf(((String) this.commandData.get(0)).trim()).intValue();
				this.commandData.remove(0);
			}
			break;
		case SOCKET:
			this.messageData = ((ByteBuf) msg).copy();
			this.id = this.messageData.readInt();
			break;
		case WEBSOCKET_BINARY:
			BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) msg;
			this.messageData = binaryFrame.content();
			this.id = this.messageData.readInt();
			break;
		}
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRequestType(ERequestType requestType) {
		this.requestType = requestType;
	}

	String readString() {
		switch (requestType) {
		case HTTP:
		case WEBSOCKET_TEXT:
			return (String) this.commandData.get(this.readIndex++);
		case SOCKET:
		case WEBSOCKET_BINARY:
			int length = this.messageData.readInt();
			byte[] c = new byte[length];
			this.messageData.readBytes(c);
			return new String(c);
		}

		return null;
	}

	int readInt() {
		switch (requestType) {
		case HTTP:
		case WEBSOCKET_TEXT:
			return Integer.parseInt((String) this.commandData.get(this.readIndex++));
		case SOCKET:
		case WEBSOCKET_BINARY:
			return this.messageData.readInt();
		}

		return -1;
	}

	short readShort() {
		switch (requestType) {
		case HTTP:
		case WEBSOCKET_TEXT:
			return Short.parseShort((String) this.commandData.get(this.readIndex++));
		case SOCKET:
		case WEBSOCKET_BINARY:
			return this.messageData.readShort();
		}

		return -1;
	}

	long readLong() {
		switch (requestType) {
		case HTTP:
		case WEBSOCKET_TEXT:
			return Long.parseLong((String) this.commandData.get(this.readIndex++));
		case SOCKET:
		case WEBSOCKET_BINARY:
			return this.messageData.readLong();
		}

		return -1L;
	}

	float readFloat() {
		switch (requestType) {
		case HTTP:
		case WEBSOCKET_TEXT:
			return Float.parseFloat((String) this.commandData.get(this.readIndex++));
		case SOCKET:
		case WEBSOCKET_BINARY:
			return this.messageData.readFloat();
		}

		return -1.0F;
	}
}
