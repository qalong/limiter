package org.stv.limiter;

import java.util.concurrent.ExecutionException;

public class Limiter {

	private LoadingCacheHolder loadingCacheHolder;// 基于缓存的时间窗口的请求统计
	private ForrbidenCacheHolder forrbidenCacheHolder;// 基于缓存的限制请求缓存

	public Limiter(int accessTimeWindow, int accessLimit, int forrbidenTime) {
		loadingCacheHolder = new LoadingCacheHolder(accessTimeWindow, accessLimit);
		forrbidenCacheHolder = new ForrbidenCacheHolder(forrbidenTime);
	}

	public void checkAndAccess(String token) throws InterruptedException {
		/* 抢单访问次数限制 */
		if (forrbidenCacheHolder.checkForbbiden(token) != null) {
			throw new InterruptedException("访问过于频繁，已设限！");
		}

		boolean isAccess = false;
		try {
			isAccess = loadingCacheHolder.checkAccess(token);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		if (!isAccess) {
			forrbidenCacheHolder.addForbbiden(token);
			throw new InterruptedException("访问过于频繁，开始设限！");
		}

	}

}
