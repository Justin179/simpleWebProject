package com.bot.TB;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;


import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;


public class JasperReportUtil2  {

	private Map parameters;
	
	private ArrayList result;
	//private JRDataSource _datasource; // page Grid data
	//private String _type;
	
	private String reportSrc = "";
	protected String fileName = "report"; // 設定檔名
	protected HttpServletResponse response; 
	protected String[] xlsSheetNames = {"ooo","222"}; // 設定xls sheet名稱
	
	private static final String TASK_PDF = "pdf";
	private static final String TASK_XLS = "xls";
	
	public void setParameters(Map parameters) {
		this.parameters = parameters;		
	}
	public void setResult(ArrayList result) {
		this.result = result;
	}
	public void setReportSrc(String reportSrc) {
		this.reportSrc = reportSrc;
	}
	public void setFileName(String fileName) {
		if(StringUtils.isNotBlank(fileName))
			this.fileName = fileName;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}


	public void printReportX(String dataType){

		InputStream is = null;

		try {
			is = srcToIs(reportSrc);

			final Map params;
			Map exportPara = null; // the exporter parameters which user set
			params = parameters;
			exportPara = (Map) params.remove("exportParameter");

			// fill the report //單筆資料
			JasperPrint jasperPrint = null;
			if (is != null && params != null) {
				JRDataSource _datasource = new JRMapCollectionDataSource(result);
				jasperPrint = JasperFillManager.fillReport(is, params, _datasource != null ? _datasource : new JREmptyDataSource());
				// logger.info("單筆資料轉jasperPrint");
			}

			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			OutputStream ouputStream = response.getOutputStream();

			
			// 分流Excel and PDF
			if (TASK_PDF.equals(dataType)) { // keep
				response.setCharacterEncoding("UTF-8");
				String responseName = URLEncoder.encode(fileName, "UTF-8");
				response.setContentType("application/pdf; charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + responseName + "\"");
				response.setHeader("Pragma", "private,no-cache");
				response.setHeader("Cache-control", "private,no-store,no-cache,max-age=0,must-revalidate");
				response.setHeader("Set-Cookie", "fileDownload=true; path=/");

				JRExporter exporter = new JRPdfExporter();
				// 判斷多筆or單筆
				if (jasperPrint != null) {
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
					exporter.exportReport();
				}

				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());

			} else if (TASK_XLS.equals(dataType)) {
				// output file set
				response.setContentType("application/vnd.ms-excel; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRXlsExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);

				// 判斷多筆or單筆
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

				// 測試
				exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);

				if (xlsSheetNames != null) {
					exporter.setParameter(JRXlsExporterParameter.SHEET_NAMES, xlsSheetNames);
				}

				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
			} else {
				throw new Exception("Type: " + dataType + " is not supported in JasperReports.");
			}
			
			
			
			// shared
			ouputStream.flush();
			ouputStream.close(); // 至此完成整個下載

		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			
			if (is != null) 
				try { is.close(); } catch (IOException e) {}

		}
		
	}
	

	
	private InputStream srcToIs(String src) throws FileNotFoundException {
		// get template file
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(src);
		if (is == null) {// try to load by file
			File fl = new File(src);
			if (!fl.exists())
				throw new RuntimeException("resource for " + src+ " not found.");
			
			is = new FileInputStream(fl);
		}

		return is;
	}

	


	
	
	
	
	
}
