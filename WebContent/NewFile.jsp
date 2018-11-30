<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery-1.11.0.js"/></script>

<script type="text/javascript">
	$(document).ready(function() {
	    var file = $('[name="file"]');
	    $('#btnUpload').on('click', function() {
	        var filename = $.trim(file.val());
	        $.ajax({
	            url: '',
	            type: "POST",
	            data: new FormData(document.getElementById("fileForm")),
	            enctype: 'multipart/form-data',
	            processData: false,
	            contentType: false
	        }).done(function(data) {
	        	  alert('File upload OK ...');
	        }).fail(function(jqXHR, textStatus) {
	              alert('File upload failed ...');
	        });
	        
	    });
	});
	    
	
	/*
	$("#files").change(function() {
	  	filename = this.files[0].name
	  	console.log(filename);
	});
	*/

	
</script>

</head>
<body>
	TEST TEST TEST
	<br><br>
	
	<!-- <input type="file" value="xxx">  -->
	<!-- 
	<div>
	  <label for="files" class="btn">Select Image</label>
	  <input id="files" style="visibility:hidden;" type="file">
	</div>
	
	<button style="display:block;width:80px; height:20px;" onclick="document.getElementById('getFile').click()">改變文字</button>
    <input type='file' id="getFile" style="display:none">
	
	 -->
	
	<div>
		<form id="fileForm">
		    <input type="file" name="file" /><br>
		    <button id="btnUpload" type="button">Upload file</button>
		</form>
	</div>
	
</body>
</html>