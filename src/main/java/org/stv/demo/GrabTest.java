package org.stv.demo;

import org.stv.limiter.Limiter;

public class GrabTest {

	private static Limiter limiter = new Limiter(4, 2, 5); // 4秒钟请求超过2次，则禁止访问5秒钟

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			quote();
			Thread.sleep(1000);
		}
	}

	public static void quote() {
		String token = "user_9818971"; // 模拟用户token

		try {
			/* 抢单访问次数限制 */
			limiter.checkAndAccess(token);

			System.out.println("抢单动作");
		} catch (Exception e) {
			System.out.println("错误:" + e.getMessage());
		}
	}

}
