package cpgame.demo.domain;

import java.util.LinkedList;
import java.util.List;

import cpgame.demo.utils.ResponseJsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class GameResponse {
	private ERequestType requestType;
	private Command command;
	private Channel channel;
	private Object rtMessage;

	public GameResponse(Channel channel, Command command, ERequestType requestType) {
		this.channel = channel;
		this.command = command;
		this.requestType = requestType;
		switch (requestType) {
		case HTTP:
		case WEBSOCKET_TEXT:
			this.rtMessage = new LinkedList<Object>();
			break;
		case SOCKET:
		case WEBSOCKET_BINARY:

			this.rtMessage = Unpooled.buffer();
			break;
		}

		write(Integer.valueOf(command.getId()));
	}

	public ERequestType getRequestType() {
		return this.requestType;
	}

	public void setRequestType(ERequestType requestType) {
		this.requestType = requestType;
	}

	public Command getCommand() {
		return this.command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public Channel getChannel() {
		return this.channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Object getRtMessage() {
		return this.rtMessage;
	}

	public void setRtMessage(Object rtMessage) {
		this.rtMessage = rtMessage;
	}

	@SuppressWarnings("unchecked")
	public void write(Object obj) {
		switch (requestType) {
		case HTTP:
		case WEBSOCKET_TEXT:
			((List<Object>) this.rtMessage).add(obj);
			break;
		case SOCKET:
		case WEBSOCKET_BINARY:
			ByteBuf buf = (ByteBuf) this.rtMessage;
			if (obj == null)
				return;
			if ((obj instanceof String)) {
				String tmp = (String) obj;
				buf.writeInt(tmp.getBytes().length);
				buf.writeBytes(tmp.getBytes());
				return;
			}
			if ((obj instanceof Short))
				buf.writeShort(((Short) obj).shortValue());
			else if ((obj instanceof Integer))
				buf.writeInt(((Integer) obj).intValue());
			else if ((obj instanceof Long))
				buf.writeLong(((Long) obj).longValue());
			else if ((obj instanceof Float))
				buf.writeFloat(((Float) obj).floatValue());
			else if ((obj instanceof Byte))
				buf.writeByte(((Byte) obj).byteValue());
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public String getWebSocketRespone() {
		return ResponseJsonUtils.list2json((List<Object>) rtMessage);
	}

	public FullHttpResponse getResp() {
		ByteBuf content = Unpooled.copiedBuffer(rtMessage.toString(), CharsetUtil.UTF_8);
		FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
		resp.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
		HttpHeaders.setContentLength(resp, content.readableBytes());
		return resp;
	}
}
