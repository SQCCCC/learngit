<!DOCTYPE html>
<html>

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>INSPINIA | Data Tables</title>

    <link href="/images/project_demo/css/bootstrap.min.css" rel="stylesheet">
    <link href="/images/project_demo/font-awesome/css/font-awesome.css" rel="stylesheet">

    <link href="/images/project_demo/css/plugins/dataTables/datatables.min.css" rel="stylesheet">

    <link href="/images/project_demo/css/plugins/bootstrap-tagsinput/bootstrap-tagsinput.css" rel="stylesheet">

    <link href="/images/project_demo/css/plugins/chosen/bootstrap-chosen.css" rel="stylesheet">

    <!-- datetimepicker -->
    <link rel="stylesheet" type="text/css" href="/images/project_demo/css/jquery.datetimepicker.css"/>

    <link href="/images/project_demo/css/plugins/select2/select2.min.css" rel="stylesheet">


    <link href="/images/project_demo/css/animate.css" rel="stylesheet">
    <link href="/images/project_demo/css/style_project_demo.css" rel="stylesheet">
    <!-- <link href="/images/project_demo/css/style.css" rel="stylesheet"> -->

</head>

<body class="pace-done mini-navbar">

    <div id="wrapper">

        <div id="page-wrapper_1" class="gray-bg">
        <div class="row border-bottom">
        </div>
            <div class="row wrapper border-bottom white-bg page-heading">
                <div class="col-lg-12">
                    <h2>${uiLabelMap.OrderStatusTracking}</h2>
                    <div>
                        <div class="">
                            <form role="form"  class="form-inline" action="<@ofbizUrl>table_data_tables</@ofbizUrl>" method="post" name="orderTracking"  id="form_clear" onsubmit="return saveReport();">
                                <!-- <div class="ibox-content"> -->
                                <div class="">

                                    <table class="ibox-content" id="table_search" width="100%"  style="border-collapse:separate; border-spacing:0px 10px;">
                                        <tbody>
                                            <tr>
                                                <td>
                                                    <div class="form-group">
                                                        <label for="StartDate" class="text-right">${uiLabelMap.startTime}</label>
                                                        <input type="text" class="form-control datetimepicker3 table_data_search" name="startDate" value="<#if startDateInput??>${startDateInput!}</#if>" id="" placeholder=""/>
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="form-group">
                                                      
                                                            <label for="EndDate" class="text-right">${uiLabelMap.endTime}</label>
                                                            <input type="text" class="form-control datetimepicker3 table_data_search" name="endDate" value="<#if endDateInput??>${endDateInput!}</#if>" id="" placeholder=""/>
                                                    </div>
                                                </td>

                                                <td>
                                                    <div class="form-group">
                                                        <label for="buyerid" class="text-right">${uiLabelMap.BuyerPlatformID}</label>
                                                        <input type="text" placeholder="" id="buyerid" name="buyerid" value="<#if buyerIdInput??>${buyerIdInput!}</#if>"
                                                               class="form-control table_data_search">
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="form-group">
                                                        <label for="trackno" class="text-right">${uiLabelMap.PPTransactionID}</label>
                                                        <input type="text" placeholder="" id="transactionid" name="transactionid" value="<#if transactionIdInput??>${transactionIdInput!}</#if>"   class="form-control table_data_search">
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="form-group">
                                                        <label for="exampleInputEmail2" class="text-right">${uiLabelMap.RecipientMailbox}</label>
                                                        <input id="receiveremail" name="receiveremail" type="email" placeholder=""  value="<#if receiveremailInput??>${receiveremailInput!}</#if>" class="form-control table_data_search">
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <div class="form-group">
                                                        <label for="exampleInputEmail2" class="text-right">${uiLabelMap.consignee}</label>
                                                        <input type="text" placeholder="" name="shiptoname" id="shiptoname"
                                                        value="<#if shipToNameInput??>${shipToNameInput!}</#if>" class="form-control table_data_search">
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="form-group">
                                                        <label for="exampleInputEmail2" class="text-right">${uiLabelMap.orderNumber}</label>
                                                        <input type="text" placeholder="" id="nid" name="nid" value="<#if nidInput??>${nidInput!}</#if>"
                                                                           class="form-control table_data_search">
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="form-group">
                                                        <label for="exampleInputEmail2" class="text-right">${uiLabelMap.phoneNumber}</label>
                                                        <input type="text" placeholder="" id="shiptophonenum" name="shiptophonenum"  value="<#if shipToPhoneNumInput??>${shipToPhoneNumInput!}</#if>" class="form-control table_data_search">
                                                    </div>
                                                </td>
                                                
												<td>
                                                    <div class="form-group">
                                                        <label for="exampleInputEmail2" class="text-right">${uiLabelMap.TrackingNumber}</label>
                                                        <input type="text" placeholder="" id="trackno" name="trackno" value="<#if tracknoInput??>${tracknoInput!}</#if>" class="form-control table_data_search">
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="form-group">
                                                        <label for="exampleInputEmail2" class="text-right">${uiLabelMap.LogisticsModel}</label>

                                                                <select name="logicswaynid" class="select2_demo_3 form-control table_data_search" id="logicswaynidInput" style="width:184px;">
                                                                    <option value=""></option>
																	
                                                                    <#list distinctShippMethodList as a>	
                                                                        <option value="${a.name!}">${a.name!}</option>
                                                                    </#list>
                                                                </select>
                                                    </div>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                    <div class="">
                                        <div class="text-right">
                                            <button type="submit" class="btn btn-sm btn-primary m-t-n-xs"><strong>${uiLabelMap.search}</strong></button>
                                            <!-- <button type="reset" class="btn btn-sm btn-warning m-t-n-xs"><strong>&nbsp;Clear&nbsp;&nbsp;</strong></button>
											<button type="submit" class="btn btn-sm btn-warning m-t-n-xs" name="res" id="clear_form"><strong>&nbsp;Clear&nbsp;&nbsp;</strong></button> -->
											<input class="btn btn-sm btn-warning m-t-n-xs" type="button1" value="Clear" placeholder="Clear" id="button" style="width:50px;"/>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>


        <div class="wrapper wrapper-content animated fadeInRight">
            <div class="row">
                <div class="col-lg-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>${uiLabelMap.OrderStatusTable}</h5>
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

                    <div class="table-responsive">

                    <table class="table table-striped table-bordered table-hover dataTables-example" style="width: 100%;" width="100%">
                    <thead>
                    <tr>
                        <th>${uiLabelMap.PPTransactionID}</th>
                        <th>${uiLabelMap.BuyerPlatformID}</th>
                        <th>${uiLabelMap.consignee}</th>
                        <th>${uiLabelMap.TransactionTime}</th>
                        <th>${uiLabelMap.deliveryTime}</th>
                        <th>${uiLabelMap.TrackingNumber}</th>
                        <th>${uiLabelMap.orderNumber}</th>
                        <th>${uiLabelMap.TotalWeight}</th>
                        <th>${uiLabelMap.CourierCompany}</th>
                        <th>${uiLabelMap.Delay}</th>
                        <th>${uiLabelMap.sellerReferredToAs}</th>
                        <th>${uiLabelMap.LogisticsModel}</th>
                        <th>${uiLabelMap.RecipientMailbox}</th>
                        <th>${uiLabelMap.ConsigneeAddress}</th>
                        <th>${uiLabelMap.ReceivingCity}</th>
                        <th>${uiLabelMap.StateOrProvince}</th>
                        <th>${uiLabelMap.CollectionPostcode}</th>
                        <th>${uiLabelMap.CollectTheCountryCode}</th>
                        <th>${uiLabelMap.receivingCountry}</th>
                        <th>${uiLabelMap.ConsigneePhone}</th>
                        <th>${uiLabelMap.PaymentStatus}</th>
                        <th>${uiLabelMap.ShopOrder}</th>
                        <th>${uiLabelMap.DespatchingPlatform}</th>
                        <th>${uiLabelMap.SellerPlatformID}</th>
                        <th>${uiLabelMap.ConsigneeCountryCn}</th>
                        <th>${uiLabelMap.deliveryStatus}</th>
                        <th>${uiLabelMap.orderStatus}</th>
                        <th>${uiLabelMap.PayerMailbox}</th>
                        <th>${uiLabelMap.PaymentFirstName}</th>
                        <th>${uiLabelMap.PaymentLastName}</th>
                        <th>${uiLabelMap.CommodityInformation}</th>
                        <th>${uiLabelMap.PickingPerson}</th>
                        <th>${uiLabelMap.OriginalPickingPerson}</th>
                        <th>${uiLabelMap.ReviewPerson}</th>
                        <th>${uiLabelMap.ReviewOrderTime}</th>
                        <th>${uiLabelMap.WeighingTime}</th>
                        <th>${uiLabelMap.refundAmount}</th>
                        <th>${uiLabelMap.RedirectionNumber}</th>
                        <th>${uiLabelMap.CustomTag}</th>
                        <th>${uiLabelMap.TypeOfPayment}</th>
                        <th>${uiLabelMap.orderAmount}</th>
                        <th>${uiLabelMap.TradingCurrencies}</th>
                        <th>${uiLabelMap.StoreMode}</th>
                        <th>${uiLabelMap.CourierFees}</th>
                    </tr>

                    </thead>
                    <tbody>
                    <#list plist as o>
                    <tr class="gradeX">
   
                        <td><#if o.transactionid?has_content>${o.transactionid}</#if></td>
                        <td><#if o.buyerid?has_content>${o.buyerid}</#if></td>
                        <td><#if o.shiptoname?has_content>${o.shiptoname}</#if></td>
                        <td><#if o.ordertime?has_content>${o.ordertime}</#if> </td>
                        <td><#if o.closingdate?has_content>${o.closingdate}</#if></td>
                        <td><#if o.trackno?has_content>${o.trackno}</#if></td>
                        <td><#if o.nid?has_content>${o.nid}</#if></td>
                        <td><#if o.totalweight?has_content>${o.totalweight}</#if></td>
                        <td><#if o.expressnid?has_content>${o.expressnid}</#if></td>
                        <td><#if o.days?has_content>${o.days}</#if> </td>
                        <td><#if o.suffix?has_content>${o.suffix}</#if></td>
                        <td><#if o.name?has_content>${o.name}</#if></td>
                        <td><#if o.receiveremail?has_content>${o.receiveremail}</#if></td>
                        <td><#if o.shiptostreet?has_content>${o.shiptostreet}</#if></td>
                        <td><#if o.shiptocity?has_content>${o.shiptocity}</#if></td>
                        <td><#if o.shiptostate?has_content>${o.shiptostate}</#if></td>
                        <td><#if o.shiptozip?has_content>${o.shiptozip}</#if></td>
                        <td><#if o.shiptocountrycode?has_content>${o.shiptocountrycode}</#if></td>
                        <td><#if o.shiptocountryname?has_content>${o.shiptocountryname}</#if></td>
                        <td><#if o.shiptophonenum?has_content>${o.shiptophonenum}</#if></td>
                        <td><#if o.paymentstatus?has_content>${o.paymentstatus}</#if></td>
                        <td><#if o.additionalcharge?has_content>${o.additionalcharge}</#if></td>
                        <td><#if o.shippingmethod?has_content>${o.shippingmethod}</#if></td>
                        <td><#if o.user?has_content>${o.user}</#if></td>
                        <td><#if o.shiptocountrycode?has_content>${o.shiptocountrycode}</#if></td>
                        <td><#if o.expressstatus?has_content>${o.expressstatus}</#if></td>
                        <td><#if o.orderStatusId?has_content>${o.orderStatusId}</#if></td>
                        <td><#if o.email?has_content>${o.email}</#if></td>
                        <td><#if o.firstname?has_content>${o.firstname}</#if></td>
                        <td><#if o.lastname?has_content>${o.lastname}</#if></td>
                        <td><#if o.allgoodsdetail?has_content>${o.allgoodsdetail}</#if></td>
                        <td><#if o.packingmen?has_content>${o.packingmen}</#if></td>
                        <td><#if o.origpackingmen?has_content>${o.origpackingmen}</#if></td>
                        <td><#if o.scanningmen?has_content>${o.scanningmen}</#if></td>
                        <td><#if o.scanningdate?has_content>${o.scanningdate}</#if></td>
                        <td><#if o.weighingdate?has_content>${o.weighingdate}</#if></td>
                        <td><#if o.handlingamt?has_content>${o.handlingamt}</#if></td>
                        <td><#if o.taxamt?has_content>${o.taxamt}</#if></td>
                        <td><#if o.reasoncode?has_content>${o.reasoncode}</#if></td>
                        <td><#if o.paymenttype?has_content>${o.paymenttype}</#if></td>
                        <td><#if o.amt?has_content>${o.amt}</#if></td>
                        <td><#if o.currencycode?has_content>${o.currencycode}</#if></td>
                        <td><#if o.custom?has_content>${o.custom}</#if></td>
                        <td><#if o.expressfare?has_content>${o.expressfare}</#if></td>
                    </tr>
                   </#list>
                    </tbody>
                    </table>
                    </div>

                    <!-- <div class="tabs-container"> -->
                    <div class="tabs-container">
                        <ul class="nav nav-tabs">
