package com.bot.TB;

import java.io.*;
import java.util.List;

import com.AStar.TBConvert.TBConvertDefine;
import com.AStar.TBConvert.Big5.ConvertBig5_UCS2;
import com.AStar.TBConvert.Big5.TB_Big5_UCS2;
import com.AStar.TBConvert.UCS2.ConvertUcs2_Big5;
import com.AStar.TBConvert.UCS2.TB_UCS2_Big5;
import com.bot.TB.dao.MakeWordDao;
import com.bot.TB.dao.OracleDao;
import com.bot.TB.vo.MakeWordVO;
import com.bot.TB.vo.OracleVO;

public class Cover {
	
	
	
	
	
	
	
	public static void main(String[] args) throws Exception{
		
	
		
		// 一寫
		OracleVO vo = new OracleVO();
		OracleDao dao = new OracleDao();
		
		String big5Name = CoverUtil.coverOracle("游錫堃"); // varchar
		String big5NameNvarchar = CoverUtil.coverOracle2("游錫堃"); // nvarchar
		
		vo.setSn(2222);
		vo.setName(big5Name);
		vo.setCompare(big5NameNvarchar);
		// vo.setContent(big5NameNvarchar);

		int count = dao.save(vo); // 寫進去 varchar 無法顯示難字
		
		
		
		// 一讀
		List<OracleVO> list = dao.query();
		list = CoverUtil.coverOracle2(list); // big5 -> unicode
		
		for(OracleVO ovo : list){
			System.out.println(ovo.getName());
			System.out.println(ovo.getCompare());
		}
		
		
		
//		try {
//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:\\ansi2.txt")), "MS950"));
//			String dataLine = null;
//			byte[] dataLineByte = null;
//			while((dataLine = br.readLine()) != null){
////				System.out.println(dataLine);
//				dataLineByte = dataLine.getBytes("MS950"); // 型轉碼
//				String Compare = conv(dataLineByte);
//				
//				MakeWordVO vo = new MakeWordVO();
//				vo.setCompare(Compare);
//				
//				
//				vo.setName(dataLine);
//				MakeWordDao dao = new MakeWordDao();
//				dao.save(vo);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return UCS2dest;
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
}

