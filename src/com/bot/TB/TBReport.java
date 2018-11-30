package com.bot.TB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.bot.TB.dao.MakeWordDao;
import com.bot.TB.vo.MakeWordVO;

/**
 * Servlet implementation class TBCReport
 */
@WebServlet("/TBReport.do")
public class TBReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(getClass());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TBReport() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JasperReportUtil jasper = new JasperReportUtil();
		String webLocalPath = request.getSession().getServletContext().getRealPath("/");
		String jasperPath = "report.jasper";
		String s = request.getParameter("sn");
		int sn = Integer.parseInt(s);
		Map<String, Object> parametersMap = new HashMap<String, Object>();
		MakeWordDao dao = new MakeWordDao();
		try {
			MakeWordVO vo = dao.query(sn);
//			vo.setName(CoverUtil.big5CoverUCS2(vo.getName()));
//			vo.setCompare(CoverUtil.big5CoverUCS2(vo.getCompare()));
			parametersMap.put("name", vo.getName());
			parametersMap.put("name1", vo.getName());
			parametersMap.put("compare", vo.getCompare());
			parametersMap.put("compare1", vo.getCompare());
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			//set jasper
			jasper.setCheck(true);
			jasper.setParameters(parametersMap);
			jasper.setReportSrc(webLocalPath+jasperPath);
			jasper.setFileName("report.pdf");
			jasper.setResponse(response);
			jasper.printReportType("pdf", "inline");
//			byte[] fileByte = jasper.printReportType("pdf");
//			FileOutputStream fos = new FileOutputStream("C:\\aaa.pdf");
//			fos.write(fileByte);
//			fos.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			webLocalPath = null;
			jasperPath = null;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
