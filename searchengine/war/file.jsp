<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.servlet.http.*" %>

<html>
<head>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search Book</title>
<script type="text/javascript">
function getCurrentResult()
{	
	var word = document.getElementById("word").value;
	var fileName = document.getElementById("filename").value;
	document.getElementById("heading").innerHTML = "Searching <strong>" + word + 
	" in " + fileName + "</strong>"; 
	var xmlhttp;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		  var re = new RegExp(word,"igm");
		  var result = xmlhttp.responseText.replace(re,"<strong>" + word 
				  + "</strong>");
	    document.getElementById("results").innerHTML=result;
	    }
	  }
	var positionIndex = getCurrentIndex();
	xmlhttp.open("GET","filequery?word="
			+word+"&fileName="+fileName+"&index="+positionIndex,true);
	xmlhttp.send();
	
}
function getNextResult()
{	
	var word = document.getElementById("word").value;
	var fileName = document.getElementById("filename").value;
	var xmlhttp;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		  var re = new RegExp(word,"igm");
		  var result = xmlhttp.responseText.replace(re,"<strong>" + word 
				  + "</strong>");
	    document.getElementById("results").innerHTML=result;
	    }
	  }
	var positionIndex = getNextIndex();
	xmlhttp.open("GET","filequery?word="
			+word+"&fileName="+fileName+"&index="+positionIndex,true);
	xmlhttp.send();
	
}
function getPreviousResult()
{
	if(getCurrentIndex() == 0){
		document.getElementById("results").innerHTML="No more occurrences of the Word";
		return;
	}
	var word = document.getElementById("word").value;
	var fileName = document.getElementById("filename").value;
	var xmlhttp;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		  var re = new RegExp(word,"igm");
		  var result = xmlhttp.responseText.replace(re,"<strong>" + word 
				  + "</strong>");
	    document.getElementById("results").innerHTML=result;
	    }
	  }
	var positionIndex = getPreviousIndex();
	xmlhttp.open("GET","filequery?word="
			+word+"&fileName="+fileName+"&index="+positionIndex,true);
	xmlhttp.send();
	
}
function getNextIndex(){
	var positionIndex = document.getElementById("index").value;
	document.getElementById("index").value = ++positionIndex;
	return positionIndex;
}
function getPreviousIndex(){
	var positionIndex = document.getElementById("index").value;
	if(positionIndex > 0){
		document.getElementById("index").value = --positionIndex;
		return positionIndex;
	}	
	else
		return "0";
}
function getCurrentIndex(){
	return document.getElementById("index").value;
}
</script>
</head>
<body>
<script>
window.onload=getCurrentResult
</script>
<% String word = request.getParameter("word");
String fileName = request.getParameter("fileName");
String index = request.getParameter("index");
%>
<center>
<img src="/images/book.jpg" width="100" height="100" align="middle">
<div id="filecontent" > 
<table border="1">
		<tr><th id ="heading"></th></tr>
		<tr><td id ="results">Click Next / Previous to Search for the word within the file</td></tr>
</table>
</div>
<button type="btnNext" onclick="getNextResult()">Next</button>
<button type="btnPrevious" onclick="getPreviousResult()">Previous</button>
</center>
<input type="hidden" name="word" id="word" value="<%= word %>"/>
<input type="hidden" name="filename" id="filename" value="<%= fileName %>"/>
<input type="hidden" name="index" id="index" value="<%= index %>"/>
</body>
</html>