<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<script type="text/javascript" src="<c:url value='/js/jquery-1.11.0.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/Astar.LibMisc.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/Astar.WebEditorLib.js'/>"></script>
<script type="text/javascript">
	function doReport(sn){
		$("#sn").val(sn);
		document.forms[0].target = "_new";
	    document.forms[0].action = '${pageContext.request.contextPath}/TBOracleReport.do?sn='+sn;
	    document.forms[0].submit();
	}
	
	function postSubmit(){
		document.forms[0].method = "get";
		document.forms[0].action = '${pageContext.request.contextPath}/TBOracleCover.do';
	    document.forms[0].submit();
	}
	
	function commit(){
		document.forms[0].method = "post";
		document.forms[0].action = '${pageContext.request.contextPath}/TBOracleCover.do';
	    document.forms[0].submit();
	}
</script>
<%-- astar start --%>
	<script type="text/javascript">
        var gEditors = new Array();
        function InitPage() {
            gEditors.push(new AstarWebEditor('name'));
        }
        function CheckSubmit() {
            ConvertBeforeSubmit(gEditors, 'name');
        }
        function OpenIME() {
            OpenAstarIME('AstarIME.htm', 'name');
        }
        function GetAllAstarWebEditors() {
            return gEditors;
        }
    </script>
    <STYLE TYPE="text/css">
		@font-face {
		font-family: BOTFont;
		font-style:  normal;
		font-weight: normal;
		src: url('BOTMing.eot'); /* IE9 Compat Modes */
		src: url('BOTMing.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
			url('BOTMing.ttf') format('truetype');  /*Safari, Android, iOS */
		}
    </STYLE>
     <%-- astar end --%>
</head>
<body style="font-family: BOTFont;" onload="InitPage()">
<form name="f1" action="./TBOracleCover.do" method="get" >
    <table border="1">
    <tr>
        <td style="text-align:right">輸入文字</td><td>
            <input type="text" id="name" name="name" value="" />
            <input type="hidden" id="sn" name="sn" value="" />
            <input style="font-family: BOTFont;" type="button" value="開啟輸入視窗" onclick="OpenIME()" />
        </td>
    </tr>
     <tr>
        <td colspan="2" align="center">
        	<input type="button" style="width:100px;" value="送出" onclick="commit();"/>
        &nbsp;&nbsp;&nbsp;<input type="button" style="width:100px;" value="直接查詢" onclick="postSubmit();"/>
        </td>
    </tr>
    </table>
    <c:if test="${list != null}">
	    <hr/>
		<table border="1">
			<tr>
				<td style="text-align:left">流水號</td>
				<td style="text-align:left">varchar儲存的姓名</td>
				<td style="text-align:left">nvarchar儲存的姓名</td>
			</tr>
			<c:forEach var="vo" items="${list}">
				<tr>
					<td style="text-align:left">${vo.sn}</td>
					<td style="text-align:left">${vo.name}</td>
					<td style="text-align:left">${vo.compare}</td>
				</tr>
			</c:forEach> 
    	</table>
    </c:if>
</form>
</body>
</html>
