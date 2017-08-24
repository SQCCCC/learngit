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
           	<li class="h3">订货表</li>
    </ul>
<br class="clear"/>
</div>
<div class="screenlet-body">

<table  border="0" cellspacing="0" cellpadding="0" class='table' id="abc" >
	<tr>
		<td>总额度</td>
		<td ><#if twoMonthsSales?has_content>${twoMonthsSales}</#if></td>
	</tr>
	<tr><td>剩余额度<input type="text" size=5 id="totalScore" value="${surplusQuota!}" name="totalScore"/></td></tr>
</table>
<br class="clear"/>

<table class="basic-table hover-bar" cellspacing='0' id="table">
<thead>
	<tr class="header-row" >
<!--	<td align="center">操作</td> -->
		<td align="center">sku</td>
		<td align="center">库存</td>
		<td align="center">品名</td>
		<td align="center">产品状态</td>
		<td align="center">5天销量</td>
		<td align="center">15天销量</td>
		<td align="center">30天销量</td>
		<td align="center">在途</td>
		<td align="center">缺货数量</td>
		<td align="center">净库存</td>
		<td align="center">订货人</td>
		<td align="center">订货数量</td>
		<td align="center">总采购额</td>
		<td align="center">组别</td>
		<td align="center">周转天数</td>
		<td align="center">pm备注</td>
		<td align="center">主次</td>
		<td align="center">供应商</td>
		<td align="center">链接</td>
		<td align="center">起订量</td>
		<td align="center">成本</td>
	</tr>
</thead>
<tbody>
 <form action="<@ofbizUrl>gotoPurchaserview</@ofbizUrl>" method="post" id=""> 
<#list reorderList! as order>
	<tr id="city_${(order.sku!)?replace('-','_')}" class="quotation" >

		<td align="center">
<!--
		<form action="<@ofbizUrl>deleteReorderSku</@ofbizUrl>" method="post" name="reorder_${(order.sku!)?replace('-','_')}" onsubmit="return delete()">
		<input type="submit" value="delete"/>
			<input type="hidden" name="productId" value="${order.sku}"/>
			<input type="hidden" name="date" value ="${order.date}">
		</form> -->
		</td>

		
		<td align="center"><#if order.sku?has_content>${order.sku}</#if></td>
		<td align="center"><#if order.qty?has_content>${order.qty}</#if></td>
		<td align="center"><#if order.chineseName?has_content>${order.chineseName}</#if></td>
		<td align="center"><#if order.statusId?has_content>${order.statusId}</#if></td>
		<td align="center"><#if order.fiveDaysSales?has_content>${order.fiveDaysSales}</#if></td>
		<td align="center"><#if order.fifteenDaysSales?has_content>${order.fifteenDaysSales}</#if></td>
		<td align="center"><#if order.oneMonthSales?has_content>${order.oneMonthSales}</#if></td>
		<td align="center"><#if order.pootw?has_content>${order.pootw}</#if></td>
		<td align="center"><#if order.outOfStock?has_content>${order.outOfStock}</#if></td>
		<td align="center"><#if order.atp?has_content>${order.atp}</#if></td>
		<td align="center"><#if order.purchaser?has_content>${order.purchaser}</#if></td>
		<td align="center"><input type="number" id="input1_${(order.sku!)?replace('-','_')}"  name="orderCount" class="per" value="<#if order.orderCount?has_content>${order.orderCount}<#else>0</#if>" size="3"  /> 
		</td>
		<td align="center"><#if order.totalPurchases?has_content>${order.totalPurchases}</#if></td>
		<td align="center"><#if order.ownerGroup?has_content>${order.ownerGroup}</#if></td>
		<td align="center"><#if order.turnOverDayCount?has_content>${order.turnOverDayCount}</#if></td>
		<td align="center"><#if order.pmNotes?has_content>${order.pmNotes}</#if></td>
		<td align="center">
			<select id="selectKK_${(order.sku!)?replace('-','_')}" class="prov" onChange="onChangeSelect();" name="zhuci"></select>
			<input type="hidden" id="classID_${(order.sku!)?replace('-','_')}"  value="MAIN" />		 
		</td>
		<td align="center"><select class="city" disabled="disabled" name="supplier"></select></td>
		<td align="center"><select class="dist" disabled="disabled" name="link"></select></td>
		<td align="center">
			<input  id="totalScore_${(order.sku!)?replace('-','_')}" name="qidingliang" value="${order.minimumOrderCountm}" autoComplete='off'   >
		</td>
		<td align="center">
			<input class="num" id="costes_${(order.sku!)?replace('-','_')}" name="cost" value="${order.minimumCostm!}" autoComplete='off'>
		</td>

	</tr>
		<input type="hidden" name="sku" value="${order.sku}"/>
		<input type="hidden" name="date" value ="${order.date}">
</#list>

	<tr>
		<input value="update" id="gotoPurch" name="ownerGoup" />
<!--
		<input type="hidden" name="reorderlist" value="${reorderList}"/>
	-->	

	</tr>
