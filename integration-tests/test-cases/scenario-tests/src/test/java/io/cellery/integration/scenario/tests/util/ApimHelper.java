/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package io.cellery.integration.scenario.tests.util;

import com.google.common.net.HttpHeaders;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testng.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * This includes helper functions to access api manager.
 */
public class ApimHelper {
    private static final String WSO2_APIM_REGISTRATION_URL = "https://wso2-apim/client-registration/v0.14/register";
    private static final String WSO2_APIM_APIS_URL = "https://wso2-apim/api/am/store/v0.14/apis";
    private static final String WSO2_APIM_APPLICATIONS_URL = "https://wso2-apim/api/am/store/v0.14/applications";
    private static final String WSO2_APIM_TOKEN_URL = "https://wso2-apim-gateway/token";
    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "clientSecret";
    private static final String APPLICATION_ID = "applicationId";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    private static final String AUTHENTICATION_TYPE_BEARER = "Bearer";
    private static final String AUTHENTICATION_TYPE_BASIC = "Basic";
    private static final String WSO2_APIM_SUBSCRIPTION_URL = "https://wso2-apim/api/am/store/v0.14/subscriptions";
    private static final String CONSUMER_KEY = "consumerKey";
    private static final String CONSUMER_SECRET = "consumerSecret";
    private static final String CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String WSO2_APIM_GATEWAY_TOKEN_URL = "https://wso2-apim-gateway/token";

