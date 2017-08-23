<div id="productLookupList" class="screenlet">
	<div class="screenlet-body">
	<form action="<@ofbizUrl><#if type=="DISCONTINUED">wishListingDomesticDiscontinuedView<#else>wishListingReviewPending</#if></@ofbizUrl>" method="post" name="form">
      <table  border="0" cellspacing="0" cellpadding="0">
		<tr>
    		<td text-align="left" class="label" >账号</td>
    		<td>
			<select name="productStoreId">
			<option value=""></option>
			<#list distinctStoreList?sort_by("productStoreId") as store>
			<option value="${store.productStoreId}" <#if parameters.productStoreId?has_content && parameters.productStoreId = store.productStoreId>SELECTED</#if>>${store.productStoreId}</option>
			</#list>
			</select>
			</td>
		</tr>
		<tr>
            <td>&nbsp;</td>
            <td>
            <input type="submit" value="Search" class="smallSubmit"/>
            </td>
        </tr>
	</form>
	</div>
</div>
    
