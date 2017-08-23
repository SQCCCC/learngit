<!DOCTYPE html>
<html>
<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>pmtable</title>

    <link href="/images/project_demo/css/bootstrap.min.css" rel="stylesheet">
    <link href="/images/project_demo/font-awesome/css/font-awesome.css" rel="stylesheet">

    <link href="/images/project_demo/css/plugins/dataTables/datatables.min.css" rel="stylesheet">
    <link href="/images/project_demo/css/plugins/dataTables/media/css/dataTables.bootstrap.min.css" rel="stylesheet">



    <link href="/images/project_demo/css/plugins/dataTables/media/css/dataTables.jqueryui.min.css" rel="stylesheet">
    <link href="/images/project_demo/css/plugins/dataTables/media/css/jquery.dataTables.min.css" rel="stylesheet">


    <link href="/images/project_demo/css/animate.css" rel="stylesheet">
    <link href="/images/project_demo/css/style_project_demo.css" rel="stylesheet">

</head>

<body class="pace-done mini-navbar">

    <div id="wrapper">

        <div id="page-wrapper" class="gray-bg">
        <div class="wrapper wrapper-content-PMTable animated fadeInRight">
            <div class="row">
                <div class="col-lg-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>订货表</h5>
                        <div class="ibox-tools">
                            <a class="collapse-link">
                                <i class="fa fa-chevron-up"></i>
                            </a>
                            <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                                <i class="fa fa-wrench"></i>
                            </a>
                            <ul class="dropdown-menu dropdown-user">
                                <li><a href="#">Config option 1</a>
                                </li>
                                <li><a href="#">Config option 2</a>
                                </li>
                            </ul>
                            <a class="close-link">
                                <i class="fa fa-times"></i>
                            </a>
                        </div>
                    </div>
                    <div class="ibox-content">
                    <div>
						<ul>
							<li>总额度<input type="text" size=5 value="${twoMonthsSales!}"/></li>
							<li>剩余额度<input type="text" size=5 id="totalScore" value="${surplusQuota!}" name="totalScore"/></li>
						</ul>


                    </div>

                    <div class="table-responsive">
                    <form action="<@ofbizUrl>gotoPurchaserview</@ofbizUrl>" method="post" id="form_order">
                    <table class="table table-bordered dataTables-example" id="table" style="width: 100%; white-space: nowrap;">
                    <thead>
                    <tr align="center">
						<th align="center">operation</th>
                        <th align="center">sku</th>
                        <th align="center">库存</th>
                        <th align="center">品名</th>
                        <th align="center">产品状态</th>
                        <th align="center">5天销量</th>
                        <th align="center">15天销量</th>
                        <th align="center">30天销量</th>
                        <th align="center">在途</th>
                        <th align="center">缺货数量</th>
                        <th align="center">净库存</th>
                        <th align="center">订货人</th>
                        <th align="center">订货数量</th>
                        <th align="center">总采购额</th>
                        <th align="center">组别</th>
                        <th align="center">周转天数</th>
                        <th align="center">pm备注</th>
                        <th align="center">主次</th>
                        <th align="center">供应商</th>
                        <th align="center">链接</th>
                        <th align="center">起订量</th>
                        <th align="center">成本</th>
                    </tr>
                    </thead>
                    <tbody>
                   	<!-- <#list reorderList! as order> -->
                        <tr id="city_${(order.sku!)?replace('-','_')}" class="quotation" >
                            <td align="center">
								<a href="<@ofbizUrl>deleteReorderSku?entityName=ReorderInfo&amp;productId=${order.sku}&date=${order.date}</@ofbizUrl>"  onsubmit='return saveReport();' target='targetIfr'>delete</a>
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
                            <td align="center"><input type="number" id="input1_${(order.sku!)?replace('-','_')}"  name="orderCount" class="per" value="<#if order.orderCount?has_content>${order.orderCount}<#else>0</#if>" size="3"/> 
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
                                <input class="num" id="costes_${(order.sku!)?replace('-','_')}" name="cost" value="${order.minimumCostm}" autoComplete='off'>
                            </td>
                        </tr>

                    <!-- </#list> -->
                    </tbody>
					<tfoot>
                    <tr>
						<th align="center">operation</th>
                        <th align="center">sku</th>
                        <th align="center">库存</th>
                        <th align="center">品名</th>
                        <th align="center">产品状态</th>
                        <th align="center">5天销量</th>
                        <th align="center">15天销量</th>
                        <th align="center">30天销量</th>
                        <th align="center">在途</th>
                        <th align="center">缺货数量</th>
                        <th align="center">净库存</th>
                        <th align="center">订货人</th>
                        <th align="center">订货数量</th>
                        <th align="center">总采购额</th>
                        <th align="center">组别</th>
                        <th align="center">周转天数</th>
                        <th align="center">pm备注</th>
                        <th align="center">主次</th>
                        <th align="center">供应商</th>
                        <th align="center">链接</th>
                        <th align="center">起订量</th>
                        <th align="center">成本</th>				
                    </tr>
                    </tfoot>
                    </table>
                    <div class="">
                            <button id="button" class="btn btn-w-m btn-warning demo4">Delete selected row</button>

                    </div>
                    </form>


                    <iframe name="targetIfr" style=""></iframe>

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
                            <td align="center"></td>
                            <td align="center"></td>
                            </tr>
                            <tr>
                            <td align="center"></td>
                            <td align="center"></td>
                            <td align="center"></td>
                            <td align="center"></td>
                            </tr>
                            </table>
                        </form>
                    </div>
                    </div>

                    </div>
                </div>
            </div>
            </div>
        </div>
        </div>
        </div>



    <!-- Mainly scripts -->
    <script src="/images/project_demo/js/bootstrap.min.js"></script>
    <script src="/images/project_demo/js/plugins/metisMenu/jquery.metisMenu.js"></script>
    <script src="/images/project_demo/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>

    <script src="/images/project_demo/js/plugins/dataTables/datatables.min.js"></script>
	<!--
    <link href="/images/project_demo/js/plugins/dataTables/media/js/dataTables.bootstrap.min.js" rel="stylesheet">
    <link href="/images/project_demo/js/plugins/dataTables/media/js/dataTables.jqueryui.min.js" rel="stylesheet">
    <link href="/images/project_demo/js/plugins/dataTables/media/js/jquery.dataTables.min.js" rel="stylesheet">
 	-->

        <!-- Chosen -->
    <script src="/images/project_demo/js/plugins/chosen/chosen.jquery.js"></script>

    <!-- Custom and plugin javascript -->
    <script src="/images/project_demo/js/inspinia.js"></script>
    <script src="/images/project_demo/js/plugins/pace/pace.min.js"></script>

    <script type="text/javascript" src="/images/reorder/1/jquery.cityselect.js"></script>

    <!-- Page-Level Scripts -->
    <script>
        $(document).ready(function(){

           var oTable = $('#table').dataTable({
				"scrollY": 400,
                "sScrollX": "100%",
                "sScrollXInner": "110%",
                "bScrollCollapse": true,
                //"pageLength": 100,
				"paging": false, // 禁止分页
                "responsive": true,
                //"dom": '<"html5buttons"B>lTfgitp',
                "bAutoWidth": true,//自动宽度
                "language": {
                    "emptyTable":     "Custom Search Message Result Is Empty"
                }
               
            });

           // 点击选中一行

           $('#table tbody').on( 'click', 'tr', function () {
                if ( $(this).hasClass('selected') ) {
                    $(this).removeClass('selected');
                }
                else {
                    oTable.$('tr.selected').removeClass('selected');
                    $(this).addClass('selected');
                }
            } );
        
            // $('#button').click( function () {
            //     oTable.row('.selected').remove().draw( false );
            // } );


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


              function saveReport() {


                // jquery 表单提交


                $("#form_order").ajaxSubmit(function(message) { 
                // 对于表单提交成功后处理，message为提交页面saveReport.htm的返回内容 
                });
                return false; // 必须返回false，否则表单会自己再做一次提交操作，并且页面跳转 
                }
            } );


    </script>

    <!-- select input 值获取 -->
    <script>
    function onChangeSelect(){
    //<#list reorderList! as order2>
            document.getElementById("classID_${(order2.sku!)?replace('-','_')}").value=document.getElementById("selectKK_${(order2.sku!)?replace('-','_')}").value;
    //</#list>
            }
    </script>


	<!-- **************************************************************************************************************** -->
<!--
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
-->
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
					//var counts_${(order4.sku!)?replace('-','_')} =  0;		
					var countsCostList_${(order4.sku!)?replace('-','_')} =  ${order4.minimumCostListm!};	
		      	}else {
		      		var counts_${(order4.sku!)?replace('-','_')} = ${order4.minimumOrderCountListb!};
					var countsCostList_${(order4.sku!)?replace('-','_')} =  ${order4.minimumCostListb!};
					//var counts_${(order4.sku!)?replace('-','_')} = 0;
					//var countsCostList_${(order4.sku!)?replace('-','_')} =  0;	

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

</body>
</html>

