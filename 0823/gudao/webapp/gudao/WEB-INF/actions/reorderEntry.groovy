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

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.ObjectType;
import java.math.BigDecimal;
import java.util.ArrayList;

userLogin = parameters.userLogin;
userLoginId = userLogin.userLoginId;
context.a1=userLoginId;
userLoginPartyId = userLogin.partyId;
context.a2=userLoginPartyId;
ownerGroup = StringUtils.remove(userLoginPartyId, "_GROUP");
context.ownerGroup = ownerGroup;

//ownerGroupAllList = delegator.findByAnd("PartyRoleDetailAndPartyDetail", UtilMisc.toMap("partyGroupComments","ACTIVE","roleTypeId","SBU"), ["officeSiteName ASC"],false);
//context.a = ownerGroupAllList.officeSiteName;
//isGroupMember = false;

classID = "classID_E000069-BK";
context.classID = classID;

actualOrderCountInput = parameters.actualOrderCountInput;							
context.actualOrderCount = actualOrderCountInput;
Calendar calendar = Calendar.getInstance();
sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
java.sql.Date today = new java.sql.Date(calendar.getTime().getTime());
context.d = today;

zhuciInput  = parameters.zhuciInput;
context.zhuciInput = zhuciInput;
surplusQuotaInput = parameters.totalScore;
context.surplusQuotaInput = surplusQuotaInput;
//		 distinctOwnerGroupList= delegator.findList("ReorderInfo",null, UtilMisc.toSet("pmSbu"),UtilMisc.toList("pmSbu"),new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true),false);
//context.d=distinctOwnerGroupList;
			findGroupMember = delegator.findOne("PartyRoleDetailAndPartyDetail",UtilMisc.toMap("partyId",userLoginPartyId,"roleTypeId","SBU"),false);
			if(UtilValidate.isNotEmpty(findGroupMember)){
			officeSiteName = findGroupMember.officeSiteName;
			ownerGroupAllDataList = delegator.findByAnd("OwnerGroupMatch", UtilMisc.toMap("officeSiteName",officeSiteName), null,false);
	context.OW = ownerGroupAllDataList;
			String sbu = null;
			for(ow in ownerGroupAllDataList){
				sbu= ow.realOwnerGroup;
			}
			findReorderList = delegator.findByAnd("ReorderInfo", UtilMisc.toMap("pmSbu",sbu,"date",today,"purchaseStatusId","CHECKING"),["productId ASC","ownerGroup ASC"],false);
	context.f = findReorderList;
			reorderList=[];
			for(ri in findReorderList){//loop datalist-start
				data=[:];
				data.date = ri.date;
				data.sku = ri.productId;
				data.qty = ri.qty;
				data.chineseName = ri.chineseName;
				data.statusId =ri.statusId;
				data.fiveDaysSales =  ri.fiveDaysSales;
				data.fifteenDaysSales = ri.fifteenDaysSales;
				data.oneMonthSales = ri.oneMonthSales;
				data.pootw = ri.poOnTheWay;
				data.outOfStock = ri.outOfStock;
				data.atp = ri.atp;
				data.purchaser = ri.purchaser;
				data.orderCount = ri.orderCount;
				data.totalPurchases = ri.totalPurchases;
				data.ownerGroup = ri.ownerGroup;
				data.turnOverDayCount = ri.turnOverDayCount;	
				supplierList = delegator.findOne("ProductMaster",UtilMisc.toMap("productId",ri.productId),false);
				data.mainSupplier = supplierList.mainSupplier;
				data.mainSupplierLink = supplierList.mainSupplierLink;
				data.backupSupplier = supplierList.backupSupplier;
				data.backupSupplierLink = supplierList.backupSupplierLink;
				notes = supplierList.notes;
				listingNotes = supplierList.listingNotes;
				data.pmNotes = listingNotes + "\t" + notes;
				//SupplierPrice:productId,supplier,identifier,type,minimumOrderValue,unitPrice;
				//主供应商对应的起订量list
				List<GenericValue> mainMinimumOrderList = delegator.findByAnd("SupplierPrice",UtilMisc.toMap("productId",ri.productId,"supplier",supplierList.mainSupplier),["minimumOrderValue ASC"],false);
				data.mainMin=mainMinimumOrderList.minimumOrderValue[0];
				data.mainCost=mainMinimumOrderList.unitPrice[0];
				//备供应商对应的起订量list
				List<GenericValue> backupMinimumOrderList = delegator.findByAnd("SupplierPrice",UtilMisc.toMap("productId",ri.productId,"supplier",supplierList.backupSupplier),["minimumOrderValue ASC"],false);
				data.backupMin=backupMinimumOrderList.minimumOrderValue[0];
				data.backupCost=backupMinimumOrderList.unitPrice[0];
//起订量版本1


				mainInfoList=[];
				backupInfoList=[];
				minimumOrderCountListm=[];
				minimumOrderCountListb=[];
				minimumCostListm = [];
				minimumCostListb = [];
			//	BigDecimal.valueOf mqi=BigDecimal.ZERO ;
			//	BigDecimal.valueOf bqi=BigDecimal.ZERO ;
				BigDecimal.valueOf mPretendCount=BigDecimal.ZERO ;
				BigDecimal.valueOf bPretendCount=BigDecimal.ZERO ;
				BigDecimal.valueOf minimumOrderCountm=BigDecimal.ZERO ;//zhu最开始的对应起订量
				BigDecimal.valueOf minimumCostm=BigDecimal.ZERO ;//zhu最开始对应的单价

				BigDecimal.valueOf minimumOrderCountb=BigDecimal.ZERO ;//bei最开始的对应起订量
				BigDecimal.valueOf minimumCostb=BigDecimal.ZERO ;//bei最开始对应的单价

				for(main in mainMinimumOrderList){
					mainData=[:];
					mainData.type = main.type;
					mainData.minimumOrderValue = main.minimumOrderValue;
					mainData.unitPrice = main.unitPrice;
					minimumOrderCountListm.add((main.minimumOrderValue).intValue());
					minimumCostListm.add(main.unitPrice);
					if(ri.orderCount < main.minimumOrderValue){
						minimumOrderCountm = data.mainMin;
						minimumCostm = data.mainCost;
					}else{
						mPretendCount = BigDecimal.valueOf(ri.orderCount).subtract(main.minimumOrderValue);
					}
					if(mPretendCount >= 0){
						minimumOrderCountm = main.minimumOrderValue;
						minimumCostm = main.unitPrice;
					}
					mainInfoList.add(mainData);	
				}
				data.minimumOrderCountm = minimumOrderCountm;
				data.minimumCostm = minimumCostm;
				data.minimumOrderCountListm = minimumOrderCountListm;
				data.minimumCostListm = minimumCostListm;

	
				for(back in backupMinimumOrderList){
					backupData=[:];
					backupData.type = back.type;
					backupData.minimumOrderValue = back.minimumOrderValue;
					backupData.unitPrice = back.unitPrice;	
					minimumOrderCountListb.add(back.minimumOrderValue);
					minimumCostListb.add(back.unitPrice);
					if(ri.orderCount < back.minimumOrderValue){
						minimumOrderCountb = data.backupMin;
						minimumCostb = data.backupCost;
					}else{
						bPretendCount = BigDecimal.valueOf(ri.orderCount).subtract(back.minimumOrderValue);
					}
					if(bPretendCount >= 0.0){
						minimumOrderCountb = back.minimumOrderValue;
						minimumCostb = back.unitPrice;
					}
					backupInfoList.add(backupData);				
				}
				data.minimumOrderCountb = minimumOrderCountb;
				data.minimumCostb = minimumCostb;
				data.minimumOrderCountListb = minimumOrderCountListb;
				data.minimumCostListb = minimumCostListb;

				data.mainInfoList = mainInfoList;
				data.backupInfoList = backupInfoList;


/*
//起订量版本2
				minimumInfoList=[];
				minimumData=[:];
				BigDecimal.valueOf pretendCount=BigDecimal.ZERO ;
				BigDecimal.valueOf minimumOrderCount=BigDecimal.ZERO ;//最开始的对应起订量
				BigDecimal.valueOf minimumCost=BigDecimal.ZERO ;//最开始对应的单价
				minimumOrderCountList=[];
				minimumCostList = [];
				if(zhuciInput.equals("MAIN")){
					for(main in mainMinimumOrderList){
						minimumData.type = main.type;
						minimumData.minimumOrderValue = main.minimumOrderValue;
						minimumData.unitPrice = main.unitPrice;
						minimumOrderCountList.add(main.minimumOrderValue);
						minimumCostList.add(main.unitPrice);
						pretendCount = BigDecimal.valueOf(ri.orderCount).subtract(main.minimumOrderValue);
						if(pretendCount >= 0.0){
							minimumOrderCount = pretendCount;
							minimumCost = main.unitPrice;
						}
					
					}
				}else{

					for(back in backupMinimumOrderList){
						minimumData.type = back.type;
						minimumData.minimumOrderValue = back.minimumOrderValue;
						minimumData.unitPrice = back.unitPrice;
						minimumOrderCountList.add(back.minimumOrderValue);
						minimumCostList.add(back.unitPrice);
						pretendCount = BigDecimal.valueOf(ri.orderCount).subtract(back.minimumOrderValue);
						if(pretendCount >= 0.0){
							minimumOrderCount = pretendCount;
							minimumCost = back.unitPrice;
						}
					}
				}


				data.minimumOrderCount = minimumOrderCount;
				data.minimumCost = minimumCost;
				data.minimumOrderCountList = minimumOrderCountList;
				data.minimumCostList = minimumCostList;
				minimumInfoList.add(minimumData);
				data.minimumInfoList = minimumInfoList;
*/
				reorderList.add(data);
			}//loop dataList--end

			context.reorderList = reorderList;	
		
			//先运行service,剩余额度没用到订货表
			twoMonthsSales= BigDecimal.ZERO; 
			overseaQty = BigDecimal.ZERO;
			occupationCount = BigDecimal.ZERO;
			surplusQuota = BigDecimal.ZERO;
			
			inventoryList = delegator.findByAnd("InventoryAmountReport",UtilMisc.toMap("parentOwnerGroup",sbu,"date",today),null,false);
			if(UtilValidate.isNotEmpty(inventoryList)){
				for(inventory in inventoryList){
					twoMonthsSales = twoMonthsSales.add(inventory.twoMonthsSales);
					overseaQty = overseaQty.add(inventory.overseaQty);
					occupationCount = occupationCount.add(inventory.occupationCount);
				}
			}
			surplusQuota = twoMonthsSales.subtract(overseaQty).subtract(occupationCount) ;
			context.twoMonthsSales =twoMonthsSales;
			context.surplusQuota = surplusQuota;
}

