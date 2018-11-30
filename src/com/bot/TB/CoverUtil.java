package com.bot.TB;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.AStar.TBConvert.TBConvertDefine;
import com.AStar.TBConvert.Big5.ConvertBig5_UCS2;
import com.AStar.TBConvert.Big5.TB_Big5_UCS2;
import com.AStar.TBConvert.UCS2.ConvertUcs2_Big5;
import com.AStar.TBConvert.UCS2.TB_UCS2_Big5;
import com.bot.TB.vo.MakeWordVO;
import com.bot.TB.vo.OracleVO;
import static java.lang.System.out;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.StringUtils;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;


// C.	big5 to unicode 請參考  CoverUtil.coverOracle(OracleVO vo)

public class CoverUtil {
	
	public static String coverOracle2(String name) {
		String big5Str = "";
		try {
			System.out.println("**********************TB_UCS2_Big5 start**********************");
//			TB_UCS2_Big5 UCS2_Big5 = new TB_UCS2_Big5();
//			String UCS2_BIG5_table_name = "C:\\TB_UCS2_BIG5.bin";
//			long UCS2_Big5_Res = 0;
//
//			// load table
//			UCS2_Big5_Res = UCS2_Big5.load(UCS2_BIG5_table_name);
//			if (TBConvertDefine.CONVT_SUCCESS != UCS2_Big5_Res) {
//				System.out.println("UCS2_Big5 load fail(" + UCS2_Big5_Res + ")");
//				return "";
//			}
			byte[] UCS2Bytes = name.getBytes("UTF-16BE");
			System.out.print("UnicodeBig byte:");
			for (byte b : UCS2Bytes) {
				System.out.printf("%02X ", b);
				
			}
			System.out.println();

			// 6E38 932B 5803 -> 0 4 8 12  
			big5Str = byteArrayTohexString(UCS2Bytes);
			// int length = big5Str.length();
			List<String> list = new ArrayList<String>();  
			for(int i = 0; i<big5Str.length(); i+=4){
				int j = i+4;
				String section = StringUtils.substring(big5Str, i, j);
				list.add(section);
			}
			System.out.println(list);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return big5Str; // 回 A4A4A4E5A672 (big5的碼)
	}
	
	
	// B.	unicode to big5請參考     CoverUtil.coverOracle(String name)
	public static String coverOracle(String name) {
		String big5Str = "";
		String big5dest = "";
		try {
			System.out.println("**********************TB_UCS2_Big5 start**********************");
			TB_UCS2_Big5 UCS2_Big5 = new TB_UCS2_Big5();
			String UCS2_BIG5_table_name = "C:\\TB_UCS2_BIG5.bin";
			long UCS2_Big5_Res = 0;

			// load table
			UCS2_Big5_Res = UCS2_Big5.load(UCS2_BIG5_table_name);
			if (TBConvertDefine.CONVT_SUCCESS != UCS2_Big5_Res) {
				System.out.println("UCS2_Big5 load fail(" + UCS2_Big5_Res + ")");
				return "";
			}
			char[] UCS2src = name.toCharArray();
			byte[] UCS2Bytes = name.getBytes("UTF-16BE");
			System.out.print("UnicodeBig byte:");
			for (byte b : UCS2Bytes) {
				System.out.printf("%02X ", b);
			}
			System.out.println();

			// convert now
			ConvertUcs2_Big5 convertUcs2_Big5 = new ConvertUcs2_Big5();
			UCS2_Big5_Res = convertUcs2_Big5.convert(UCS2_Big5, UCS2src, UCS2src.length, false);
			if (TBConvertDefine.CONVT_SUCCESS != UCS2_Big5_Res) {
				System.out.println("ConvertUcs2_Big5 convert fail");
				return "";
			}
			//
			byte[] dest = convertUcs2_Big5.getResult();
			System.out.print("big5 byte:");
			for (byte b : dest) {
				System.out.printf("%02X ", b);
			}
			big5Str = byteArrayTohexString(dest);
			System.out.println();
			big5dest = new String(dest, "MS950");
			System.out.println("big5 str1:" + big5dest); // 中文字 (型) --> 
			System.out.println("big5 str2:" + big5Str);  // A4A4A4E5A672 (碼)
			
			// 偵測器，傳入(碼) 回傳傳入碼的charset
			String detectedCharset = charset(big5Str, new String[] { "ISO-8859-1", "UTF-8" }); // ISO-8859-1
			System.out.println(detectedCharset); // ISO-8859-1
			
			// 傳入型
			String detectedCharset2 = charset(big5dest, new String[] { "ISO-8859-1", "UTF-8" }); // ISO-8859-1
			System.out.println(detectedCharset2); // UTF-8
			
			
			
			System.out.println();
			System.out.println("**********************TB_UCS2_Big5 end**********************");
			System.out.println("**********************TB_Big5_Ucs2 start **********************");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return big5Str; // 回 A4A4A4E5A672 (big5的碼)
	}
	
	public static String charset(String value, String charsets[]) throws UnsupportedEncodingException {
		  String probe = StandardCharsets.UTF_8.name();
		  for(String c : charsets) {
		    Charset charset = Charset.forName(c);
		    if(charset != null) {
		      if(value.equals(convert(convert(value, charset.name(), probe), probe, charset.name()))) {
		        return c;
		      }
		    }
		  }
		  return StandardCharsets.UTF_8.name();
		}
	
	public static String convert(String value, String fromEncoding, String toEncoding) throws UnsupportedEncodingException {
		  return new String(value.getBytes(fromEncoding), toEncoding);
		}
	
	public static void printBytes(byte[] array, String name) {
	    for (int k = 0; k < array.length; k++) {
	        System.out.println(name + "[" + k + "] = " + "0x" +
	            array[k]);
	    }
	}
	
	public static void main(String[] args) throws Exception{
		
		
		//String textUtf8 = "中文字";
		
		//String textBig5 = coverOracle(textUtf8); // utf8轉成big5		
		//System.out.println(textBig5);
//		try {
//		    byte[] utf8Bytes = original.getBytes("UTF8");
////		    byte[] defaultBytes = original.getBytes();
//
//		    
//		    
//		    
//		    String roundTrip = new String(utf8Bytes, "UTF8");
//		    System.out.println("roundTrip = " + roundTrip);
//		    
//		    
//		    //System.out.println();
//		    //printBytes(utf8Bytes, "utf8Bytes");
////		    System.out.println();
////		    printBytes(defaultBytes, "defaultBytes");
//		} 
//		catch (UnsupportedEncodingException e) {
//		    e.printStackTrace();
//		}
		
		
		
		
		// C:\\sample_big5.txt
		// 
//        File f = new File("C:\\sample_utf8.txt");
//
//        String[] charsetsToBeTested = {"MS950", "windows-1253", "utf-8"}; // 有一個對到就可以
//
//        CoverUtil cd = new CoverUtil();
//        Charset charset = cd.detectCharset(f, charsetsToBeTested);
//
//        if (charset != null) {
//            try {
//                InputStreamReader reader = new InputStreamReader(new FileInputStream(f), charset);
//                int c = 0;
//                while ((c = reader.read()) != -1) {
//                    System.out.print((char)c);
//                }
//                reader.close();
//            } catch (FileNotFoundException fnfe) {
//                fnfe.printStackTrace();
//            }catch(IOException ioe){
//                ioe.printStackTrace();
//            }
//
//        }else{
//            System.out.println("Unrecognized charset.");
//        }
		
		
		
		// 取得字串的位元組資料，可以使用 String 的 getBytes() 方法
//        print("UTF-16", "測試".getBytes("UTF-16"));
//        print("UTF-8", "測試".getBytes("UTF-8"));
//         print("Big5", "測試".getBytes("Big5")); // 就是MS950
//        print("default", "測試".getBytes()); // 預設 -> 可以在 workspace Text file encoding 進行修改 (目前是UTF-8)
		
        // 如果有一個位元組陣列，可以用來建構字串，建構時可指定以何種編碼方式來解釋所提供的位元組陣列
//        out.println(new String(toBytes(0xfe, 0xff, 0x6e, 0x2c, 0x8a, 0x66), "UTF-16"));
//        out.println(new String(toBytes(0xe6, 0xb8, 0xac, 0xe8, 0xa9,0xa6), "UTF-8"));
//        out.println(new String(toBytes(0xb4, 0xfa, 0xb8, 0xd5), "Big5"));
//        out.println(new String(toBytes(0xe6, 0xb8, 0xac, 0xe8, 0xa9,0xa6)));
        
        
        
//        CharsetDetector detector = new CharsetDetector();
//        detector.setText(yourStr.getBytes());
  //      detector.detect();  // <- return the result, you can check by .getName() method
        
//        try(BufferedReader reader = new BufferedReader(new FileReader("C:\\sample_utf8.txt"))) { // 兩邊一致就可讀
//            out.println(reader.readLine());
//            detector.setText(reader.readLine().getBytes());
//            CharsetMatch cm = detector.detect();
//            // out.println(detector.detect().getName());
//            System.out.println();
//            
//        }
        
//        try(BufferedReader reader = new BufferedReader(new FileReader("C:\\sample_big5.txt"))) {
//            out.println(reader.readLine());
//            detector.setText(reader.readLine().getBytes());
//            out.println(detector.detect().getName());
//        }
        
        //Charset ch = Charset.defaultCharset(); // 取得預設編碼。
        // 基本上，System.out 採用 JVM 預設編碼，若 JVM 預設編碼為 Big5，System.out 就採用 Big5
        //System.out.println(ch);
        

        
        /*
        Set<String> strs = Charset.availableCharsets().keySet();
        for (String charset: strs) {
        	  System.out.println("charset = " + charset);
              try {
                  // 當初是哪個位元組陣列被解釋為 UTF-8 的？逐一嘗試！
                  // byte[] bytes = "®õ¤s½Y¥Û±Ð·|".getBytes(charset);
                  // byte[] bytes = "測試".getBytes(charset);
                  out.printf("%s %s%n", charset, new String(bytes, "Big5"));
                  
              } catch (Exception e) {
                  // nope
              }
        }
         */
        
        

//		1. unicode to big5請參考CoverUtil. coverOracle(String name)
//		2. big5 to unicode 請參考CoverUtil.coverOracle(OracleVO vo)

		String aaa = "焿";
//		char[] charArr = "焿".toCharArray();
		// 取得不同編碼的字碼
//		System.out.println("big5:"+byteArrayTohexString(aaa.getBytes("big5")));
//		System.out.println("utf-8:"+byteArrayTohexString(aaa.getBytes("utf-8")));
//		System.out.println("defaul:"+byteArrayTohexString(aaa.getBytes()));

		
		// utf-8
		String textUtf8 = "中文字";
		
		// 偵測器(用來驗證上面的方法…)，傳入(碼) 回傳傳入碼的charset
//		String detectedCharset1 = charset(textUtf8, new String[] { "ISO-8859-1", "UTF-8" }); 
//		System.out.println(detectedCharset1); // UTF-8
//		System.out.println();

		// big5
		String textBig5 = coverOracle(textUtf8); // utf8轉成big5	-> ok	
		
		byte y[] = textBig5.getBytes("big5"); // 型---big5--->碼

		for (int i = 0; i < y.length; i++) {
			System.out.printf("%x ", y[i]); // a4 a4 a4 e5 b0 54 ae a7 (big5 一個型佔 2 bytes)
		}

		
//		String detectedCharset2 = charset(textBig5, new String[] { "ISO-8859-1", "UTF-8" }); // 
//		System.out.println(detectedCharset2); // 
//		System.out.println();
		
		



		
		
		

		
		
		
		TB_UCS2_Big5 UCS2_Big5 = new TB_UCS2_Big5();
		String UCS2_BIG5_table_name = "C:\\TB_UCS2_BIG5.bin";
		long UCS2_Big5_Res = 0;

		// load table
		UCS2_Big5_Res = UCS2_Big5.load(UCS2_BIG5_table_name);
		if (TBConvertDefine.CONVT_SUCCESS != UCS2_Big5_Res) {
			System.out.println("UCS2_Big5 load fail(" + UCS2_Big5_Res + ")");
			
		}
		
		char[] UCS2src = aaa.toCharArray();
		byte[] UCS2Bytes = aaa.getBytes("UTF-16BE");
		System.out.print("UnicodeBig byte:");
		for (byte b : UCS2Bytes) {
			System.out.printf("%02X ", b);
		}
		System.out.println();
		System.out.println("**********************TB_Big5_Ucs2 end**********************");
		
		
//		try {
//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:\\ansi2.txt")), "MS950"));
//			String dataLine = null;
//			byte[] dataLineByte = null;
//			while((dataLine = br.readLine()) != null){
//				System.out.println(big5CoverUCS2(dataLine));
//				dataLineByte = dataLine.getBytes("MS950");
//				System.out.println(dataLine);
//				System.out.println(conv(dataLineByte));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	
    public Charset detectCharset(File f, String[] charsets) {

        Charset charset = null;

        for (String charsetName : charsets) {
            charset = detectCharset(f, Charset.forName(charsetName));
            if (charset != null) {
                break;
            }
        }

        return charset;
    }

    private Charset detectCharset(File f, Charset charset) {
        try {
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(f));

            CharsetDecoder decoder = charset.newDecoder();
            decoder.reset();

            byte[] buffer = new byte[512];
            boolean identified = false;
            while ((input.read(buffer) != -1) && (!identified)) {
                identified = identify(buffer, decoder);
            }

            input.close();

            if (identified) {
                return charset;
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    private boolean identify(byte[] bytes, CharsetDecoder decoder) {
        try {
            decoder.decode(ByteBuffer.wrap(bytes));
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }
	
	
	// 字串 -> 位元組
    private static void print(String encoding, byte[] bytes) {
        out.printf("%s\t", encoding);
        for(byte b : bytes) {
            out.printf("%-3h", b & 0x00FF);
        }
        out.println();
    }
    // 位元組 -> 字串
    private static byte[] toBytes(int... ints) {
        byte[] bytes = new byte[ints.length];
        for(int i = 0; i < ints.length; i++) {
            bytes[i] = (byte) ints[i]; 
        }
        return bytes;
    }
	
	
	public static String byteArrayTohexString(byte[] bytes) {
		char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			System.out.printf(hexChars[j * 2] + "" + hexChars[j * 2 + 1]);
			System.out.println();
		}
		return new String(hexChars);
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
			System.out.printf("%02X ", data[i / 2]);
			System.out.println();
		}
		return data;
	}


	
	public static List<OracleVO> coverOracle2(List<OracleVO> dataList) {
		for(int i = 0; i < dataList.size(); i++){
			OracleVO vo = dataList.get(i);
			try{
				byte[] nameByte = hexStringToByteArray(vo.getName());
				TB_Big5_UCS2 Big5_UCS2 = new TB_Big5_UCS2();
				String BIG5_UCS2_table_name = "C:\\TB_BIG5_UCS2_SYS.bin";
				long Big5_UCS2_nRes = 0;
	
				// load table
				Big5_UCS2_nRes = Big5_UCS2.load(BIG5_UCS2_table_name);
				if (TBConvertDefine.CONVT_SUCCESS != Big5_UCS2_nRes) {
					System.out.println("TB_Big5_UCS2 load fail(" + Big5_UCS2_nRes + ")");
					continue;
				}
				// convert now
				ConvertBig5_UCS2 convertBig5_UCS2 = new ConvertBig5_UCS2();
				Big5_UCS2_nRes = convertBig5_UCS2.convert(Big5_UCS2, nameByte, nameByte.length);
				if (TBConvertDefine.CONVT_SUCCESS != Big5_UCS2_nRes) {
					System.out.println("ConvertBig5_UCS2 convert fail");
					continue;
				}
				char[] destBuf = convertBig5_UCS2.getResult();
				String UCS2dest = new String(destBuf);
				System.out.println(UCS2dest);
				vo.setName(UCS2dest);
				byte[] bb = UCS2dest.getBytes("UTF-16BE");// UTF-16BE
				for (byte c : bb) {
					System.out.printf("%02X ", c);
	
				}
				System.out.println();
				System.out.println("**********************TB_Big5_Ucs2 end**********************");
	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return dataList;
	}
	public static List<MakeWordVO> coverMSSQL2(List<MakeWordVO> dataList) {
		for(int i = 0; i < dataList.size(); i++){
			MakeWordVO vo = dataList.get(i);
			try{
				
//				byte[] nameByte = hexStringToByteArray(vo.getCompare());
				byte[] nameByte = vo.getName().getBytes("MS950");
				TB_Big5_UCS2 Big5_UCS2 = new TB_Big5_UCS2();
				String BIG5_UCS2_table_name = "C:\\TB_BIG5_UCS2_SYS.bin";
				long Big5_UCS2_nRes = 0;
	
				// load table
				Big5_UCS2_nRes = Big5_UCS2.load(BIG5_UCS2_table_name);
				if (TBConvertDefine.CONVT_SUCCESS != Big5_UCS2_nRes) {
					System.out.println("TB_Big5_UCS2 load fail(" + Big5_UCS2_nRes + ")");
					continue;
				}
				// convert now
				ConvertBig5_UCS2 convertBig5_UCS2 = new ConvertBig5_UCS2();
				Big5_UCS2_nRes = convertBig5_UCS2.convert(Big5_UCS2, nameByte, nameByte.length);
				if (TBConvertDefine.CONVT_SUCCESS != Big5_UCS2_nRes) {
					System.out.println("ConvertBig5_UCS2 convert fail");
					continue;
				}
				char[] destBuf = convertBig5_UCS2.getResult();
				String UCS2dest = new String(destBuf);
				System.out.println(UCS2dest);
				vo.setName(UCS2dest);
				byte[] bb = UCS2dest.getBytes("UTF-16BE");// UTF-16BE
				for (byte c : bb) {
					System.out.printf("%02X ", c);
	
				}
				System.out.println();
				System.out.println("**********************TB_Big5_Ucs2 end**********************");
	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return dataList;
	}
	public static OracleVO coverOracle(OracleVO vo) {
			try{
				
				byte[] nameByte = hexStringToByteArray(vo.getName());
				TB_Big5_UCS2 Big5_UCS2 = new TB_Big5_UCS2();
				String BIG5_UCS2_table_name = "C:\\TB_BIG5_UCS2_SYS.bin";
				long Big5_UCS2_nRes = 0;
	
				// load table
				Big5_UCS2_nRes = Big5_UCS2.load(BIG5_UCS2_table_name);
				if (TBConvertDefine.CONVT_SUCCESS != Big5_UCS2_nRes) {
					System.out.println("TB_Big5_UCS2 load fail(" + Big5_UCS2_nRes + ")");
					return vo;
				}
				// convert now
				ConvertBig5_UCS2 convertBig5_UCS2 = new ConvertBig5_UCS2();
				Big5_UCS2_nRes = convertBig5_UCS2.convert(Big5_UCS2, nameByte, nameByte.length);
				if (TBConvertDefine.CONVT_SUCCESS != Big5_UCS2_nRes) {
					System.out.println("ConvertBig5_UCS2 convert fail");
					return vo;
				}
				char[] destBuf = convertBig5_UCS2.getResult();
				String UCS2dest = new String(destBuf);
				System.out.println(UCS2dest);
				vo.setName(UCS2dest);
				byte[] bb = UCS2dest.getBytes("UTF-16BE");// UTF-16BE
				for (byte c : bb) {
					System.out.printf("%02X ", c);
	
				}
				System.out.println();
				System.out.println("**********************TB_Big5_Ucs2 end**********************");
	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		return vo;
	}
	
	public static String big5CoverUCS2(String str) {
		String UCS2dest = null;
		try{
//			byte[] nameByte = hexStringToByteArray(vo.getName());
			byte[] nameByte = str.getBytes("MS950");
			TB_Big5_UCS2 Big5_UCS2 = new TB_Big5_UCS2();
			String BIG5_UCS2_table_name = "C:\\TB_BIG5_UCS2_SYS.bin";
			long Big5_UCS2_nRes = 0;

			// load table
			Big5_UCS2_nRes = Big5_UCS2.load(BIG5_UCS2_table_name);
			if (TBConvertDefine.CONVT_SUCCESS != Big5_UCS2_nRes) {
				System.out.println("TB_Big5_UCS2 load fail(" + Big5_UCS2_nRes + ")");
				return null;
			}
			// convert now
			ConvertBig5_UCS2 convertBig5_UCS2 = new ConvertBig5_UCS2();
			Big5_UCS2_nRes = convertBig5_UCS2.convert(Big5_UCS2, nameByte, nameByte.length);
			if (TBConvertDefine.CONVT_SUCCESS != Big5_UCS2_nRes) {
				System.out.println("ConvertBig5_UCS2 convert fail");
				return null;
			}
			char[] destBuf = convertBig5_UCS2.getResult();
			UCS2dest = new String(destBuf);
			System.out.println(UCS2dest);
			byte[] bb = UCS2dest.getBytes("UTF-16BE");// UTF-16BE
			for (byte c : bb) {
				System.out.printf("%02X ", c);

			}
			System.out.println();
			System.out.println("**********************TB_Big5_Ucs2 end**********************");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	return UCS2dest;
}
	
	

	
	
	
	public static String conv(byte[] dataLineByte){
		String UCS2dest = null;
		try {
			TB_Big5_UCS2 Big5_UCS2 = new TB_Big5_UCS2();
			String BIG5_UCS2_table_name = "C:\\TB_BIG5_UCS2_SYS.bin";
			long Big5_UCS2_nRes = 0;
	
			// load table
			Big5_UCS2_nRes = Big5_UCS2.load(BIG5_UCS2_table_name);
			if (TBConvertDefine.CONVT_SUCCESS != Big5_UCS2_nRes) {
				System.out.println("TB_Big5_UCS2 load fail(" + Big5_UCS2_nRes + ")");
			}
			// convert now
			ConvertBig5_UCS2 convertBig5_UCS2 = new ConvertBig5_UCS2();
			Big5_UCS2_nRes = convertBig5_UCS2.convert(Big5_UCS2, dataLineByte, dataLineByte.length);
			if (TBConvertDefine.CONVT_SUCCESS != Big5_UCS2_nRes) {
				System.out.println("ConvertBig5_UCS2 convert fail");
			}
			char[] destBuf = convertBig5_UCS2.getResult();
			UCS2dest = new String(destBuf);
			System.out.println(byteArrayTohexString(UCS2dest.getBytes("UTF-16BE")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return UCS2dest;
	}
}
