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

    <link href="/images/project_demo/css/plugins/bootstrap-tagsinput/bootstrap-tagsinput.css" rel="stylesheet">

    <link href="/images/project_demo/css/plugins/chosen/bootstrap-chosen.css" rel="stylesheet">

    <link href="/images/project_demo/css/animate.css" rel="stylesheet">
    <link href="/images/project_demo/css/style_project_demo.css" rel="stylesheet">
    <!-- <link href="/images/project_demo/css/style.css" rel="stylesheet"> -->

    <!-- <link href="/images/project_demo/js/external/google-code-prettify/prettify.css" rel="stylesheet"> -->

    <!-- Sweet Alert -->
    <link href="/images/project_demo/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">



</head>

<body class="pace-done mini-navbar">

    <div id="wrapper">

        <div id="page-wrapper_1" class="gray-bg">
        <div class="wrapper wrapper-content-PMTable animated fadeInRight">
            <div class="row">
                <div class="col-lg-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>采购表</h5>
                        <div class="ibox-tools">
                            <a class="collapse-link">
                                <i class="fa fa-chevron-up"></i>
                            </a>
                            <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                                <i class="fa fa-wrench"></i>
                            </a>
                            <!-- <ul class="dropdown-menu dropdown-user">
                                <li><a href="#">Config option 1</a>
                                </li>
                                <li><a href="#">Config option 2</a>
                                </li>
                            </ul> -->
                            <a class="close-link">
                                <i class="fa fa-times"></i>
                            </a>
                        </div>
                    </div>
                    <div class="ibox-content">

                    <!-- <div class="table-responsive"> -->
                    <table class="table table-bordered dataTables-example" id="mainTable" style="width: 100%; white-space: nowrap;" width="100%">
                    <thead>
                    <tr>
                        <th>供应商</th>
                        <th>sku</th>
                        <th>订货数量</th>
                        <th>品名</th>
                        <th>产品编号</th>
                        <th>单价</th>
                        <th>总采购额</th>
                        <th>pm备注</th>
                        <th>起订量</th>
                        <th>组别</th>
                        <th>订货人</th>
                        <th>运费</th>
                        <th>订单编号</th>
                        <th>备注</th>
                        <th>状态</th>
                    </tr>
                    </thead>
                    <tbody>
						<tr>
                            <td>供应商</td>
                            <td>sku</td>
                            <td>订货数量</td>
                            <td>品名</td>
                            <td>产品编号</td>
                            <td>单价</td>
                            <td>总采购额</td>
                            <td>pm备注</td>
                            <td>起订量</td>
                            <td>组别</td>
                            <td>订货人</td>
                            <td><input type="text" value="运费"></td>
                            <td><input type="text" value="订单编号"></td>
                            <td><input type="text" value="备注"></td>
                            <td>
                                <select data-placeholder="Choose ..." class=""  tabindex="1">
                                    <option value="Select">Select</option>
                                    <option value="United States">United States</option>
                                </select>
                            </td>
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
		                        <td>
		                            <select data-placeholder="Choose ..." class=""  tabindex="1">
		                                <option value="Select">Y</option>
		                                <option value="N">N</option>
		                            </select>
		                        </td>
                       		 </tr>
                        	</#list>
						</form>
                    </tbody>
                   
                    </table>
                    <div class="">
                            <button id="button" class="btn btn-w-m btn-primary demo1">提交</button>

                    </div>
                    <!-- </div> -->
                    </div>
                </div>
            </div>


            </div>
        </div>

        </div>
        </div>



    <!-- Mainly scripts -->
    <script src="/images/project_demo/js/jquery-3.1.1.min.js"></script>
    <script src="/images/project_demo/js/bootstrap.min.js"></script>
    <script src="/images/project_demo/js/plugins/metisMenu/jquery.metisMenu.js"></script>
    <script src="/images/project_demo/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>

    <script src="/images/project_demo/js/plugins/dataTables/datatables.min.js"></script>
    <link href="/images/project_demo/js/plugins/dataTables/media/js/dataTables.bootstrap.min.js" rel="stylesheet">
    <link href="/images/project_demo/js/plugins/dataTables/media/js/dataTables.jqueryui.min.js" rel="stylesheet">
    <link href="/images/project_demo/js/plugins/dataTables/media/js/jquery.dataTables.min.js" rel="stylesheet">

    <!-- Sweet alert -->
    <script src="/images/project_demo/js/plugins/sweetalert/sweetalert.min.js"></script>

    <!-- Page-Level Scripts -->
    <script>
        $(document).ready(function(){

           var oTable = $('.dataTables-example').DataTable({
                "scrollY": 380,
                "sScrollX": "100%",
                "sScrollXInner": "110%",
                "bScrollCollapse": true,
                // "pageLength": 100,
                "paging": false, // 禁止分页
                "responsive": true,
                "dom": '<"html5buttons"B>lTfgitp',
                "buttons": [
                    // { extend: ''},
                    { extend: 'copy'},
                    {extend: 'csv'},
                    {extend: 'excel', title: 'ExampleFile'},
                    {extend: 'pdf', title: 'ExampleFile'},

                    {extend: 'print',
                     customize: function (win){
                            $(win.document.body).addClass('white-bg');
                            $(win.document.body).css('font-size', '10px');

                            $(win.document.body).find('table')
                                    .addClass('compact')
                                    .css('font-size', 'inherit');
                    }
                    }
                ],
                "columns": [
                    { className: "my_class0" },
                    { className: "my_class1" },
                    { className: "my_class2" },
                    { className: "my_class3" },
                    { className: "my_class4" },
                    { className: "my_class5" },
                    { className: "my_class6" },
                    { className: "my_class7" },
                    { className: "my_class8" },
                    { className: "my_class9" },
                    { className: "my_class10" },
                    { className: "my_class11" },
                    { className: "my_class12" },
                    { className: "my_class13" },
                    { className: "my_class14" }
                ],
                "bAutoWidth": true,//自动宽度
                "language": {
                    "emptyTable":     "Custom Search Message Result Is Empty"
                }

            });

           // 点击选中一行

           $('#mainTable tbody').on( 'click', 'tr', function () {
                if ( $(this).hasClass('selected') ) {
                    $(this).removeClass('selected');
                }
                else {
                    oTable.$('tr.selected').removeClass('selected');
                    $(this).addClass('selected');
                }
            } );


           $('.demo1').click(function(){
                swal({
                    title: "成功",
                    text: "提交成功！"
                });
            });


        });


    </script>





</body>

</html>

