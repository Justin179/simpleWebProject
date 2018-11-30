package com.bot.TB.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.Map;

import com.bot.TB.vo.MakeWordVO;

public class MakeWordDao {
	public int save(MakeWordVO vo) throws Exception{
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connUrl = "jdbc:sqlserver://220.132.189.248:3940;databaseName=GNWEB_APDB";
		Connection conn = DriverManager.getConnection(connUrl, "necpoc", "necpoc123!");
		String sql = "insert into MAKEWORD(NAME, COMPARE)values(?, ?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, vo.getName());
		ps.setString(2, vo.getCompare());
		return ps.executeUpdate();
	}
	public List<MakeWordVO> query() throws Exception{
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connUrl = "jdbc:sqlserver://220.132.189.248:3940;databaseName=GNWEB_APDB";
		Connection conn = DriverManager.getConnection(connUrl, "necpoc", "necpoc123!");
		String sql = "select sn, NAME, COMPARE from MAKEWORD order by sn desc";
		PreparedStatement ps = conn.prepareStatement(sql);
		List<MakeWordVO> list = new ArrayList<MakeWordVO>();
		ResultSet rs = ps.executeQuery();
		MakeWordVO vo = null;
		while(rs.next()){
			vo = new MakeWordVO();
			vo.setSn(rs.getInt(1));
			vo.setName(rs.getString(2));
			vo.setCompare(rs.getString(3));
			list.add(vo);
		}
		return list;
	}
	public MakeWordVO query(int sn) throws Exception{
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connUrl = "jdbc:sqlserver://220.132.189.248:3940;databaseName=GNWEB_APDB";
		Connection conn = DriverManager.getConnection(connUrl, "necpoc", "necpoc123!");
		String sql = "select sn, NAME, COMPARE from MAKEWORD where sn = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, sn);
		ResultSet rs = ps.executeQuery();
		MakeWordVO vo = null;
		while(rs.next()){
			vo = new MakeWordVO();
			vo.setSn(rs.getInt(1));
			vo.setName(rs.getString(2));
			vo.setCompare(rs.getString(3));
		}
		return vo;
	}
}
