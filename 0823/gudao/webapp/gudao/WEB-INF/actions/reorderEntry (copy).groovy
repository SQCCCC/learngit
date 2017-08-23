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

import javolution.util.FastMap;
import javolution.util.FastList;

import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
/*import java.text.DateFormat;*/
import java.text.SimpleDateFormat;

import com.gudao.bulkModule.bulkService;

userLogin = parameters.userLogin;
userLoginId = userLogin.userLoginId;
userLoginPartyId = userLogin.partyId;
ownerGroup = StringUtils.remove(userLoginPartyId, "_GROUP");
context.ownerGroup = ownerGroup;

ownerGroupAllList = delegator.findByAnd("PartyRoleDetailAndPartyDetail", UtilMisc.toMap("partyGroupComments","ACTIVE","roleTypeId","SBU"), ["officeSiteName ASC"],false);
context.a = ownerGroupAllList.officeSiteName;


userLoginIdInput = parameters.userLoginIdInput;	

actualOrderCountInput = parameters.actualOrderCountInput;							


//		 distinctOwnerGroupList= delegator.findList("ReorderInfo",null, UtilMisc.toSet("pmSbu"),UtilMisc.toList("pmSbu"),new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true),false);
		sbuList=[];
		//for(ow in distinctOwnerGroupList){
			sbuData=[:];
			//sbu = ow.pmSbu;
			sbu = "AMY";
		 	findReorderList = delegator.findByAnd("ReorderInfo", UtilMisc.toMap("pmSbu","AMY"),["chineseName ASC","statusId ASC"],false);
context.f = findReorderList;
			reorderList=[];

			for(ri in findReorderList){
				data=[:];
					data.date = ri.date;
					data.sku = ri.productId;
					supplierList = delegator.findOne("ProductMaster",UtilMisc.toMap("productId",ri.productId),false);
					if(UtilValidate.isNotEmpty(supplierList)){
						data.qty = ri.qty;
						data.chineseName = ri.chineseName;
						data.statusId =ri.statusId;
						data.fiveSales =  ri.days5SoldQty;
						data.fifteenSales = ri.days15SoldQty;
						data.thirtySales = ri.days30SoldQty;
						data.pootw = ri.poOnTheWay;
						data.outOfStock = ri.outOfStock;
						data.atp = ri.atp;
						data.purchaser = ri.purchaser;
						data.orderCount = ri.orderCount;
						data.totalPurchases = ri.totalPurchases;
						data.ownerGroup = ri.ownerGroup;
						data.turnOverDayCount = ri.turnOverDayCount;					
						data.mainSupplier = supplierList.mainSupplier;
						data.mainSupplierLink = supplierList.mainSupplierLink;
						data.backupSupplier = supplierList.backupSupplier;
						data.backupSupplierLink = supplierList.backupSupplierLink;
						notes = supplierList.notes;
						listingNotes = supplierList.listingNotes;
						data.pmNotes = listingNotes + "\t" + notes;
				
						List<GenericValue> mainMinimumOrderList = delegator.findByAnd("SupplierPrice",UtilMisc.toMap("productId",ri.productId,"supplier",ri.mainSupplier),null,false);
						List<GenericValue> backupMinimumOrderList = delegator.findByAnd("SupplierPrice",UtilMisc.toMap("productId",ri.productId,"supplier",ri.mainSupplier),null,false);
						mainInfoList=[];
						backupInfoList=[];
						mainData=[:];
						backupData=[:];
						for(main in mainMinimumOrderList){
							mainData.type = main.type;
							mainData.minimumOrderValue = main.minimumOrderValue;
							mainData.unitPrice = main.unitPrice;
						}
						mainInfoList.add(mainData);
						for(back in backupMinimumOrderList){
							backupData.type = back.type;
							backupData.minimumOrderValue = back.minimumOrderValue;
							backupData.unitPrice = back.unitPrice;					
						}
						backupInfoList.add(backupData);
						data.mainInfoList = mainInfoList;
						data.backupInfoList = backupInfoList;
					}

		}
		reorderList.add(data);
		sbuData.reorderList = reorderList;
//}
sbuList.add(sbuData);
context.reorderList = reorderList;

