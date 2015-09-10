package cpgame.demo.netty;

import cpgame.demo.dispatcher.HandlerDispatcher;
import cpgame.demo.domain.ERequestType;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
	private int timeout = 3600;
	private HandlerDispatcher handlerDispatcher;
	private String requestType = ERequestType.SOCKET.getValue();

	public void init() {
		new Thread(this.handlerDispatcher).start();
	}

	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		if (ERequestType.SOCKET.getValue().equals(this.requestType.trim().toLowerCase())) {
			ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));

			ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
		} else {
			pipeline.addLast("codec-http", new HttpServerCodec());
			pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
		}
		pipeline.addLast("timeout", new ReadTimeoutHandler(this.timeout));
		pipeline.addLast("handler", new ServerAdapter(this.handlerDispatcher));
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setHandlerDispatcher(HandlerDispatcher handlerDispatcher) {
		this.handlerDispatcher = handlerDispatcher;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestType() {
		return this.requestType;
	}
}