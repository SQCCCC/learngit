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
import com.gudao.product.productService;

userLogin = parameters.userLogin;
userLoginId = userLogin.getString("userLoginId");
userLoginPartyId = userLogin.getString("partyId");
externalAuthId = userLogin.getString("externalAuthId");

userLoginAdmin = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId, "groupId", "GUDAO-ADMIN"), null, false);
boolean hasUpdatePermission = false;

productId = parameters.productId;
skuType = parameters.skuType;

productMaster = delegator.findOne("ProductMaster", ["productId" : productId],false);
ownerGroup = productMaster.ownerGroup;

userLoginPartyIdCheck = StringUtils.remove(userLoginPartyId, "_GROUP");
if (ownerGroup.equals(userLoginPartyIdCheck)) {
    hasUpdatePermission = true;
}
if (externalAuthId.equals("MARKETING")) {
    hasUpdatePermission = true;
}

if (UtilValidate.isNotEmpty(userLoginAdmin)) {
    hasUpdatePermission = true;
}
context.hasUpdatePermission = hasUpdatePermission;

language = parameters.language;
if (UtilValidate.isEmpty(language)) {
language = "en";
}

if (hasUpdatePermission) {
    feature = delegator.findOne("ProductDescription", UtilMisc.toMap("productId", productId, "type", "FEATURE", "language", language), false);
    if (UtilValidate.isEmpty(feature)) {
        product = delegator.findOne("ProductMaster", UtilMisc.toMap("productId", productId), false);
        if (UtilValidate.isNotEmpty(product)) {
            if (UtilValidate.isNotEmpty(product.motherSku)) {
                productId = product.motherSku;
                skuType = "PARENT";
            }
        }
    }
    context.language = language;

    feature = delegator.findOne("ProductDescription", UtilMisc.toMap("productId", productId, "type", "FEATURE", "language", language), false);
    context.feature = feature;

    specification = delegator.findOne("ProductDescription", UtilMisc.toMap("productId", productId, "type", "SPECIFICATION", "language", language), false);
    context.specification = specification;

    packageIncludes = delegator.findOne("ProductDescription", UtilMisc.toMap("productId", productId, "type", "PACKAGE", "language", language), false);
    context.packageIncludes = packageIncludes;

}
context.productId = productId;
context.skuType = skuType;

