package com.bot.TB;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
@SuppressWarnings({"rawtypes", "unused", "unchecked"})
public class JasperReport {
	private Logger logger = Logger.getLogger(getClass());
	private static final String TASK_PDF = "pdf";
	private static final String TASK_XML = "xml";
	private static final String TASK_HTML = "html";
	private static final String TASK_XLS = "xls";
	private static final String TASK_DOCX = "docx";
	private static final String TASK_CSV = "csv";
	private static final String IMAGE_DIR = "img/";
	private int _medver;
	private String _type = TASK_PDF; // file type
	private Locale _locale; // i18n

	private String _src; // jasper template
	
	private Map _parameters; // page header data
	private JRDataSource _datasource; // page Grid data

	private String[] _srcArray; // jasper template//多筆
	private Map[] _paraArray; // page header data//多筆
	private JRDataSource[] _datasourceArray; // page Grid data//多筆

	protected String[] xlsSheetNames; // 設定xls sheet名稱

	protected String fileName = "report"; // 設定檔名

	private transient Map _imageMap;

	protected HttpServletResponse response; // 設定檔名

	public JasperReport() {

	}

	public JasperReport(String src) {
		setSrc(src);
	}

	public String getSrc() {
		return _src;
	}

	public void setSrc(String src) {
		if (src != null && src.length() == 0) {
			src = null;
		} else {
			_src = src;
		}
	}

	public Map getReportParameters() {
		return _parameters;
	}

	public void setReportParameters(Map parameters) {
		_parameters = parameters;
	}

	public JRDataSource getDatasource() {
		return _datasource;
	}