    /**
     * Gets the access token required to access apim store.
     *
     * @param username Username of a a user
     * @param password Password of a user
     * @return access token
     * @throws NoSuchAlgorithmException if sendPost method fails
     * @throws IOException              if sendPost method fails
     * @throws KeyManagementException   if sendPost method fails
     */
    public String getAccessTokenForApiStore(String username, String password) throws NoSuchAlgorithmException
            , IOException, KeyManagementException {
        String accessTokenForApiStore;
        String registrationUrlParameters = "{\"callbackUrl\": \"www.google.lk\", " +
                "\"clientName\": \"rest_api_store\", " +
                "\"owner\": \"" + username + "\", " +
                "\"grantType\": \"password refresh_token\", " +
                "\"saasApp\": true}";
        String base64UsernamePassword = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(
                StandardCharsets.UTF_8));
        // Get the client id and client secret for the user
        Map<String, String> registrationUrlHeaders = new HashMap<>();
        registrationUrlHeaders.put(HttpHeaders.AUTHORIZATION, AUTHENTICATION_TYPE_BASIC + " " + base64UsernamePassword);
        registrationUrlHeaders.put(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON);
        String registrationResponse = HttpClient.sendPost(WSO2_APIM_REGISTRATION_URL, registrationUrlParameters
                , registrationUrlHeaders);
        JsonObject registrationResponseJson = new JsonParser().parse(registrationResponse).getAsJsonObject();
        Assert.assertTrue(registrationResponseJson.isJsonObject());
        // Get client id and client secret from the response
        String clientId = registrationResponseJson.get(CLIENT_ID).getAsString();
        String clientSecret = registrationResponseJson.get(CLIENT_SECRET).getAsString();
        // Use the client id and client secret to get the access token to apim store
        // Add headers to the http request
        Map<String, String> tokenUrlHeaders = new HashMap<>();
        tokenUrlHeaders.put(HttpHeaders.AUTHORIZATION, AUTHENTICATION_TYPE_BASIC + " " + Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)));
        String tokenUrlPayload = "grant_type=password&username=" + username + "&password=" + password + "&scope=apim:" +
                "subscribe";
        String tokenResponse = HttpClient.sendPost(WSO2_APIM_TOKEN_URL, tokenUrlPayload, tokenUrlHeaders);
        JsonObject tokenResponseJson = new JsonParser().parse(tokenResponse).getAsJsonObject();
        Assert.assertTrue(tokenResponseJson.isJsonObject());
        // Get the token from the response
        accessTokenForApiStore = tokenResponseJson.get(ACCESS_TOKEN).toString().replaceAll("^\"|\"$", "");
        return accessTokenForApiStore;
    }

    /**
     * Get the id of an api.
     *
     * @return api id
     * @throws NoSuchAlgorithmException if sendGet method fails
     * @throws IOException              if sendGet method fails
     * @throws KeyManagementException   if sendGet method fails
     */
    public String getApiId(String apiName) throws NoSuchAlgorithmException, IOException
            , KeyManagementException {
        String apiId = "";
        //Add headers
        Map<String, String> headers = new HashMap<>();
        String apis = HttpClient.sendGet(WSO2_APIM_APIS_URL, headers);
        JsonObject responseJson = new JsonParser().parse(apis).getAsJsonObject();
        JsonArray apiList = responseJson.get("list").getAsJsonArray();
        for (int i = 0; i < apiList.size(); i++) {
            JsonObject jsonItem = apiList.get(i).getAsJsonObject();
            // Get the id of api
            if (jsonItem.get("name").toString().equals("\"" + apiName + "\"")) {
                apiId = jsonItem.get("id").toString().replaceAll("^\"|\"$", "");
            }
        }
        return apiId;
    }

    /**
     * Get the id of an application.
     *
     * @param accessToken An access token to access wso2-apim
     * @return application id
     * @throws NoSuchAlgorithmException if sendGet method fails
     * @throws IOException              if sendGet method fails
     * @throws KeyManagementException   if sendGet method fails
     */
    public String getApplicationId(String accessToken, String applicationName) throws NoSuchAlgorithmException
            , IOException, KeyManagementException {
        String applicationId = "";
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, AUTHENTICATION_TYPE_BEARER + " " + accessToken);
        String applications = HttpClient.sendGet(WSO2_APIM_APPLICATIONS_URL, headers);
        JsonObject responseJson = new JsonParser().parse(applications).getAsJsonObject();
        Assert.assertTrue(responseJson.isJsonObject());
        JsonArray apiList = responseJson.get("list").getAsJsonArray();
        for (int i = 0; i < apiList.size(); i++) {
            JsonObject jsonItem = apiList.get(i).getAsJsonObject();
            // Get the id of application
            if (jsonItem.get("name").toString().equals("\"" + applicationName + "\"")) {
                applicationId = jsonItem.get(APPLICATION_ID).toString().replaceAll("^\"|\"$", "");
            }
        }
        return applicationId;
    }

    /**
     * Subscribe for an application.
     *
     * @param apiId         Id of api
     * @param applicationId Id of application
     * @param oauthToken    An oauth token to access wso2-apim
     * @throws NoSuchAlgorithmException if sendPost method fails
     * @throws IOException              if sendPost method fails
     * @throws KeyManagementException   if sendPost method fails
     */
    public void subscribeForApplication(String apiId, String applicationId, String oauthToken) throws
            NoSuchAlgorithmException, IOException, KeyManagementException {
        String payload = "{\n" +
                "  \"tier\": \"Unlimited\",\n" +
                "  \"apiIdentifier\": \"" + apiId + "\",\n" +
                "  \"applicationId\": \"" + applicationId + "\"\n" +
                "}";
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, AUTHENTICATION_TYPE_BEARER + " " + oauthToken);
        headers.put(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON);
        HttpClient.sendPost(WSO2_APIM_SUBSCRIPTION_URL, payload, headers);
    }

    /**
     * Get base64 encoded consumer key consumer secret needed to generate access token .
     *
     * @param oauthToken           An oauth token to access wso2-apim
     * @param applicationId Application id
     * @return base64 encoded concatenated string comprising consumer key consumer secret
     * @throws NoSuchAlgorithmException if sendGet method fails
     * @throws IOException              if sendGet method fails
     * @throws KeyManagementException   if sendGet method fails
     */
    public String getConsumerKeyConsumerSecretForApplication(String oauthToken, String applicationId)
            throws NoSuchAlgorithmException, IOException, KeyManagementException {
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, AUTHENTICATION_TYPE_BEARER + " " + oauthToken);
        String applications = HttpClient.sendGet(WSO2_APIM_APPLICATIONS_URL + "/" + applicationId, headers);
        JsonObject responseJson = new JsonParser().parse(applications).getAsJsonObject();
        Assert.assertTrue(responseJson.isJsonObject());
        JsonArray keyList = responseJson.get("keys").getAsJsonArray();
        // Get consumer key and consumer secret from the response
        String consumerKey = keyList.get(0).getAsJsonObject().get(CONSUMER_KEY).toString()
                .replaceAll("^\"|\"$", "");
        String consumerSecret = keyList.get(0).getAsJsonObject().get(CONSUMER_SECRET).toString()
                .replaceAll("^\"|\"$", "");
        return Base64.getEncoder().encodeToString((consumerKey + ":" + consumerSecret).getBytes(
                StandardCharsets.UTF_8));
    }

    /**
     * Get the access token required to access the wso2-apim gateway.
     *
     * @param consumerKeyConsumerSecret A concatenated string comprising consumer key and consumer secret.
     * @return an access token
     * @throws NoSuchAlgorithmException if sendPost method fails
     * @throws IOException              if sendPost method fails
     * @throws KeyManagementException   if sendPost method fails
     */
    public String getWso2ApimGatewayToken(String consumerKeyConsumerSecret, String username, String password) throws
            NoSuchAlgorithmException, IOException, KeyManagementException {
        String gatewayToken;
        String payload = "grant_type=password&username=" + username + "&password=" + password;
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, AUTHENTICATION_TYPE_BASIC + " " + consumerKeyConsumerSecret);
        headers.put(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED);
        String response = HttpClient.sendPost(WSO2_APIM_GATEWAY_TOKEN_URL, payload, headers);
        JsonObject responseJson = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertTrue(responseJson.isJsonObject());
        // Get the token from the response
        gatewayToken = responseJson.get(ACCESS_TOKEN).getAsString().replaceAll("^\"|\"$", "");
        return gatewayToken;
    }

    /**
     * Generate production keys for an application.
     *
     * @param oauthToken An oauth token to access wso2-apim
     * @param applicationId Application id
     * @throws NoSuchAlgorithmException if sendPost method fails
     * @throws IOException              if sendPost method fails
     * @throws KeyManagementException   if sendPost method fails
     */
    public void generateKeysForApplication(String oauthToken, String applicationId) throws NoSuchAlgorithmException
            , IOException, KeyManagementException {
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, AUTHENTICATION_TYPE_BEARER + " " + oauthToken);
        headers.put(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON);
        String payload = "{ \"validityTime\": \"3600\", " +
                "\"keyType\": \"PRODUCTION\", " +
                "\"accessAllowDomains\": [ \"ALL\" ], " +
                "\"scopes\": [ \"am_application_scope\", \"default\" ], " +
                "\"supportedGrantTypes\": [ \"urn:ietf:params:oauth:grant-type:saml2-bearer\", \"iwa:ntlm\", " +
                "\"refresh_token\", \"client_credentials\", \"password\" ] }";
        String response = HttpClient.sendPost(WSO2_APIM_APPLICATIONS_URL + "/generate-keys?applicationId=" +
                applicationId, payload, headers);
        JsonObject responseJson = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertTrue(responseJson.isJsonObject());
    }
}
