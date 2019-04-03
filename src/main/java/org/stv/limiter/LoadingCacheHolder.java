package org.stv.limiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 储存所有key对应的时间窗口数据，并且在获取访问权限的时候会自动计数
 * 
 */
public class LoadingCacheHolder {

	private Integer seconds; // 时间窗口大小
	private Integer limit; // 时间窗口内的数量限制

	public LoadingCacheHolder(Integer seconds, Integer limit) {
		this.seconds = seconds;
		this.limit = limit;
	}

	private Map<String, LoadingCache<Long, AtomicLong>> holder = new ConcurrentHashMap<>();

	public LoadingCache<Long, AtomicLong> getLimiter(String key) {
		LoadingCache<Long, AtomicLong> counter = holder.get(key);
		if (counter == null) {
			counter = CacheBuilder.newBuilder()
//					.softValues()//默认strong
					.expireAfterWrite(seconds + 1, TimeUnit.SECONDS).build(new CacheLoader<Long, AtomicLong>() {
						@Override
						public AtomicLong load(Long seconds) throws Exception {
							return new AtomicLong(0);
						}
					});
			holder.put(key, counter);

		}
		return counter;
	}

	public boolean checkAccess(String key) throws ExecutionException {
		LoadingCache<Long, AtomicLong> counter = getLimiter(key); // 获取每个用户的计数器

		long currentSeconds = System.currentTimeMillis() / 1000 / (seconds / 2); // 计算时间窗口大小（根据传入参数的时间）

		AtomicLong currentSecond = counter.get(currentSeconds); // 获取当前时间窗口的数量
		AtomicLong previousSecond = counter.get(currentSeconds - 1); // 获取上个时间窗口的数量

		long second1 = currentSecond.incrementAndGet();
		long second2 = previousSecond.get();

		if (second1 + second2 > limit) {
			return false;
		} else {
			return true;
		}

	}
}