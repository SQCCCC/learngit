/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
/*import java.sql.Date;
import java.text.DateFormat;*/
import java.text.SimpleDateFormat;
import javolution.util.FastMap;
import javolution.util.FastList;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.ObjectType;



userLogin = parameters.userLogin;
userLoginId = userLogin.getString("userLoginId");
userLoginPartyId = userLogin.getString("partyId");
context.userLoginId = userLoginId;



startDateInput = parameters.startDate;
endDateInput  = parameters.endDate;
context.startDateInput = startDateInput;
context.endDateInput = endDateInput;

buyerIdInput= parameters.buyerid;
context.buyerIdInput = buyerIdInput;

transactionIdInput  = parameters.transactionid;
context.transactionIdInput = transactionIdInput;


receiveremailInput = parameters.receiveremail;
context.receiveremailInput = receiveremailInput;

shipToNameInput = parameters.shiptoname;
context.shipToNameInput = shipToNameInput;


tracknoInput = parameters.trackno;
context.tracknoInput = tracknoInput;

nidInput = parameters.nid;
context.nidInput = nidInput;

shipToPhoneNumInput = parameters.shiptophonenum;
context.shipToNameInput = shipToPhoneNumInput;

logicswaynidInput = parameters.logicswaynid;
context.logicswaynidInput = logicswaynidInput;

distinctShippMethodList= delegator.findList("BLogisticWay",null, UtilMisc.toSet("name"),UtilMisc.toList("name"),new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true),false);
context.distinctShippMethodList = distinctShippMethodList;


//Calendar now = Calendar.getInstance();
//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");


int mark = 0;
boolean notnull = false;
long logicswaynid = 0;
findShippingList =[];
shippingList=[];
EntityCondition condition = null;
List<EntityCondition> conditionList = FastList.newInstance();

if (UtilValidate.isNotEmpty(startDateInput)) {
	notnull = true;
	Timestamp startDateTS = ObjectType.simpleTypeConvert(startDateInput, "Timestamp", null, null);
    conditionList.add(EntityCondition.makeCondition("ordertime", EntityOperator.GREATER_THAN, startDateTS));
}
	
if (UtilValidate.isNotEmpty(endDateInput)) {
	notnull =true;
	Timestamp toDateTS = ObjectType.simpleTypeConvert(endDateInput, "Timestamp", null, null);
    conditionList.add(EntityCondition.makeCondition("ordertime", EntityOperator.LESS_THAN, toDateTS));
}

if (UtilValidate.isNotEmpty(buyerIdInput)) {
	notnull = true;
    conditionList.add(EntityCondition.makeCondition("buyerid", EntityOperator.EQUALS, buyerIdInput));
}

if (UtilValidate.isNotEmpty(transactionIdInput)) {
	notnull = true;
   	conditionList.add(EntityCondition.makeCondition("transactionid", EntityOperator.EQUALS, transactionIdInput));
}

if (UtilValidate.isNotEmpty(receiveremailInput)) {
	notnull = true;
   	conditionList.add(EntityCondition.makeCondition("receiveremail", EntityOperator.EQUALS, receiveremailInput));
}

if (UtilValidate.isNotEmpty(shipToNameInput)) {
	notnull = true;
   	conditionList.add(EntityCondition.makeCondition("shiptoname", EntityOperator.EQUALS, shipToNameInput));
}

if (UtilValidate.isNotEmpty(nidInput)) {
	notnull = true;
   	conditionList.add(EntityCondition.makeCondition("nid", EntityOperator.EQUALS, nidInput));
}

if (UtilValidate.isNotEmpty(shipToPhoneNumInput)) {
	notnull = true;
   	conditionList.add(EntityCondition.makeCondition("shiptophonenum", EntityOperator.EQUALS, shipToPhoneNumInput));
}

if (UtilValidate.isNotEmpty(tracknoInput)) {
	notnull = true;
   	conditionList.add(EntityCondition.makeCondition("trackno", EntityOperator.EQUALS, tracknoInput));
}

if (UtilValidate.isNotEmpty(logicswaynidInput)) {
	notnull = true;
	findShippingList = delegator.findByAnd("BLogisticWay",UtilMisc.toMap("name", logicswaynidInput), null,false);
	if(UtilValidate.isNotEmpty(findShippingList)){
		for(s in findShippingList){ 
		shippingList.add(Long.valueOf(s.nid));
		}
	
	}
   	conditionList.add(EntityCondition.makeCondition("logicswaynid", EntityOperator.IN, shippingList));
}

context.shipa = findShippingList;
if (conditionList.size() > 0) {
    condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
}
pTradeList =[];
if(notnull){
	pTradeList = delegator.findList("PTrade", condition, null, ["filterflag", "nid ASC"], null, false);
	if(UtilValidate.isEmpty(pTradeList)){
		mark = 1;
		pTradeList = delegator.findList("PTradeHis", condition, null, ["filterflag", "nid ASC"], null, false);
		if(UtilValidate.isEmpty(pTradeList)){
			mark = 2;
			pTradeList = delegator.findList("PTradeUn", condition, null, ["filterflag", "nid ASC"], null, false);
			if(UtilValidate.isEmpty(pTradeList)){
				mark = 3;
				pTradeList = delegator.findList("PTradeUnHis", condition, null, ["filterflag", "nid ASC"], null, false);
			}
		}
	}
}

