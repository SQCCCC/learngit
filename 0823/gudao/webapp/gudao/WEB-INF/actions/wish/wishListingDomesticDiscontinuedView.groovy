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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
/*import java.sql.Date;
import java.text.DateFormat;*/
import java.text.SimpleDateFormat;
import javolution.util.FastMap;
import javolution.util.FastList;

userLogin = parameters.userLogin;
userLoginId = userLogin.getString("userLoginId");
userLoginPartyId = userLogin.getString("partyId");
userLoginAdmin = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId, "groupId", "GUDAO-ADMIN"), null, false);

productStoreIdInput = parameters.productStoreId;
EntityCondition cond = null;
List<EntityCondition> conditions = FastList.newInstance();
if(UtilValidate.isNotEmpty(productStoreIdInput)){
	conditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreIdInput));
}
cond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
productStoreList = delegator.findList("WishListingDomesticDiscontinued", cond, null, ["productStoreId" ,"wishId","sku"], null, false);
List<GenericValue> distinctStoreList = delegator.findList("WishListingDomesticDiscontinued", null,  UtilMisc.toSet("productStoreId"), UtilMisc.toList("productStoreId"),
                   new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true), false);
context.distinctStoreList = distinctStoreList;
allEndProductList=[];
for(endList in productStoreList){
	data=[:];
	oneStoreId = endList.productStoreId;
	oneSku = endList.normalizedSku;
	enabled =endList.enabled;
	wishSku = endList.sku;
	data.productStoreId = oneStoreId;
	data.normalizedSku = oneSku;
	data.enabled = enabled;
	data.sku = wishSku;
	data.wishId = endList.wishId;
	data.varSeqId = endList.varSeqId;
	allEndProductList.add(data);
}
context.wish = allEndProductList;

context.type = "DISCONTINUED";


