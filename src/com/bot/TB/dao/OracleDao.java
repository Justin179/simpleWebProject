package com.bot.TB.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.bot.TB.CoverUtil;
import com.bot.TB.vo.MakeWordVO;
import com.bot.TB.vo.OracleVO;

public class OracleDao {
	
	// 一寫
	public int save(OracleVO vo) {
		int count = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			
			
			String connUrl = "jdbc:oracle:thin:@10.1.9.75:1521:testdb";
			conn = DriverManager.getConnection(connUrl, "GNWEB_APUSER", "GNWEB_APUSER");
			
//			String connUrl = "jdbc:oracle:thin:@220.132.189.248:3939:gnwpdb";
//			conn = DriverManager.getConnection(connUrl, "necpoc", "necpoc123!");
			
			
			String big5Str =  vo.getCompare();
			List<String> list = new ArrayList<String>();  
			for(int i = 0; i<big5Str.length(); i+=4){
				int j = i+4;
				String section = StringUtils.substring(big5Str, i, j);
				list.add(section);
			}
			System.out.println(list);
			StringBuilder sb = new StringBuilder(" UNISTR('\\");
			for(int i = 0; i<list.size(); i++){
				sb.append(list.get(i));
				if( (list.size()-1) == i ){
					sb.append("') "); // 最後一個
				}else{
					sb.append("') || UNISTR('\\");
				}
			}
			
			
			String sql = "insert into MAKEWORD(SN, NAME, COMPARE, CONTENT)values(?, UTL_RAW.CAST_TO_VARCHAR2(?), "
					+ sb.toString()
					+ ", '')";
			
			ps = conn.prepareStatement(sql);
			ps.setInt(1, vo.getSn());
			ps.setString(2, vo.getName()); // big5Name
			
			// ps.setString(3, sb.toString());
			
			count = ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(ps != null) ps.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
		
	}
	
	// 一讀
	public List<OracleVO> query() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		OracleVO vo = null;
		List<OracleVO> list = new ArrayList<OracleVO>();
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			
			
			String connUrl = "jdbc:oracle:thin:@10.1.9.75:1521:testdb";
			conn = DriverManager.getConnection(connUrl, "GNWEB_APUSER", "GNWEB_APUSER");
			
//			String connUrl = "jdbc:oracle:thin:@220.132.189.248:3939:gnwpdb";
//			conn = DriverManager.getConnection(connUrl, "necpoc", "necpoc123!");
			
			
			String sql = "select SN, UTL_RAW.CAST_TO_RAW(NAME), COMPARE, CONTENT from MAKEWORD order by sn desc";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				vo = new OracleVO();
				vo.setSn(rs.getInt(1));
				vo.setName(rs.getString(2));
				vo.setCompare(rs.getString(3));
				vo.setContent(rs.getString(4));
				list.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public int queryMaxSn() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		OracleVO vo = null;
		int sn = 0;
		try {
			Class.forName("oracle.jdbc.OracleDriver");
//			String connUrl = "jdbc:oracle:thin:@10.1.9.75:1521:testdb";
//			conn = DriverManager.getConnection(connUrl, "GNWEB_APUSER", "GNWEB_APUSER");
			String connUrl = "jdbc:oracle:thin:@220.132.189.248:3939:gnwpdb";
			conn = DriverManager.getConnection(connUrl, "necpoc", "necpoc123!");
			String sql = "select nvl(max(sn), 0) as sn from MAKEWORD";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				sn = rs.getInt(1);
			}
			System.out.println("sn:"+sn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return sn;
	}
	

	
	public OracleVO query(int sn) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		OracleVO vo = new OracleVO();
		try {
			Class.forName("oracle.jdbc.OracleDriver");
//			String connUrl = "jdbc:oracle:thin:@10.1.9.75:1521:testdb";
//			conn = DriverManager.getConnection(connUrl, "GNWEB_APUSER", "GNWEB_APUSER");
			String connUrl = "jdbc:oracle:thin:@220.132.189.248:3939:gnwpdb";
			conn = DriverManager.getConnection(connUrl, "necpoc", "necpoc123!");
			String sql = "select SN, UTL_RAW.CAST_TO_RAW(NAME), COMPARE, CONTENT from MAKEWORD where sn = ?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, sn);
			rs = ps.executeQuery();
			while(rs.next()){
				vo = new OracleVO();
				vo.setSn(rs.getInt(1));
				vo.setName(rs.getString(2));
				vo.setCompare(rs.getString(3));
				vo.setContent(rs.getString(4));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return vo;
	}
}
