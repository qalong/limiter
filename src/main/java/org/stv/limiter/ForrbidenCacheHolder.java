package org.stv.limiter;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * 惩罚限制器，当超过单位时间的访问限制后，会在这里记录，禁止一段时间
 */
public class ForrbidenCacheHolder {
	private Integer lockTime; // 锁时间

	public ForrbidenCacheHolder(Integer lockTime) {
		this.lockTime = lockTime;
	}

	private static Cache<String, String> forrbidenCache = CacheBuilder.newBuilder()
//			.softValues()//默认strong
			.removalListener(new RemovalListener<String, String>() {
				public void onRemoval(RemovalNotification<String, String> notification) {
					System.out.println("限制放开：" + notification.getKey());
				}
			}).expireAfterWrite(5, TimeUnit.SECONDS).maximumSize(1000).build();
	// 强制限制5秒钟

	public String checkForbbiden(String key) {
		return forrbidenCache.getIfPresent(key);
	}

	public void addForbbiden(String key) {
		forrbidenCache.put(key, key);
	}
}