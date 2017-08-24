<style type="text/css">
table.withBorder {
font-size:12px;
text-align:center;
border: 1px solid #333333;
border-collapse:collapse;
padding: 0px 0px 0px 0px;
line-height: 2.9em;
margin-left:  0.0em;
padding: 0.4em 0.6em 0.4em 0.6em;
font-weight: bold;
}
tr.withBorder  td.withBorder {
border: 1px solid #333333;
display:table-cell;
padding: 0px 10px 0px 10px;
}
</style>
<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">SKU Search</li>
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body">
        <form action="<@ofbizUrl>editProductBasicInfo</@ofbizUrl>" method="post" name="editProductBasicInfo" id="editProductBasicInfo">
            <input type="hidden" name="language" value="en"/>
            <input type="hidden" name="platform" value="EBAY"/>
            <table cellspacing="0" class="basic-table">
                <tbody>
                    <tr>
                        <td class="label">Product ID</td>
                        <td>
                            <input type="text" name="productId" size="32" value="${productId?if_exists}"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="label">SKU type</td>
                        <td>
                            <select name="skuType">
                                <option value="PARENT" <#if skuType == "PARENT">SELECTED</#if>>母SKU</option>
                                <option value="CHILD" <#if skuType == "CHILD">SELECTED</#if>>子SKU</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td>
                            <input type="submit" value="Search" class="smallSubmit"/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </form>
        <br />

        <#if productId?has_content>
            <form action="<@ofbizUrl>updateProductBasicInfo</@ofbizUrl>" method="post" name="updateProductBasicInfoForm" id="updateProductBasicInfoForm">
                <input type="hidden" name="language" value="${language}"/>
                <input type="hidden" name="platform" value="EBAY"/>
                <input type="hidden" name="productId" value="${productId!}"/>
                <input type="hidden" name="skuType" value="${skuType!}"/>
                <table cellspacing="0" class="basic-table">
                    <tbody>
                        <#if hasUpdatePermission>
                            <tr>
                                <td class="label">Feature</td>
                                <td>
                                    <textarea cols="80" rows="5" maxlength="5000" name="feature"><#if feature??>${feature.content!}</#if></textarea>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">Specification</td>
                                <td>
                                    <textarea cols="80" rows="5" maxlength="5000" name="specification"><#if specification??>${specification.content!}</#if></textarea>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">Package Includes</td>
                                <td>
                                    <textarea cols="80" rows="3" maxlength="5000" name="packageIncludes"><#if packageIncludes??>${packageIncludes.content!}</#if></textarea>
                                </td>
                            </tr>
                            <tr>
                                <td>&nbsp;</td>
                                <td>
                                    <#if hasUpdatePermission>
                                        <input type="submit" value="Update" class="smallSubmit"/>
                                    </#if>

                                </td>
                            </tr>
                        <#else>No Permission - 没有权限
                        </#if>
                    </tbody>
                </table>
            </form>
        <#else>No SKU found - 找不到这个SKU
        </#if>
<div align=center>
<table>
<tr>
<td>
<form action="<@ofbizUrl>productBasicInfoMini</@ofbizUrl>" method="post" name="backToProductBasicInfo" id="backToProductBasicInfo">
<input type="hidden" name="productId" value="${productId}"/>
<input type="submit" value="Back" class="smallSubmit"/>
</form>
</td>
<td>
<form action="" method="post" onclick="window.close();">

<input type="submit" value="Cancel/Close" class="smallSubmit"/>
</form>
<!--<a href="javascript:void(0)" onclick="window.close();" class="smallSubmit">Cancel/Close</a>-->
</td>
</tr>
</table>

</div>
    </div>
</div>