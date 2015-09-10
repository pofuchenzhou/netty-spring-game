package cpgame.demo.netty;

import cpgame.demo.dispatcher.HandlerDispatcher;
import cpgame.demo.domain.ERequestType;
import cpgame.demo.domain.GameRequest;
import cpgame.demo.utils.HttpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

public class ServerAdapter extends SimpleChannelInboundHandler<Object> {
	private static final String WEBSOCKET_PATH = "/websocket";
	private WebSocketServerHandshaker handshaker;
	private HandlerDispatcher handlerDispatcher;

	public void setHandshaker(WebSocketServerHandshaker handshaker) {
		this.handshaker = handshaker;
	}

	public void setHandlerDispatcher(HandlerDispatcher handlerDispatcher) {
		this.handlerDispatcher = handlerDispatcher;
	}

	public ServerAdapter(HandlerDispatcher handlerDispatcher) {
		this.handlerDispatcher = handlerDispatcher;
	}

	public ServerAdapter() {
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if ((msg instanceof ByteBuf))
			socketRequest(ctx, msg);
		else if ((msg instanceof FullHttpRequest))
			httpFullRequest(ctx, msg);
		else if ((msg instanceof WebSocketFrame))
			handleWebSocketFrame(ctx, msg);
		
	}

//	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		if ((msg instanceof ByteBuf))
//			socketRequest(ctx, msg);
//		else if ((msg instanceof FullHttpRequest))
//			httpFullRequest(ctx, msg);
//		else if ((msg instanceof WebSocketFrame))
//			handleWebSocketFrame(ctx, msg);
//	}

	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

	private void socketRequest(ChannelHandlerContext ctx, Object msg) throws Exception {
		this.handlerDispatcher.addMessage(new GameRequest(ctx, ERequestType.SOCKET, msg));
	}

	private void httpFullRequest(ChannelHandlerContext ctx, Object msg) throws Exception {
		FullHttpRequest req = (FullHttpRequest) msg;
		if (!req.getDecoderResult().isSuccess()) {
			HttpUtils.sendHttpResponse(ctx, req,
					new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));

			return;
		}
		if (req.getMethod() == HttpMethod.POST) {
			this.handlerDispatcher.addMessage(new GameRequest(ctx, ERequestType.HTTP, msg));
		} else if (req.getMethod() == HttpMethod.GET) {
			if ("/websocket".equals(req.getUri())) {
				WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
						getWebSocketLocation(req), null, false);

				this.handshaker = wsFactory.newHandshaker(req);
				if (this.handshaker == null) {
					WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
				} else {
					this.handshaker.handshake(ctx.channel(), req);
				}
			} else if ("/".equals(req.getUri())) {
				ByteBuf content = WebSocketServerIndexPage.getContent(getWebSocketLocation(req));

				FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
						content);

				res.headers().set("Content-Type", "text/html; charset=UTF-8");
				HttpHeaders.setContentLength(res, content.readableBytes());

				HttpUtils.sendHttpResponse(ctx, req, res);
			}
		} else {
			HttpUtils.sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));

			return;
		}
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, Object msg) {
		WebSocketFrame frame = (WebSocketFrame) msg;
		if ((frame instanceof CloseWebSocketFrame)) {
			this.handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());

			return;
		}
		if ((frame instanceof PingWebSocketFrame)) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));

			return;
		}
		if ((!(frame instanceof TextWebSocketFrame)) || ((frame instanceof BinaryWebSocketFrame))) {
			throw new UnsupportedOperationException(
					String.format("%s frame types not supported", new Object[] { frame.getClass().getName() }));
		}

		try {
			if ((frame instanceof TextWebSocketFrame)) {
				this.handlerDispatcher.addMessage(new GameRequest(ctx, ERequestType.WEBSOCKET_TEXT, msg));
			} else
				this.handlerDispatcher.addMessage(new GameRequest(ctx, ERequestType.WEBSOCKET_BINARY, msg));
		} catch (Exception e) {
		}
	}

	private static String getWebSocketLocation(FullHttpRequest req) {
		return "ws://" + req.headers().get("Host") + WEBSOCKET_PATH;
	}

	
}