<!-- </form> -->
</tbody>
</table>
</div>


<div class="screenlet-title-bar">
	<ul>
		<li class="h3">补货表</li>
	</ul>
	<br class="clear"/>
</div>
<div class="screenlet-body">
	<form action="<@ofbizUrl>addNewSku</@ofbizUrl>" method="post" name="form1" onsubmit="return checkscript()">
		<table  border="0" cellspacing="0" cellpadding="0" id='table' >
		<tr class="header-row">
		<td align="center">sku</td>
		<td align="center">订货数量</td>
		<td align="center">起订量</td>
		<td align="center">成本</td>
		</tr>
		<tr>
		<td align="center"></td>
		<td align="center"></td>
		</tr>
		</table>
	</form>
</div>

<script type="text/javascript" src="/images/reorder/js/jquery.js"></script>
<script type="text/javascript" src="/images/reorder/1/jquery.cityselect.js"></script>
<script type="text/javascript">
$(document).ready(function() {
//<#list reorderList! as order1>
        $("#city_${(order1.sku!)?replace('-','_')}").citySelect({
            url: {"citylist": [
               {"p": "MAIN", "c": [{"n": "${order1.mainSupplier!}", "a": [{"s": "${order1.mainSupplierLink!}"}]}]},
               {"p": "BACKUP", "c": [{"n": "${order1.backupSupplier!}", "a": [{"s": "${order1.backupSupplierLink!}"}]}]},
            ]},
            prov: "",
            city: "",
            dist: "",
            nodata: "none"
        });
//</#list>
		var virtual_total1=0.00;
		$('#table').change(function(){
		        var TOTAL;
		        TOTAL=0.00;
				TOTALLAST=0.00;
				virtual_total=0.00;
		        $(".quotation").each(function() {
		            var per = $(this).find('.per').val();    
		            var num = $(this).find('.num').val();    
		            var subtotal = per*num;             
		            var SUBTOTAL = subtotal;             

		            if(SUBTOTAL){ 
		                TOTAL = parseFloat(SUBTOTAL)+TOTAL;
						virtual_total = virtual_total1;
						TOTALLAST = virtual_total - TOTAL;
		                $('#totalScore').val(TOTALLAST.toFixed(2));
						if(TOTALLAST >= 0){
							$("#gotoPurch").prop("type", "submit");
						}else{
							$("#gotoPurch").prop("type", "hidden");
						}     
		            }
		        });
		    });
		var TOTAL1;
		TOTAL1=0.00;
		virtual_total11=0.00;
		$(".quotation").each(function() {
		     var per1 = $(this).find('.per').val();    
		     var num1 = $(this).find('.num').val();    
		     var subtotal1 = per1*num1;             
		     var SUBTOTAL1 = subtotal1;             
		     if(SUBTOTAL1){ 
		       TOTAL1 = parseFloat(SUBTOTAL1)+TOTAL1;
				virtual_total1 = ${surplusQuota!} + TOTAL1;
			   TOTAL1LAST = virtual_total1 - TOTAL1;
		       $('#totalScore').val(TOTAL1LAST.toFixed(2));  
				if(TOTAL1LAST >= 0){
							$("#gotoPurch").prop("type", "submit");
						}else{
							$("#gotoPurch").prop("type", "hidden");
						}    
		     }
		});

});

</script>
<!-- select input 值获取 -->
<script>
function onChangeSelect(){
//<#list reorderList! as order2>
		document.getElementById("classID_${(order2.sku!)?replace('-','_')}").value=document.getElementById("selectKK_${(order2.sku!)?replace('-','_')}").value;
//</#list>
		}
</script>
<script type="text/javascript">
function delete()
{
	   var check=false;
       if (confirm("Are you sure you want to delete?")) {  
            return true; 
        }  
        else {  
            check=false;
        }  
		return check;
}
</script>

