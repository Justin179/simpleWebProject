package com.bot.TB;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bot.TB.dao.MakeWordDao;
import com.bot.TB.vo.MakeWordVO;
import com.bot.TB.vo.OracleVO;

/**
 * Servlet implementation class TBCover
 */
@WebServlet("/TBCover.do")
public class TBCover extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public TBCover() {
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
		MakeWordDao dao = new MakeWordDao();
		MakeWordVO vo = new MakeWordVO();
//		String big5Name = CoverUtil.cover(name);
//		vo.setName(big5Name);
//		vo.setCompare(big5Name);
		vo.setName(name);
		vo.setCompare(name);
		try {
			int count = dao.save(vo);
			System.out.println(count);
			List<MakeWordVO> list = dao.query();
//			list = CoverUtil.coverMSSQL2(list);
			
			if(list != null && list.size() > 0){
				session.setAttribute("list", list);
			}else{
				session.setAttribute("list", null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		response.sendRedirect("index.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		MakeWordDao dao = new MakeWordDao();
		session.setAttribute("list", null);
		try {
			List<MakeWordVO> dataList = dao.query();
			List<MakeWordVO> list = CoverUtil.coverMSSQL2(dataList);
			if(list != null && list.size() > 0){
				session.setAttribute("list", list);
			}else{
				session.setAttribute("list", null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		response.sendRedirect("index.jsp");
	}

}
