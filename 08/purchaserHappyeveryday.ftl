<style type="text/css">
#table{
font-size:12px;
text-align:center;
border: 1px solid #696969;
border-collapse:separate;
padding:0px 0px 0px 0px;
width:auto;
height:auto;
}
#table tr  td {
border: 1px solid #696969;
display:table-cell; 
empty-cells:show;
}
#001.overflow{
overflow:auto;
}
</style>


<div id="productLookupList" class="screenlet">
<div class="screenlet-title-bar">
	<ul>
           	<li class="h3">采购表</li>
    </ul>
<br class="clear"/>
</div>
														

<div class="screenlet-body">
<table class="basic-table hover-bar" cellspacing='0' id="table">
	<tr class="header-row" >
	<td align="center">供应商</td>
	<td align="center">sku</td>
	<td align="center">订货数量</td>
	<td align="center">品名</td>
	<td align="center">产品编号</td>
	<td align="center">单价</td>
	<td align="center">总采购额</td>
	<td align="center">pm备注</td>
	<td align="center">起订量</td>
	<td align="center">组别</td>
	<td align="center">订货人</td>
	<td align="center">运费</td>
	<td align="center">订单编号</td>
	<td align="center">备注</td>
	<td align="center">状态</td>

	</tr>
<form action="<@ofbizUrl>purchaserHappyeveryday</@ofbizUrl>" method="post" id="form">
	<#list findReorderList! as order>
	<tr>
		<td align="center">
        	<a href="${order.mainSupplierLink}" title="supplier" target="_blank" class="buttontext">supplier</a>
        </td>
		<td align="center"><#if order.sku?has_content>${order.sku}</#if></td>
		<td align="center" class="per"><#if order.orderCount?has_content>${order.orderCount}</#if></td>
		<td align="center"><#if order.chineseName?has_content>${order.chineseName}</#if></td>
		<td></td>
		<td align="center"><#if order.unitPrice?has_content>${order.unitPrice}</#if></td>
		<td align="center"><#if order.totalPurchases?has_content>${order.totalPurchases}</#if></td>
		<td align="center"><#if order.pmNotes?has_content>${order.pmNotes}</#if></td>
		<td align="center"><#if order.minimumQty?has_content>${order.minimumQty}</#if></td>
		<td align="center"><#if order.pmSbu?has_content>${order.pmSbu}</#if></td>
		<td align="center"><#if order.purchaser?has_content>${order.purchaser}</#if></td>
		<td align="center"><input type="text" title="运费"/></td>
		<td align="center"><input type="text" title="订单编号"/></td>
		<td align="center"><input type="text" title="备注"/></td>
		<td align="center"><input type="checkbox"/></td>
	</tr>
	</#list>
</form>
</table>
</div>








