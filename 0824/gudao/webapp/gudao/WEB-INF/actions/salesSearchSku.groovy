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


userLogin = parameters.userLogin;
userLoginId = userLogin.getString("userLoginId");
userLoginPartyId = userLogin.getString("partyId");

userLoginAdmin = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId, "groupId", "GUDAO-ADMIN"), null, false);

isSbu = false;
sbuPartyRoleList = delegator.findByAnd("PartyRoleDetailAndPartyDetail", UtilMisc.toMap("partyId", userLoginPartyId, "roleTypeId", "SBU", "statusId", "PARTY_ENABLED"), null, false);
if (UtilValidate.isNotEmpty(sbuPartyRoleList)) {
    isSbu = true;
    ownerGroup = EntityUtil.getFirst(sbuPartyRoleList).officeSiteName;
    context.ownerGroup = ownerGroup;
} else {
    ownerGroup = StringUtils.remove(userLoginPartyId, "_GROUP");
    context.ownerGroup = ownerGroup;
}
isPlatform = false;
platformPartyRoleList = delegator.findByAnd("PartyRoleDetailAndPartyDetail", UtilMisc.toMap("partyId", userLoginPartyId, "roleTypeId", "PLATFORM", "statusId", "PARTY_ENABLED"), null, false);
if (UtilValidate.isNotEmpty(platformPartyRoleList)) {
    isPlatform = true;
}

isAdmin = false;
if (UtilValidate.isNotEmpty(userLoginAdmin)) {
    isAdmin = true;
}
context.isSbuParameter = isSbu;
context.isAdmin = isAdmin;


productId = parameters.productId;
fromDateInput = parameters.fromDate;
toDateInput = parameters.toDate;

if (UtilValidate.isNotEmpty(productId)) {
    productId = productId.trim();
    context.productId = productId;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    Calendar today = Calendar.getInstance();
    todayStr = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH) + 1) + "-" + today.get(Calendar.DATE);
    java.sql.Date todaySql = new java.sql.Date(format.parse(todayStr).getTime());

    Calendar yesterdayCal = Calendar.getInstance();
    yesterdayCal.add(Calendar.DATE, -1);
    yesterdayStr = yesterdayCal.get(Calendar.YEAR) + "-" + (yesterdayCal.get(Calendar.MONTH) + 1) + "-" + yesterdayCal.get(Calendar.DATE);
    java.sql.Date yesterdaySql = new java.sql.Date(format.parse(yesterdayStr).getTime());

    java.sql.Date fromDate = yesterdaySql;
    java.sql.Date toDate = todaySql;

    if (UtilValidate.isNotEmpty(fromDateInput)) {
        Date fromDateParse = format.parse(fromDateInput.substring(0,10));
        fromDate = new java.sql.Date(fromDateParse.getTime());
    }

    if (UtilValidate.isNotEmpty(toDateInput)) {
        Date toDateParse = format.parse(toDateInput.substring(0,10));
        toDate = new java.sql.Date(toDateParse.getTime());
    }

    Timestamp fromDateTs = new Timestamp ((fromDate).getTime());
    Timestamp toDateTs = new Timestamp ((toDate).getTime());

    salesReportImportCondition = EntityCondition.makeCondition(UtilMisc.toList(
                                                                    EntityCondition.makeCondition("productId", EntityOperator.LIKE, "%" + productId + "%"),
                                                                    EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateTs),
                                                                    EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO, toDateTs)
                                                                    )
                                                            , EntityJoinOperator.AND);
    salesReportImportList = delegator.findList("SalesReportImport", salesReportImportCondition, null, ["productId ASC"], null, false);

    context.salesReportImportList = salesReportImportList;
    context.orderCount = salesReportImportList.size();
}




