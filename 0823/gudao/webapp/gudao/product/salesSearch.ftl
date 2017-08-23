
<#if security.hasEntityPermission("PM", "_VIEW", session)>

    <div id="salesSearchLookup" class="screenlet">
        <div class="screenlet-title-bar">
        </div>
        <div class="screenlet-body">
            <form action="<@ofbizUrl>salesSearch</@ofbizUrl>" method="post" name="salesSearchForm" id="salesSearchFormProduct">
Order ID <input type="text" name="orderId" size="25" value="${orderId!}" title="OrderId"/>
                <input type="submit" value="Search" class="smallSubmit"/>
            </form>
        </div>
    </div>

    <div id="salesSearchLookup" class="screenlet">
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">Order Count: <b>${orderCount!}</b></li>
            </ul>
            <br class="clear"/>
        </div>
        <div class="screenlet-body">

<table class="basic-table hover-bar" cellspacing='10'>
<tr class="header-row" style="white-space: nowrap;">
<td align="center">platform</td>
<td align="center">ownerGroup</td>
<td align="center">title</td>
<td align="center">date</td>
<td align="center">puyuanOrderId</td>
<td align="center">platformOrderId</td>
<td align="center">sellerName</td>
<td align="center">productId</td>
<td align="center">orderTotalQuantity</td>
<td align="center">orderItemQuantity</td>
<td align="center">productPrice</td>
<td align="center">shippingFee</td>
<td align="center">ebayFee</td>
<td align="center">paypalFee</td>
<td align="center">productCost</td>
<td align="center">shippingCost</td>
<td align="center">itemProfit</td>
<td align="center">profitRate</td>
<td align="center">currency</td>
<td align="center">exchangeRate</td>
<td align="center">transactionId</td>
<td align="center">createdBy</td>
<td align="center">salesReportId</td>
<td align="center">puyuanName</td>
<td align="center">refund</td>
<td align="center">sales</td>
<td align="center">platformFee</td>
<td align="center">productFee</td>
<td align="center">domesticFee</td>
<td align="center">internationalFee</td>
<td align="center">materialFee</td>
<td align="center">profit</td>
<td align="center">platformPartyId</td>
<td align="center">shippingMethod</td>
</tr>
<#assign alt_row = false>
<#list salesReportImportList! as salesReportImport>
<tr>
<td align="center">${salesReportImport.platform!}</td>
<td align="center">${salesReportImport.ownerGroup!}</td>
<td align="center">${salesReportImport.title!}</td>
<td align="center">${salesReportImport.date!}</td>
<td align="center">${salesReportImport.puyuanOrderId!}</td>
<td align="center">${salesReportImport.platformOrderId!}</td>
<td align="center">${salesReportImport.sellerName!}</td>
<td align="center">${salesReportImport.productId!}</td>
<td align="center">${salesReportImport.orderTotalQuantity!}</td>
<td align="center">${salesReportImport.orderItemQuantity!}</td>
<td align="center">${salesReportImport.productPrice!}</td>
<td align="center">${salesReportImport.shippingFee!}</td>
<td align="center">${salesReportImport.ebayFee!}</td>
<td align="center">${salesReportImport.paypalFee!}</td>
<td align="center">${salesReportImport.productCost!}</td>
<td align="center">${salesReportImport.shippingCost!}</td>
<td align="center">${salesReportImport.itemProfit!}</td>
<td align="center">${salesReportImport.profitRate!}</td>
<td align="center">${salesReportImport.currency!}</td>
<td align="center">${salesReportImport.exchangeRate!}</td>
<td align="center">${salesReportImport.transactionId!}</td>
<td align="center">${salesReportImport.createdBy!}</td>
<td align="center">${salesReportImport.salesReportId!}</td>
<td align="center">${salesReportImport.puyuanName!}</td>
<td align="center">${salesReportImport.refund!}</td>
<td align="center">${salesReportImport.sales!}</td>
<td align="center">${salesReportImport.platformFee!}</td>
<td align="center">${salesReportImport.productFee!}</td>
<td align="center">${salesReportImport.domesticFee!}</td>
<td align="center">${salesReportImport.internationalFee!}</td>
<td align="center">${salesReportImport.materialFee!}</td>
<td align="center">${salesReportImport.profit!}</td>
<td align="center">${salesReportImport.platformPartyId!}</td>
<td align="center">${salesReportImport.shippingMethod!}</td>
</tr>
<#assign alt_row = !alt_row>
</#list>
</table>




        </div>
    </div>




<#else>
No Permission - 没有权限
</#if>