<!--                             <li class="active"><a data-toggle="tab" href="#tab-1">${uiLabelMap.OperationLog}</a></li>
                            <li class=""><a data-toggle="tab" href="#tab-2">${uiLabelMap.CommodityInformation}</a></li> -->
                            <li class="active"><a data-toggle="tab" href="#tab-3">${uiLabelMap.OperationLog}</a></li>
                        </ul>
                        <div class="tab-content">
<!--                             <div id="tab-1" class="tab-pane">
                                <div class="panel-body">
                                    <strong>${uiLabelMap.orderStatus}</strong>

                                    <p>null</p>
                                </div>
                            </div>
                            <div id="tab-2" class="tab-pane">
                                <div class="panel-body">
                                    <strong>${uiLabelMap.CommodityInformation}</strong>

                                    <p>null</p>
                                </div>
                            </div> -->
                            <div id="tab-3" class="tab-pane active">
                                <div class="panel-body">
                                    <!-- <strong>${uiLabelMap.OperationLog}</strong> -->
                       				 <p>
									<#list plist as r >	
		                                <#list r.logList as d >
		                                <#if d.logs?has_content>${d.logs}</#if>
		                                </#list>
									</#list>
									</p>
                                </div>
                            </div>
                        </div>


                    </div>

                    </div>





                </div>
                    
            </div>
