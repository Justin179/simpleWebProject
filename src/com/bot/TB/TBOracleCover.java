package com.bot.TB;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bot.TB.dao.OracleDao;
import com.bot.TB.vo.OracleVO;

/**
 * Servlet implementation class TBCover
 */
@WebServlet("/TBOracleCover.do")
public class TBOracleCover extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public TBOracleCover() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		System.out.println("CharacterEncoding:"+request.getCharacterEncoding());
		HttpSession session = request.getSession();
		session.setAttribute("list", null);
		OracleDao dao = new OracleDao();
		OracleVO vo = new OracleVO();
		int sn = dao.queryMaxSn();
		
		
		String big5Name = CoverUtil.coverOracle(name);
		
		
		vo.setSn(sn+1);
		vo.setName(big5Name);
		vo.setCompare(name);
		try {
			
			
			int count = dao.save(vo); // save big5
			
			
			List<OracleVO> list = dao.query(); // big5
			
			list = CoverUtil.coverOracle2(list); // big5 -> unicode
			
			
			if(list != null && list.size() > 0){
				session.setAttribute("list", list);
			}else{
				session.setAttribute("list", null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		response.sendRedirect("index2.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		OracleDao dao = new OracleDao();
		session.setAttribute("list", null);
		try {
			List<OracleVO> list = dao.query();
			list = CoverUtil.coverOracle2(list);
			if(list != null && list.size() > 0){
				session.setAttribute("list", list);
			}else{
				session.setAttribute("list", null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		response.sendRedirect("index2.jsp");
	}
	
}
