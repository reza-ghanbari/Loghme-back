<%@ taglib prefix="mytags" uri="http://kharchal.com" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="models.OrderItem" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String username = (String) request.getAttribute("user");
    String restaurant = (String) request.getAttribute("restaurant");
    ArrayList<OrderItem> items = null;
    double total = 0;
    Boolean empty = (Boolean) request.getAttribute("empty");
    Boolean lowCredit = (Boolean) request.getAttribute("lowCredit");
    if(!empty){
        items = (ArrayList<OrderItem>) request.getAttribute("orders");
        total = (Double) request.getAttribute("total");
    }
    if(lowCredit==null){
        lowCredit = true;
    }
%>

<html>
<head>
    <title><%=username%>'s Cart</title>
</head>
<body>
<c:set var="isemptyv" value="<%=empty%>"></c:set>
<c:set var="lowcredit" value="<%=lowCredit%>"></c:set>
<c:if test="${not isemptyv}">
    <h3>restaurant : <%=restaurant%></h3>
    <c:forEach items="<%=items%>" var="item">
        <mytags:OrderItem item="${item}"></mytags:OrderItem>
    </c:forEach>
    <br>
    <h3>
        Total : <%=total%>
    </h3>
    <br>
    <h2>thanks for shopping</h2>
</c:if>
</body>
</html>