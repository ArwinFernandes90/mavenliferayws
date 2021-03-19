<%@ include file="/init.jsp" %>

<p>
	<b><liferay-ui:message key="my_first_mvn_module.caption"/></b>
</p>
<h1>Testing Maven</h1>

<liferay-portlet:actionURL name="test" var="testURL" />

<aui:form action="<%= testURL %>" method="post" name="fm">
	Input Text: <aui:input name="name" type="text" />

	<aui:button-row>
		<aui:button type="submit"></aui:button>
	</aui:button-row>
</aui:form>