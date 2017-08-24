package com.gudao.wish;
//http://developer.ebay.com/DevZone/XML/docs/WebHelp/wwhelp/wwhimpl/js/html/wwhelp.htm?context=eBay_XML_API&topic=StandardData
//Reference: http://developer.ebay.com/DevZone/XML/docs/Reference/ebay/GetItem.html

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.List;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.Random;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.calendar.RecurrenceRule;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringEscapeUtils;

import org.json.JSONObject;
import org.json.XML;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import javolution.util.FastMap;

public class wish {
	private static final String module = wish.class.getName();
    
    public static Map<String, String> wishProperties ()
    throws IOException {
        
        Map<String, String> mapContent = FastMap.newInstance();
        try {   //main try -- START
            Properties properties = new Properties();
            properties.load(new FileInputStream("hot-deploy/gudao/config/wish.properties"));
            Enumeration e = properties.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                mapContent.put(key, properties.getProperty(key).toString());
            }
        }   //main try -- END
        catch (IOException e) {
            e.printStackTrace();
        }
        return mapContent;
    }
    
    public static int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }
    
    public static String sendHttpRequest(String myUrl) throws IOException
    
    {   //sendHttpRequest
        String response = null;
        
        //new code
        try {
            
            URL url = new URL(myUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(60000);
            connection.connect();
            
            int responseCode = connection.getResponseCode();
            InputStream inputStream = null;
            
            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                response = inputStreamToString(inputStream);
            } else if (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                do {
                    Debug.logError("connection Time out, repost HTTP request", module);
                    HttpURLConnection newConnection = (HttpURLConnection) url.openConnection();;
                    newConnection.setRequestMethod("GET");
                    newConnection.setReadTimeout(60000);
                    newConnection.connect();
                    
                    responseCode = newConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                        inputStream = newConnection.getInputStream();
                        response = inputStreamToString(inputStream);
                    }
                    
                }
                while (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT);
            }
            else {
                inputStream = connection.getErrorStream();
                response = inputStreamToString(inputStream);
            }
            

            //return (response == null || "".equals(response.trim())) ? String.valueOf(responseCode) : response;
        }//new code
        catch (Exception e) {
            e.printStackTrace();
        }//new code
        return response;
        
    }   //End of sendHttpRequest
    
    public static Map<String, Object> wishGetRefreshCode(DispatchContext dctx, Map context)
    throws IOException, GenericEntityException, SAXException, ParserConfigurationException, GenericServiceException
    {   //wishGetRefreshCode
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        String getAccessCode = (String) context.get("getAccessCode");
        
        boolean getAccessCodeBoolean = false;
        if (UtilValidate.isNotEmpty(getAccessCode) && getAccessCode.toUpperCase().equals("Y")) {
            getAccessCodeBoolean = true;
        }

        String response = null;
        Properties properties = new Properties();
        properties.load(new FileInputStream("hot-deploy/gudao/config/wish.properties"));
        
        String urlFromProperty = "refreshCodeUrl";
        if (getAccessCodeBoolean) {
            urlFromProperty = "accessCodeUrl";
        }
        String postItemsUrl = properties.getProperty(urlFromProperty);
        
        Calendar now = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(now.getTime());
        
        StringBuffer propertyUrl = new StringBuffer("?&format=xml");
        try {   //main TRY == START
            String clientId = null;
            String clientSecret = null;
            String redirectUri = null;
            String authorizationCode = null;
            String accessCode = null;
            String refreshCode = null;
            
            GenericValue clientIdGV = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_CLIENT_ID"), false);
            if (UtilValidate.isNotEmpty(clientIdGV)) {
                clientId = URLEncoder.encode(clientIdGV.getString("idValue"), "UTF-8");
                propertyUrl.append("&client_id=" + clientId);
            }
            
            GenericValue clientSecretGV = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_CLIENT_SECRET"), false);
            if (UtilValidate.isNotEmpty(clientSecretGV)) {
                clientSecret = URLEncoder.encode(clientSecretGV.getString("idValue"), "UTF-8");
                propertyUrl.append("&client_secret=" + clientSecret);
            }
            
            if (getAccessCodeBoolean) {
                GenericValue redirectUriGV = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_REDIRECT_URI"), false);
                if (UtilValidate.isNotEmpty(redirectUriGV)) {
                    redirectUri = URLEncoder.encode(redirectUriGV.getString("idValue"), "UTF-8");
                    propertyUrl.append("&redirect_uri=" + redirectUri);
                }
                
                GenericValue authorizationCodeGV = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_AUTHORIZATION_CODE"), false);
                if (UtilValidate.isNotEmpty(authorizationCodeGV)) {
                    authorizationCode = URLEncoder.encode(authorizationCodeGV.getString("idValue"), "UTF-8");
                    propertyUrl.append("&code=" + authorizationCode);
                    propertyUrl.append("&grant_type=authorization_code");
                }
            } else {
                GenericValue refreshCodeGV = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_REFRESH_CODE"), false);
                if (UtilValidate.isNotEmpty(refreshCodeGV)) {
                    refreshCode = URLEncoder.encode(refreshCodeGV.getString("idValue"), "UTF-8");
                    propertyUrl.append("&refresh_token=" + refreshCode);
                    propertyUrl.append("&grant_type=refresh_token");
                }
            }
            
        }   //main TRY == END
        catch (Exception e) {
            e.printStackTrace();
        }
        //Debug.logError(postItemsUrl + propertyUrl.toString(), module);
        URL url = new URL(postItemsUrl + propertyUrl.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(60000);
        connection.connect();
        
        int responseCode = connection.getResponseCode();
        InputStream inputStream = null;
        
        if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
            inputStream = connection.getInputStream();
            response = inputStreamToString(inputStream);
        } else if (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
            do {
                Debug.logError("connection Time out, repost HTTP request", module);
                HttpURLConnection newConnection = (HttpURLConnection) url.openConnection();;
                newConnection.setRequestMethod("GET");
                newConnection.setReadTimeout(60000);
                newConnection.connect();
                
                responseCode = newConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = newConnection.getInputStream();
                    response = inputStreamToString(inputStream);
                }
                
            }
            while (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        }
        else {
            inputStream = connection.getErrorStream();
            response = inputStreamToString(inputStream);
        }
        
        String responseXML = response;
        //Debug.logError(responseXML, module);
        Document docResponse = UtilXml.readXmlDocument(responseXML, true);
        Element elemResponse = docResponse.getDocumentElement();
        String ack = UtilXml.childElementValue(elemResponse, "Code", null);
        //Debug.logError("Code: " + ack, module);
        if (ack.equals("0")) {    //if ack success -- START
            
            String jsonCode = UtilXml.childElementValue(elemResponse, "Data", null);
            if (UtilValidate.isNotEmpty(jsonCode)) {    //if jsonCode is not empty == START
                JSONObject json = new JSONObject(jsonCode);
                String xml = XML.toString(json);
                StringBuffer responseXmlBuffer = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><Response>");
                responseXmlBuffer.append(xml);
                responseXmlBuffer.append("</Response>");
                String responseXml = responseXmlBuffer.toString();
                //Debug.logError(responseXml, module);
                Document rootDoc = UtilXml.readXmlDocument(responseXml, true);
                Element rootElement = rootDoc.getDocumentElement();
                
                
                String resultAccessCode = UtilXml.childElementValue(rootElement, "access_token", null);
                if (UtilValidate.isNotEmpty(resultAccessCode)) {
                    resultAccessCode = resultAccessCode.replaceAll("u&apos;","");
                    resultAccessCode = resultAccessCode.replaceAll("&apos;","");
                    resultAccessCode = resultAccessCode.replaceAll("u'","");
                    resultAccessCode = resultAccessCode.replaceAll("'","");
                }
                
                String refreshCode = UtilXml.childElementValue(rootElement, "refresh_token", null);
                refreshCode = refreshCode.replaceAll("u&apos;","");
                refreshCode = refreshCode.replaceAll("&apos;","");
                refreshCode = refreshCode.replaceAll("u'","");
                refreshCode = refreshCode.replaceAll("'","");
                String merchantUser = UtilXml.childElementValue(rootElement, "merchant_user_id", null);
                
                GenericValue accessCodeGV = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_ACCESS_CODE"));
                accessCodeGV.set("idValue", resultAccessCode);
                delegator.createOrStore(accessCodeGV);
                
                GenericValue refreshCodeGV = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_REFRESH_CODE"));
                refreshCodeGV.set("idValue", refreshCode);
                delegator.createOrStore(refreshCodeGV);
                
                GenericValue merchantUserGV = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_MERCHANT_USER_ID"));
                merchantUserGV.set("idValue", merchantUser);
                delegator.createOrStore(merchantUserGV);
            }   //if jsonCode is not empty == END
            
            //writing data into database -- START
            /*
            int accessCodeStart = responseXML.indexOf("'access_token':");
            int accessCodeEnd = responseXML.indexOf("', '", accessCodeStart);
            
            String resultAccessCode = responseXML.substring(accessCodeStart + 17, accessCodeEnd);
            
            if (UtilValidate.isNotEmpty(resultAccessCode)) {
                resultAccessCode = resultAccessCode.replaceAll("'","");
                
                int refreshCodeStart = responseXML.indexOf("'refresh_token':");
                int refreshCodeEnd = responseXML.indexOf("'}", refreshCodeStart);
                String refreshCode = responseXML.substring(refreshCodeStart + 18, refreshCodeEnd);
                refreshCode = refreshCode.replaceAll("'","");
                
                int merchantUserStart = responseXML.indexOf("'merchant_user_id':");
                int merchantUserEnd = responseXML.indexOf("', '", merchantUserStart);
                String merchantUser = responseXML.substring(merchantUserStart + 21, merchantUserEnd);
                merchantUser = merchantUser.replaceAll("'","");
                
                GenericValue accessCodeGV = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_ACCESS_CODE"));
                accessCodeGV.set("idValue", resultAccessCode);
                delegator.createOrStore(accessCodeGV);
                
                GenericValue refreshCodeGV = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_REFRESH_CODE"));
                refreshCodeGV.set("idValue", refreshCode);
                delegator.createOrStore(refreshCodeGV);
                
                GenericValue merchantUserGV = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_MERCHANT_USER_ID"));
                merchantUserGV.set("idValue", merchantUser);
                delegator.createOrStore(merchantUserGV);
            }*/
            //writing data into database -- END
            
        }   //if ack success -- END
        else {  //if ack failure -- START
            String errorMessage = UtilXml.childElementValue(elemResponse, "Message", null);
            FileWriter f1 = new FileWriter("hot-deploy/gudao/webapp/gudao/wish/logError/oauth.log", true);
            f1.write(today + ": product Store ID: " + productStoreId + ", Codes : " + ack + ": " + errorMessage + "\n");
            f1.close();
        }   //if ack failure -- END
        //Reading XML -- END
        
        return result;
    }   //wishGetRefreshCode
 
    private static String inputStreamToString(InputStream inputStream) throws IOException
    {
        String string;
        StringBuilder outputBuilder = new StringBuilder();
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (null != (string = reader.readLine())) {
                outputBuilder.append(string).append('\n');
            }
        }
        return outputBuilder.toString();
    }   //End of inputStreamToString
    
    public static Map<String, Object> crawlAllAccount (DispatchContext dctx, Map context)
    throws GenericEntityException, GenericServiceException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        try {   //main try -- START
            List<GenericValue> productStoreList = delegator.findByAnd("ProductStore", UtilMisc.toMap("primaryStoreGroupId", "WISH", "isDemoStore", "N"), null, false);
            for (GenericValue productStore : productStoreList) {        //loop productStoreList -- START
                String productStoreId = productStore.getString("productStoreId");
                Map getAllProducts = dispatcher.runSync("wishGetAllProducts", UtilMisc.toMap("productStoreId", productStoreId, "userLogin", userLogin));
            }   //loop productStoreList -- START
        }   //main try -- END
        catch (GenericEntityException e) {
            Debug.logError("crawlAllAccount GenericEntityException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (GenericServiceException e) {
            Debug.logError("crawlAllAccount GenericEntityException Error for TRY CATCH: " + e.getMessage(), module);
        }
        return result;
        
    }
    
    public static Map<String, Object> getAllProducts (DispatchContext dctx, Map context)
    throws GenericEntityException, IOException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        String url = wishProperties().get("apiUrl") + "product/multi-get";
        
        if (UtilValidate.isNotEmpty(productStoreId)) {
            productStoreId = productStoreId.trim();
        }
        
        Calendar now = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(now.getTime());
        java.sql.Date dateSql = new java.sql.Date(now.getTime().getTime());
        int productCount = 0;
        
        try {   //main try -- START
            Debug.logError("Wish crawling " + productStoreId, module);
            GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
            Map removeListing = dispatcher.runSync("wishRemoveListing", UtilMisc.toMap("productStoreId", productStoreId, "userLogin", userLogin));
            String productStoreGroup = productStore.getString("primaryStoreGroupId");
            if (!productStoreGroup.equals("WISH")) {
                return ServiceUtil.returnSuccess("productStoreId " + productStoreId + " is not a wish account");
            }
            List<GenericValue> productStoreRoleList = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStore.getString("productStoreId"), "roleTypeId", "WISH_ACCOUNT"), null, false);
            if (UtilValidate.isEmpty(productStoreRoleList)) {   //if productStoreRoleList is empty -- START
                Debug.logError("ProductStoreId " + productStoreId + " does not have Wish account role", module);
                return ServiceUtil.returnError("ProductStoreId " + productStoreId + " does not have Wish account role");
            }   //if productStoreRoleList is empty -- END
            GenericValue productStoreRole = EntityUtil.getFirst(productStoreRoleList);
            GenericValue wishAccountPartyGroup = productStoreRole.getRelatedOne("PartyGroup", false);
            GenericValue wishAccountParty = wishAccountPartyGroup.getRelatedOne("Party", false);
            GenericValue partyIdentification = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId",wishAccountParty.getString("partyId"), "partyIdentificationTypeId", "WISH_ACCESS_CODE"), false);
            String apiKey = URLEncoder.encode(partyIdentification.getString("idValue"), "UTF-8");
            
            String requestUrl = url + "?format=xml&limit=250&access_token=" + apiKey;
            boolean keepLooping = false;
            int loopCount = 0;
            //Debug.logError("URL: " + requestUrl, module);
            do {    //do loop -- START
                //Debug.logError("run Check DO", module);
                keepLooping = false;
                //Debug.logError("URL: " + requestUrl, module);
                String responseXML = sendHttpRequest(requestUrl);
                //Debug.logError(responseXML, module);
                //Reading XML -- START
                Document docResponse = UtilXml.readXmlDocument(responseXML, true);
                Element elemResponse = docResponse.getDocumentElement();
                String ack = UtilXml.childElementValue(elemResponse, "Code", null);
                //Debug.logError("Code: " + ack, module);
                if (ack.equals("0")) {    //if ack success -- START
                    //writing data into database -- START
                    Map writeResult = writeWishProductIntoDatabase(dctx, context, responseXML);
                    productCount += (Integer) writeResult.get("productCount");
                    //writing data into database -- END
                    //FileWriter f1 = new FileWriter("hot-deploy/gudao/webapp/gudao/wish/logError/responseXML" + loopCount + ".xml", true);
                    //f1.write(responseXML);
                    //f1.close();
                    
                    Element pagingElement = UtilXml.firstChildElement(elemResponse, "Paging");
                    List<? extends Element> nextElements = UtilXml.childElementList(pagingElement, "Next");
                    Iterator<? extends Element> nextElementsElemIter = nextElements.iterator();
                    while (nextElementsElemIter.hasNext()) {    //check Next Element -- START
                        Element nextElement = nextElementsElemIter.next();
                        String nextUrl = UtilXml.elementValue(nextElement);
                        if (UtilValidate.isNotEmpty(nextUrl)) {
                            keepLooping = true;
                            requestUrl = nextUrl;
                        }
                    }   //check Next Element -- END
                }   //if ack success -- END
                else if (ack.equals("1016") || ack.equals("1015")) {  //refresh token == START
                    Map refreshWishToken = dispatcher.runSync("wishGetRefreshCode", UtilMisc.toMap("productStoreId", productStoreId, "userLogin", userLogin));
                    if (ServiceUtil.isSuccess(refreshWishToken)) {
                        Map rerunThisFunction = dispatcher.runSync("wishGetAllProducts", UtilMisc.toMap("productStoreId", productStoreId, "userLogin", userLogin));
                    }
                    break;
                }   //refresh token == END
                else {  //if ack failure -- START
                    String errorMessage = UtilXml.childElementValue(elemResponse, "Message", null);
                    FileWriter f1 = new FileWriter("hot-deploy/gudao/webapp/gudao/wish/logError/product-multi-get.log", true);
                    f1.write(today + ": product Store ID: " + productStoreId + ", Codes : " + ack + ": " + errorMessage + "\n");
                    f1.close();
                }   //if ack failure -- END
                //Reading XML -- END
                loopCount++;
            }   //do loop -- END
            while (keepLooping);
            //Debug.logError("run Check End", module);
        }   //main try -- END
        catch (GenericEntityException e) {
            Debug.logError("Yasin: getAllProducts GenericEntityException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (SAXException e) {
            Debug.logError("Yasin: getAllProducts SAXException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (ParserConfigurationException e) {
            Debug.logError("Yasin: getAllProducts ParserConfigurationException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (IOException e) {
            Debug.logError("Yasin: getAllProducts IOException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (Exception e) {
            e.printStackTrace();
            Debug.logError("Yasin: getAllProducts Exception Error for TRY CATCH: " + e.getMessage(), module);
            try {
                Map rerunGetAllProducts = dispatcher.runSync("wishGetAllProducts", UtilMisc.toMap("productStoreId", productStoreId, "userLogin", userLogin));
            }
            catch (Exception ee) {
                ee.printStackTrace();
                Debug.logError("Rerun exception also return another exception", module);
                
                GenericValue updateActiveListingStatusFailed = delegator.makeValue("ActiveListingStatus", UtilMisc.toMap("productStoreId", productStoreId, "platform", "WISH", "date", Timestamp.valueOf(today)));
                updateActiveListingStatusFailed.set("totalListingCount", Long.valueOf(productCount));
                updateActiveListingStatusFailed.set("status", "CRAWL_FAILED");
                delegator.createOrStore(updateActiveListingStatusFailed);
                
                return ServiceUtil.returnError("ProductStoreId " + productStoreId + " failed Crawling WISH allhash");
            }
        }
        
        GenericValue updateLastRuntime = delegator.makeValue("AutoScheduleJobHistory", UtilMisc.toMap("productStoreId", productStoreId, "serviceName", "wishGetAllProducts", "lastRuntime", Timestamp.valueOf(today)));
        delegator.createOrStore(updateLastRuntime);
        
        GenericValue updateActiveListingStatus = delegator.makeValue("ActiveListingStatus", UtilMisc.toMap("productStoreId", productStoreId, "platform", "WISH", "date", Timestamp.valueOf(today)));
        updateActiveListingStatus.set("totalListingCount", Long.valueOf(productCount));
        updateActiveListingStatus.set("status", "CRAWL_FINISHED");
        delegator.createOrStore(updateActiveListingStatus);
        return result;
    }   //getAllProducts
    
    public static Map<String, Object> writeWishProductIntoDatabase(DispatchContext dctx, Map context, String responseXML)
    throws GenericEntityException, GenericServiceException, SAXException, ParserConfigurationException, IOException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        //formatting the sequence ID
        DecimalFormat df = new DecimalFormat("00000");
        String productStoreId = (String) context.get("productStoreId");
        Calendar now = Calendar.getInstance();
        Date nowDate = new Date();
        int productCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        
        try {   //main try -- START
            //Debug.logError("run Check Write", module);
            Document docResponse = UtilXml.readXmlDocument(responseXML, true);
            Element elemResponse = docResponse.getDocumentElement();
            String code = UtilXml.childElementValue(elemResponse, "Code", null);
            
            if (code.equals("0")) { //if response OK -- START
                
                Element dataElement = UtilXml.firstChildElement(elemResponse, "Data");
                List<? extends Element> productElements = UtilXml.childElementList(dataElement, "Product");
                Iterator<? extends Element> productElementsElemIter = productElements.iterator();
                while (productElementsElemIter.hasNext()) { //loop Product -- START
                    
                    Element productElement = productElementsElemIter.next();
                    String mainImage = UtilXml.childElementValue(productElement, "main_image", null);
                    String isPromoted = UtilXml.childElementValue(productElement, "is_promoted", null);
                    String description = UtilXml.childElementValue(productElement, "description", null);
                    String reviewStatus = UtilXml.childElementValue(productElement, "review_status", null);
                    String upc = UtilXml.childElementValue(productElement, "upc", null);
                    String extraImages = UtilXml.childElementValue(productElement, "extra_images", null);
                    String numberSaves = UtilXml.childElementValue(productElement, "number_saves", null);
                    String numberSold = UtilXml.childElementValue(productElement, "number_sold", null);
                    String parentSku = UtilXml.childElementValue(productElement, "parent_sku", null);
                    String wishId = UtilXml.childElementValue(productElement, "id", null);
                    String name = UtilXml.childElementValue(productElement, "name", null);
                    String dateUploaded = UtilXml.childElementValue(productElement, "date_uploaded", null);
                    
                    GenericValue wishListing = delegator.findOne("WishListing", UtilMisc.toMap("wishId", wishId), false);
                    if (UtilValidate.isEmpty(wishListing)) {
                        wishListing = delegator.makeValue("WishListing", UtilMisc.toMap("wishId", wishId));
                    }
                    
                    wishListing.set("parentSku", parentSku);
                    wishListing.set("productStoreId", productStoreId);
                    wishListing.set("mainImage", mainImage);
                    wishListing.set("name", name);
                    wishListing.set("isPromoted", isPromoted.toUpperCase());
                    wishListing.set("description", description);
                    wishListing.set("reviewStatus", reviewStatus.toUpperCase());
                    wishListing.set("extraImages", extraImages);
                    if (UtilValidate.isEmpty(numberSaves)) {
                        numberSaves = "0";
                    }
                    if (UtilValidate.isEmpty(numberSold)) {
                        numberSold = "0";
                    }
                    wishListing.set("numberSaves", Long.valueOf(numberSaves));
                    wishListing.set("numberSold", Long.valueOf(numberSold));
                    wishListing.set("dateUploaded", new java.sql.Date(sdf.parse(dateUploaded).getTime()));
                    
                    Element wishExpressElement = UtilXml.firstChildElement(productElement, "wish_express_country_codes");
                    String wishExpress = UtilXml.childElementValue(wishExpressElement, "wish_express_country_codes", null);
                    wishListing.set("wishExpress", wishExpress);
                    
                    delegator.createOrStore(wishListing);
                    
                    
                    Element tagsElement = UtilXml.firstChildElement(productElement, "tags");
                    List<? extends Element> tagsList = UtilXml.childElementList(tagsElement, "Tag");
                    Iterator<? extends Element> tagsListElemIter = tagsList.iterator();
                    int tagSeq = 0;
                    while (tagsListElemIter.hasNext()) {    //loop tags -- START
                        
                        tagSeq++;
                        String tagSeqId = df.format(tagSeq);
                        Element tagElement = tagsListElemIter.next();
                        String tagId = UtilXml.childElementValue(tagElement, "id", null);
                        String tagName = UtilXml.childElementValue(tagElement, "name", null);
                        GenericValue wishTag = delegator.findOne("WishTags", UtilMisc.toMap("wishId", wishId, "tagSeqId", tagSeqId, "tagType", "INPUT_TAG"), false);
                        if (UtilValidate.isEmpty(wishTag)) {
                            wishTag = delegator.makeValue("WishTags", UtilMisc.toMap("wishId", wishId, "tagSeqId", tagSeqId, "tagType", "INPUT_TAG"));
                        }
                        wishTag.set("tagId", tagId);
                        wishTag.set("tagName", tagName);
                        delegator.createOrStore(wishTag);
                    }   //loop tags -- START
                    
                    Element autoTagsElement = UtilXml.firstChildElement(productElement, "auto_tags");
                    if (UtilValidate.isNotEmpty(autoTagsElement)) { //if auto_tags not empty == START
                        List<? extends Element> autoTagsList = UtilXml.childElementList(autoTagsElement, "Tag");
                        Iterator<? extends Element> autoTagsListElemIter = autoTagsList.iterator();
                        int autoTagSeq = 0;
                        while (autoTagsListElemIter.hasNext()) {    //loop auto tags -- START
                            autoTagSeq++;
                            String autoTagSeqId = df.format(autoTagSeq);
                            Element tagElement = autoTagsListElemIter.next();
                            String tagId = UtilXml.childElementValue(tagElement, "id", null);
                            String tagName = UtilXml.childElementValue(tagElement, "name", null);
                            GenericValue wishTag = delegator.findOne("WishTags", UtilMisc.toMap("wishId", wishId, "tagSeqId", autoTagSeqId, "tagType", "AUTO_TAG"), false);
                            if (UtilValidate.isEmpty(wishTag)) {
                                wishTag = delegator.makeValue("WishTags", UtilMisc.toMap("wishId", wishId, "tagSeqId", autoTagSeqId, "tagType", "AUTO_TAG"));
                            }
                            wishTag.set("tagId", tagId);
                            wishTag.set("tagName", tagName);
                            delegator.createOrStore(wishTag);
                        }   //loop auto tags -- START
                    }   //if auto_tags not empty == END
                    
                    
                    //Variations -- START
                    Element variantsElement = UtilXml.firstChildElement(productElement, "variants");
                    List<? extends Element> variantList = UtilXml.childElementList(variantsElement, "Variant");
                    Iterator<? extends Element> variantListElemIter = variantList.iterator();
                    int varSeq = 0;
                    while (variantListElemIter.hasNext()) { //loop variants -- START
                        //Debug.logError("variation runs", module);
                        varSeq++;
                        String varSeqId = df.format(varSeq);
                        Element variantElement = variantListElemIter.next();
                        String sku = UtilXml.childElementValue(variantElement, "sku", null);
                        String productId = UtilXml.childElementValue(variantElement, "product_id", null);
                        String size = UtilXml.childElementValue(variantElement, "size", null);
                        String color = UtilXml.childElementValue(variantElement, "color", null);
                        String price = UtilXml.childElementValue(variantElement, "price", null);
                        String enabled = UtilXml.childElementValue(variantElement, "enabled", null);
                        String shipping = UtilXml.childElementValue(variantElement, "shipping", null);
                        String allImages = UtilXml.childElementValue(variantElement, "all_images", null);
                        String inventory = UtilXml.childElementValue(variantElement, "inventory", null);
                        String variationId = UtilXml.childElementValue(variantElement, "id", null);
                        String msrp = UtilXml.childElementValue(variantElement, "msrp", null);
                        String shippingTime = UtilXml.childElementValue(variantElement, "shipping_time", null);
                        
                        GenericValue wishListingVariation = delegator.findOne("WishListingVariation", UtilMisc.toMap("wishId", wishId, "varSeqId", varSeqId), false);
                        if (UtilValidate.isEmpty(wishListingVariation)) {
                            wishListingVariation = delegator.makeValue("WishListingVariation", UtilMisc.toMap("wishId", wishId, "varSeqId", varSeqId));
                        }
                        wishListingVariation.set("sku", sku);
                        wishListingVariation.set("normalizedSku", normalizeSkuWish(delegator,sku));
                        wishListingVariation.set("productStoreId", productStoreId);
                        wishListingVariation.set("productId", productId);
                        wishListingVariation.set("size", size);
                        wishListingVariation.set("color", color);
                        if (UtilValidate.isEmpty(price)) {
                            price = "0";
                        }
                        wishListingVariation.set("price", Double.valueOf(price));
                        wishListingVariation.set("enabled", enabled.toUpperCase());
                        if (UtilValidate.isEmpty(shipping)) {
                            shipping = "0";
                        }
                        wishListingVariation.set("shipping", Double.valueOf(shipping));
                        wishListingVariation.set("allImages", allImages);
                        if (UtilValidate.isEmpty(inventory)) {
                            inventory = "0";
                        }
                        wishListingVariation.set("inventory", Long.valueOf(inventory));
                        wishListingVariation.set("variationId", variationId);
                        if (UtilValidate.isEmpty(msrp)) {
                            msrp = "0";
                        }
                        wishListingVariation.set("msrp", Double.valueOf(msrp));
                        wishListingVariation.set("shippingTime", shippingTime);
                        delegator.createOrStore(wishListingVariation);
                    }   //loop variants -- END
                    //Variations -- END
                    productCount++;
                }   //loop Product -- END
            }   //if response OK -- END
        }   //main try -- END
        catch (GenericEntityException e) {
            Debug.logError("Yasin: writeWishProductIntoDatabase GenericEntityException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (SAXException e) {
            Debug.logError("Yasin: writeWishProductIntoDatabase SAXException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (ParserConfigurationException e) {
            Debug.logError("Yasin: writeWishProductIntoDatabase ParserConfigurationException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (IOException e) {
            Debug.logError("Yasin: writeWishProductIntoDatabase IOException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (Exception e) {
            e.printStackTrace();
            Debug.logError("Yasin: writeWishProductIntoDatabase Exception Error for TRY CATCH: " + e.getMessage(), module);
        }
        result.put("productCount", productCount);
        return result;
    }   //writeWishProductIntoDatabase
    
    public static Map<String, Object> removeListing (DispatchContext dctx, Map context)
    throws GenericEntityException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String productStoreId = (String) context.get("productStoreId");
        Calendar now = Calendar.getInstance();
        java.sql.Date date = new java.sql.Date(now.getTime().getTime());
        
        try {
            delegator.removeByAnd("WishTags", UtilMisc.toMap("productStoreId", productStoreId));
            delegator.removeByAnd("WishListingVariation", UtilMisc.toMap("productStoreId", productStoreId));
            delegator.removeByAnd("WishListing", UtilMisc.toMap("productStoreId", productStoreId));
        }
        catch (GenericEntityException e) {
            Debug.logError("Yasin: removeWishListing GenericEntityException Error for TRY CATCH: " + e.getMessage(), module);
        }
        return result;
    }
    
    public static String normalizeSkuWish(Delegator delegator, String sku)	//Description
    throws GenericEntityException, GenericServiceException {
        
        try {
            GenericValue productMaster = delegator.findOne("ProductMaster", UtilMisc.toMap("productId", sku), false);
            if (UtilValidate.isEmpty(productMaster)) {
                int skuLength = sku.length();
                if (sku.contains("|")) {    //new 变体 format -- START
                    String term[]= sku.split("\\|");
                    sku = term[0];
                }
                else {  //old 变体 format -- START
                    if (sku.startsWith("AA")) {
                        sku = sku.replaceFirst("AA","");
                    }
                    if (sku.startsWith("BB")) {
                        sku = sku.replaceFirst("BB","");
                    }
                    if (sku.startsWith("CC")) {
                        sku = sku.replaceFirst("CC","");
                    }
                    if (sku.startsWith("DD")) {
                        sku = sku.replaceFirst("DD","");
                    }
                    if (sku.startsWith("EE")) {
                        sku = sku.replaceFirst("EE","");
                    }
                    if (sku.startsWith("FF")) {
                        sku = sku.replaceFirst("FF","");
                    }
                    if (sku.startsWith("GG")) {
                        sku = sku.replaceFirst("GG","");
                    }
                    if (sku.startsWith("HH")) {
                        sku = sku.replaceFirst("HH","");
                    }
                    if (sku.startsWith("II")) {
                        sku = sku.replaceFirst("II","");
                    }
                    if (sku.startsWith("JJ")) {
                        sku = sku.replaceFirst("JJ","");
                    }
                    if (sku.startsWith("KK")) {
                        sku = sku.replaceFirst("KK","");
                    }
                    if (sku.startsWith("LL")) {
                        sku = sku.replaceFirst("LL","");
                    }
                    if (sku.startsWith("MM")) {
                        sku = sku.replaceFirst("MM","");
                    }
                    if (sku.startsWith("NN")) {
                        sku = sku.replaceFirst("NN","");
                    }
                    if (sku.startsWith("OO")) {
                        sku = sku.replaceFirst("OO","");
                    }
                    if (sku.startsWith("QQ")) {
                        sku = sku.replaceFirst("QQ","");
                    }
                    if (sku.startsWith("SS")) {
                        sku = sku.replaceFirst("SS","");
                    }
                    if (sku.startsWith("WW")) {
                        sku = sku.replaceFirst("WW","");
                    }
                    if (sku.startsWith("YY")) {
                        sku = sku.replaceFirst("YY","");
                    }
                    if (sku.startsWith("ZZ")) {
                        sku = sku.replaceFirst("ZZ","");
                    }
                    if (sku.startsWith("FR")) {
                        sku = sku.replaceFirst("FR","");
                    }
                    if (sku.startsWith("LT")) {
                        sku = sku.replaceFirst("LT","");
                    }
                    if (sku.startsWith("WSGD-")) {
                        sku = sku.replaceFirst("WSGD-","");
                    }
                    if (sku.startsWith("WSGDPL-")) {
                        sku = sku.replaceFirst("WSGDPL-","");
                    }
                    if (sku.startsWith("WSDE-")) {
                        sku = sku.replaceFirst("WSDE-","");
                    }
                    if (sku.startsWith("WSDEPL-")) {
                        sku = sku.replaceFirst("WSDEPL-","");
                    }
                    if (sku.startsWith("WSWA-")) {
                        sku = sku.replaceFirst("WSWA-","");
                    }
                    if (sku.startsWith("WSWAPL-")) {
                        sku = sku.replaceFirst("WSWAPL-","");
                    }
                    if (sku.startsWith("WSOM-")) {
                        sku = sku.replaceFirst("WSOM-","");
                    }
                    if (sku.startsWith("WSOMPL-")) {
                        sku = sku.replaceFirst("WSOMPL-","");
                    }
                    if (sku.startsWith("WSHG-")) {
                        sku = sku.replaceFirst("WSHG-","");
                    }
                    if (sku.startsWith("WSHGPL-")) {
                        sku = sku.replaceFirst("WSHGPL-","");
                    }
                    if (sku.startsWith("WSWF-")) {
                        sku = sku.replaceFirst("WSWF-","");
                    }
                    if (sku.startsWith("WSHB-")) {
                        sku = sku.replaceFirst("WSHB-","");
                    }
                    if (sku.startsWith("WSSB-")) {
                        sku = sku.replaceFirst("WSSB-","");
                    }
                    if (sku.startsWith("WSMF-")) {
                        sku = sku.replaceFirst("WSMF-","");
                    }
                    if (sku.startsWith("WSJW-")) {
                        sku = sku.replaceFirst("WSJW-","");
                    }
                    if (sku.contains("#")) {
                        sku = sku.replaceAll("#","");
                    }
                    if (sku.endsWith("AAAA")) {
                        sku = sku.replaceFirst("AAAA","");
                    }
                    if (sku.endsWith("BB")) {
                        if (sku.endsWith("BBBB")) {
                            sku = sku.replaceFirst("BBBB","");
                        } else {
                            sku = sku.substring(0,skuLength - 2);
                        }
                    }
                    if (sku.endsWith("GG")) {
                        sku = sku.substring(0,skuLength - 2);
                    }
                }   //old 变体 format -- END
                
                if (sku.contains(" ")) {
                    sku = sku.replaceAll(" ","");
                }
            }
        }
        catch (GenericEntityException e) {
            e.printStackTrace();
            //Debug.logError(e.getMessage(), module);
        }
        return sku;
        
    }   //normalizeSku
    
    public static Map<String, Object> createNewAccount (DispatchContext dctx, Map context)
    throws GenericEntityException, GenericServiceException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("accountName");
        String clientId = (String) context.get("clientId");
        String clientSecret = (String) context.get("clientSecret");
        String authorizationCode = (String) context.get("authorizationCode");
        String redirectUri = (String) context.get("redirectUri");
        productStoreId = productStoreId.trim();
        clientId = clientId.trim();
        clientSecret =  clientSecret.trim();
        authorizationCode = authorizationCode.trim();
        if (UtilValidate.isNotEmpty(redirectUri)) {
            redirectUri = redirectUri.trim();
        } else {
            redirectUri = "https://merchant.wish.com";
        }
        
        try {   //main try == START
            String owner = userLogin.getString("partyId");
            List<GenericValue> existingProductStoreList = delegator.findByAnd("ProductStore", UtilMisc.toMap("primaryStoreGroupId", "WISH"), null, false);
            int wishSeq = 1;
            if (UtilValidate.isNotEmpty(existingProductStoreList)) {
                for (GenericValue existingProductStore : existingProductStoreList) {    //loop existingProductStoreList == START
                    String existingSubtitle = existingProductStore.getString("subtitle");
                    int currentStoreSeq = Integer.parseInt(existingSubtitle);
                    if (wishSeq <= currentStoreSeq) {
                        wishSeq = currentStoreSeq;
                    }
                }   //loop existingProductStoreList == END
                wishSeq = wishSeq + 1;
            }
            GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
            if (UtilValidate.isEmpty(productStore)) {   //if productStore is not empty == START
                productStore = delegator.makeValue("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
                productStore.set("storeName", productStoreId);
                productStore.set("title", productStoreId);
                productStore.set("subtitle", wishSeq + "");
                productStore.set("companyName", "Gudao");
                productStore.set("primaryStoreGroupId", "WISH");
                productStore.set("isDemoStore", "N");
                productStore.set("visualThemeId", "EC_DEFAULT");
                productStore.set("inventoryFacilityId", "PDWarehouse");
                productStore.set("defaultSalesChannelEnumId", "WISH_SALES_CHANNEL");
                productStore.set("owner", owner);
                delegator.create(productStore);
                
                GenericValue party = delegator.makeValue("Party", UtilMisc.toMap("partyId", productStoreId, "partyTypeId", "PARTY_GROUP", "preferredCurrencyUomId", "USD", "statusId", "PARTY_ENABLED"));
                delegator.create(party);
                
                GenericValue partyGroup = delegator.makeValue("PartyGroup", UtilMisc.toMap("partyId", productStoreId, "groupName", productStoreId));
                delegator.create(partyGroup);
                
                GenericValue partyRole = delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", productStoreId, "roleTypeId", "WISH_ACCOUNT"));
                delegator.create(partyRole);
                
                Calendar now = Calendar.getInstance();
                String fixedFromDate = new SimpleDateFormat("2010-01-01 HH:mm:ss.SSS").format(now.getTime());
                GenericValue productStoreRole = delegator.makeValue("ProductStoreRole", UtilMisc.toMap("partyId", productStoreId, "roleTypeId", "WISH_ACCOUNT", "productStoreId", productStoreId, "fromDate", Timestamp.valueOf(fixedFromDate)));
                delegator.create(productStoreRole);
                
                GenericValue clientIdGV = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", productStoreId));
                clientIdGV.set("partyIdentificationTypeId", "WISH_CLIENT_ID");
                clientIdGV.set("idValue", clientId);
                delegator.create(clientIdGV);
                
                GenericValue clientSecretGV = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", productStoreId));
                clientSecretGV.set("partyIdentificationTypeId", "WISH_CLIENT_SECRET");
                clientSecretGV.set("idValue", clientSecret);
                delegator.create(clientSecretGV);
                
                GenericValue redirectUriGV = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", productStoreId));
                redirectUriGV.set("partyIdentificationTypeId", "WISH_REDIRECT_URI");
                redirectUriGV.set("idValue", redirectUri);
                delegator.create(redirectUriGV);
                
                GenericValue authorizationCodeGV = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", productStoreId));
                authorizationCodeGV.set("partyIdentificationTypeId", "WISH_AUTHORIZATION_CODE");
                authorizationCodeGV.set("idValue", authorizationCode);
                delegator.create(authorizationCodeGV);
                
                Map wishGetRefreshCode = dispatcher.runSync("wishGetRefreshCode", UtilMisc.toMap("productStoreId", productStoreId, "getAccessCode", "Y", "userLogin", userLogin));
                if (ServiceUtil.isSuccess(wishGetRefreshCode)) {
                    result = ServiceUtil.returnSuccess();
                } else {
                    result = ServiceUtil.returnError("wishGetRefreshCode returns error: " + wishGetRefreshCode.get("errorMessage"));
                }
            }   //if productStore is not empty == END
            else {  //if productStore is empty == START
                result = ServiceUtil.returnSuccess("AccountName " + productStoreId + " has been created before. Not doing anything");
            }   //if productStore is empty == END
        }   //main try == END
        catch (GenericEntityException e) {
            e.printStackTrace();
            //Debug.logError(e.getMessage(), module);
        }
        catch (GenericServiceException e) {
            e.printStackTrace();
            //Debug.logError(e.getMessage(), module);
        }
        
        return result;

        
    }   //createNewAccount
    
    public static Map<String, Object> getAccountStatistic (DispatchContext dctx, Map context)
    throws GenericEntityException, GenericServiceException, SAXException, ParserConfigurationException, IOException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("accountName");
        
        if (UtilValidate.isNotEmpty(productStoreId)) {
            productStoreId = productStoreId.trim();
        }
        
        try {   //main try == START
            List<GenericValue> wishListingVariationList = delegator.findByAnd("WishListingVariation", UtilMisc.toMap("productStoreId", productStoreId, "enabled", "TRUE"), UtilMisc.toList("lastUpdatedStamp DESC"), true);
            if (UtilValidate.isNotEmpty(wishListingVariationList)) {    //if wishListingVariationList is not empty == START
                GenericValue wishListingVariation = EntityUtil.getFirst(wishListingVariationList);
                GenericValue wishListing = delegator.findOne("WishListing", UtilMisc.toMap("listingId", wishListingVariation.getString("listingId")), false);
                String wishId = wishListing.getString("wishId");
                
                Map wishCrawlPageSingle = dispatcher.runSync("wishCrawlPageSingle", UtilMisc.toMap("wishId", wishId, "userLogin", userLogin));
                if (ServiceUtil.isSuccess(wishCrawlPageSingle)) {   //if wishCrawlPageSingle success == START
                    String responseXml = wishCrawlPageSingle.get("xml").toString();
                
                    if (UtilValidate.isNotEmpty(responseXml)) { //if responseXml not empty == START
                        Document docResponse = UtilXml.readXmlDocument(responseXml, true);
                        Element elemResponse = docResponse.getDocumentElement();
                        
                        GenericValue wishAccountStatistic = delegator.makeValue("WishAccountStatistic", UtilMisc.toMap("productStoreId", productStoreId));
                        String trustedStore = UtilXml.childElementValue(elemResponse, "from_trusted_store", null);
                        if (UtilValidate.isNotEmpty(trustedStore)) {    //update trustedStore == START
                            if (trustedStore.toUpperCase().equals(trustedStore)) {
                                wishAccountStatistic.set("trustedStore", "Y");
                            } else {
                                wishAccountStatistic.set("trustedStore", "N");
                            }
                        } else {
                            wishAccountStatistic.set("trustedStore", "N");
                        }   //update trustedStore == END
                        
                        Element dataElement = UtilXml.firstChildElement(elemResponse, "commerce_product_info");
                        List<? extends Element> variationsElements = UtilXml.childElementList(dataElement, "variations");
                        Iterator<? extends Element> variationsElementsElemIter = variationsElements.iterator();
                        while (variationsElementsElemIter.hasNext()) {  //loop variationsElementsElemIter == START
                            Element variationsElement = variationsElementsElemIter.next();
                            String merchantName = UtilXml.childElementValue(variationsElement, "merchant_name", null);
                            String merchantId = UtilXml.childElementValue(variationsElement, "merchant_id", null);
                            String merchantRatingCount = UtilXml.childElementValue(variationsElement, "merchant_rating_count", null);
                            String merchantRating = UtilXml.childElementValue(variationsElement, "merchant_rating", null);
                            
                            if (UtilValidate.isNotEmpty(merchantName)) {
                                wishAccountStatistic.set("merchantName", merchantName);
                            }
                            if (UtilValidate.isNotEmpty(merchantId)) {
                                wishAccountStatistic.set("merchantId", merchantId);
                            }
                            if (UtilValidate.isNotEmpty(merchantRatingCount)) {
                                wishAccountStatistic.set("merchantRatingCount", Long.valueOf(merchantRatingCount));
                            }
                            if (UtilValidate.isNotEmpty(merchantRating)) {
                                wishAccountStatistic.set("merchantRating", Double.valueOf(merchantRating));
                            }
                        }   //loop variationsElementsElemIter == END
                        delegator.createOrStore(wishAccountStatistic);
                    }   //if responseXml not empty == END
                }   //if wishCrawlPageSingle success == END
            }   //if wishListingVariationList is not empty == END
        }   //main try == END
        catch (GenericEntityException e) {
            e.printStackTrace();
            //Debug.logError(e.getMessage(), module);
        }
        /*catch (GenericServiceException e) {
            e.printStackTrace();
            //Debug.logError(e.getMessage(), module);
        }*/
        catch (SAXException e) {
            Debug.logError("Yasin: getAccountStatistic SAXException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (ParserConfigurationException e) {
            Debug.logError("Yasin: getAccountStatistic ParserConfigurationException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (IOException e) {
            Debug.logError("Yasin: getAccountStatistic IOException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (Exception e) {
            e.printStackTrace();
            Debug.logError("Yasin: getAccountStatistic Exception Error for TRY CATCH: " + e.getMessage(), module);
        }
        return result;
    }   //getAccountStatistic
    
    public static Map<String, Object> crawlPageSingle (DispatchContext dctx, Map context) {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String wishId = (String) context.get("wishId");
        
        if (UtilValidate.isNotEmpty(wishId)) {
            wishId = wishId.trim();
        }
        
        try {   //main try == START
            long requestHeaderCount = delegator.findCountByCondition("WishHtmlRequestHeader", null, null, null);
            int randomWishHtmlRequestHeader = randomWithRange(1, new Long(requestHeaderCount).intValue());
            GenericValue wishHtmlRequestHeader = delegator.findOne("WishHtmlRequestHeader", UtilMisc.toMap("sequence", Long.valueOf(randomWishHtmlRequestHeader)), false);
            
            String userAgent = wishHtmlRequestHeader.getString("userAgent");
            String bsid = null;
            String xsrf = null;
            String cookieExpires = null;
            boolean newRequestHeader = false;
            
            if (UtilValidate.isEmpty(wishHtmlRequestHeader.getString("bsid"))) { //if bsid empty == START
                URL obj = new URL("http://www.wish.com");
                URLConnection conn = obj.openConnection();
                Map<String, List<String>> initialWishCon = conn.getHeaderFields();
                String cookieOriginal = initialWishCon.get("Set-Cookie").toString();
                String[] cookieArray = cookieOriginal.split(";");
                for (int i = 0; i < cookieArray.length; i++) {  //loop cookieArray == START
                    String cookie = cookieArray[i];
                    if (UtilValidate.isNotEmpty(cookie)) {  //if cookie is not empty == START
                        if (cookie.matches(".*bsid.*")) {
                            int bsidStart = cookie.indexOf("bsid=") + 5;
                            bsid = cookie.substring(bsidStart).trim();
                        }
                        if (cookie.matches(".*_xsrf.*")) {
                            int xsrfStart = cookie.indexOf("_xsrf=") + 6;
                            xsrf = cookie.substring(xsrfStart).trim();
                        }
                        if (cookie.matches(".*expires.*")) {
                            int expiresStart = cookie.indexOf("expires=") + 8;
                            cookieExpires = cookie.substring(expiresStart).trim();
                        }
                    }   //if cookie is not empty == END
                }   //loop cookieArray == END
                wishHtmlRequestHeader.set("bsid", bsid);
                wishHtmlRequestHeader.set("xsrf", xsrf);
                wishHtmlRequestHeader.set("cookieExpires", cookieExpires);
                delegator.store(wishHtmlRequestHeader);
                newRequestHeader = true;
            }   //if bsid empty == END
            
            if (UtilValidate.isEmpty(bsid)) {
                bsid = wishHtmlRequestHeader.getString("bsid");
            }
            
            if (UtilValidate.isEmpty(xsrf)) {
                xsrf = wishHtmlRequestHeader.getString("xsrf");
            }
            
            if (!newRequestHeader) { //if newRequestHeader is TRUE == START
                URL obj = new URL("http://www.wish.com");
                URLConnection conn = obj.openConnection();
                
                Map<String, List<String>> initialWishCon = conn.getHeaderFields();
                
                String cookieOriginal = initialWishCon.get("Set-Cookie").toString();
                String[] cookieArray = cookieOriginal.split(";");
                for (int i = 0; i < cookieArray.length; i++) {  //loop cookieArray == START
                    String cookie = cookieArray[i];
                    if (UtilValidate.isNotEmpty(cookie)) {  //if cookie is not empty == START
                        if (cookie.matches(".*bsid.*")) {
                            int bsidStart = cookie.indexOf("bsid=") + 5;
                            bsid = cookie.substring(bsidStart).trim();
                        }
                        if (cookie.matches(".*_xsrf.*")) {
                            int xsrfStart = cookie.indexOf("_xsrf=") + 6;
                            xsrf = cookie.substring(xsrfStart).trim();
                        }
                        if (cookie.matches(".*expires.*")) {
                            int expiresStart = cookie.indexOf("expires=") + 8;
                            cookieExpires = cookie.substring(expiresStart).trim();
                        }
                    }   //if cookie is not empty == END
                }   //loop cookieArray == END
                wishHtmlRequestHeader.set("bsid", bsid);
                wishHtmlRequestHeader.set("xsrf", xsrf);
                wishHtmlRequestHeader.set("cookieExpires", cookieExpires);
                delegator.store(wishHtmlRequestHeader);
            }   //if newRequestHeader is TRUE == END
            
            String requestCookie = "bsid=" + bsid + "; _xsrf=" + xsrf;
            String postItemsUrl = "https://www.wish.com/c/" + wishId;
            String response = null;
            
            Map<String, String> requestPropertyMap = FastMap.<String, String>newInstance();
            requestPropertyMap.put("Host", "www.wish.com");
            requestPropertyMap.put("User-Agent", userAgent);
            requestPropertyMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            requestPropertyMap.put("Accept-Language", "en-US,en;q=0.5");
            requestPropertyMap.put("Accept-Encoding", "gzip, deflate, br");
            requestPropertyMap.put("DNT", "1");
            requestPropertyMap.put("Cookie", requestCookie);
            requestPropertyMap.put("Connection", "keep-alive");
            requestPropertyMap.put("Upgrade-Insecure-Requests", "1");
            
            URL url = new URL(postItemsUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(60*1000);
            connection.connect();
            
            int responseCode = connection.getResponseCode();
            InputStream inputStream = null;
            
            
            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                response = inputStreamToString(inputStream);
            } else {
                inputStream = connection.getErrorStream();
                response = inputStreamToString(inputStream);
            }
            
            int jsonStart = response.indexOf("pageParams['mainContestObj'] =");
            int jsonEnd = response.indexOf("pageParams", jsonStart + 1);
            
            String jsonCode = response.substring(jsonStart + 30, jsonEnd - 2);
            
            JSONObject json = new JSONObject(jsonCode);
            String xml = XML.toString(json);
            
            StringBuffer responseXmlBuffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><CrawlResponse xmlns=\"urn:ebay:apis:eBLBaseComponents\">");
            responseXmlBuffer.append(xml);
            responseXmlBuffer.append("</CrawlResponse>");
            String responseXml = responseXmlBuffer.toString();
            
            int extraPhotoStart = responseXml.indexOf("<extra_photo_urls>");
            int extraPhotoEnd = responseXml.indexOf("</extra_photo_urls>", extraPhotoStart);
            StringBuffer responseBuffer = new StringBuffer(responseXml);
            responseBuffer.replace(extraPhotoStart, extraPhotoEnd + 19, "");
            responseXml = responseBuffer.toString();
            
            int extraPhotoDetailsStart = responseXml.indexOf("<extra_photo_details>");
            int extraPhotoDetailsEnd = responseXml.indexOf("</extra_photo_details>", extraPhotoDetailsStart);
            StringBuffer responseBuffer2 = new StringBuffer(responseXml);
            responseBuffer2.replace(extraPhotoDetailsStart, extraPhotoDetailsEnd + 22, "");
            responseXml = responseBuffer2.toString();
            
            int sizingChartStart = responseXml.indexOf("<sizing_chart_data>");
            int sizingChartEnd = responseXml.indexOf("</sizing_chart_data>", sizingChartStart);
            StringBuffer responseBuffer3 = new StringBuffer(responseXml);
            responseBuffer3.replace(sizingChartStart, sizingChartEnd + 10, "");
            responseXml = responseBuffer3.toString();
            
            //Debug.logError(xml, module);
            result.put("xml", responseXml);
        }   //main try == END
        catch (Exception e) {
            e.printStackTrace();
            Debug.logError("Yasin: crawlPageSingle Exception Error for TRY CATCH: " + e.getMessage(), module);
        }
        return result;
    }   //crawlPageSingle
    
    public static Map<String, Object> crawlPageSingleMap (DispatchContext dctx, Map context) {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Map<String, Object> xmlMapResult = FastMap.<String, Object>newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String wishId = (String) context.get("wishId");
        
        if (UtilValidate.isNotEmpty(wishId)) {
            wishId = wishId.trim();
        }
        
        try {   //main try == START
            long requestHeaderCount = delegator.findCountByCondition("WishHtmlRequestHeader", null, null, null);
            int randomWishHtmlRequestHeader = randomWithRange(1, new Long(requestHeaderCount).intValue());
            GenericValue wishHtmlRequestHeader = delegator.findOne("WishHtmlRequestHeader", UtilMisc.toMap("sequence", Long.valueOf(randomWishHtmlRequestHeader)), false);
            
            String userAgent = wishHtmlRequestHeader.getString("userAgent");
            String bsid = null;
            String xsrf = null;
            String cookieExpires = null;
            boolean newRequestHeader = false;
            
            if (UtilValidate.isEmpty(wishHtmlRequestHeader.getString("bsid"))) { //if bsid empty == START
                URL obj = new URL("http://www.wish.com");
                URLConnection conn = obj.openConnection();
                Map<String, List<String>> initialWishCon = conn.getHeaderFields();
                String cookieOriginal = initialWishCon.get("Set-Cookie").toString();
                String[] cookieArray = cookieOriginal.split(";");
                for (int i = 0; i < cookieArray.length; i++) {  //loop cookieArray == START
                    String cookie = cookieArray[i];
                    if (UtilValidate.isNotEmpty(cookie)) {  //if cookie is not empty == START
                        if (cookie.matches(".*bsid.*")) {
                            int bsidStart = cookie.indexOf("bsid=") + 5;
                            bsid = cookie.substring(bsidStart).trim();
                        }
                        if (cookie.matches(".*_xsrf.*")) {
                            int xsrfStart = cookie.indexOf("_xsrf=") + 6;
                            xsrf = cookie.substring(xsrfStart).trim();
                        }
                        if (cookie.matches(".*expires.*")) {
                            int expiresStart = cookie.indexOf("expires=") + 8;
                            cookieExpires = cookie.substring(expiresStart).trim();
                        }
                    }   //if cookie is not empty == END
                }   //loop cookieArray == END
                wishHtmlRequestHeader.set("bsid", bsid);
                wishHtmlRequestHeader.set("xsrf", xsrf);
                wishHtmlRequestHeader.set("cookieExpires", cookieExpires);
                delegator.store(wishHtmlRequestHeader);
                newRequestHeader = true;
            }   //if bsid empty == END
            
            if (UtilValidate.isEmpty(bsid)) {
                bsid = wishHtmlRequestHeader.getString("bsid");
            }
            
            if (UtilValidate.isEmpty(xsrf)) {
                xsrf = wishHtmlRequestHeader.getString("xsrf");
            }
            
            if (!newRequestHeader) { //if newRequestHeader is TRUE == START
                URL obj = new URL("http://www.wish.com");
                URLConnection conn = obj.openConnection();
                
                Map<String, List<String>> initialWishCon = conn.getHeaderFields();
                
                String cookieOriginal = initialWishCon.get("Set-Cookie").toString();
                String[] cookieArray = cookieOriginal.split(";");
                for (int i = 0; i < cookieArray.length; i++) {  //loop cookieArray == START
                    String cookie = cookieArray[i];
                    if (UtilValidate.isNotEmpty(cookie)) {  //if cookie is not empty == START
                        if (cookie.matches(".*bsid.*")) {
                            int bsidStart = cookie.indexOf("bsid=") + 5;
                            bsid = cookie.substring(bsidStart).trim();
                        }
                        if (cookie.matches(".*_xsrf.*")) {
                            int xsrfStart = cookie.indexOf("_xsrf=") + 6;
                            xsrf = cookie.substring(xsrfStart).trim();
                        }
                        if (cookie.matches(".*expires.*")) {
                            int expiresStart = cookie.indexOf("expires=") + 8;
                            cookieExpires = cookie.substring(expiresStart).trim();
                        }
                    }   //if cookie is not empty == END
                }   //loop cookieArray == END
                wishHtmlRequestHeader.set("bsid", bsid);
                wishHtmlRequestHeader.set("xsrf", xsrf);
                wishHtmlRequestHeader.set("cookieExpires", cookieExpires);
                delegator.store(wishHtmlRequestHeader);
            }   //if newRequestHeader is TRUE == END
            
            String requestCookie = "bsid=" + bsid + "; _xsrf=" + xsrf;
            String postItemsUrl = "https://www.wish.com/c/" + wishId;
            String response = null;
            
            Map<String, String> requestPropertyMap = FastMap.<String, String>newInstance();
            requestPropertyMap.put("Host", "www.wish.com");
            requestPropertyMap.put("User-Agent", userAgent);
            requestPropertyMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            requestPropertyMap.put("Accept-Language", "en-US,en;q=0.5");
            requestPropertyMap.put("Accept-Encoding", "gzip, deflate, br");
            requestPropertyMap.put("DNT", "1");
            requestPropertyMap.put("Cookie", requestCookie);
            requestPropertyMap.put("Connection", "keep-alive");
            requestPropertyMap.put("Upgrade-Insecure-Requests", "1");
            
            URL url = new URL(postItemsUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(60*1000);
            connection.connect();
            
            int responseCode = connection.getResponseCode();
            InputStream inputStream = null;
            
            
            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                response = inputStreamToString(inputStream);
            } else {
                inputStream = connection.getErrorStream();
                response = inputStreamToString(inputStream);
            }
            
            int jsonStart = response.indexOf("pageParams['mainContestObj'] =");
            int jsonEnd = response.indexOf("pageParams", jsonStart + 1);
            
            String jsonCode = response.substring(jsonStart + 30, jsonEnd - 2);
            
            JSONObject json = new JSONObject(jsonCode);
            String xml = XML.toString(json);
            
            StringBuffer responseXmlBuffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><CrawlResponse xmlns=\"urn:ebay:apis:eBLBaseComponents\">");
            responseXmlBuffer.append(xml);
            responseXmlBuffer.append("</CrawlResponse>");
            String responseXml = responseXmlBuffer.toString();
            
            int extraPhotoStart = responseXml.indexOf("<extra_photo_urls>");
            int extraPhotoEnd = responseXml.indexOf("</extra_photo_urls>", extraPhotoStart);
            StringBuffer responseBuffer = new StringBuffer(responseXml);
            responseBuffer.replace(extraPhotoStart, extraPhotoEnd + 19, "");
            responseXml = responseBuffer.toString();
            
            int extraPhotoDetailsStart = responseXml.indexOf("<extra_photo_details>");
            int extraPhotoDetailsEnd = responseXml.indexOf("</extra_photo_details>", extraPhotoDetailsStart);
            StringBuffer responseBuffer2 = new StringBuffer(responseXml);
            responseBuffer2.replace(extraPhotoDetailsStart, extraPhotoDetailsEnd + 22, "");
            responseXml = responseBuffer2.toString();
            
            int sizingChartStart = responseXml.indexOf("<sizing_chart_data>");
            int sizingChartEnd = responseXml.indexOf("</sizing_chart_data>", sizingChartStart);
            StringBuffer responseBuffer3 = new StringBuffer(responseXml);
            responseBuffer3.replace(sizingChartStart, sizingChartEnd + 10, "");
            responseXml = responseBuffer3.toString();
            
            Debug.logError(responseXml, module);
            
            Document rootDoc = UtilXml.readXmlDocument(responseXml, true);
            Element rootElement = rootDoc.getDocumentElement();
            
            String activeSweep = UtilXml.childElementValue(rootElement, "active_sweep", "FALSE");
            String aspectRatio = UtilXml.childElementValue(rootElement, "aspect_ratio", "0.0");
            String brand = UtilXml.childElementValue(rootElement, "brand", null);
            String canGift = UtilXml.childElementValue(rootElement, "can_gift", "FALSE");
            String canPromo = UtilXml.childElementValue(rootElement, "can_promo", "FALSE");
            String commentCount = UtilXml.childElementValue(rootElement, "comment_count", "0");
            String contestPagePicture = UtilXml.childElementValue(rootElement, "contest_page_picture", null);
            String firstRatingImageIndex = UtilXml.childElementValue(rootElement, "first_rating_image_index", "0");
            String fromTrustedStore = UtilXml.childElementValue(rootElement, "from_trusted_store", "FALSE");
            String gender = UtilXml.childElementValue(rootElement, "gender", null);
            String generationTime = UtilXml.childElementValue(rootElement, "generation_time", null);
            String hasReward = UtilXml.childElementValue(rootElement, "has_reward", "FALSE");
            String isActive = UtilXml.childElementValue(rootElement, "is_active", "FALSE");
            String isAdminUser = UtilXml.childElementValue(rootElement, "is_admin_user", "FALSE");
            String isClean = UtilXml.childElementValue(rootElement, "is_clean", "FALSE");
            String isConcept = UtilXml.childElementValue(rootElement, "is_concept", "FALSE");
            String isContentReviewer = UtilXml.childElementValue(rootElement, "is_content_reviewer", "FALSE");
            String isDeleted = UtilXml.childElementValue(rootElement, "is_deleted", "FALSE");
            String isDirty = UtilXml.childElementValue(rootElement, "is_dirty", "FALSE");
            String isExpired = UtilXml.childElementValue(rootElement, "is_expired", "FALSE");
            String isVerified = UtilXml.childElementValue(rootElement, "is_verified", "FALSE");
            String name = UtilXml.childElementValue(rootElement, "name", null);
            String numBought = UtilXml.childElementValue(rootElement, "num_bought", "0");
            String numEntered = UtilXml.childElementValue(rootElement, "num_entered", "0");
            String numWishes = UtilXml.childElementValue(rootElement, "num_wishes", "0");
            String numWon = UtilXml.childElementValue(rootElement, "num_won", "0");
            String numContestPhotos = UtilXml.childElementValue(rootElement, "num_contest_photos", "0");
            String numExtraPhotos = UtilXml.childElementValue(rootElement, "num_extra_photos", "0");
            
            String requiresReview = UtilXml.childElementValue(rootElement, "requires_review", "FALSE");
            String sourceCountry = UtilXml.childElementValue(rootElement, "source_country", null);
            String uploadSource = UtilXml.childElementValue(rootElement, "upload_source", null);
            String userInActiveSweep = UtilXml.childElementValue(rootElement, "user_in_active_sweep", "FALSE");
            String value = UtilXml.childElementValue(rootElement, "value", null);
            
            Element commerceProductInfoElement = UtilXml.firstChildElement(rootElement, "commerce_product_info");
            String fbwActive = UtilXml.childElementValue(commerceProductInfoElement, "fbw_active", "0");
            String fbwPending = UtilXml.childElementValue(commerceProductInfoElement, "fbw_pending", "0");
            String isActiveFbwInUs = UtilXml.childElementValue(commerceProductInfoElement, "is_active_fbw_in_us", "FALSE");
            String isFulfillByWish = UtilXml.childElementValue(commerceProductInfoElement, "is_fulfill_by_wish", "FALSE");
            String removed = UtilXml.childElementValue(commerceProductInfoElement, "removed", "FALSE");
            
            Double minOriginalPrice = 10000.0;
            Double maxOriginalPrice = 0.0;
            Double minOriginalShipping = 10000.0;
            Double maxOriginalShipping = 0.0;
            
            List<? extends Element> variationsElements = UtilXml.childElementList(commerceProductInfoElement, "variations");
            Iterator<? extends Element> variationsElementsElemIter = variationsElements.iterator();
            while (variationsElementsElemIter.hasNext()) {  //loop variationsElementsElemIter == START
                Element variationsElement = variationsElementsElemIter.next();
                String merchantName = UtilXml.childElementValue(variationsElement, "merchant_name", null);
                String merchantRatingClass = UtilXml.childElementValue(variationsElement, "merchant_rating_class", null);
                String merchantRatingCount = UtilXml.childElementValue(variationsElement, "merchant_rating_count", null);
                String merchantRating = UtilXml.childElementValue(variationsElement, "merchant_rating", null);
                String shipsFrom = UtilXml.childElementValue(variationsElement, "ships_from", null);
                
                Double originalPrice = Double.valueOf(UtilXml.childElementValue(variationsElement, "original_price", "0"));
                Double originalShipping = Double.valueOf(UtilXml.childElementValue(variationsElement, "original_shipping", "0"));
                
                if (minOriginalPrice >= originalPrice) {
                    minOriginalPrice = originalPrice;
                }
                
                if (maxOriginalPrice <= originalPrice) {
                    maxOriginalPrice = originalPrice;
                }
                
                if (minOriginalShipping >= originalShipping) {
                    minOriginalShipping = originalShipping;
                }
                
                if (maxOriginalShipping <= originalShipping) {
                    maxOriginalShipping = originalShipping;
                }
                
                if (UtilValidate.isNotEmpty(merchantName)) {
                    xmlMapResult.put("merchantName", merchantName);
                }
                if (UtilValidate.isNotEmpty(merchantRatingClass)) {
                    xmlMapResult.put("merchantRatingClass", merchantRatingClass);
                }
                if (UtilValidate.isNotEmpty(merchantRatingCount)) {
                    xmlMapResult.put("merchantRatingCount", Double.valueOf(merchantRatingCount));
                }
                if (UtilValidate.isNotEmpty(merchantRating)) {
                    xmlMapResult.put("merchantRating", Double.valueOf(merchantRating));
                }
                if (UtilValidate.isNotEmpty(shipsFrom)) {
                    xmlMapResult.put("shipsFrom", shipsFrom);
                }
            }   //loop variationsElementsElemIter == END
            
            Element productRatingElement = UtilXml.firstChildElement(rootElement, "product_rating");
            String productRating = UtilXml.childElementValue(productRatingElement, "rating", "0.0");
            String productRatingClass = UtilXml.childElementValue(productRatingElement, "rating_class", null);
            String productRatingCount = UtilXml.childElementValue(productRatingElement, "rating_count", "0");
            
            List<String> tagsList= new ArrayList<String>();
            List<? extends Element> tagsElementList = UtilXml.childElementList(rootElement, "tags");
            Iterator<? extends Element> tagsListElemIter = tagsElementList.iterator();
            while (tagsListElemIter.hasNext()) {    //loop tags -- START
                Element tagElement = tagsListElemIter.next();
                String tagName = UtilXml.childElementValue(tagElement, "name", null);
                tagsList.add(tagName);
            }   //loop tags -- START
            
            List<String> merchantTagsList= new ArrayList<String>();
            List<? extends Element> merchantTagsElementList = UtilXml.childElementList(rootElement, "merchant_tags");
            Iterator<? extends Element> merchantTagsListElemIter = merchantTagsElementList.iterator();
            while (merchantTagsListElemIter.hasNext()) {    //loop tags -- START
                Element tagElement = merchantTagsListElemIter.next();
                String tagName = UtilXml.childElementValue(tagElement, "name", null);
                merchantTagsList.add(tagName);
            }   //loop tags -- START
            
            xmlMapResult.put("wishId", wishId);
            xmlMapResult.put("tagsList", tagsList);
            xmlMapResult.put("merchantTagsList", merchantTagsList);
            xmlMapResult.put("activeSweep", activeSweep.toUpperCase());
            xmlMapResult.put("aspectRatio", Double.valueOf(aspectRatio));
            xmlMapResult.put("brand", brand);
            xmlMapResult.put("canGift", canGift.toUpperCase());
            xmlMapResult.put("canPromo", canPromo.toUpperCase());
            xmlMapResult.put("commentCount", Long.valueOf(commentCount));
            xmlMapResult.put("contestPagePicture", contestPagePicture);
            xmlMapResult.put("fbwActive", Long.valueOf(fbwActive));
            xmlMapResult.put("fbwPending", Long.valueOf(fbwPending));
            xmlMapResult.put("firstRatingImageIndex", Long.valueOf(firstRatingImageIndex));
            xmlMapResult.put("fromTrustedStore", fromTrustedStore.toUpperCase());
            xmlMapResult.put("gender", gender);
            xmlMapResult.put("generationTime", generationTime);
            xmlMapResult.put("hasReward", hasReward.toUpperCase());
            xmlMapResult.put("isActive", isActive.toUpperCase());
            xmlMapResult.put("isActiveFbwInUs", isActiveFbwInUs.toUpperCase());
            xmlMapResult.put("isAdminUser", isAdminUser.toUpperCase());
            xmlMapResult.put("isClean", isClean.toUpperCase());
            xmlMapResult.put("isConcept", isConcept.toUpperCase());
            xmlMapResult.put("isContentReviewer", isContentReviewer.toUpperCase());
            xmlMapResult.put("isDeleted", isDeleted.toUpperCase());
            xmlMapResult.put("isDirty", isDirty.toUpperCase());
            xmlMapResult.put("isExpired", isExpired.toUpperCase());
            xmlMapResult.put("isFulfillByWish", isFulfillByWish.toUpperCase());
            xmlMapResult.put("isVerified", isVerified.toUpperCase());
            xmlMapResult.put("name", name);
            xmlMapResult.put("numBought", Long.valueOf(numBought));
            xmlMapResult.put("numEntered", Long.valueOf(numEntered));
            xmlMapResult.put("numWishes", Long.valueOf(numWishes));
            xmlMapResult.put("numWon", Long.valueOf(numWon));
            xmlMapResult.put("numContestPhotos", Long.valueOf(numContestPhotos));
            xmlMapResult.put("numExtraPhotos", Long.valueOf(numExtraPhotos));
            xmlMapResult.put("productRating", Double.valueOf(productRating));
            xmlMapResult.put("productRatingClass", productRatingClass);
            xmlMapResult.put("productRatingCount", Double.valueOf(productRatingCount));
            xmlMapResult.put("removed", removed.toUpperCase());
            xmlMapResult.put("requiresReview", requiresReview.toUpperCase());
            xmlMapResult.put("sourceCountry", sourceCountry);
            xmlMapResult.put("uploadSource", uploadSource);
            xmlMapResult.put("userInActiveSweep", userInActiveSweep.toUpperCase());
            xmlMapResult.put("value", value);
            xmlMapResult.put("wishId", wishId);
            xmlMapResult.put("minOriginalPrice", minOriginalPrice);
            xmlMapResult.put("maxOriginalPrice", maxOriginalPrice);
            xmlMapResult.put("minOriginalShipping", minOriginalShipping);
            xmlMapResult.put("maxOriginalShipping", maxOriginalShipping);
            
            //Debug.logError(xml, module);
            result.put("xmlMapResult", xmlMapResult);
        }   //main try == END
        catch (Exception e) {
            e.printStackTrace();
            Debug.logError("Yasin: crawlPageSingle Exception Error for TRY CATCH: " + e.getMessage(), module);
        }
        return result;
    }   //crawlPageSingleMap

    
    public static Map<String, Object> updateBestSellingSingle (DispatchContext dctx, Map context)
    throws GenericEntityException, IOException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        String wishId = (String) context.get("wishId");
        String url = wishProperties().get("apiUrl") + "product";
        
        if (UtilValidate.isNotEmpty(productStoreId)) {
            productStoreId = productStoreId.trim();
        }
        
        Calendar now = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(now.getTime());
        java.sql.Date dateSql = new java.sql.Date(now.getTime().getTime());
        DecimalFormat df = new DecimalFormat("00000");
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        
        try {   //main try -- START
            GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
            String productStoreGroup = productStore.getString("primaryStoreGroupId");
            if (!productStoreGroup.equals("WISH")) {
                return ServiceUtil.returnSuccess("productStoreId " + productStoreId + " is not a wish account");
            }
            List<GenericValue> productStoreRoleList = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStore.getString("productStoreId"), "roleTypeId", "WISH_ACCOUNT"), null, false);
            if (UtilValidate.isEmpty(productStoreRoleList)) {   //if productStoreRoleList is empty -- START
                Debug.logError("ProductStoreId " + productStoreId + " does not have Wish account role", module);
                return ServiceUtil.returnError("ProductStoreId " + productStoreId + " does not have Wish account role");
            }   //if productStoreRoleList is empty -- END
            GenericValue productStoreRole = EntityUtil.getFirst(productStoreRoleList);
            GenericValue wishAccountPartyGroup = productStoreRole.getRelatedOne("PartyGroup", false);
            GenericValue wishAccountParty = wishAccountPartyGroup.getRelatedOne("Party", false);
            GenericValue partyIdentification = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId",wishAccountParty.getString("partyId"), "partyIdentificationTypeId", "WISH_ACCESS_CODE"), false);
            String apiKey = URLEncoder.encode(partyIdentification.getString("idValue"), "UTF-8");
            
            String requestUrl = url + "?format=xml&access_token=" + apiKey + "&id=" + wishId;
            //Debug.logError(requestUrl, module);
            String responseXML = sendHttpRequest(requestUrl);
            //Debug.logError(responseXML, module);
            
            Document docResponse = UtilXml.readXmlDocument(responseXML, true);
            Element elemResponse = docResponse.getDocumentElement();
            String ack = UtilXml.childElementValue(elemResponse, "Code", null);
            if (ack.equals("0")) {    //if ack success -- START
                //writing data into database -- START
                Element dataElement = UtilXml.firstChildElement(elemResponse, "Data");
                List<? extends Element> productElements = UtilXml.childElementList(dataElement, "Product");
                Iterator<? extends Element> productElementsElemIter = productElements.iterator();
                while (productElementsElemIter.hasNext()) { //loop Product -- START
                    GenericValue wishListing = delegator.findOne("WishBestSellingHeader", UtilMisc.toMap("productStoreId", productStoreId, "wishId", wishId, "date", new java.sql.Date(nowDate.getTime())), false);
                    if (UtilValidate.isEmpty(wishListing)) {
                        wishListing = delegator.makeValue("WishBestSellingHeader", UtilMisc.toMap("productStoreId", productStoreId, "wishId", wishId, "date", new java.sql.Date(nowDate.getTime())));
                    }
                    
                    Map wishCrawlPageSingle = dispatcher.runSync("wishCrawlPageSingle", UtilMisc.toMap("wishId", wishId, "userLogin", userLogin));
                    if (ServiceUtil.isSuccess(wishCrawlPageSingle)) {   //if wishCrawlPageSingle successfull == START
                        //Debug.logError(wishCrawlPageSingle.get("xml").toString(), module);
                        Document rootDoc = UtilXml.readXmlDocument(wishCrawlPageSingle.get("xml").toString(), true);
                        Element rootElement = rootDoc.getDocumentElement();
                        
                        String activeSweep = UtilXml.childElementValue(rootElement, "active_sweep", "FALSE");
                        String aspectRatio = UtilXml.childElementValue(rootElement, "aspect_ratio", "0.0");
                        String brand = UtilXml.childElementValue(rootElement, "brand", null);
                        String canGift = UtilXml.childElementValue(rootElement, "can_gift", "FALSE");
                        String canPromo = UtilXml.childElementValue(rootElement, "can_promo", "FALSE");
                        String commentCount = UtilXml.childElementValue(rootElement, "comment_count", "0");
                        String firstRatingImageIndex = UtilXml.childElementValue(rootElement, "first_rating_image_index", "0");
                        String fromTrustedStore = UtilXml.childElementValue(rootElement, "from_trusted_store", "FALSE");
                        String gender = UtilXml.childElementValue(rootElement, "gender", null);
                        String generationTime = UtilXml.childElementValue(rootElement, "generation_time", null);
                        String hasReward = UtilXml.childElementValue(rootElement, "has_reward", "FALSE");
                        String isActive = UtilXml.childElementValue(rootElement, "is_active", "FALSE");
                        String isAdminUser = UtilXml.childElementValue(rootElement, "is_admin_user", "FALSE");
                        String isClean = UtilXml.childElementValue(rootElement, "is_clean", "FALSE");
                        String isConcept = UtilXml.childElementValue(rootElement, "is_concept", "FALSE");
                        String isContentReviewer = UtilXml.childElementValue(rootElement, "is_content_reviewer", "FALSE");
                        String isDeleted = UtilXml.childElementValue(rootElement, "is_deleted", "FALSE");
                        String isDirty = UtilXml.childElementValue(rootElement, "is_dirty", "FALSE");
                        String isExpired = UtilXml.childElementValue(rootElement, "is_expired", "FALSE");
                        String isVerified = UtilXml.childElementValue(rootElement, "is_verified", "FALSE");
                        String numBought = UtilXml.childElementValue(rootElement, "num_bought", "0");
                        String numEntered = UtilXml.childElementValue(rootElement, "num_entered", "0");
                        String numWishes = UtilXml.childElementValue(rootElement, "num_wishes", "0");
                        String numWon = UtilXml.childElementValue(rootElement, "num_won", "0");
                        String numContestPhotos = UtilXml.childElementValue(rootElement, "num_contest_photos", "0");
                        String numExtraPhotos = UtilXml.childElementValue(rootElement, "num_extra_photos", "0");
                        
                        String requiresReview = UtilXml.childElementValue(rootElement, "requires_review", "FALSE");
                        String sourceCountry = UtilXml.childElementValue(rootElement, "source_country", null);
                        String uploadSource = UtilXml.childElementValue(rootElement, "upload_source", null);
                        String userInActiveSweep = UtilXml.childElementValue(rootElement, "user_in_active_sweep", "FALSE");
                        String value = UtilXml.childElementValue(rootElement, "value", null);
                        
                        Element commerceProductInfoElement = UtilXml.firstChildElement(rootElement, "commerce_product_info");
                        String fbwActive = UtilXml.childElementValue(commerceProductInfoElement, "fbw_active", "0");
                        String fbwPending = UtilXml.childElementValue(commerceProductInfoElement, "fbw_pending", "0");
                        String isActiveFbwInUs = UtilXml.childElementValue(commerceProductInfoElement, "is_active_fbw_in_us", "FALSE");
                        String isFulfillByWish = UtilXml.childElementValue(commerceProductInfoElement, "is_fulfill_by_wish", "FALSE");
                        String removed = UtilXml.childElementValue(commerceProductInfoElement, "removed", "FALSE");
                        
                        Double minOriginalPrice = 10000.0;
                        Double maxOriginalPrice = 0.0;
                        Double minOriginalShipping = 10000.0;
                        Double maxOriginalShipping = 0.0;
                        
                        List<? extends Element> variationsElements = UtilXml.childElementList(commerceProductInfoElement, "variations");
                        Iterator<? extends Element> variationsElementsElemIter = variationsElements.iterator();
                        while (variationsElementsElemIter.hasNext()) {  //loop variationsElementsElemIter == START
                            Element variationsElement = variationsElementsElemIter.next();
                            String merchantRatingClass = UtilXml.childElementValue(variationsElement, "merchant_rating_class", null);
                            String merchantRatingCount = UtilXml.childElementValue(variationsElement, "merchant_rating_count", null);
                            String merchantRating = UtilXml.childElementValue(variationsElement, "merchant_rating", null);
                            String shipsFrom = UtilXml.childElementValue(variationsElement, "ships_from", null);
                            
                            Double originalPrice = Double.valueOf(UtilXml.childElementValue(variationsElement, "original_price", "0"));
                            Double originalShipping = Double.valueOf(UtilXml.childElementValue(variationsElement, "original_shipping", "0"));
                            
                            if (minOriginalPrice >= originalPrice) {
                                minOriginalPrice = originalPrice;
                            }
                            
                            if (maxOriginalPrice <= originalPrice) {
                                maxOriginalPrice = originalPrice;
                            }
                            
                            if (minOriginalShipping >= originalShipping) {
                                minOriginalShipping = originalShipping;
                            }
                            
                            if (maxOriginalShipping <= originalShipping) {
                                maxOriginalShipping = originalShipping;
                            }
                            
                            if (UtilValidate.isNotEmpty(merchantRatingClass)) {
                                wishListing.set("merchantRatingClass", merchantRatingClass);
                            }
                            if (UtilValidate.isNotEmpty(merchantRatingCount)) {
                                wishListing.set("merchantRatingCount", Double.valueOf(merchantRatingCount));
                            }
                            if (UtilValidate.isNotEmpty(merchantRating)) {
                                wishListing.set("merchantRating", Double.valueOf(merchantRating));
                            }
                            if (UtilValidate.isNotEmpty(shipsFrom)) {
                                wishListing.set("shipsFrom", shipsFrom);
                            }
                        }   //loop variationsElementsElemIter == END

                        Element productRatingElement = UtilXml.firstChildElement(rootElement, "product_rating");
                        String productRating = UtilXml.childElementValue(productRatingElement, "rating", "0.0");
                        String productRatingClass = UtilXml.childElementValue(productRatingElement, "rating_class", null);
                        String productRatingCount = UtilXml.childElementValue(productRatingElement, "rating_count", "0");
                        
                        wishListing.set("activeSweep", activeSweep.toUpperCase());
                        wishListing.set("aspectRatio", Double.valueOf(aspectRatio));
                        wishListing.set("brand", brand);
                        wishListing.set("canGift", canGift.toUpperCase());
                        wishListing.set("canPromo", canPromo.toUpperCase());
                        wishListing.set("commentCount", Long.valueOf(commentCount));
                        wishListing.set("fbwActive", Long.valueOf(fbwActive));
                        wishListing.set("fbwPending", Long.valueOf(fbwPending));
                        wishListing.set("firstRatingImageIndex", Long.valueOf(firstRatingImageIndex));
                        wishListing.set("fromTrustedStore", fromTrustedStore.toUpperCase());
                        wishListing.set("gender", gender);
                        wishListing.set("generationTime", generationTime);
                        wishListing.set("hasReward", hasReward.toUpperCase());
                        wishListing.set("isActive", isActive.toUpperCase());
                        wishListing.set("isActiveFbwInUs", isActiveFbwInUs.toUpperCase());
                        wishListing.set("isAdminUser", isAdminUser.toUpperCase());
                        wishListing.set("isClean", isClean.toUpperCase());
                        wishListing.set("isConcept", isConcept.toUpperCase());
                        wishListing.set("isContentReviewer", isContentReviewer.toUpperCase());
                        wishListing.set("isDeleted", isDeleted.toUpperCase());
                        wishListing.set("isDirty", isDirty.toUpperCase());
                        wishListing.set("isExpired", isExpired.toUpperCase());
                        wishListing.set("isFulfillByWish", isFulfillByWish.toUpperCase());
                        wishListing.set("isVerified", isVerified.toUpperCase());
                        wishListing.set("numBought", Long.valueOf(numBought));
                        wishListing.set("numEntered", Long.valueOf(numEntered));
                        wishListing.set("numWishes", Long.valueOf(numWishes));
                        wishListing.set("numWon", Long.valueOf(numWon));
                        wishListing.set("numContestPhotos", Long.valueOf(numContestPhotos));
                        wishListing.set("numExtraPhotos", Long.valueOf(numExtraPhotos));
                        wishListing.set("productRating", Double.valueOf(productRating));
                        wishListing.set("productRatingClass", productRatingClass);
                        wishListing.set("productRatingCount", Double.valueOf(productRatingCount));
                        wishListing.set("removed", removed.toUpperCase());
                        wishListing.set("requiresReview", requiresReview.toUpperCase());
                        wishListing.set("sourceCountry", sourceCountry);
                        wishListing.set("uploadSource", uploadSource);
                        wishListing.set("userInActiveSweep", userInActiveSweep.toUpperCase());
                        wishListing.set("value", value);
                        
                        wishListing.set("minOriginalPrice", minOriginalPrice);
                        wishListing.set("maxOriginalPrice", maxOriginalPrice);
                        wishListing.set("minOriginalShipping", minOriginalShipping);
                        wishListing.set("maxOriginalShipping", maxOriginalShipping);
                    }   //if wishCrawlPageSingle successfull == END
                    
                    Element productElement = productElementsElemIter.next();
                    String mainImage = UtilXml.childElementValue(productElement, "main_image", null);
                    String isPromoted = UtilXml.childElementValue(productElement, "is_promoted", null);
                    String description = UtilXml.childElementValue(productElement, "description", null);
                    String reviewStatus = UtilXml.childElementValue(productElement, "review_status", null);
                    String upc = UtilXml.childElementValue(productElement, "upc", null);
                    String extraImages = UtilXml.childElementValue(productElement, "extra_images", null);
                    String numberSaves = UtilXml.childElementValue(productElement, "number_saves", null);
                    String numberSold = UtilXml.childElementValue(productElement, "number_sold", null);
                    String parentSku = UtilXml.childElementValue(productElement, "parent_sku", null);
                    String name = UtilXml.childElementValue(productElement, "name", null);
                    String originalImageUrl = UtilXml.childElementValue(productElement, "original_image_url", null);
                    String dateUploaded = UtilXml.childElementValue(productElement, "date_uploaded", null);
                    
                    wishListing.set("parentSku", parentSku);
                    wishListing.set("mainImage", mainImage);
                    wishListing.set("name", name);
                    wishListing.set("upc", upc);
                    wishListing.set("isPromoted", isPromoted.toUpperCase());
                    wishListing.set("description", description);
                    wishListing.set("reviewStatus", reviewStatus.toUpperCase());
                    wishListing.set("extraImages", extraImages);
                    wishListing.set("originalImageUrl", originalImageUrl);
                    if (UtilValidate.isEmpty(numberSaves)) {
                        numberSaves = "0";
                    }
                    if (UtilValidate.isEmpty(numberSold)) {
                        numberSold = "0";
                    }
                    wishListing.set("numberSaves", Long.valueOf(numberSaves));
                    wishListing.set("numberSold", Long.valueOf(numberSold));
                    wishListing.set("dateUploaded", new java.sql.Date(sdf.parse(dateUploaded).getTime()));
                    delegator.createOrStore(wishListing);
                    
                    delegator.removeByAnd("WishBestSellingTags", UtilMisc.toMap("wishId", wishId));
                    
                    Element tagsElement = UtilXml.firstChildElement(productElement, "tags");
                    List<? extends Element> tagsList = UtilXml.childElementList(tagsElement, "Tag");
                    Iterator<? extends Element> tagsListElemIter = tagsList.iterator();
                    int tagSeq = 0;
                    while (tagsListElemIter.hasNext()) {    //loop tags -- START
                        
                        tagSeq++;
                        String tagSeqId = df.format(tagSeq);
                        Element tagElement = tagsListElemIter.next();
                        String tagId = UtilXml.childElementValue(tagElement, "id", null);
                        String tagName = UtilXml.childElementValue(tagElement, "name", null);
                        GenericValue wishTag = delegator.findOne("WishBestSellingTags", UtilMisc.toMap("wishId", wishId, "productStoreId", productStoreId, "tagSeqId", tagSeqId, "tagType", "INPUT_TAG"), false);
                        if (UtilValidate.isEmpty(wishTag)) {
                            wishTag = delegator.makeValue("WishBestSellingTags", UtilMisc.toMap("wishId", wishId, "productStoreId", productStoreId, "tagSeqId", tagSeqId, "tagType", "INPUT_TAG"));
                        }
                        wishTag.set("tagId", tagId);
                        wishTag.set("tagName", tagName);
                        delegator.createOrStore(wishTag);
                    }   //loop tags -- START
                    
                    Element autoTagsElement = UtilXml.firstChildElement(productElement, "auto_tags");
                    if (UtilValidate.isNotEmpty(autoTagsElement)) { //if auto_tags not empty == START
                        List<? extends Element> autoTagsList = UtilXml.childElementList(autoTagsElement, "Tag");
                        Iterator<? extends Element> autoTagsListElemIter = autoTagsList.iterator();
                        int autoTagSeq = 0;
                        while (autoTagsListElemIter.hasNext()) {    //loop auto tags -- START
                            autoTagSeq++;
                            String autoTagSeqId = df.format(autoTagSeq);
                            Element tagElement = autoTagsListElemIter.next();
                            String tagId = UtilXml.childElementValue(tagElement, "id", null);
                            String tagName = UtilXml.childElementValue(tagElement, "name", null);
                            GenericValue wishTag = delegator.findOne("WishBestSellingTags", UtilMisc.toMap("wishId", wishId, "productStoreId", productStoreId, "tagSeqId", autoTagSeqId, "tagType", "AUTO_TAG"), false);
                            if (UtilValidate.isEmpty(wishTag)) {
                                wishTag = delegator.makeValue("WishBestSellingTags", UtilMisc.toMap("wishId", wishId, "productStoreId", productStoreId, "tagSeqId", autoTagSeqId, "tagType", "AUTO_TAG"));
                            }
                            wishTag.set("tagId", tagId);
                            wishTag.set("tagName", tagName);
                            delegator.createOrStore(wishTag);
                        }   //loop auto tags -- START
                    }   //if auto_tags not empty == END
                    
                    
                    //Variations -- START
                    Element variantsElement = UtilXml.firstChildElement(productElement, "variants");
                    List<? extends Element> variantList = UtilXml.childElementList(variantsElement, "Variant");
                    Iterator<? extends Element> variantListElemIter = variantList.iterator();
                    int varSeq = 0;
                    while (variantListElemIter.hasNext()) { //loop variants -- START
                        //Debug.logError("variation runs", module);
                        varSeq++;
                        String varSeqId = df.format(varSeq);
                        Element variantElement = variantListElemIter.next();
                        String sku = UtilXml.childElementValue(variantElement, "sku", null);
                        String productId = UtilXml.childElementValue(variantElement, "product_id", null);
                        String size = UtilXml.childElementValue(variantElement, "size", null);
                        String color = UtilXml.childElementValue(variantElement, "color", null);
                        String price = UtilXml.childElementValue(variantElement, "price", null);
                        String enabled = UtilXml.childElementValue(variantElement, "enabled", null);
                        String shipping = UtilXml.childElementValue(variantElement, "shipping", null);
                        String allImages = UtilXml.childElementValue(variantElement, "all_images", null);
                        String inventory = UtilXml.childElementValue(variantElement, "inventory", null);
                        String variationId = UtilXml.childElementValue(variantElement, "id", null);
                        String msrp = UtilXml.childElementValue(variantElement, "msrp", null);
                        String shippingTime = UtilXml.childElementValue(variantElement, "shipping_time", null);
                        
                        GenericValue wishListingVariation = delegator.findOne("WishBestSellingVariant", UtilMisc.toMap("wishId", wishId, "productStoreId", productStoreId, "varSeqId", varSeqId, "date", new java.sql.Date(nowDate.getTime())), false);
                        if (UtilValidate.isEmpty(wishListingVariation)) {
                            wishListingVariation = delegator.makeValue("WishBestSellingVariant", UtilMisc.toMap("wishId", wishId, "productStoreId", productStoreId, "varSeqId", varSeqId, "date", new java.sql.Date(nowDate.getTime())));
                        }
                        wishListingVariation.set("sku", sku);
                        wishListingVariation.set("normalizedSku", normalizeSkuWish(delegator,sku));
                        wishListingVariation.set("productStoreId", productStoreId);
                        wishListingVariation.set("productId", productId);
                        wishListingVariation.set("size", size);
                        wishListingVariation.set("color", color);
                        if (UtilValidate.isEmpty(price)) {
                            price = "0";
                        }
                        wishListingVariation.set("price", Double.valueOf(price));
                        wishListingVariation.set("enabled", enabled.toUpperCase());
                        if (UtilValidate.isEmpty(shipping)) {
                            shipping = "0";
                        }
                        wishListingVariation.set("shipping", Double.valueOf(shipping));
                        wishListingVariation.set("allImages", allImages);
                        if (UtilValidate.isEmpty(inventory)) {
                            inventory = "0";
                        }
                        wishListingVariation.set("inventory", Long.valueOf(inventory));
                        wishListingVariation.set("variationId", variationId);
                        if (UtilValidate.isEmpty(msrp)) {
                            msrp = "0";
                        }
                        wishListingVariation.set("msrp", Double.valueOf(msrp));
                        wishListingVariation.set("shippingTime", shippingTime);
                        delegator.createOrStore(wishListingVariation);
                    }   //loop variants -- END
                    //Variations -- END
                }   //loop Product -- END
                //writing data into database -- END
            }   //if ack success -- END
            else if (ack.equals("1016")) {  //refresh token == START
                Map refreshWishToken = dispatcher.runSync("wishGetRefreshCode", UtilMisc.toMap("productStoreId", productStoreId, "userLogin", userLogin));
                if (ServiceUtil.isSuccess(refreshWishToken)) {
                    Map rerunThisFunction = dispatcher.runSync("wishUpdateBestSellingSingle", UtilMisc.toMap("productStoreId", productStoreId, "wishId", wishId, "userLogin", userLogin));
                }
            }   //refresh token == END
            else {  //if ack failure -- START
                String errorMessage = UtilXml.childElementValue(elemResponse, "Message", null);
                FileWriter f1 = new FileWriter("hot-deploy/gudao/webapp/gudao/wish/logError/updateBestSelling.log", true);
                f1.write(today + ": product Store ID: " + productStoreId + ", Codes : " + ack + ": " + errorMessage + "\n");
                f1.close();
            }   //if ack failure -- END
        }   //main try -- END
        catch (GenericEntityException e) {
            Debug.logError("Yasin: updateBestSellingSingle GenericEntityException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (SAXException e) {
            Debug.logError("Yasin: updateBestSellingSingle SAXException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (ParserConfigurationException e) {
            Debug.logError("Yasin: updateBestSellingSingle ParserConfigurationException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (IOException e) {
            Debug.logError("Yasin: updateBestSellingSingle IOException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (Exception e) {
            e.printStackTrace();
            Debug.logError("Yasin: updateBestSellingSingle Exception Error for TRY CATCH: " + e.getMessage(), module);
        }
        return result;
    }   //updateBestSellingSingle
    
    public static Map<String, Object> updateBestSellingAuto (DispatchContext dctx, Map context)
    throws GenericEntityException, GenericServiceException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreIdInput = (String) context.get("productStoreId");
        
        if (UtilValidate.isNotEmpty(productStoreIdInput)) {
            productStoreIdInput = productStoreIdInput.trim();
        }

        try {   //main try == START
            EntityCondition cond = null;
            List<EntityCondition> conditions = FastList.newInstance();
            conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ACTIVE"));
            if (UtilValidate.isNotEmpty(productStoreIdInput)) {   //productListCondition == START
                conditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreIdInput));
            }
            cond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
            List<GenericValue> wishBestSellingList = delegator.findList("WishBestSellingList", cond, null, null, null, false);
            for (GenericValue wishBestSelling : wishBestSellingList) {  //loop wishBestSellingList == START
                String productStoreId = wishBestSelling.getString("productStoreId");
                String wishId = wishBestSelling.getString("wishId");
                Map wishUpdateBestSellingSingle = dispatcher.runSync("wishUpdateBestSellingSingle", UtilMisc.toMap("productStoreId", productStoreId, "wishId", wishId, "userLogin", userLogin));
            }   //loop wishBestSellingList == END
        }   //main try == END
        catch (GenericEntityException e) {
            Debug.logError("Yasin: updateBestSellingAuto GenericEntityException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (GenericServiceException e) {
            Debug.logError("Yasin: updateBestSellingAuto GenericServiceException Error for TRY CATCH: " + e.getMessage(), module);
        }
        return result;
    }   //updateBestSellingAuto
    
    public static Map<String, Object> updateWishListingInventory (DispatchContext dctx, Map context)
    throws GenericEntityException, GenericServiceException, IOException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Calendar now = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(now.getTime());
        String oneProductStoreId = (String) context.get("productStoreId");
        Debug.logError("Starting updateWishListingInventory" , module);
        try {   //main try -- START
            String url = wishProperties().get("apiUrl");
            
            List<GenericValue> distinctOwnerList = delegator.findList( "WishListingLowQuantity",null,UtilMisc.toSet("owner"),UtilMisc.toList("owner"),new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true), false);
            
            for(GenericValue distinctOwner : distinctOwnerList){ //loop distinctOwnerList --start
                String belongToOwner = distinctOwner.getString("owner");
                EntityCondition cond = null;
                List<EntityCondition> conditions = FastList.newInstance();
                if (UtilValidate.isNotEmpty(oneProductStoreId)) {
                    conditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, oneProductStoreId));
                }
                conditions.add(EntityCondition.makeCondition("owner", EntityOperator.EQUALS, belongToOwner));
                cond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                //cond = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS,"darkking");
                List<GenericValue> wishListingLowQuantityList = delegator.findList("WishListingLowQuantity", cond, null, UtilMisc.toList("productStoreId"), null, false);
                StringBuffer allStoreMessage = new StringBuffer();
                allStoreMessage.append("<html><body>");
                boolean sendEmailStatus = false;
                String replaceAccount = "below";
                
                for (GenericValue wishListingLowQuantity : wishListingLowQuantityList)   {   //loop wishListingLowQuantityList == START
                    String normalizedSku = wishListingLowQuantity.getString("normalizedSku");
                    String wishId = wishListingLowQuantity.getString("wishId");
                    GenericValue product = delegator.findOne("ProductMaster", UtilMisc.toMap("productId", normalizedSku), false);
                    boolean doAgain = false;
                    StringBuffer words = new StringBuffer();
                    words.append("sku: ");
                    if (UtilValidate.isNotEmpty(product)){  //if product is not empty == START
                        String productStoreId = wishListingLowQuantity.getString("productStoreId");
                        if(!(replaceAccount.equals(productStoreId))){
                            replaceAccount = productStoreId;
                            allStoreMessage.append("productStoreId : ");
                            allStoreMessage.append(replaceAccount);
                            allStoreMessage.append("<br/>");
                        }
                        GenericValue partyIdentification = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_ACCESS_CODE"), false);
                        String accessCode = partyIdentification.getString("idValue");
                        String sku = wishListingLowQuantity.getString("sku");
                        
                        do{
                            String requestUrl = url + "variant/update-inventory?format=xml&access_token=" + accessCode + "&sku=" + sku + "&inventory=5000";
                            String responseXML = sendHttpRequest(requestUrl);
                            Document docResponse = UtilXml.readXmlDocument(responseXML, true);
                            Element elemResponse = docResponse.getDocumentElement();
                            //String ack = UtilXml.childElementValue(elemResponse, "Code", null);
                            String ack ="1004";
                            if (!ack.equals("0")) {    //if ack failure -- start
                                String errorMessage = UtilXml.childElementValue(elemResponse, "Message", null);
                                FileWriter f1 = new FileWriter("hot-deploy/gudao/webapp/gudao/wish/logError/updateInventory.log", true);
                                f1.write(today + ": product Store ID: " + productStoreId + ", Codes : " + ack + ": " + errorMessage + "\n");
                                f1.close();
                                
                                if (ack.equals("1016") || ack.equals("1015")) {  //refresh token == START
                                    Map refreshWishToken = dispatcher.runSync("wishGetRefreshCode", UtilMisc.toMap("productStoreId", productStoreId, "userLogin", userLogin));
                                    if(ServiceUtil.isSuccess(refreshWishToken)){
                                        doAgain = true;
                                    }   //refresh token == END
                                }
                                else{
                                    sendEmailStatus = true;
                                    words.append(sku);
                                    words.append("\t");
                                }
                            }//if ack failure -- end
                        }while(doAgain);
                        
                    } //if product is not empty == end
                    String skuAre = words.toString();
                    allStoreMessage.append(skuAre);
                    allStoreMessage.append("<br/>");
                }   //loop wishListingLowQuantityList == END
                
                if(sendEmailStatus){
                    Properties properties = new Properties();
                    properties.load(new FileInputStream("hot-deploy/gudao/config/wish.properties"));
                    String sendEmailToProp = properties.getProperty(belongToOwner);
                    String sendCc = properties.getProperty("sendCc");
                    String sendBcc = properties.getProperty("sendBcc");
                    String subject = "these wishCounts need to checks";
                    allStoreMessage.append("</body></html>");
                    String body = allStoreMessage.toString();
                    boolean startTlsEnabled = true;
                    String[] sendEmailTo = sendEmailToProp.split(",");
                    for (int k = 0; k < sendEmailTo.length; k++) {   //loop sending email == START
                        Map sendEmail = dispatcher.runSync("sendMail", UtilMisc.toMap("sendFrom", "898515425@qq.com",
                                                                                      "sendTo", sendEmailTo[k].trim(),
                                                                                      "sendCc", sendCc,
                                                                                      "sendBcc", sendBcc,
                                                                                      "socketFactoryPort", "465",
                                                                                      "startTLSEnabled", startTlsEnabled,
                                                                                      "subject", subject,
                                                                                      "body", body,
                                                                                      "userLogin", userLogin));
                    }//loop sending email == end
                }
            }//loop distinctOwner --end
        }   //main try -- END
        catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        Debug.logError("Finished updateWishListingInventory" , module);
        return result;
    }   //updateWishListingInventory
    
    public static Map<String, Object> disableWishListingVariation (DispatchContext dctx, Map context)
    throws GenericEntityException, GenericServiceException, IOException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        
        Calendar now = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(now.getTime());
        String productStoreIdInput = (String) context.get("productStoreId");
        String variantSku = (String) context.get("sku");
        try {   //main try -- START
            GenericValue accessCodeList = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId", productStoreIdInput, "partyIdentificationTypeId", "WISH_ACCESS_CODE"), false);
            String idValue = accessCodeList.getString("idValue");
            String url = wishProperties().get("apiUrl");
            EntityCondition cond = null;
            List<EntityCondition> conditions = FastList.newInstance();
            conditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreIdInput));
            conditions.add(EntityCondition.makeCondition("sku", EntityOperator.EQUALS, variantSku));
            cond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
            List<GenericValue> allWishListingInventoryList = delegator.findList("WishListingVariation", cond, null, null, null, false);
            for (GenericValue updateList : allWishListingInventoryList) {   //loop allWishListingInventoryList == START
                
                String normalizedSku = updateList.getString("normalizedSku");
                GenericValue product = delegator.findOne("ProductMaster", UtilMisc.toMap("productId", normalizedSku), false);
                if (UtilValidate.isNotEmpty(product)){ // if  find sku --start
                    String requestUrl = url + "variant/disable?format=xml&access_token=" +idValue +"&sku="+ variantSku;
                    //Debug.logError("requestUrl: " + requestUrl, module);
                    String responseXML = sendHttpRequest(requestUrl);
                    Document docResponse = UtilXml.readXmlDocument(responseXML, true);
                    Element elemResponse = docResponse.getDocumentElement();
                    String ack = UtilXml.childElementValue(elemResponse, "Code", null);
                    if(!ack.equals("0")) {    //if ack failure -- start
                        String errorMessage = UtilXml.childElementValue(elemResponse, "Message", null);
                        FileWriter f1 = new FileWriter("hot-deploy/gudao/webapp/gudao/wish/logError/updateInventory.log", true);
                        f1.write(today + ": product Store ID: " + productStoreIdInput + ", Codes : " + ack + ": " + errorMessage + "\n");
                        f1.close();
                    }   //if ack failure -- END
                } // if find sku --END
            }   //loop allWishListingInventoryList == END
        }   //main try -- END
        catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        Debug.logError("finished disableWishListingVariation" , module);
        return result;
    }   //disableWishListingVariation
    
    public static Map<String, Object> orderMultiGet (DispatchContext dctx, Map context)
    throws GenericEntityException, IOException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        String start = (String) context.get("start");
        String limit = (String) context.get("limit");
        String sinceLastXMonth = (String) context.get("sinceLastXMonth");
        String sinceLastXDay = (String) context.get("sinceLastXDay");
        String wishExpressOnly = (String) context.get("wishExpressOnly");
        String url = wishProperties().get("apiUrl") + "order/multi-get";
        
        Calendar now = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(now.getTime());
        java.sql.Date dateSql = new java.sql.Date(now.getTime().getTime());
        
        Calendar since = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        
        if (UtilValidate.isNotEmpty(wishExpressOnly)) { //if wishExpressOnly not empty == START
            if (wishExpressOnly.toUpperCase().startsWith("T") || wishExpressOnly.toUpperCase().startsWith("Y")) {
                wishExpressOnly = "True";
            }
        }   //if wishExpressOnly not empty == END
        
        if (UtilValidate.isEmpty(limit)) {
            limit = "500";
        } else {
            limit = limit.trim();
        }
        
        if (UtilValidate.isNotEmpty(productStoreId)) {
            productStoreId = productStoreId.trim();
        }
        
        if (UtilValidate.isNotEmpty(sinceLastXDay)) {   //sinceLastXDay overruled sinceLastXDMonth == START
            sinceLastXDay = sinceLastXDay.trim();
            since.add(Calendar.DAY_OF_MONTH, - Integer.parseInt(sinceLastXDay));
        } else {
            if (UtilValidate.isEmpty(sinceLastXMonth)) {
                sinceLastXMonth = "3";
            } else {
                sinceLastXMonth = sinceLastXMonth.trim();
            }
            since.add(Calendar.MONTH, - Integer.parseInt(sinceLastXMonth));
        }   //sinceLastXDay overruled sinceLastXDMonth == END
        
        String sinceDate = sdfDate.format(since.getTime());
        
        try {   //main try -- START
            Debug.logError("Wish runs order multi get: " + productStoreId, module);
            GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
            String productStoreGroup = productStore.getString("primaryStoreGroupId");
            if (!productStoreGroup.equals("WISH")) {
                return ServiceUtil.returnSuccess("productStoreId " + productStoreId + " is not a wish account");
            }
            List<GenericValue> productStoreRoleList = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStore.getString("productStoreId"), "roleTypeId", "WISH_ACCOUNT"), null, false);
            if (UtilValidate.isEmpty(productStoreRoleList)) {   //if productStoreRoleList is empty -- START
                Debug.logError("ProductStoreId " + productStoreId + " does not have Wish account role", module);
                return ServiceUtil.returnError("ProductStoreId " + productStoreId + " does not have Wish account role");
            }   //if productStoreRoleList is empty -- END
            GenericValue productStoreRole = EntityUtil.getFirst(productStoreRoleList);
            GenericValue wishAccountPartyGroup = productStoreRole.getRelatedOne("PartyGroup", false);
            GenericValue wishAccountParty = wishAccountPartyGroup.getRelatedOne("Party", false);
            GenericValue partyIdentification = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId",wishAccountParty.getString("partyId"), "partyIdentificationTypeId", "WISH_ACCESS_CODE"), false);
            String apiKey = URLEncoder.encode(partyIdentification.getString("idValue"), "UTF-8");
            
            StringBuffer requestUrlStrBuffer = new StringBuffer(url);
            requestUrlStrBuffer.append("?format=xml");
            requestUrlStrBuffer.append("&limit=" + limit);
            requestUrlStrBuffer.append("&access_token=" + apiKey);
            requestUrlStrBuffer.append("&since=" + sinceDate);
            
            if (UtilValidate.isNotEmpty(start)) {
                requestUrlStrBuffer.append("&start=" + start.trim());
            }
            
            if (UtilValidate.isNotEmpty(wishExpressOnly)) {
                requestUrlStrBuffer.append("&wish_express_only=" + wishExpressOnly);
            }
            
            String requestUrl = requestUrlStrBuffer.toString();
            boolean keepLooping = false;
            int loopCount = 0;
            //Debug.logError("URL: " + requestUrl, module);
            do {    //do loop -- START
                keepLooping = false;
                //Debug.logError("URL: " + requestUrl, module);
                String responseXML = sendHttpRequest(requestUrl);
                //Debug.logError(responseXML, module);
                //Reading XML -- START
                Document docResponse = UtilXml.readXmlDocument(responseXML, true);
                Element elemResponse = docResponse.getDocumentElement();
                String ack = UtilXml.childElementValue(elemResponse, "Code", null);
                //Debug.logError("Code: " + ack, module);
                if (ack.equals("0")) {    //if ack success -- START
                    //writing data into database -- START
                    Map writeResult = writeWishOrderIntoDatabase(dctx, context, responseXML);
                    
                    Element pagingElement = UtilXml.firstChildElement(elemResponse, "Paging");
                    List<? extends Element> nextElements = UtilXml.childElementList(pagingElement, "Next");
                    Iterator<? extends Element> nextElementsElemIter = nextElements.iterator();
                    while (nextElementsElemIter.hasNext()) {    //check Next Element -- START
                        Element nextElement = nextElementsElemIter.next();
                        String nextUrl = UtilXml.elementValue(nextElement);
                        if (UtilValidate.isNotEmpty(nextUrl)) {
                            keepLooping = true;
                            requestUrl = nextUrl;
                        }
                    }   //check Next Element -- END
                }   //if ack success -- END
                else if (ack.equals("1016") || ack.equals("1015")) {  //refresh token == START
                    Map refreshWishToken = dispatcher.runSync("wishGetRefreshCode", UtilMisc.toMap("productStoreId", productStoreId, "userLogin", userLogin));
                    if (ServiceUtil.isSuccess(refreshWishToken)) {
                        Map rerunThisFunction = dispatcher.runSync("wishOrderMultiGet", UtilMisc.toMap("productStoreId", productStoreId,
                                                                                                        "start", start,
                                                                                                        "limit", limit,
                                                                                                        "sinceLastXMonth", sinceLastXMonth,
                                                                                                        "wishExpressOnly", wishExpressOnly,
                                                                                                        "userLogin", userLogin));
                    }
                    break;
                }   //refresh token == END
                else {  //if ack failure -- START
                    String errorMessage = UtilXml.childElementValue(elemResponse, "Message", null);
                    FileWriter f1 = new FileWriter("hot-deploy/gudao/webapp/gudao/wish/logError/product-multi-get.log", true);
                    f1.write(today + ": product Store ID: " + productStoreId + ", Codes : " + ack + ": " + errorMessage + "\n");
                    f1.close();
                }   //if ack failure -- END
                //Reading XML -- END
                loopCount++;
            }   //do loop -- END
            while (keepLooping);
            //Debug.logError("run Check End", module);
        }   //main try -- END
        catch (GenericEntityException e) {
            Debug.logError("Yasin: orderMultiGet GenericEntityException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (SAXException e) {
            Debug.logError("Yasin: orderMultiGet SAXException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (ParserConfigurationException e) {
            Debug.logError("Yasin: orderMultiGet ParserConfigurationException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (IOException e) {
            Debug.logError("Yasin: orderMultiGet IOException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (Exception e) {
            e.printStackTrace();
            Debug.logError("Yasin: orderMultiGet Exception Error for TRY CATCH: " + e.getMessage(), module);
            try {
                Map rerunOrderMultiGet = dispatcher.runSync("wishOrderMultiGet", UtilMisc.toMap("productStoreId", productStoreId,
                                                                                                "start", start,
                                                                                                "limit", limit,
                                                                                                "sinceLastXMonth", sinceLastXMonth,
                                                                                                "wishExpressOnly", wishExpressOnly,
                                                                                                "userLogin", userLogin));
            }
            catch (Exception ee) {
                ee.printStackTrace();
                Debug.logError("Rerun orderMultiGet exception also return another exception", module);
                return ServiceUtil.returnError("ProductStoreId " + productStoreId + " failed Crawling WISH orderMultiGet");
            }
        }
        
        GenericValue updateLastRuntime = delegator.makeValue("AutoScheduleJobHistory", UtilMisc.toMap("productStoreId", productStoreId, "serviceName", "wishOrderMultiGet", "lastRuntime", Timestamp.valueOf(today)));
        delegator.createOrStore(updateLastRuntime);
        return result;
    }   //orderMultiGet
    
    public static Map<String, Object> writeWishOrderIntoDatabase(DispatchContext dctx, Map context, String responseXML)
    throws GenericEntityException, GenericServiceException, SAXException, ParserConfigurationException, IOException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        //formatting the sequence ID
        DecimalFormat df = new DecimalFormat("00000");
        String productStoreId = (String) context.get("productStoreId");
        Calendar now = Calendar.getInstance();
        Date nowDate = new Date();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try {   //main try -- START
            //Debug.logError("run Check Write", module);
            Document docResponse = UtilXml.readXmlDocument(responseXML, true);
            Element elemResponse = docResponse.getDocumentElement();
            String code = UtilXml.childElementValue(elemResponse, "Code", null);
            
            if (code.equals("0")) { //if response OK -- START
                
                Element dataElement = UtilXml.firstChildElement(elemResponse, "Data");
                List<? extends Element> orderElements = UtilXml.childElementList(dataElement, "Order");
                Iterator<? extends Element> orderElementsElemIter = orderElements.iterator();
                while (orderElementsElemIter.hasNext()) { //loop Order -- START
                    
                    Element orderElement = orderElementsElemIter.next();
                    String orderId = UtilXml.childElementValue(orderElement, "order_id", null);
                    String transactionId = UtilXml.childElementValue(orderElement, "transaction_id", null);
                    String productId = UtilXml.childElementValue(orderElement, "product_id", null);
                    String variantId = UtilXml.childElementValue(orderElement, "variant_id", null);
                    String buyerId = UtilXml.childElementValue(orderElement, "buyer_id", null);
                    String quantity = UtilXml.childElementValue(orderElement, "quantity", null);
                    String sku = UtilXml.childElementValue(orderElement, "sku", null);
                    String size = UtilXml.childElementValue(orderElement, "size", null);
                    String color = UtilXml.childElementValue(orderElement, "color", null);
                    String state = UtilXml.childElementValue(orderElement, "state", null);
                    String shippingProvider = UtilXml.childElementValue(orderElement, "shipping_provider", null);
                    String trackingNumber = UtilXml.childElementValue(orderElement, "tracking_number", null);
                    String shippedDateStr = UtilXml.childElementValue(orderElement, "shipped_date", null);
                    String shipNote = UtilXml.childElementValue(orderElement, "ship_note", null);
                    String lastUpdatedStr = UtilXml.childElementValue(orderElement, "last_updated", null);
                    String orderTotal = UtilXml.childElementValue(orderElement, "order_total", null);
                    String daysToFulfill = UtilXml.childElementValue(orderElement, "days_to_fulfill", null);
                    String hoursToFulfill = UtilXml.childElementValue(orderElement, "hours_to_fulfill", null);
                    String price = UtilXml.childElementValue(orderElement, "price", null);
                    String cost = UtilXml.childElementValue(orderElement, "cost", null);
                    String shipping = UtilXml.childElementValue(orderElement, "shipping", null);
                    String shippingCost = UtilXml.childElementValue(orderElement, "shipping_cost", null);
                    String productName = UtilXml.childElementValue(orderElement, "product_name", null);
                    String productImageUrl = UtilXml.childElementValue(orderElement, "product_image_url", null);
                    String orderTimeStr = UtilXml.childElementValue(orderElement, "order_time", null);
                    String refundedBy = UtilXml.childElementValue(orderElement, "refunded_by", null);
                    String refundedTimeStr = UtilXml.childElementValue(orderElement, "refunded_time", null);
                    String refundedReason = UtilXml.childElementValue(orderElement, "refunded_reason", null);
                    String isWishExpress = UtilXml.childElementValue(orderElement, "is_wish_express", null);
                    String weRequiredDeliveryDate = UtilXml.childElementValue(orderElement, "we_required_delivery_date", null);
                    String trackingConfirmed = UtilXml.childElementValue(orderElement, "tracking_confirmed", null);
                    String trackingConfirmedDateStr = UtilXml.childElementValue(orderElement, "tracking_confirmed_date", null);
                    
                    GenericValue wishOrderHeader = delegator.makeValue("WishOrderHeader", UtilMisc.toMap("orderId", orderId));
                    wishOrderHeader.set("productStoreId", productStoreId);
                    wishOrderHeader.set("orderId", orderId);
                    wishOrderHeader.set("transactionId", transactionId);
                    wishOrderHeader.set("productId", productId);
                    wishOrderHeader.set("variantId", variantId);
                    wishOrderHeader.set("buyerId", buyerId);
                    wishOrderHeader.set("sku", sku);
                    wishOrderHeader.set("size", size);
                    wishOrderHeader.set("color", color);
                    wishOrderHeader.set("state", state);
                    wishOrderHeader.set("shippingProvider", shippingProvider);
                    wishOrderHeader.set("trackingNumber", trackingNumber);
                    wishOrderHeader.set("shipNote", shipNote);
                    wishOrderHeader.set("productName", productName);
                    wishOrderHeader.set("productImageUrl", productImageUrl);
                    wishOrderHeader.set("refundedBy", refundedBy);
                    wishOrderHeader.set("refundedReason", refundedReason);
                    wishOrderHeader.set("isWishExpress", isWishExpress);
                    wishOrderHeader.set("weRequiredDeliveryDate", weRequiredDeliveryDate);
                    wishOrderHeader.set("trackingConfirmed", trackingConfirmed);
                    
                    if (UtilValidate.isNotEmpty(shippedDateStr)) {
                        wishOrderHeader.set("shippedDate", new java.sql.Date(sdfDate.parse(shippedDateStr).getTime()));
                    }
                    
                    if (UtilValidate.isNotEmpty(refundedTimeStr)) {
                        wishOrderHeader.set("refundedTime", new java.sql.Date(sdfDate.parse(refundedTimeStr).getTime()));
                    }
                    
                    if (UtilValidate.isNotEmpty(trackingConfirmedDateStr)) {
                        trackingConfirmedDateStr = trackingConfirmedDateStr.replaceAll("T"," ");
                        wishOrderHeader.set("trackingConfirmedDate", new java.sql.Timestamp(sdfTimestamp.parse(trackingConfirmedDateStr).getTime()));
                    }
                    
                    if (UtilValidate.isNotEmpty(lastUpdatedStr)) {
                        //Debug.logError("lastUpdatedStr: " + lastUpdatedStr, module);
                        lastUpdatedStr = lastUpdatedStr.replaceAll("T"," ");
                        //Debug.logError("lastUpdatedStr: " + lastUpdatedStr, module);
                        wishOrderHeader.set("lastUpdated", new java.sql.Timestamp(sdfTimestamp.parse(lastUpdatedStr).getTime()));
                        //Debug.logError("lastUpdatedStr: " + new java.sql.Timestamp(sdfTimestamp.parse(lastUpdatedStr).getTime()), module);
                    }
                    
                    if (UtilValidate.isNotEmpty(orderTimeStr)) {
                        orderTimeStr = orderTimeStr.replaceAll("T"," ");
                        wishOrderHeader.set("orderTime", new java.sql.Timestamp(sdfTimestamp.parse(orderTimeStr).getTime()));
                    }
                    
                    if (UtilValidate.isNotEmpty(quantity)) {
                        wishOrderHeader.set("quantity", Long.valueOf(quantity));
                    }
                    
                    if (UtilValidate.isNotEmpty(orderTotal)) {
                        wishOrderHeader.set("orderTotal", new BigDecimal(orderTotal));
                    }
                    
                    if (UtilValidate.isNotEmpty(daysToFulfill)) {
                        wishOrderHeader.set("daysToFulfill", Long.valueOf(daysToFulfill));
                    }
                    
                    if (UtilValidate.isNotEmpty(hoursToFulfill)) {
                        wishOrderHeader.set("hoursToFulfill", Long.valueOf(hoursToFulfill));
                    }
                    
                    if (UtilValidate.isNotEmpty(price)) {
                        wishOrderHeader.set("price", new BigDecimal(price));
                    }
                    
                    if (UtilValidate.isNotEmpty(cost)) {
                        wishOrderHeader.set("cost", new BigDecimal(cost));
                    }
                    
                    if (UtilValidate.isNotEmpty(shipping)) {
                        wishOrderHeader.set("shipping", new BigDecimal(shipping));
                    }
                    
                    if (UtilValidate.isNotEmpty(shippingCost)) {
                        wishOrderHeader.set("shippingCost", new BigDecimal(shippingCost));
                    }
                    
                    delegator.createOrStore(wishOrderHeader);
                    
                    //SHIPPING DETAILS == START
                    Element shippingDetailElement = UtilXml.firstChildElement(orderElement, "ShippingDetail");
                    String name = UtilXml.childElementValue(shippingDetailElement, "name", null);
                    String streetAddress1 = UtilXml.childElementValue(shippingDetailElement, "street_address1", null);
                    String streetAddress2 = UtilXml.childElementValue(shippingDetailElement, "street_address2", null);
                    String city = UtilXml.childElementValue(shippingDetailElement, "city", null);
                    String stateProvince = UtilXml.childElementValue(shippingDetailElement, "state", null);
                    String country = UtilXml.childElementValue(shippingDetailElement, "country", null);
                    String zipcode = UtilXml.childElementValue(shippingDetailElement, "zipcode", null);
                    String phoneNumber = UtilXml.childElementValue(shippingDetailElement, "phone_number", null);
                    
                    GenericValue wishOrderShipping = delegator.makeValue("WishOrderShipping", UtilMisc.toMap("orderId", orderId));
                    wishOrderShipping.set("productStoreId", productStoreId);
                    wishOrderShipping.set("name", name);
                    wishOrderShipping.set("streetAddress1", streetAddress1);
                    wishOrderShipping.set("streetAddress2", streetAddress2);
                    wishOrderShipping.set("city", city);
                    wishOrderShipping.set("state", stateProvince);
                    wishOrderShipping.set("country", country);
                    wishOrderShipping.set("zipcode", zipcode);
                    wishOrderShipping.set("phoneNumber", phoneNumber);
                    delegator.createOrStore(wishOrderShipping);
                    //SHIPPING DETAILS == END
                }   //loop Order -- END
            }   //if response OK -- END
        }   //main try -- END
        catch (GenericEntityException e) {
            Debug.logError("Yasin: writeWishOrderIntoDatabase GenericEntityException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (SAXException e) {
            Debug.logError("Yasin: writeWishOrderIntoDatabase SAXException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (ParserConfigurationException e) {
            Debug.logError("Yasin: writeWishOrderIntoDatabase ParserConfigurationException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (IOException e) {
            Debug.logError("Yasin: writeWishOrderIntoDatabase IOException Error for TRY CATCH: " + e.getMessage(), module);
        }
        catch (Exception e) {
            e.printStackTrace();
            Debug.logError("Yasin: writeWishOrderIntoDatabase Exception Error for TRY CATCH: " + e.getMessage(), module);
        }
        
        return result;
    }   //writeWishOrderIntoDatabase
    
    public static Map<String, Object> uploadWishXlsFile(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        ByteBuffer imageData = (ByteBuffer) context.get("uploadedFile");
        String uploadFileName = (String) context.get("_uploadedFile_fileName");
        uploadFileName = uploadFileName.replaceAll(" ","");
        String importFormId = (String) context.get("importFormId");
        
        if (UtilValidate.isNotEmpty(uploadFileName) && UtilValidate.isNotEmpty(imageData)) {
            try {   //main try -- START
                String fileServerPath = "hot-deploy/gudao/webapp/gudao/wish/upload/";
                File rootTargetDir = new File(fileServerPath);
                if (!rootTargetDir.exists()) {
                    boolean created = rootTargetDir.mkdirs();
                    if (!created) {
                        String errMsg = "Not create target directory";
                        Debug.logFatal(errMsg, module);
                        return ServiceUtil.returnError(errMsg);
                    }
                }
                String batchId = new SimpleDateFormat("yyyyMMdd-HHmm").format(Calendar.getInstance().getTime()) + "";
                String fileName = importFormId + "-" + batchId + "-" + userLogin.getString("userLoginId") + "-" + uploadFileName;
                
                String filePath = fileServerPath + "/" + fileName;
                File file = new File(filePath);
                try {
                    RandomAccessFile out = new RandomAccessFile(file, "rw");
                    out.write(imageData.array());
                    out.close();
                } catch (FileNotFoundException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("");
                } catch (IOException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("");
                }
                if (file.exists()) {    //if file exist -- START
                    Map result = readUploadWishXls(dispatcher, delegator, userLogin, filePath, uploadFileName, batchId);
                }   //if file exist -- END
            } catch (Exception e) {
                return ServiceUtil.returnError(e.getMessage());
            }   //main try -- END
        }
        return ServiceUtil.returnSuccess();
    }   //uploadWishXlsFile
    
    private static Map<String, Object> readUploadWishXls(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String filePath, String originalFilename, String batchId) {
        
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<String> errorList = FastList.newInstance();
        FileInputStream fis = null;
        POIFSFileSystem fs = null;
        HSSFWorkbook wb = null;
        Calendar now = Calendar.getInstance();
        
        try {   //main try -- START
            fis = new FileInputStream(new File(filePath));
            fs = new POIFSFileSystem(fis);
            
            //filePath.endsWith(".xlsx")
            wb = new HSSFWorkbook(fs);
            
            HSSFSheet sheet = wb.getSheetAt(0);
            int sheetLastRowNumber = sheet.getLastRowNum();
            HSSFRow firstRow = sheet.getRow(0);
            int minColIx = firstRow.getFirstCellNum();
            int maxColIx = firstRow.getLastCellNum();
            
            int colWeek = 0;
            int colProductStoreId = 0;
            int colWishId = 0;
            int colSku = 0;
            int colGender = 0;
            int colAge = 0;
            int colSize = 0;
            int colColor = 0;
            int colItemPrice = 0;
            int colShippingFee = 0;
            int colMonday = 0;
            int colTuesday = 0;
            int colWednesday = 0;
            int colThursday = 0;
            int colFriday = 0;
            int colSaturday = 0;
            int colSunday = 0;
            int colNotes = 0;
            int colType = 0;
            
            for(int colHead = minColIx; colHead < maxColIx; colHead++) {  //get column header -- START
                HSSFCell cellHead = firstRow.getCell(colHead);
                if(cellHead == null) {
                    continue;
                }
                cellHead.setCellType(HSSFCell.CELL_TYPE_STRING);
                String colHeader = cellHead.getRichStringCellValue().toString().toUpperCase().trim();
                //read column header data -- START
                if ("WEEK".equals(colHeader) || "WEEKNUMBER".equals(colHeader) || "WEEKNUM".equals(colHeader)) {
                    colWeek = colHead;
                } else if ("PRODUCT STORE ID".equals(colHeader) || "PRODUCTSTOREID".equals(colHeader) || "ACCOUNT".equals(colHeader) || "ACCOUNTNAME".equals(colHeader) || "ACCOUNT NAME".equals(colHeader)) {
                    colProductStoreId = colHead;
                } else if ("PRODUCT ID".equals(colHeader) || "PRODUCTID".equals(colHeader) || "WISH ID".equals(colHeader) || "WISHID".equals(colHeader)) {
                    colWishId = colHead;
                } else if ("SKU".equals(colHeader)) {
                    colSku = colHead;
                } else if ("FEMALE/MALE".equals(colHeader) || "GENDER".equals(colHeader)) {
                    colGender = colHead;
                } else if ("AGE".equals(colHeader)) {
                    colAge = colHead;
                } else if ("SIZE".equals(colHeader)) {
                    colSize = colHead;
                } else if ("COLOR".equals(colHeader)) {
                    colColor = colHead;
                } else if ("ITEM PRICE".equals(colHeader)) {
                    colItemPrice = colHead;
                } else if ("SHIPPING FEES".equals(colHeader)) {
                    colShippingFee = colHead;
                } else if ("MON".equals(colHeader)) {
                    colMonday = colHead;
                } else if ("TUE".equals(colHeader)) {
                    colTuesday = colHead;
                } else if ("WED".equals(colHeader)) {
                    colWednesday = colHead;
                } else if ("THUR".equals(colHeader)) {
                    colThursday = colHead;
                } else if ("FRI".equals(colHeader)) {
                    colFriday = colHead;
                } else if ("SAT".equals(colHeader)) {
                    colSaturday = colHead;
                } else if ("SUN".equals(colHeader)) {
                    colSunday = colHead;
                } else if ("NOTES".equals(colHeader)) {
                    colNotes = colHead;
                } else if ("TYPE".equals(colHeader)) {
                    colType = colHead;
                }
                //read column header data -- END
            }   //get column header -- END
            
            for (int j = 1; j <= sheetLastRowNumber; j++) { //loop rows -- START
                HSSFRow row = sheet.getRow(j);
                if (row != null) {  //if row is not empty -- START
                    String week = null;
                    long sunday = 0;
                    long monday = 0;
                    long tuesday = 0;
                    long wednesday = 0;
                    long thursday = 0;
                    long friday = 0;
                    long saturday = 0;
                    GenericValue wishFakePurchase = delegator.makeValue("WishFakePurchase", UtilMisc.toMap("batchId", batchId));
                    
                    for(int colIx = minColIx; colIx < maxColIx; colIx++) {    //loop cell in a row -- START
                        HSSFCell cell = row.getCell(colIx);
                        if(cell == null) {
                            continue;
                        }
                        
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        String dataString = null;
                        if (UtilValidate.isNotEmpty(cell.getRichStringCellValue())) {
                            dataString = cell.getRichStringCellValue().toString().trim();
                        }
                        
                        if (colIx == (colWeek)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                week = dataString;
                            }
                        } else if (colIx == (colProductStoreId)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wishFakePurchase.set("productStoreId", dataString);
                            }
                        } else if (colIx == (colWishId)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wishFakePurchase.set("wishId", dataString);
                            }
                        } else if (colIx == (colSku)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wishFakePurchase.set("sku", dataString);
                            }
                        } else if (colIx == (colGender)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wishFakePurchase.set("gender", dataString);
                            }
                        } else if (colIx == (colAge)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wishFakePurchase.set("age", dataString);
                            }
                        } else if (colIx == (colSize)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wishFakePurchase.set("size", dataString);
                            }
                        } else if (colIx == (colColor)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wishFakePurchase.set("color", dataString);
                            }
                        } else if (colIx == (colItemPrice)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wishFakePurchase.set("itemPrice", new BigDecimal(dataString));
                            }
                        } else if (colIx == (colShippingFee)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wishFakePurchase.set("shippingFee", new BigDecimal(dataString));
                            }
                        } else if (colIx == (colMonday)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                monday = Long.valueOf(dataString);
                            }
                        } else if (colIx == (colTuesday)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                tuesday = Long.valueOf(dataString);
                            }
                        } else if (colIx == (colWednesday)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wednesday = Long.valueOf(dataString);
                            }
                        } else if (colIx == (colThursday)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                thursday = Long.valueOf(dataString);
                            }
                        } else if (colIx == (colFriday)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                friday = Long.valueOf(dataString);
                            }
                        } else if (colIx == (colSaturday)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                saturday = Long.valueOf(dataString);
                            }
                        } else if (colIx == (colSunday)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                sunday = Long.valueOf(dataString);
                            }
                        } else if (colIx == (colNotes)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wishFakePurchase.set("notes", dataString);
                            }
                        } else if (colIx == (colType)) {
                            if (UtilValidate.isNotEmpty(dataString)) {
                                wishFakePurchase.set("type", "STATUS");
                            }
                        }
                        //read record -- END
                    }   //loop cell in a row -- END
                    week = now.get(Calendar.YEAR) + week;
                    wishFakePurchase.set("week", week);
                    wishFakePurchase.set("uploadedBy", userLogin.getString("userLoginId"));
                    
                    wishFakePurchase.set("sunday", sunday);
                    wishFakePurchase.set("monday", monday);
                    wishFakePurchase.set("tuesday", tuesday);
                    wishFakePurchase.set("wednesday", wednesday);
                    wishFakePurchase.set("thursday", thursday);
                    wishFakePurchase.set("friday", friday);
                    wishFakePurchase.set("saturday", saturday);
                    
                    if (sunday > 0) {
                        wishFakePurchase.set("sundayStatus", "PENDING");
                    }
                    if (monday > 0) {
                        wishFakePurchase.set("mondayStatus", "PENDING");
                    }
                    if (tuesday > 0) {
                        wishFakePurchase.set("tuesdayStatus", "PENDING");
                    }
                    if (wednesday > 0) {
                        wishFakePurchase.set("wednesdayStatus", "PENDING");
                    }
                    if (thursday > 0) {
                        wishFakePurchase.set("thursdayStatus", "PENDING");
                    }
                    if (friday > 0) {
                        wishFakePurchase.set("fridayStatus", "PENDING");
                    }
                    if (saturday > 0) {
                        wishFakePurchase.set("saturdayStatus", "PENDING");
                    }
                    delegator.createOrStore(wishFakePurchase);
                }   //if row is not empty -- END
            }   //loop rows -- END
        } catch (IOException e) {
            return ServiceUtil.returnError(e.getMessage());
        } catch (Exception e) {
            return ServiceUtil.returnError(e.getMessage());
        }   //main try -- END
        finally {
            try {
                if (fis!=null) {
                    fis.close();
                }
            }
            catch (IOException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        return ServiceUtil.returnSuccess();
    }   //readUploadWishXls
    
    public static Map<String, Object> wishFakePurchaseAutoAddQty (DispatchContext dctx, Map context)
    throws GenericEntityException, GenericServiceException, FileNotFoundException, IOException {
        
        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Calendar now = Calendar.getInstance();
        
        try {   //main try == START
            List<String> errorMsgList = FastList.newInstance();
            String week = now.get(Calendar.YEAR) + "" + now.get(Calendar.WEEK_OF_YEAR);
            int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            String[] dayOfWeekList = {"EMPTY", "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
            String[] dayOfWeekStatusList = {"EMPTY", "sundayStatus", "mondayStatus", "tuesdayStatus", "wednesdayStatus", "thursdayStatus", "fridayStatus", "saturdayStatus", "sundayStatus"};
            List<GenericValue> wishFakePurchaseList = delegator.findByAnd("WishFakePurchase", UtilMisc.toMap("week", week), null, false);
            Debug.logError("week: " + week + ", dayOfWeek: " + dayOfWeek + ", size: " + wishFakePurchaseList.size(), module);
            for (GenericValue wishFakePurchase : wishFakePurchaseList) {    //loop wishFakePurchaseList == START
                long qtyToBeAdded = wishFakePurchase.getLong(dayOfWeekList[dayOfWeek]);
                String weekStatus = wishFakePurchase.getString(dayOfWeekStatusList[dayOfWeek]);
                String wishId = wishFakePurchase.getString("wishId");
                String productId = wishFakePurchase.getString("sku");
                String productStoreId = wishFakePurchase.getString("productStoreId");
                String type = wishFakePurchase.getString("type");
                
                GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
                
                if (UtilValidate.isNotEmpty(productStore)) {   //if productStore is not empty == START
                    if (UtilValidate.isNotEmpty(weekStatus) && weekStatus.equals("PENDING")) { //if weekStatus is PENDING == START
                        if (qtyToBeAdded > 0) { //if need to add qty == START
                            String inputParameter = null;
                            if (UtilValidate.isEmpty(type)) {   //add qty == START
                                inputParameter = "[sku:" + productId + "],[inventory:" + qtyToBeAdded + "]";
                            }   //add qty == END
                            else {  //change status == START
                                inputParameter = "[sku:" + productId + "],[enabled:True]";
                            }   //change status == END
                            Map wishUpdateVariant = dispatcher.runSync("wishUpdateVariant", UtilMisc.toMap("productStoreId", productStoreId, "inputParameters", inputParameter, "userLogin", userLogin));
                            if (ServiceUtil.isSuccess(wishUpdateVariant)) {  //if wishUpdateVariant success == START
                                if (UtilValidate.isEmpty(wishUpdateVariant.get("errorMsg"))) {   //if wishUpdateVariant successfully add qty == START
                                    wishFakePurchase.set(dayOfWeekStatusList[dayOfWeek], "SUCCESS");
                                    delegator.store(wishFakePurchase);
                                }   //if wishUpdateVariant successfully add qty == END
                                else {  //if wishUpdateVariant failed add qty == START
                                    wishFakePurchase.set(dayOfWeekStatusList[dayOfWeek], "FAILED");
                                    wishFakePurchase.set("notes", wishUpdateVariant.get("errorMsg"));
                                    delegator.store(wishFakePurchase);
                                    errorMsgList.add("Account: " + productStoreId + ", WishId: " + wishId + ", SKU: " + productId + ", ErrorNotes: " + wishUpdateVariant.get("errorMsg"));
                                }   //if wishUpdateVariant failed add qty == END
                            }   //if wishUpdateVariant success == END
                            else {  //if wishUpdateVariant failed == START
                                wishFakePurchase.set(dayOfWeekStatusList[dayOfWeek], "FAILED");
                                wishFakePurchase.set("notes", wishUpdateVariant.get("errorMessage"));
                                delegator.store(wishFakePurchase);
                                errorMsgList.add("Account: " + productStoreId + ", WishId: " + wishId + ", SKU: " + productId + ", ErrorNotes: " + wishUpdateVariant.get("errorMsg"));
                            }   //if wishUpdateVariant failed == END
                        }   //if need to add qty == END
                    }   //if weekStatus is PENDING == END
                }   //if productStore is not empty == END
                else {  //if productStore is empty == START
                    wishFakePurchase.set(dayOfWeekStatusList[dayOfWeek], "FAILED");
                    wishFakePurchase.set("notes", productStoreId + "账号不存在");
                    delegator.store(wishFakePurchase);
                    errorMsgList.add("Account: " + productStoreId + ", WishId: " + wishId + ", SKU: " + productId + ", ErrorNotes: " + productStoreId + "账号不存在");
                }   //if productStore is empty == END
            }   //loop wishFakePurchaseList == END
            
            //Send Email == START
            StringBuffer mailBodyBuffer = new StringBuffer("<html><body>");
            
            if (UtilValidate.isEmpty(errorMsgList)) {   //if errorMsgList is empty == START
                mailBodyBuffer.append("Auto add qty for fake purchase has no problem");
            }   //if errorMsgList is empty == END
            else {  //if errorMsgList is NOT empty == START
                mailBodyBuffer.append("Auto add qty for fake purchase has following problems:<br />");
                for (String errorMsg : errorMsgList) {  //loop errorMsgList == START
                    mailBodyBuffer.append(errorMsg + "<br />");
                }   //loop errorMsgList == END
            }   //if errorMsgList is NOT empty == END
            mailBodyBuffer.append("</body></html>");
            
            Properties properties = new Properties();
            properties.load(new FileInputStream("hot-deploy/gudao/config/wish.properties"));
            String sendEmailToProp = properties.getProperty("fakePurchaseQQList");
            String sendCc = properties.getProperty("sendCc");
            String sendBcc = properties.getProperty("sendBcc");
            String subject = "WISH假拍信息";
            String body = mailBodyBuffer.toString();
            
            Debug.logError("body: " + body, module);
            boolean startTlsEnabled = true;
            String[] sendEmailTo = sendEmailToProp.split(",");
            for (int k = 0; k < sendEmailTo.length; k++) {   //loop sending email == START
                Map sendEmail = dispatcher.runSync("sendMail", UtilMisc.toMap("sendFrom", "898515425@qq.com",
                                                                              "sendTo", sendEmailTo[k].trim(),
                                                                              "sendCc", sendCc,
                                                                              "sendBcc", sendBcc,
                                                                              "socketFactoryPort", "465",
                                                                              "startTLSEnabled", startTlsEnabled,
                                                                              "subject", subject,
                                                                              "body", body,
                                                                              "userLogin", userLogin));
            }//loop sending email == end
            //Send Email == END
        }   //main try == END
        catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        catch (GenericServiceException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        catch (FileNotFoundException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        catch (IOException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        
        return result;
    }   //wishFakePurchaseAutoAddQty
    
    public static Map<String, Object> wishUpdateVariant (DispatchContext dctx, Map context)
    throws GenericEntityException, GenericServiceException, IOException {
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        String inputParameters = (String) context.get("inputParameters");
        
        try {   //main try -- START
            String inputParametersArray[]= inputParameters.split("\\],\\[");
            int countParameters = inputParametersArray.length;
            
            String url = wishProperties().get("apiUrl");
            GenericValue partyIdentification = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId", productStoreId, "partyIdentificationTypeId", "WISH_ACCESS_CODE"), false);
            String accessCode = partyIdentification.getString("idValue");
            
            StringBuffer requestUrlBuffer = new StringBuffer(url + "variant/update-inventory?format=xml&access_token=" + accessCode);
            for (int i = 0; i < countParameters; i++) {
                String parameterRaw = inputParametersArray[i];
                if (parameterRaw.contains("[") || parameterRaw.contains("]")) {
                    parameterRaw = parameterRaw.replaceAll("\\[", "");
                    parameterRaw = parameterRaw.replaceAll("\\]", "");
                }
                String parameterArray[] = parameterRaw.split(":");
                String parameter = parameterArray[0];
                String value = parameterArray[1];
                requestUrlBuffer.append("&" + parameter + "=" + value);
            }
            
            String requestUrl =  requestUrlBuffer.toString();
            Debug.logError("requestUrl: " + requestUrl, module);
            /*boolean keepLooping = false;
            int loopCount = 1;
            do {
                String responseXML = sendHttpRequest(requestUrl);
                Document docResponse = UtilXml.readXmlDocument(responseXML, true);
                Element elemResponse = docResponse.getDocumentElement();
                String ack = UtilXml.childElementValue(elemResponse, "Code", null);
                
                if (ack.equals("0")) {    //if ack success -- START
                    
                }   //if ack success -- END
                else if (ack.equals("1016") || ack.equals("1015")) {  //refresh token == START
                    Map refreshWishToken = dispatcher.runSync("wishGetRefreshCode", UtilMisc.toMap("productStoreId", productStoreId, "userLogin", userLogin));
                    if (ServiceUtil.isSuccess(refreshWishToken)) {
                        keepLooping = true;
                    }
                }   //refresh token == END
                else {  //if ack failure -- START
                    String errorMessage = UtilXml.childElementValue(elemResponse, "Message", null);
                    result.put("errorMsg", errorMessage);
                }   //if ack failure -- END
                loopCount++;
                if (loopCount >= 3) {   //break the loop once it has run for 3 times == START
                    keepLooping = false;
                }   //break the loop once it has run for 3 times == END
            } while (keepLooping);*/
        }   //main try -- END
        catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        result.put("errorMsg", "SOME ERROR MESSAGE WILL BE HERE");
        return result;
    }   //wishUpdateVariant
    
    public static Map<String, Object> wishFakePurchaseAutoDisableVariant (DispatchContext dctx, Map context)
    throws GenericEntityException, GenericServiceException, FileNotFoundException, IOException {
        
        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Calendar now = Calendar.getInstance();
        
        try {   //main try == START
            List<String> errorMsgList = FastList.newInstance();
            String week = now.get(Calendar.YEAR) + "" + now.get(Calendar.WEEK_OF_YEAR);
            int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            String[] dayOfWeekList = {"EMPTY", "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
            String[] dayOfWeekStatusList = {"EMPTY", "sundayStatus", "mondayStatus", "tuesdayStatus", "wednesdayStatus", "thursdayStatus", "fridayStatus", "saturdayStatus", "sundayStatus"};
            
            EntityCondition cond = null;
            List<EntityCondition> conditions = FastList.newInstance();
            conditions.add(EntityCondition.makeCondition("week", EntityOperator.EQUALS, week));
            conditions.add(EntityCondition.makeCondition(dayOfWeekStatusList[dayOfWeek], EntityOperator.EQUALS, "SUCCESS"));
            conditions.add(EntityCondition.makeCondition("type", EntityOperator.NOT_EQUAL, null));
            cond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
            List<GenericValue> wishFakePurchaseList = delegator.findList("WishFakePurchase", cond, null, null, null, false);
            
            Debug.logError("week: " + week + ", dayOfWeek: " + dayOfWeek + ", size: " + wishFakePurchaseList.size(), module);
            for (GenericValue wishFakePurchase : wishFakePurchaseList) {    //loop wishFakePurchaseList == START
                long qtyToBeAdded = wishFakePurchase.getLong(dayOfWeekList[dayOfWeek]);
                String weekStatus = wishFakePurchase.getString(dayOfWeekStatusList[dayOfWeek]);
                String wishId = wishFakePurchase.getString("wishId");
                String productId = wishFakePurchase.getString("sku");
                String productStoreId = wishFakePurchase.getString("productStoreId");
                String type = wishFakePurchase.getString("type");
                
                GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
                
                if (UtilValidate.isNotEmpty(productStore)) {   //if productStore is not empty == START
                    if (UtilValidate.isNotEmpty(weekStatus) && weekStatus.equals("SUCCESS")) { //if weekStatus is SUCCESS == START
                        if (qtyToBeAdded > 0) { //if need to add qty == START
                            String inputParameter = "[sku:" + productId + "],[enabled:False]";
                            Map wishUpdateVariant = dispatcher.runSync("wishUpdateVariant", UtilMisc.toMap("productStoreId", productStoreId, "inputParameters", inputParameter, "userLogin", userLogin));
                            if (ServiceUtil.isSuccess(wishUpdateVariant)) {  //if wishUpdateVariant success == START
                                if (UtilValidate.isEmpty(wishUpdateVariant.get("errorMsg"))) {   //if wishUpdateVariant successfully add qty == START
                                    wishFakePurchase.set(dayOfWeekStatusList[dayOfWeek], "SUCCESS");
                                    delegator.store(wishFakePurchase);
                                }   //if wishUpdateVariant successfully add qty == END
                                else {  //if wishUpdateVariant failed add qty == START
                                    wishFakePurchase.set(dayOfWeekStatusList[dayOfWeek], "FAILED");
                                    wishFakePurchase.set("notes", wishUpdateVariant.get("errorMsg"));
                                    delegator.store(wishFakePurchase);
                                    errorMsgList.add("Account: " + productStoreId + ", WishId: " + wishId + ", SKU: " + productId + ", ErrorNotes: " + wishUpdateVariant.get("errorMsg"));
                                }   //if wishUpdateVariant failed add qty == END
                            }   //if wishUpdateVariant success == END
                            else {  //if wishUpdateVariant failed == START
                                wishFakePurchase.set(dayOfWeekStatusList[dayOfWeek], "FAILED");
                                wishFakePurchase.set("notes", wishUpdateVariant.get("errorMessage"));
                                delegator.store(wishFakePurchase);
                                errorMsgList.add("Account: " + productStoreId + ", WishId: " + wishId + ", SKU: " + productId + ", ErrorNotes: " + wishUpdateVariant.get("errorMsg"));
                            }   //if wishUpdateVariant failed == END
                        }   //if need to add qty == END
                    }   //if weekStatus is SUCCESS == END
                }   //if productStore is not empty == END
                else {  //if productStore is empty == START
                    wishFakePurchase.set(dayOfWeekStatusList[dayOfWeek], "FAILED");
                    wishFakePurchase.set("notes", productStoreId + "账号不存在");
                    delegator.store(wishFakePurchase);
                    errorMsgList.add("Account: " + productStoreId + ", WishId: " + wishId + ", SKU: " + productId + ", ErrorNotes: " + productStoreId + "账号不存在");
                }   //if productStore is empty == END
            }   //loop wishFakePurchaseList == END
            
            //Send Email == START
            StringBuffer mailBodyBuffer = new StringBuffer("<html><body>");
            
            if (UtilValidate.isEmpty(errorMsgList)) {   //if errorMsgList is empty == START
                mailBodyBuffer.append("Auto disable fake purchase has no problem");
            }   //if errorMsgList is empty == END
            else {  //if errorMsgList is NOT empty == START
                mailBodyBuffer.append("Auto disable fake purchase has following problem:<br />");
                for (String errorMsg : errorMsgList) {  //loop errorMsgList == START
                    mailBodyBuffer.append(errorMsg + "<br />");
                }   //loop errorMsgList == END
            }   //if errorMsgList is NOT empty == END
            mailBodyBuffer.append("</body></html>");
            
            Properties properties = new Properties();
            properties.load(new FileInputStream("hot-deploy/gudao/config/wish.properties"));
            String sendEmailToProp = properties.getProperty("fakePurchaseQQList");
            String sendCc = properties.getProperty("sendCc");
            String sendBcc = properties.getProperty("sendBcc");
            String subject = "WISH假拍信息";
            String body = mailBodyBuffer.toString();
            boolean startTlsEnabled = true;
            String[] sendEmailTo = sendEmailToProp.split(",");
            for (int k = 0; k < sendEmailTo.length; k++) {   //loop sending email == START
                Map sendEmail = dispatcher.runSync("sendMail", UtilMisc.toMap("sendFrom", "898515425@qq.com",
                                                                              "sendTo", sendEmailTo[k].trim(),
                                                                              "sendCc", sendCc,
                                                                              "sendBcc", sendBcc,
                                                                              "socketFactoryPort", "465",
                                                                              "startTLSEnabled", startTlsEnabled,
                                                                              "subject", subject,
                                                                              "body", body,
                                                                              "userLogin", userLogin));
            }//loop sending email == end
            //Send Email == END
        }   //main try == END
        catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        catch (GenericServiceException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        catch (FileNotFoundException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        catch (IOException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        
        return result;
    }   //wishFakePurchaseAutoDisableVariant

}
