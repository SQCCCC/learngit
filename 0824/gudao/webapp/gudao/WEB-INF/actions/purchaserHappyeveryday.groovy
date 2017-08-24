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
Calendar calendar = Calendar.getInstance();
sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
java.sql.Date today = new java.sql.Date(calendar.getTime().getTime());
context.d = today;
String externalAuthId = null;
purchaserList = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId",userLoginId), false);
if((purchaserList.partyId)=="PURCHASING_GROUP"){
	externalAuthId = purchaserList.externalAuthId;
	findReorderList = delegator.findByAnd("ReorderInfo", UtilMisc.toMap("purchaser",externalAuthId,"purchaseStatusId","PENDING","date",today),null,false);
}
context.findReorderList = findReorderList;