plist=[];

for(p in pTradeList){
	long days = Math.abs(nowTimestamp.getTime()) / (24*60*60*1000) + 1;;
	String name = null;
	String operator = null;
	String shiptocountrycode = null;
	String orderStatusId = null;
	long eub = 0;
	String logs =null;
	data=[:];
	data.transactionid = p.transactionid;
	data.buyerid = p.buyerid;
	data.shiptoname = p.shiptoname;

	data.ordertime = p.ordertime;
	data.closingdate = p.closingdate;
	data.trackno = p.trackno;
	nid = p.nid;
	data.nid =nid;
	data.totalweight = p.totalweight;
	data.expressnid = p.expressnid;
	if(p.ordertime != null){
		days = Math.abs(nowTimestamp.getTime() - (p.ordertime).getTime()) / (24*60*60*1000) + 1;
	}
	data.days = days;
	data.suffix = p.suffix;
	
	shippingmethodList =  delegator.findOne("BLogisticWay",UtilMisc.toMap("nid", String.valueOf(p.logicswaynid)), false);
	if(UtilValidate.isNotEmpty(shippingmethodList)){
		name = shippingmethodList.name;
		eub = shippingmethodList.eub;
	}
	data.name = name;
	data.receiveremail = p.receiveremail;
	data.shiptostreet = p.shiptostreet;
	data.shiptocity = p.shiptocity;
	data.shiptostate = p.shiptostate;
	data.shiptozip = p.shiptozip;
	data.shiptocountrycode = p.shiptocountrycode;
	data.shiptocountryname = p.shiptocountryname;
	data.shiptophonenum = p.shiptophonenum;
	data.paymentstatus = p.paymentstatus;
	data.additionalcharge =p.additionalcharge;
	data.shippingmethod = p.shippingmethod;
	data.user = p.user;
	shiptocountrycodeList = delegator.findOne("BCountry",UtilMisc.toMap("countryCode", p.shiptocountrycode), false);
	if(UtilValidate.isNotEmpty(shiptocountrycodeList)){
		shiptocountrycode = shiptocountrycodeList.countryznname;
	}
	data.shiptocountrycode = shiptocountrycode;
	data.expressstatus = p.expressstatus; 



	filterflag = p.filterflag;
	switch(mark) {
		case 0: switch(filterflag){
					case 5:   orderStatusId="等待派单";break;
					case 6:    switch(eub){
									case 1:   orderStatusId="派至E邮宝";break;
									case 2:   orderStatusId="派至E线下邮宝";	break;	
									case 3:   orderStatusId="派4PX独立帐户";break;
									case 4:   orderStatusId="派至非E邮宝";break;
								}
					case 20:  orderStatusId="未拣货";	break;	
					case 22:  orderStatusId="未核单";break;
					case 24:  orderStatusId="未包装";break;
					case 26:  orderStatusId="订单缺货(仓库)";break;
					case 28:  orderStatusId="缺货待包装";break;
					case 40:  orderStatusId="待发货";break;
					case 100: orderStatusId="已发货";	break;		
				} ;break;
		case 1:				  orderStatusId="已归档";break;
		case 2:  switch(filterflag){
					case 0:   orderStatusId="等待付款";break;
					case 1:   orderStatusId="订单缺货";break;
					case 2:   orderStatusId="订单退货";	break;	
					case 3:   orderStatusId="订单取消";break;
					case 4:   orderStatusId="其它异常单";break;
				} ;break;
		case 3:				  orderStatusId="异常已归档";break;
		
	}
	data.orderStatusId = orderStatusId;
	data.email = p.email;
	data.firstname = p.firstname;
	data.lastname = p.lastname;
	data.allgoodsdetail = p.allgoodsdetail;
	data.packingmen = p.packingmen;
	data.origpackingmen =p.origpackingmen;
	data.scanningmen = p.scanningmen;
	data.scanningdate = p.scanningdate;
	data.weighingdate = p.weighingdate;
	data.handlingamt = p.handlingamt;
	data.taxamt = p.taxamt;
	data.reasoncode = p.reasoncode;
	data.paymenttype = p.paymenttype;
	data.amt = p.amt;
	data.currencycode = p.currencycode;
	data.custom = p.custom;
	data.expressfare = p.expressfare;
	//logs
	pTradeLogsList = delegator.findByAnd("PTradeLogs",UtilMisc.toMap("tradenid", nid), null,false);
	logList=[];
	if(UtilValidate.isNotEmpty(pTradeLogsList)){
	for(log in pTradeLogsList){
		operator = log.operator;
		logs = log.logs;
		logList.add(logs);
	}
	}
	data.logList = logList;
	
	plist.add(data);
}
context.plist = plist;