<!-- **************************************************************************************************************** -->
<script type="text/javascript">
$(document).ready(function() {
    $('input').bind('input propertychange', function() {  
	Array.prototype.min = function() {
			  return Math.min.apply(null, this);
			};
//<#list reorderList! as order2>
        var input1_${(order2.sku!)?replace('-','_')} = $("#input1_${(order2.sku!)?replace('-','_')}").val();
        // var result = 0;
        
          if(input1_${(order2.sku!)?replace('-','_')}==null||input1_${(order2.sku!)?replace('-','_')}==""){
              input1_${(order2.sku!)?replace('-','_')} = parseInt(0);
          }else{
              input1_${(order2.sku!)?replace('-','_')} = parseInt(input1_${(order2.sku!)?replace('-','_')});
              if(input1_${(order2.sku!)?replace('-','_')}<0){
                  alert("请输入大于0的数");
                  $("#input1_${(order2.sku!)?replace('-','_')}").val("");
                  input1_${(order2.sku!)?replace('-','_')} = parseInt(0);
              }
          }
          var a_${(order2.sku!)?replace('-','_')}=document.getElementById("selectKK_${(order2.sku!)?replace('-','_')}").value;
          	if(a_${(order2.sku!)?replace('-','_')} == "MAIN") {
          		var counts_${(order2.sku!)?replace('-','_')} = ${order2.minimumOrderCountListm!};
				var countsCostList_${(order2.sku!)?replace('-','_')} =  ${order2.minimumCostListm!};	
          		
          	}else {
          		var counts_${(order2.sku!)?replace('-','_')} = ${order2.minimumOrderCountListb!};
				var countsCostList_${(order2.sku!)?replace('-','_')} =  ${order2.minimumCostListb!};
          	}
          	goal_${(order2.sku!)?replace('-','_')} = input1_${(order2.sku!)?replace('-','_')};

			var counts_min_${(order2.sku!)?replace('-','_')} = counts_${(order2.sku!)?replace('-','_')}.min();
			if (counts_min_${(order2.sku!)?replace('-','_')} > goal_${(order2.sku!)?replace('-','_')}) {
				var closest_${(order2.sku!)?replace('-','_')} = counts_${(order2.sku!)?replace('-','_')}.min();
				index1 = counts_${(order2.sku!)?replace('-','_')}.indexOf(closest_${(order2.sku!)?replace('-','_')});
				costes_${(order2.sku!)?replace('-','_')} = countsCostList_${(order2.sku!)?replace('-','_')}[index1];
			} else{
				var closest_${(order2.sku!)?replace('-','_')} = 
					Math.max.apply(Math, counts_${(order2.sku!)?replace('-','_')}.filter(function(x){return x <= goal_${(order2.sku!)?replace('-','_')}}));
				index1 = counts_${(order2.sku!)?replace('-','_')}.indexOf(closest_${(order2.sku!)?replace('-','_')});
				costes_${(order2.sku!)?replace('-','_')} = countsCostList_${(order2.sku!)?replace('-','_')}[index1];
			}
          	$("#totalScore_${(order2.sku!)?replace('-','_')}").val(closest_${(order2.sku!)?replace('-','_')});
			$("#costes_${(order2.sku!)?replace('-','_')}").val(costes_${(order2.sku!)?replace('-','_')});
//</#list>
    });  
});
</script>
<script>
		function onChangeSelect(){
		Array.prototype.min = function() {
			  return Math.min.apply(null, this);
			};
//<#list reorderList! as order4>
		document.getElementById("classID_${(order4.sku!)?replace('-','_')}").value=document.getElementById("selectKK_${(order4.sku!)?replace('-','_')}").value;
		var input11_${(order4.sku!)?replace('-','_')} = $("#input1_${(order4.sku!)?replace('-','_')}").val();
          var a_${(order4.sku!)?replace('-','_')}=document.getElementById("selectKK_${(order4.sku!)?replace('-','_')}").value;
          	if(a_${(order4.sku!)?replace('-','_')} == "MAIN") {
          		var counts_${(order4.sku!)?replace('-','_')} =  ${order4.minimumOrderCountListm!};	
				var countsCostList_${(order4.sku!)?replace('-','_')} =  ${order4.minimumCostListm!};	
          	}else {
          		var counts_${(order4.sku!)?replace('-','_')} = ${order4.minimumOrderCountListb!};
				var countsCostList_${(order4.sku!)?replace('-','_')} =  ${order4.minimumCostListb!};	

          	}
          	goal11_${(order4.sku!)?replace('-','_')} = input11_${(order4.sku!)?replace('-','_')};
			
			var counts_min_${(order4.sku!)?replace('-','_')} = counts_${(order4.sku!)?replace('-','_')}.min();
			if (counts_min_${(order4.sku!)?replace('-','_')} > goal11_${(order4.sku!)?replace('-','_')}) {
				var closest_${(order4.sku!)?replace('-','_')} = counts_${(order4.sku!)?replace('-','_')}.min();
				index1 = counts_${(order4.sku!)?replace('-','_')}.indexOf(closest_${(order4.sku!)?replace('-','_')});
				costes_${(order4.sku!)?replace('-','_')} = countsCostList_${(order4.sku!)?replace('-','_')}[index1];
			} else{
				var closest_${(order4.sku!)?replace('-','_')} = 
					Math.max.apply(Math, counts_${(order4.sku!)?replace('-','_')}.filter(function(x){return x <= goal11_${(order4.sku!)?replace('-','_')}}));
				index1 = counts_${(order4.sku!)?replace('-','_')}.indexOf(closest_${(order4.sku!)?replace('-','_')});
				costes_${(order4.sku!)?replace('-','_')} = countsCostList_${(order4.sku!)?replace('-','_')}[index1];
			}
          	$("#totalScore_${(order4.sku!)?replace('-','_')}").val(closest_${(order4.sku!)?replace('-','_')});
			$("#costes_${(order4.sku!)?replace('-','_')}").val(costes_${(order4.sku!)?replace('-','_')});
//</#list>
		}
</script>



