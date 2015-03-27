<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	response.setHeader("WWW-Authenticate",
			"Basic realm=\"Bank Agent Application\"");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>401 Unauthorized</title>
</head>
<body>
	<h1>401 Unauthorized</h1>
	<p>You are not authorized to view this page.</p>
</body>
</html>