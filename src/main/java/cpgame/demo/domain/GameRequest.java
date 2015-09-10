package cpgame.demo.domain;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class GameRequest {
	private ERequestType requestType;
	private Command command;
	private Channel channel;
	private ChannelHandlerContext ctx;
	private Object msg;

	public GameRequest(ChannelHandlerContext ctx, ERequestType requestType, Object msg) {
		try {
			this.ctx = ctx;
			this.channel = ctx.channel();
			this.requestType = requestType;
			this.msg = msg;
			this.command = new Command(requestType, msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}

	public GameRequest(Channel channel, Command command) {
		this.channel = channel;
		this.command = command;
	}

	public GameRequest(Command command) {
		this.channel = null;
		this.command = command;
	}

	public Channel getChannel() {
		return this.channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public int getCommandId() {
		if (this.command != null) {
			return this.command.getId();
		}
		return -1;
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

	public int readInt() {
		return this.command.readInt();
	}

	public short readShort() {
		return this.command.readShort();
	}

	public long readLong() {
		return this.command.readLong();
	}

	public float readFloat() {
		return this.command.readFloat();
	}

	public String readString() {
		return this.command.readString();
	}
}