<!--             <div class="row">
                <div class="col-lg-12">
                    <div class="tabs-container">
                        <ul class="nav nav-tabs">
                            <li class="active"><a data-toggle="tab" href="#tab-1"></a></li>
                            <li class=""><a data-toggle="tab" href="#tab-2"></a></li>
                            <li class="active"><a data-toggle="tab" href="#tab-3"></a></li>
                        </ul>
                        <div class="tab-content">
                            <div id="tab-1" class="tab-pane">
                                <div class="panel-body">
                                    <strong>${uiLabelMap.orderStatus}</strong>

                                    <p>null</p>
                                </div>
                            </div>
                            <div id="tab-2" class="tab-pane">
                                <div class="panel-body">
                                    <strong>${uiLabelMap.CommodityInformation}</strong>

                                    <p>null</p>
                                </div>
                            </div>
                            <div id="tab-3" class="tab-pane active">
                                <div class="panel-body">
                                    <strong>${uiLabelMap.OperationLog}</strong>
                        
                                    <#list plist as r >
                                    <pre><#if r.logs?has_content>${r.logs}</#if></pre>
                                    </#list>
                                </div>
                            </div>
                        </div>


                    </div>
                </div>
            </div> -->

            </div>
        </div>
<!--         <div class="footer">
            <div class="pull-right">
                10GB of <strong>250GB</strong> Free.
            </div>
            <div>
                <strong></strong> GuDao Company &copy; 2010-2017
            </div>
        </div> -->

        </div>
        </div>



    <!-- Mainly scripts -->
    <script src="/images/project_demo/js/jquery-3.1.1.min.js"></script>
    <script src="/images/project_demo/js/bootstrap.min.js"></script>
    <script src="/images/project_demo/js/plugins/metisMenu/jquery.metisMenu.js"></script>
    <script src="/images/project_demo/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>

    <script src="/images/project_demo/js/plugins/dataTables/datatables.min.js"></script>

        <!-- Chosen -->
    <script src="/images/project_demo/js/plugins/chosen/chosen.jquery.js"></script>

    <!-- Custom and plugin javascript -->
    <script src="/images/project_demo/js/inspinia.js"></script>
    <script src="/images/project_demo/js/plugins/pace/pace.min.js"></script>

        <!-- Tags Input -->
    <script src="/images/project_demo/js/plugins/bootstrap-tagsinput/bootstrap-tagsinput.js"></script>

    <!-- datetimepicker -->
    <script src="/images/project_demo/js/jquery.datetimepicker.full.js"></script>

    <!-- Select2 -->
    <script src="/images/project_demo/js/plugins/select2/select2.full.min.js"></script>

    <!-- <script src="/images/project_demo/js/plugins/bootstrap-select/js/bootstrap-select.min.js"></script> -->

    <!-- Page-Level Scripts -->
    <script>
        $(document).ready(function(){
           var oTable = $('.dataTables-example').DataTable({
                "scrollY": 500,
                "sScrollX": "100%",
                "sScrollXInner": "110%",
                "bScrollCollapse": true,
                "pageLength": 1000,
                // "bLengthChange":false,
                "responsive": true,
                "dom": '<"html5buttons"B>lTfgitp',
                "buttons": [
                    { extend: ''},
                    // { extend: 'copy'},
                    // {extend: 'csv'},
                    // {extend: 'excel', title: 'ExampleFile'},
                    // {extend: 'pdf', title: 'ExampleFile'},

                    // {extend: 'print',
                    //  customize: function (win){
                    //         $(win.document.body).addClass('white-bg');
                    //         $(win.document.body).css('font-size', '10px');

                    //         $(win.document.body).find('table')
                    //                 .addClass('compact')
                    //                 .css('font-size', 'inherit');
                    // }
                    // }
                ],
                "bAutoWidth": true,//自动宽度
                "language": {
                    "emptyTable":     "Custom Search Message Result Is Empty"
                }

            });

            $('.tagsinput').tagsinput({
                tagClass: 'label label-primary'
            });

            $('.chosen-select').chosen({width: "100%"});

            $('.datetimepicker3').datetimepicker({
            // format: 'y-m-d H:i:s.Z'
            format: 'Y-m-d H:i:s.z'
            });

            $(".select2_demo_1").select2();

			$(".select2_demo_3").select2({
                placeholder: "Select a state",
                allowClear: true
            });

            $("#flip").click(function(){
                $("#panel").slideToggle("slow");
            });

            //然后通过trigger来触发reset按钮 
            // $("button[type='reset']").trigger("click");//触发reset按钮 

            //通过form表单的dom对象的reset方法来清空
            // $('form')[0].reset();


			$(function(){
               
                $('#button').click(function(){
                   $(':input','#form_clear')
             .not(':button, :submit, :reset, :hidden')
             .val('')
             .removeAttr('checked')
             .removeAttr('selected');
                }); 
                
            });

           


        });

    </script>




</body>

</html>
