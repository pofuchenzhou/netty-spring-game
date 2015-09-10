package cpgame.demo.domain;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: FiexThreadPoolExecutor
 * @Description: netty额外给我们提供了两种线程池：
 *               MemoryAwareThreadPoolExecutor和OrderedMemoryAwareThreadPoolExecutor
 *               MemoryAwareThreadPoolExecutor确保jvm不会因为过多的线程而导致内存溢出错误
 *               OrderedMemoryAwareThreadPoolExecutor是前一个线程池的子类
 *               ，除了保证没有内存溢出之外，还可以保证 channel event的处理次序。
 * @author chenpeng
 * @date 2012-8-9 下午02:15:07
 * 
 * 
 */
public class FiexThreadPoolExecutor extends ThreadPoolExecutor {
	private static Logger logger = LoggerFactory.getLogger(FiexThreadPoolExecutor.class);
	private String poolName;

	public FiexThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveSecond, String poolName) {
		super(corePoolSize, maximumPoolSize, keepAliveSecond, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(10 * corePoolSize), Executors
						.defaultThreadFactory());
		this.poolName = poolName;
		setRejectedExecutionHandler(new DiscardPolicy() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
				StackTraceElement stes[] = Thread.currentThread()
						.getStackTrace();
				for (StackTraceElement ste : stes) {
					logger.warn(ste.toString());
				}
			}

		});
	}

	public FiexThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveSecond) {
		super(corePoolSize, maximumPoolSize, keepAliveSecond, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(10 * corePoolSize));
		setRejectedExecutionHandler(new DiscardPolicy() {

			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
				StackTraceElement stes[] = Thread.currentThread()
						.getStackTrace();
				for (StackTraceElement ste : stes) {
					logger.warn(ste.toString());
				}
			}

		});
	}

	@Override
	public void execute(Runnable command) {
		super.execute(command);
		if (super.getCorePoolSize() * 10 - this.getQueue()
				.remainingCapacity()>100) {
			logger.error(poolName
					+ " ThreadPool blocking Queue  size : "
					+ (super.getCorePoolSize() * 10 - this.getQueue()
							.remainingCapacity()));
		}
	}

}