	public void setDatasource(JRDataSource dataSource) {
		_datasource = dataSource;
	}

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		if (type == null) {
			type = "pdf";
		} else {
			_type = type;
		}
	}

	public String[] getXlsSheetNames() {
		return xlsSheetNames;
	}

	public void setXlsSheetNames(String[] xlsSheetNames) {
		if (xlsSheetNames != null) {
			if (xlsSheetNames.length != 0) {
				this.xlsSheetNames = xlsSheetNames;
			} else {
				this.xlsSheetNames = null;
			}
		}
	}

	protected void setJasperData(String[] srcArray, Map[] paraArray
			, JRDataSource[] datasourceArray) {
		if (srcArray != null && srcArray.length == 0) {
			srcArray = null;
		}

		this._srcArray = srcArray;
		_paraArray = paraArray;
		_datasourceArray = datasourceArray;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public byte[] doReport() {
		InputStream is = null;
		InputStream[] isArray = null;

		try {
			// get template file
			if (StringUtils.isNotBlank(_src)) {
				is = srcToIs(_src);
			} else if (_srcArray != null && _srcArray.length != 0) {
				List<InputStream> isList = new ArrayList<InputStream>();
				for (int i = 0; i < _srcArray.length; i++) {
					isList.add(srcToIs(_srcArray[i]));
				}

				isArray = isList.toArray(new InputStream[isList.size()]);
			}

			// 暫無判斷多筆狀況
			// Default value
			final Map params;
			Map exportPara = null; // the exporter parameters which user set

			if (_parameters == null)
				params = new HashMap();
			else {
				params = _parameters;
				exportPara = (Map) params.remove("exportParameter");
			}

			// 暫無判斷多筆狀況
			if (_locale != null)
				params.put(JRParameter.REPORT_LOCALE, _locale);
			// else if (!params.containsKey(JRParameter.REPORT_LOCALE))
			// params.put(JRParameter.REPORT_LOCALE, Locales.getCurrent());

			// fill the report //單筆資料
			JasperPrint jasperPrint = null;
			if (is != null && params != null) {
				jasperPrint = JasperFillManager.fillReport(is, params, _datasource != null ? _datasource : new JREmptyDataSource());
			}

			// fill the report //多筆資料
			List<JasperPrint> jasperPrintList = null;
			if (isArray != null && _paraArray != null) {
				jasperPrintList = getJasperPrintList(isArray, _paraArray, _datasourceArray);
			}

			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			OutputStream ouputStream = response.getOutputStream();

			// export one type of report
			if (TASK_PDF.equals(_type)) {
				// String path = "C:\\AGNFile";
				// output file set
				response.setCharacterEncoding("UTF-8");
				String responseName = URLEncoder.encode(fileName, "UTF-8");
				response.setContentType("application/pdf; charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + responseName + "\"");
				response.setHeader("Pragma", "private,no-cache");
				response.setHeader("Cache-control", "private,no-store,no-cache,max-age=0,must-revalidate");

				JRExporter exporter = new JRPdfExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				// exporter.setParameter(JRExporterParameter.JASPER_PRINT,
				// jasperPrint);

				// 判斷多筆or單筆
				if (jasperPrint != null) {
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				} else if (jasperPrintList != null) {
					exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
				}

				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.exportReport();
				arrayOutputStream.close();
				// FileOutputStream out = new
				// FileOutputStream("C:\\AGNFile\\"+fileName);
				// out.write(arrayOutputStream.toByteArray());
				// out.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else if (TASK_XML.equals(_type)) {
				// output file set
				response.setContentType("application/xml; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRXmlExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else if (TASK_HTML.equals(_type)) {
				// output file set
				response.setContentType("text/html; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRHtmlExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, new HashMap());
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, IMAGE_DIR);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
			} else if (TASK_XLS.equals(_type)) {
				// output file set
				response.setContentType("application/vnd.ms-excel; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRXlsExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);

				// 判斷多筆or單筆
				if (jasperPrint != null) {
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				} else if (jasperPrintList != null) {
					exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
				}

				// 測試
				exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);

				if (xlsSheetNames != null) {
					exporter.setParameter(JRXlsExporterParameter.SHEET_NAMES
							, xlsSheetNames);
				}

				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else if (TASK_DOCX.equals(_type)) {
				// output file set
				response.setContentType("application/vnd.ms-word; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRDocxExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else if (TASK_CSV.equals(_type)) {
				// output file set
				response.setContentType("text/csv; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRCsvExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "Big5");
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else {
				throw new Exception("Type: " + _type
						+ " is not supported in JasperReports.");
			}

			ouputStream.flush();
			ouputStream.close();

			return arrayOutputStream.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warn(ex.getMessage());
			return "".getBytes();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.warn("Ignored: unable to close", e);
				}
			}
		}
	}

	private List<JasperPrint> getJasperPrintList(InputStream[] isArray, Map[] paraArray
			, JRDataSource[] datasourceArray) throws JRException {
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();

		for (int i = 0; i < isArray.length; i++) {
			jasperPrintList.add(JasperFillManager
					.fillReport(isArray[i], paraArray[i],
							datasourceArray[i] != null ? datasourceArray[i] : new JREmptyDataSource()));
		}

		return jasperPrintList;
	}

	private InputStream srcToIs(String src) throws FileNotFoundException {
		// get template file
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(src);
		if (is == null) {// try to load by file
			File fl = new File(src);
			if (!fl.exists())
				throw new RuntimeException("resource for " + src
						+ " not found.");
			is = new FileInputStream(fl);
		}

		return is;
	}

	// 修改成另開視窗下載 20151218 ducnan
	public byte[] doReport(String attachment) {
		InputStream is = null;
		InputStream[] isArray = null;

		try {
			// get template file
			if (StringUtils.isNotBlank(_src)) {
				is = srcToIs(_src);
			} else if (_srcArray != null && _srcArray.length != 0) {
				List<InputStream> isList = new ArrayList<InputStream>();
				for (int i = 0; i < _srcArray.length; i++) {
					isList.add(srcToIs(_srcArray[i]));
				}

				isArray = isList.toArray(new InputStream[isList.size()]);
			}

			// 暫無判斷多筆狀況
			// Default value
			final Map params;
			Map exportPara = null; // the exporter parameters which user set

			if (_parameters == null)
				params = new HashMap();
			else {
				params = _parameters;
				exportPara = (Map) params.remove("exportParameter");
			}

			// 暫無判斷多筆狀況
			if (_locale != null)
				params.put(JRParameter.REPORT_LOCALE, _locale);
			// else if (!params.containsKey(JRParameter.REPORT_LOCALE))
			// params.put(JRParameter.REPORT_LOCALE, Locales.getCurrent());

			// fill the report //單筆資料
			JasperPrint jasperPrint = null;
			if (is != null && params != null) {
				jasperPrint = JasperFillManager.fillReport(is, params, _datasource != null ? _datasource : new JREmptyDataSource());
				logger.info("單筆資料轉jasperPrint");
			}

			// fill the report //多筆資料
			List<JasperPrint> jasperPrintList = null;
			if (isArray != null && _paraArray != null) {
				jasperPrintList = getJasperPrintList(isArray, _paraArray, _datasourceArray);
				logger.info("多筆資料轉jasperPrint List");
			}

			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			OutputStream ouputStream = response.getOutputStream();

			// export one type of report
			if (TASK_PDF.equals(_type)) {
				// String path = "C:\\AGNFile";
				// output file set
				response.setCharacterEncoding("UTF-8");
				String responseName = URLEncoder.encode(fileName, "UTF-8");
				response.setContentType("application/pdf; charset=UTF-8");
				response.setHeader("Content-Disposition", "\"" + attachment + "; filename=\"" + responseName + "\"");
				response.setHeader("Cache-Control", "private");
				response.setHeader("Accept-Ranges", "none");
				logger.info("filename = " + fileName);
				JRExporter exporter = new JRPdfExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);

				// 判斷多筆or單筆
				if (jasperPrint != null) {
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				} else if (jasperPrintList != null) {
					exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
				}

				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.exportReport();
				arrayOutputStream.close();
				// FileOutputStream out = new
				// FileOutputStream("C:\\AGNFile\\"+fileName);
				// out.write(arrayOutputStream.toByteArray());
				// out.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else if (TASK_XML.equals(_type)) {
				// output file set
				response.setContentType("application/xml; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRXmlExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else if (TASK_HTML.equals(_type)) {
				// output file set
				response.setContentType("text/html; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRHtmlExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, new HashMap());
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, IMAGE_DIR);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
			} else if (TASK_XLS.equals(_type)) {
				// output file set
				response.setContentType("application/vnd.ms-excel; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRXlsExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);

				// 判斷多筆or單筆
				if (jasperPrint != null) {
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				} else if (jasperPrintList != null) {
					exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
				}

				// 測試
				exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);

				if (xlsSheetNames != null) {
					exporter.setParameter(JRXlsExporterParameter.SHEET_NAMES
							, xlsSheetNames);
				}

				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else if (TASK_DOCX.equals(_type)) {
				// output file set
				response.setContentType("application/vnd.ms-word; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRDocxExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else if (TASK_CSV.equals(_type)) {
				// output file set
				response.setContentType("text/csv; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRCsvExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "Big5");
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else {
				throw new Exception("Type: " + _type
						+ " is not supported in JasperReports.");
			}

			ouputStream.flush();
			ouputStream.close();

			return arrayOutputStream.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warn(ex.getMessage());
			return "".getBytes();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.warn("Ignored: unable to close", e);
				}
			}
		}
	}

	// 修改成另開視窗下載 20151218 ducnan
	public byte[] doReport(String attachment, boolean isEncrypted) {
		InputStream is = null;
		InputStream[] isArray = null;

		try {
			// get template file
			if (StringUtils.isNotBlank(_src)) {
				is = srcToIs(_src);
			} else if (_srcArray != null && _srcArray.length != 0) {
				List<InputStream> isList = new ArrayList<InputStream>();
				for (int i = 0; i < _srcArray.length; i++) {
					isList.add(srcToIs(_srcArray[i]));
				}

				isArray = isList.toArray(new InputStream[isList.size()]);
			}

			// 暫無判斷多筆狀況
			// Default value
			final Map params;
			Map exportPara = null; // the exporter parameters which user set

			if (_parameters == null)
				params = new HashMap();
			else {
				params = _parameters;
				exportPara = (Map) params.remove("exportParameter");
			}

			// 暫無判斷多筆狀況
			if (_locale != null)
				params.put(JRParameter.REPORT_LOCALE, _locale);
			// else if (!params.containsKey(JRParameter.REPORT_LOCALE))
			// params.put(JRParameter.REPORT_LOCALE, Locales.getCurrent());

			// fill the report //單筆資料
			JasperPrint jasperPrint = null;
			if (is != null && params != null) {
				jasperPrint = JasperFillManager.fillReport(is, params, _datasource != null ? _datasource : new JREmptyDataSource());
				logger.info("單筆資料轉jasperPrint");
			}

			// fill the report //多筆資料
			List<JasperPrint> jasperPrintList = null;
			if (isArray != null && _paraArray != null) {
				jasperPrintList = getJasperPrintList(isArray, _paraArray, _datasourceArray);
				logger.info("多筆資料轉jasperPrint List");
			}

			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			OutputStream ouputStream = response.getOutputStream();

			// export one type of report
			if (TASK_PDF.equals(_type)) {
				if ("attachment".equals(attachment)) {
					// attachment
					// output file set
					response.setCharacterEncoding("UTF-8");
					String responseName = URLEncoder.encode(fileName, "UTF-8");
					response.setContentType("application/pdf; charset=UTF-8");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + responseName + "\"");
					response.setHeader("Pragma", "private,no-cache");
					response.setHeader("Cache-control", "private,no-store,no-cache,max-age=0,must-revalidate");
					logger.info("setCookie");
					response.setHeader("Set-Cookie", "fileDownload=true; path=/");

					logger.info("filename = " + fileName);
					JRExporter exporter = new JRPdfExporter();
					if (exportPara != null)
						exporter.setParameters(exportPara);
					// exporter.setParameter(JRExporterParameter.JASPER_PRINT,
					// jasperPrint);

					// 判斷多筆or單筆
					if (jasperPrint != null) {
						exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					} else if (jasperPrintList != null) {
						exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
					}

					exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
					// Encrypted
					if (isEncrypted) {
						exporter.setParameter(JRPdfExporterParameter.IS_ENCRYPTED, Boolean.TRUE);
						exporter.setParameter(JRPdfExporterParameter.IS_128_BIT_KEY, Boolean.TRUE);
						SimpleDateFormat sdf = new SimpleDateFormat("MMdd");

						// exporter.setParameter(JRPdfExporterParameter.PERMISSIONS,
						// new Integer (PdfWriter.ALLOW_COPY));
						// exporter.setParameter(JRPdfExporterParameter.METADATA_TITLE,
						// "How to produce encrypted PDF reports");
						// exporter.setParameter(JRPdfExporterParameter.METADATA_CREATOR,
						// "JasperReports/Java");
						// exporter.setParameter(JRPdfExporterParameter.METADATA_KEYWORDS,
						// "metadata, JasperReports, encrypt, PDF");
						// exporter.setParameter(JRPdfExporterParameter.METADATA_SUBJECT,
						// "How to produce encrypted PDF with JasperReports");
						// exporter.setParameter(JRPdfExporterParameter.METADATA_AUTHOR,
						// "Howard");
					}
					exporter.exportReport();
					arrayOutputStream.close();
					// FileOutputStream out = new
					// FileOutputStream("C:\\AGNFile\\"+fileName);
					// out.write(arrayOutputStream.toByteArray());
					// out.close();
					ouputStream.write(arrayOutputStream.toByteArray());
					// _imageMap = (Map)
					// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);

				} else {
					// inline
					// output file set
					response.setCharacterEncoding("UTF-8");
					String responseName = URLEncoder.encode(fileName, "UTF-8");
					response.setContentType("application/pdf; charset=UTF-8;name=" + responseName);
					response.setHeader("Content-Disposition", "\"" + attachment + "; filename=\"" + responseName + "\"");
					response.setHeader("Cache-Control", "private");
					response.setHeader("Accept-Ranges", "none");
					logger.info("filename = " + fileName);
					JRExporter exporter = new JRPdfExporter();
					if (exportPara != null)
						exporter.setParameters(exportPara);

					// 判斷多筆or單筆
					if (jasperPrint != null) {
						exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					} else if (jasperPrintList != null) {
						exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
					}

					exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);

					if (isEncrypted) {
						exporter.setParameter(JRPdfExporterParameter.IS_ENCRYPTED, Boolean.TRUE);
						exporter.setParameter(JRPdfExporterParameter.IS_128_BIT_KEY, Boolean.TRUE);
						SimpleDateFormat sdf = new SimpleDateFormat("MMdd");

						// exporter.setParameter(JRPdfExporterParameter.PERMISSIONS,
						// new Integer (PdfWriter.ALLOW_COPY));
						// exporter.setParameter(JRPdfExporterParameter.METADATA_TITLE,
						// "How to produce encrypted PDF reports");
						// exporter.setParameter(JRPdfExporterParameter.METADATA_CREATOR,
						// "JasperReports/Java");
						// exporter.setParameter(JRPdfExporterParameter.METADATA_KEYWORDS,
						// "metadata, JasperReports, encrypt, PDF");
						// exporter.setParameter(JRPdfExporterParameter.METADATA_SUBJECT,
						// "How to produce encrypted PDF with JasperReports");
						// exporter.setParameter(JRPdfExporterParameter.METADATA_AUTHOR,
						// "Howard");
					}

					exporter.exportReport();
					arrayOutputStream.close();
					// FileOutputStream out = new
					// FileOutputStream("C:\\AGNFile\\"+fileName);
					// out.write(arrayOutputStream.toByteArray());
					// out.close();
					ouputStream.write(arrayOutputStream.toByteArray());
					// _imageMap = (Map)
					// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
				}
			} else if (TASK_XML.equals(_type)) {
				// output file set
				response.setContentType("application/xml; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRXmlExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else if (TASK_HTML.equals(_type)) {
				// output file set
				response.setContentType("text/html; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRHtmlExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, new HashMap());
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, IMAGE_DIR);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
			} else if (TASK_XLS.equals(_type)) {
				// output file set
				response.setContentType("application/vnd.ms-excel; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRXlsExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);

				// 判斷多筆or單筆
				if (jasperPrint != null) {
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				} else if (jasperPrintList != null) {
					exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
				}

				// 測試
				exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);

				if (xlsSheetNames != null) {
					exporter.setParameter(JRXlsExporterParameter.SHEET_NAMES
							, xlsSheetNames);
				}

				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else if (TASK_DOCX.equals(_type)) {
				// output file set
				response.setContentType("application/vnd.ms-word; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRDocxExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else if (TASK_CSV.equals(_type)) {
				// output file set
				response.setContentType("text/csv; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

				JRExporter exporter = new JRCsvExporter();
				if (exportPara != null)
					exporter.setParameters(exportPara);
				exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "Big5");
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, arrayOutputStream);
				exporter.exportReport();
				arrayOutputStream.close();
				ouputStream.write(arrayOutputStream.toByteArray());
				// _imageMap = (Map)
				// exporter.getParameter(JRHtmlExporterParameter.IMAGES_MAP);
			} else {
				throw new Exception("Type: " + _type
						+ " is not supported in JasperReports.");
			}

			ouputStream.flush();
			ouputStream.close();

			return arrayOutputStream.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warn(ex.getMessage());
			return "".getBytes();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.warn("Ignored: unable to close", e);
				}
			}
		}
	}

}
