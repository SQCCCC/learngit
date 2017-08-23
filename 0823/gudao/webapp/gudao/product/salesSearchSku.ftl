
<#if security.hasEntityPermission("PM", "_VIEW", session)>
<!--
    <div id="salesSearchLookup" class="screenlet">
        <div class="screenlet-title-bar">
        </div>
        <div class="screenlet-body">
            <form action="<@ofbizUrl>salesSearchSku</@ofbizUrl>" method="post" name="salesSearchSkuForm" id="salesSearchSkuForm">
                <table  border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td class="label">Product Id</td>
                        <td>
                            <input type="text" name="productId" size="25" value="${productId!}" title="SKU"/>
                        </td>
                    </tr>
                    <tr>
                        <td width='25%' align='right' class='label'>${uiLabelMap.CommonDateFilter}</td>
                        <td align='left'>
                            <table class="basic-table" cellspacing='0'>
                                <tr>
                                    <td nowrap="nowrap">
                                        <@htmlTemplate.renderDateTimeField name="minDate" event="" action="" value="${requestParameters.minDate?default(Static['org.ofbiz.base.util.UtilDateTime'].getXMonthFromToday(nowTimestamp,3))}" className="" alert="" title="Format: yyyy-MM-dd" size="25" maxlength="30" id="minDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><span class='label'>${uiLabelMap.CommonFrom}</span>
                                    </td>

                                </tr>
                                <tr>
                                    <td nowrap="nowrap">
                                        <@htmlTemplate.renderDateTimeField name="maxDate" event="" action="" value="${requestParameters.maxDate?default(Static['org.ofbiz.base.util.UtilDateTime'].getXMonthFromToday(nowTimestamp,0))}" className="" alert="" title="Format: yyyy-MM-dd" size="25" maxlength="30" id="maxDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/><span class='label'>${uiLabelMap.CommonThru}</span>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>

                    <tr>
                        <td></td>
                        <td>
                            <input type="submit"  value="search"  class="smallSubmit" />
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
-->

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