package com.bot.TB;

public class TestRun {

	public static void main(String[] args) throws Exception {

//		System.out.println("UTF-16: " + unicode);
//		char y[] = unicode.toCharArray();
//		for (int i = 0; i < y.length; i++) {
//			System.out.printf("%x ", (int) y[i]);
//		}
//		System.out.println();

		// 一共有三個關鍵人物: 
		// 「型」 -> 肉眼所見的圖樣 
		// 「碼」 -> 以byte為單位去思考，不要以bit為單位，先確立這一點
		// 「key」 -> big5, utf-8 是 encode & decode 的key
		
		String unicode = "中文轉碼測試";
		
		// unicode 轉成 Big5 編碼
		byte[] big5 = unicode.getBytes("Big5"); // 型轉碼(big5)
		
		// Big5 編碼 轉回 unicode
		unicode = new String(big5, "Big5"); // 碼轉型(big5)
		
		System.out.println("Big5: " + unicode); // 型(utf-8) ---big5---> 碼  ---big5---> 型
		
		byte x[] = big5;
		for (int i = 0; i < x.length; i++) {
			System.out.printf("%x ", x[i]);
		}
		System.out.println();
//		Big5: 	中	     文		轉	     碼		測	     試       	-> big5 一個型佔 2 bytes
//				a4 a4 a4 e5 c2 e0 bd 58 b4 fa b8 d5

		
		// Big5 編碼 轉回 unicode 再轉成 UTF-8 編碼
		byte[] utf8 = null;
		utf8 = new String(big5, "Big5").getBytes("UTF-8"); // 型轉碼(utf-8)
		
		unicode = new String(utf8, "UTF-8"); // 碼轉型(utf-8)

		System.out.println("UTF-8: " + unicode);
		
		byte z[] = utf8;
		for (int i = 0; i < z.length; i++) {
			System.out.printf("%x ", z[i]);
		}
		
// 	UTF-8:	中	             文		      轉	 	        碼		測	             試       		-> UTF-8      一個型佔 3 bytes		
// 			e4 b8 ad e6 96 87 e8 bd 89 e7 a2 bc e6 b8 ac e8 a9 a6 		
		

	}

}

/*
 * 這與特定編碼所能處理的值域有關。

Big5 編碼能夠處理的 unicode point 有限(Big5 只能 encode 一部份的 unicode char 成 byte sequence，同樣也只能 decode 某些 byte sequence 為 unicode char value)，
所以當你把字串經過UTF-8 encode 之後的 byte sequence 以 Big5 編碼來 decode 會使得數據失真，數據失真後就沒有任何方式可以還原成原始的數據。

透過 String constructor 來做 byte sequence 的 decoding 操作，當使用的編碼法無法處理遭遇的 byte sequence 時，
預設是把無法處理的 byte sequence decode 成 '?' 字元(這時候就已經失真了)。

* 紅色的部分就是 '?'。 (兩個3f)
* 銝剜�頧�Ⅳ皜祈岫
e4 b8 ad e6 3f e8 bd 3f a2 bc e6 b8 ac e8 a9 a6 
* 
 */



















