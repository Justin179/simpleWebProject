package com.bot.TB;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class JasperReportUtil extends JasperReport {
	private Logger logger = Logger.getLogger(this.getClass());
	
	public static final String ON_CLICK_JASPERBTN_All = "onClickJasperBtn";
	
	public static final String ON_CLICK_JASPERBTN_PDF = "onClickJasperBtn1";

	@SuppressWarnings("rawtypes")
	private Map parameters;
	@SuppressWarnings("rawtypes")
	private ArrayList result;
	private String reportSrc = "";
	
	@SuppressWarnings("rawtypes")
	private Map[] paraArray;
	@SuppressWarnings("rawtypes")
	private ArrayList[] resultArray;
	private String[] reportSrcArray;
		
	private boolean isCheck = false;
	
	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	@SuppressWarnings("rawtypes")
	public void setJasperData(String[] reportSrcArray, Map[] paraArray
			, ArrayList[] resultArray){
		this.reportSrcArray = reportSrcArray;
		this.paraArray = paraArray;
		this.resultArray = resultArray;
	}

	@SuppressWarnings("rawtypes")
	public void setParameters(Map parameters) {
		this.parameters = parameters;		
	}
	
	@SuppressWarnings("rawtypes")
	public void setParameters(Map parameters, boolean debug) {
		if(debug)logger.info("[DEBUG]JasperReport,sparameters=="+parameters);
		setParameters(parameters);	
	}

	@SuppressWarnings("rawtypes")
	public void setResult(ArrayList result) {
		this.result = result;
	}

	public void setReportSrc(String reportSrc) {
		this.reportSrc = reportSrc;
	}
	
	public void setFileName(String fileName) {
		if(StringUtils.isNotBlank(fileName)){
			super.fileName = fileName;
		}
	}
	
	public void setResponse(HttpServletResponse response) {
		super.response = response;
	}
	
	public void setXlsSheetNames(String[] xlsSheetNames) {
		if(xlsSheetNames!=null){
			if(xlsSheetNames.length!=0)
			super.xlsSheetNames = xlsSheetNames;
		}
	}

	/**
	 * 前端直接發request呼叫，不須window.open另開
	 * @param type ex:pdf
	 * @return
	 * @throws FileNotFoundException
	 */
	public byte[] printReportType(String type) throws FileNotFoundException {	
		return printReport(type);		
	}
	/**
	 * 前端使用window.open，把檔案寫入新視窗
	 * @param type ex:pdf
	 * @param attachment 給定inline
	 * @return
	 * @throws FileNotFoundException
	 */
	public byte[] printReportType(String type,String attachment) throws FileNotFoundException {	
		return printReport(type,attachment);		
	}
	/**
	 * 
	 * @param type ex:pdf
	 * @param attachment 給定 attachment(直接呼叫) or inline(需搭配window.open)
	 * @param isEncrypted 給予加密(自動產生密碼) 
	 * @return
	 * @throws FileNotFoundException
	 */
	public byte[] printReportType(String type,String attachment,boolean isEncrypted) throws FileNotFoundException {	
		return printReport(type,attachment,isEncrypted);		
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private byte[] printReport(String dataType){
		if(parameters==null){
			parameters = new HashMap<String, String>();
			logger.info("Page Header無資料匯入");
		}
		
		if(result==null){
			result = new ArrayList();
			result.add(new HashMap<String, String>());
			logger.info("Page Grid無資料匯入");
		}else{
			if(result.size()==0){
				result = new ArrayList();
				result.add(new HashMap<String, String>());
				logger.info("Page Grid無查詢資料");
			}
		}
		
		if(StringUtils.isNotBlank(reportSrc)){
			reportSrcArray = null;
		}else if(reportSrcArray!=null){
			reportSrc = null;
		}else{
			logger.info("無jasper frame檔案匯入");
			return "".getBytes();
		}
		
		if(reportSrc!=null){
			super.setReportParameters(parameters);
			super.setDatasource(new JRMapCollectionDataSource(result));
			super.setSrc(reportSrc);
			super.setType(dataType);
			
			logger.info("匯入Jasper單筆資料");
		}else{
			List<JRMapCollectionDataSource> jRMapList = new ArrayList<JRMapCollectionDataSource>();
			for(int i=0 ; i<resultArray.length ; i++){
				jRMapList.add(new JRMapCollectionDataSource(resultArray[i]));
			}
			
			super.setSrc(null);
			super.setType(dataType);
			super.setJasperData(reportSrcArray, paraArray
					, jRMapList.toArray(new JRMapCollectionDataSource[jRMapList.size()]));
			
			logger.info("匯入Jasper多筆資料");
		}
		
		
		
		return doReport();		
	}

	
	
	
	/**
	 *改寫成另開視窗pdf	
	 * 
	 * @param dataType
	 * @param attchment為下載再讓user點、inline就是另開視窗
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private byte[] printReport(String dataType,String attachment){
		if(parameters==null){
			parameters = new HashMap<String, String>();
			logger.info("Page Header無資料匯入");
		}
		
		if(result==null){
			result = new ArrayList();
			result.add(new HashMap<String, String>());
			logger.info("Page Grid無資料匯入");
		}else{
			if(result.size()==0){
				result = new ArrayList();
				result.add(new HashMap<String, String>());
				logger.info("Page Grid無查詢資料");
			}
		}
		
		if(StringUtils.isNotBlank(reportSrc)){
			reportSrcArray = null;
		}else if(reportSrcArray!=null){
			reportSrc = null;
		}else{
			logger.info("無jasper frame檔案匯入");
			return "".getBytes();
		}
		
		if(reportSrc!=null){
			super.setReportParameters(parameters);
			super.setDatasource(new JRMapCollectionDataSource(result));
			super.setSrc(reportSrc);
			super.setType(dataType);
			
			logger.info("匯入Jasper單筆資料");
		}else{
			List<JRMapCollectionDataSource> jRMapList = new ArrayList<JRMapCollectionDataSource>();
			for(int i=0 ; i<resultArray.length ; i++){
				jRMapList.add(new JRMapCollectionDataSource(resultArray[i]));
			}
			
			super.setSrc(null);
			super.setType(dataType);
			super.setJasperData(reportSrcArray, paraArray
					, jRMapList.toArray(new JRMapCollectionDataSource[jRMapList.size()]));
			
			logger.info("匯入Jasper多筆資料");
		}
		
		
		
		return doReport(attachment);		
	}

	
	
	/**
	 *改寫成另開視窗pdf	
	 * 
	 * @param dataType
	 * @param attchment為下載再讓user點、inline就是另開視窗
	 * @param isEncrypted 是否加密
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private byte[] printReport(String dataType,String attachment,boolean isEncrypted){
		if(parameters==null){
			parameters = new HashMap<String, String>();
			logger.info("Page Header無資料匯入");
		}
		
		if(result==null){
			result = new ArrayList();
			result.add(new HashMap<String, String>());
			logger.info("Page Grid無資料匯入");
		}else{
			if(result.size()==0){
				result = new ArrayList();
				result.add(new HashMap<String, String>());
				logger.info("Page Grid無查詢資料");
			}
		}
		
		if(StringUtils.isNotBlank(reportSrc)){
			reportSrcArray = null;
		}else if(reportSrcArray!=null){
			reportSrc = null;
		}else{
			logger.info("無jasper frame檔案匯入");
			return "".getBytes();
		}
		
		if(reportSrc!=null){
			super.setReportParameters(parameters);
			super.setDatasource(new JRMapCollectionDataSource(result));
			super.setSrc(reportSrc);
			super.setType(dataType);
			
			logger.info("匯入Jasper單筆資料");
		}else{
			List<JRMapCollectionDataSource> jRMapList = new ArrayList<JRMapCollectionDataSource>();
			for(int i=0 ; i<resultArray.length ; i++){
				jRMapList.add(new JRMapCollectionDataSource(resultArray[i]));
			}
			
			super.setSrc(null);
			super.setType(dataType);
			super.setJasperData(reportSrcArray, paraArray
					, jRMapList.toArray(new JRMapCollectionDataSource[jRMapList.size()]));
			
			logger.info("匯入Jasper多筆資料");
		}
		
		
		
		return doReport(attachment,isEncrypted);		
	}

	
	
	
	
	
}
