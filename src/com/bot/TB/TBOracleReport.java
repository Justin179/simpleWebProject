package com.bot.TB;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.bot.TB.dao.OracleDao;
import com.bot.TB.vo.OracleVO;

/**
 * Servlet implementation class TBCReport
 */
@WebServlet("/TBOracleReport.do")
public class TBOracleReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(getClass());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TBOracleReport() {
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
		OracleDao dao = new OracleDao();
		try {
			OracleVO vo = dao.query(sn);
			vo = CoverUtil.coverOracle(vo);
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
