package com.bot.TB;

import java.io.UnsupportedEncodingException;

public class TestRun2 {

	public static void main(String[] args) throws Exception {

		// 測試中文轉碼
		String x = "中文訊息";
		System.out.printf("%s:\t%s\n", "String", x); // String:	中文訊息

		
		System.out.print("BIG5:\t");
		byte y[] = x.getBytes("big5"); // 型---big5--->碼
		for (int i = 0; i < y.length; i++) {
			System.out.printf("%x ", y[i]); // a4 a4 a4 e5 b0 54 ae a7 (big5 一個型佔 2 bytes)
		}

		
		System.out.print("UTF-8:\t");
		byte z[] = x.getBytes("utf-8"); // 型---utf8--->碼
		for (int i = 0; i < z.length; i++) {
			System.out.printf("%x ", z[i]); // e4 b8 ad e6 96 87 e8 a8 8a e6 81 af  (utf8 一個型佔 3 bytes)
		}

		// 看來搞懂了…
		System.out.println();
		String v = new String(y);
		System.out.println("BIG5:\t".concat(v));
		String v1 = new String(y,"big5");
		System.out.println("BIG5:\t".concat(v1));

		String w = new String(z);
		System.out.println("UTF-8:\t".concat(w));
            
	}

}
