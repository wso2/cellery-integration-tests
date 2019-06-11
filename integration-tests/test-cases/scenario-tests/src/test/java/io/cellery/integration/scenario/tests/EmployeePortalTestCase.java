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
package io.cellery.integration.scenario.tests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cellery.integration.scenario.tests.util.HttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * This includes the test cases related to employee portal.
 */
public class EmployeePortalTestCase extends BaseTestCase {
    private static final String STOCK_INSTANCE_NAME = "stock-inst";
    private static final String STOCK_IMAGE_NAME = "stock-cell";
    private static final String EMPLOYEE_INSTANCE_NAME = "employee-inst";
    private static final String EMPLOYEE_IMAGE_NAME = "employee-cell";
    private static final String HR_INSTANCE_NAME = "hr-inst";
    private static final String HR_IMAGE_NAME = "hr-cell";
    private static final String VERSION = "1.0.0";
    private static final String LINK_STOCK = "stockCellDep:stock-inst";
    private static final String LINK_EMPLOYEE = "employeeCellDep:employee-inst";
    private HttpClient httpClient;

    @BeforeClass
    public void setup() {
        this.httpClient = new HttpClient();
    }

    @Test(description = "Tests the building of stock cell image.")
    public void buildStock() throws Exception {
        build("stocks.bal", Constants.TEST_CELL_ORG_NAME, STOCK_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "stock").toFile().getAbsolutePath());
    }

    @Test(description = "Tests the running of stock cell instance.")
    public void runStock() throws Exception {
        run(Constants.TEST_CELL_ORG_NAME, STOCK_IMAGE_NAME, VERSION, STOCK_INSTANCE_NAME, 120);
    }

    @Test(description = "Tests the building of employee cell image.")
    public void buildEmployee() throws Exception {
        build("employee.bal", Constants.TEST_CELL_ORG_NAME, EMPLOYEE_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "employee").toFile().getAbsolutePath());
    }

    @Test(description = "Tests the running of employee cell instance.")
    public void runEmployee() throws Exception {
        run(Constants.TEST_CELL_ORG_NAME, EMPLOYEE_IMAGE_NAME, VERSION, EMPLOYEE_INSTANCE_NAME, 120);
    }

    @Test(description = "Tests the building of hr cell image.")
    public void buildHr() throws Exception {
        build("hr.bal", Constants.TEST_CELL_ORG_NAME, HR_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "hr").toFile().getAbsolutePath());
    }

    @Test(description = "Tests the running of hr cell end instance.")
    public void runHr() throws Exception {
        String[] links = new String[]{LINK_STOCK, LINK_EMPLOYEE};
        run(Constants.TEST_CELL_ORG_NAME, HR_IMAGE_NAME, VERSION, HR_INSTANCE_NAME, links,
                120);
    }

    @Test(description = "Sends http requests and asserts response with expected employee data")
    public void validateData() throws Exception {
        // Get the value of clientIdColonClientSecret
        String clientIdColonClientSecret = getClientIdColonClientSecret();

        // Get the oauth token to access apim
        String oauthToken = getOauthToken(clientIdColonClientSecret);

        // Get the id of hr api
        String hrApiId = getHrApiId();

        // Get the id of DefaultApplication
        String defaultApplicationId = getDefaultApplicationId(oauthToken);

        // Subscribe for hr api default application
        subscribeHrApiDefaultApplication(hrApiId, defaultApplicationId, oauthToken);

        // Get consumerKeyColonConsumerSecret
        String consumerKeyColonConsumerSecret = getConsumerKeyColonConsumerSecret(oauthToken, defaultApplicationId);

        // Get the token to access wso2-apim-gateway
        String gatewayToken = getGatewayToken(consumerKeyColonConsumerSecret);

        // Validate data
        validateData(gatewayToken);
    }

    @Test(description = "This tests the termination of hr, employee and stock cell instances")
    public void terminate() throws Exception {
        terminateCell(HR_INSTANCE_NAME);
        terminateCell(EMPLOYEE_INSTANCE_NAME);
        terminateCell(STOCK_INSTANCE_NAME);
    }

    @Test(description = "This tests the deletion hr, employee and stock cell images")
    public void deleteImages() throws Exception {
        delete(Constants.TEST_CELL_ORG_NAME + "/" + HR_IMAGE_NAME + ":" + VERSION);
        delete(Constants.TEST_CELL_ORG_NAME + "/" + EMPLOYEE_IMAGE_NAME + ":" + VERSION);
        delete(Constants.TEST_CELL_ORG_NAME + "/" + STOCK_IMAGE_NAME + ":" + VERSION);
    }

    @AfterClass
    public void cleanup() {
        try {
            terminateCell(HR_INSTANCE_NAME);
            terminateCell(EMPLOYEE_INSTANCE_NAME);
            terminateCell(STOCK_INSTANCE_NAME);
        } catch (Exception ignored) {
        }
    }

    /**
     * Get client id and client secret needed to generate oauth token to access apim.
     * @return a concatenated string comprising client id and client secret delimited by a colon
     * @throws NoSuchAlgorithmException if sendPost method fails
     * @throws IOException if sendPost method fails
     * @throws KeyManagementException if sendPost method fails
     */
    private String getClientIdColonClientSecret() throws NoSuchAlgorithmException
            , IOException, KeyManagementException {
        // Get client ID and client secret for alice user
        String url = "https://wso2-apim/client-registration/v0.14/register";
        String usernameColonPassword = "alice" + ":" + "alice123";
        String parameters = "{\"callbackUrl\": \"www.google.lk\", \"clientName\": \"rest_api_store\", \"owner\": " +
                "\"alice\", \"grantType\": \"password refresh_token\", \"saasApp\": true}";
        String authenticationKey = Base64.getEncoder().encodeToString(usernameColonPassword.getBytes(
                StandardCharsets.UTF_8));
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + authenticationKey);
        headers.put("Content-Type", "application/json");
        String response = httpClient.sendPost(url, parameters, headers);
        JsonObject responseJson = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertTrue(responseJson.isJsonObject());
        // Get client id and client secret from the response
        String clientId = responseJson.get("clientId").getAsString();
        String clientSecret = responseJson.get("clientSecret").getAsString();
        return clientId + ":" + clientSecret;
    }

    /**
     * Get the id of hr api.
     * @return hr api id
     * @throws NoSuchAlgorithmException if sendGet method fails
     * @throws IOException if sendGet method fails
     * @throws KeyManagementException if sendGet method fails
     */
    private String getHrApiId() throws NoSuchAlgorithmException, IOException
            , KeyManagementException {
        String hrApiId = "";
        // Get the list of APIs
        String url = "https://wso2-apim/api/am/store/v0.14/apis";
        //Add headers
        Map<String, String> headers = new HashMap<>();
        String apis = httpClient.sendGet(url, headers);
        JsonObject responseJson = new JsonParser().parse(apis).getAsJsonObject();
        JsonArray apiList = responseJson.get("list").getAsJsonArray();
        for (int i = 0; i < apiList.size(); i++) {
            JsonObject jsonItem = apiList.get(i).getAsJsonObject();
            // Get the id of hr api
            if (jsonItem.get("name").toString().equals("\"hr_inst_global_1_0_0_hr_api\"")) {
                hrApiId = jsonItem.get("id").toString().replaceAll("^\"|\"$", "");
            }
        }
        return hrApiId;
    }

    /**
     * Get the oauth token required to access wso2-apim.
     * @param clientIdColonClientSecret
     *        A concatenated string comprising client id and client secret delimited by a colon
     * @return oauth token
     * @throws NoSuchAlgorithmException if sendPost method fails
     * @throws IOException if sendPost method fails
     * @throws KeyManagementException if sendPost method fails
     */
    private String getOauthToken(String clientIdColonClientSecret) throws NoSuchAlgorithmException, IOException
            , KeyManagementException {
        String passwordGrantType;
        String url = "https://wso2-apim-gateway/token";
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                clientIdColonClientSecret.getBytes(StandardCharsets.UTF_8)));

        String payload = "grant_type=password&username=alice&password=alice123&scope=apim:subscribe";
        String response = httpClient.sendPost(url, payload, headers);
        JsonObject responseJson = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertTrue(responseJson.isJsonObject());
        // Get the token from the response
        passwordGrantType = responseJson.get("access_token").toString().replaceAll("^\"|\"$", "");
        return passwordGrantType;
    }

    /**
     * Get the id of default application.
     * @param oauthToken
     *        An oauth token to access wso2-apim
     * @return default application id
     * @throws NoSuchAlgorithmException if sendGet method fails
     * @throws IOException if sendGet method fails
     * @throws KeyManagementException if sendGet method fails
     */
    private String getDefaultApplicationId(String oauthToken) throws NoSuchAlgorithmException, IOException
            , KeyManagementException {
        String defaultApplicationId = "";
        String url = "https://wso2-apim/api/am/store/v0.14/applications";
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + oauthToken);
        String applications = httpClient.sendGet(url, headers);
        JsonObject responseJson = new JsonParser().parse(applications).getAsJsonObject();
        Assert.assertTrue(responseJson.isJsonObject());
        JsonArray apiList = responseJson.get("list").getAsJsonArray();
        for (int i = 0; i < apiList.size(); i++) {
            JsonObject jsonItem = apiList.get(i).getAsJsonObject();
            // Get the id of DefaultApplication
            if (jsonItem.get("name").toString().equals("\"DefaultApplication\"")) {
                defaultApplicationId = jsonItem.get("applicationId").toString().replaceAll("^\"|\"$", "");
            }
        }
        return defaultApplicationId;
    }

    /**
     * Get consumer key and consumer secret needed to generate access token .
     * @param oauthToken
     *        An oauth token to access wso2-apim
     * @param defaultApplicationId
     *        Default application id
     * @return a concatenated string comprising consumer key and consumer secret delimited by a colon
     * @throws NoSuchAlgorithmException if sendGet method fails
     * @throws IOException if sendGet method fails
     * @throws KeyManagementException if sendGet method fails
     */
    private String getConsumerKeyColonConsumerSecret(String oauthToken, String defaultApplicationId)
            throws NoSuchAlgorithmException, IOException, KeyManagementException {
        String url = "https://wso2-apim/api/am/store/v0.14/applications/" + defaultApplicationId;
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + oauthToken);
        String applications = httpClient.sendGet(url, headers);
        JsonObject responseJson = new JsonParser().parse(applications).getAsJsonObject();
        Assert.assertTrue(responseJson.isJsonObject());
        JsonArray keyList = responseJson.get("keys").getAsJsonArray();
        // Get consumer key and consumer secret from the response
        String consumerKey = keyList.get(0).getAsJsonObject().get("consumerKey").toString()
                .replaceAll("^\"|\"$", "");
        String consumerSecret = keyList.get(0).getAsJsonObject().get("consumerSecret").toString()
                .replaceAll("^\"|\"$", "");
        return consumerKey + ":" + consumerSecret;
    }

    /**
     * Subscribe for default application of hr api using alice user.
     * @param hrApiId
     *        Id of hr api
     * @param defaultApplicationId
     *        Id of default application
     * @param oauthToken
     *        An oauth token to access wso2-apim
     * @throws NoSuchAlgorithmException if sendPost method fails
     * @throws IOException if sendPost method fails
     * @throws KeyManagementException if sendPost method fails
     */
    private void subscribeHrApiDefaultApplication(String hrApiId, String defaultApplicationId
            , String oauthToken) throws NoSuchAlgorithmException, IOException, KeyManagementException {
        String subscriptionUrl = "https://wso2-apim/api/am/store/v0.14/subscriptions";
        String payload = "{\n" +
                "  \"tier\": \"Unlimited\",\n" +
                "  \"apiIdentifier\": \"" + hrApiId + "\",\n" +
                "  \"applicationId\": \"" + defaultApplicationId + "\"\n" +
                "}";
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + oauthToken);
        headers.put("Content-Type", "application/json");
        httpClient.sendPost(subscriptionUrl, payload, headers);
    }

    /**
     * Get the access token required to get the employee data.
     * @param consumerKeyConsumerSecret
     *        A concatenated string comprising consumer key and consumer secret delimited by a colon
     * @return an access token
     * @throws NoSuchAlgorithmException if sendPost method fails
     * @throws IOException if sendPost method fails
     * @throws KeyManagementException if sendPost method fails
     */
    private String getGatewayToken(String consumerKeyConsumerSecret) throws NoSuchAlgorithmException, IOException
            , KeyManagementException {
        String gatewayToken;
        String gatewayTokenUrl = "https://wso2-apim-gateway/token";
        String authenticationKey = Base64.getEncoder().encodeToString((consumerKeyConsumerSecret).getBytes(
                StandardCharsets.UTF_8));
        String payload = "grant_type=password&username=alice&password=alice123";
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + authenticationKey);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        String response = httpClient.sendPost(gatewayTokenUrl, payload, headers);
        JsonObject responseJson = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertTrue(responseJson.isJsonObject());
        // Get the token from the response
        gatewayToken = responseJson.get("access_token").getAsString().replaceAll("^\"|\"$", "");
        return gatewayToken;
    }

    /**
     * Send an http request and get the employee data and validate against the expected values.
     * @param token
     *        An access token
     * @throws NoSuchAlgorithmException if sendGet method fails
     * @throws IOException if sendGet method fails
     * @throws KeyManagementException if sendGet method fails
     */
    private void validateData(String token) throws NoSuchAlgorithmException, IOException, KeyManagementException {
        String gatewayUrl = "https://wso2-apim-gateway/hr-inst/hr-api";
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        String response = httpClient.sendGet(gatewayUrl, headers);
        JsonObject responseJson = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertTrue(responseJson.isJsonObject());
        String employeeId = responseJson.get("employee").getAsJsonObject().get("details").getAsJsonObject().get("id")
                .toString().replaceAll("^\"|\"$", "");
        String employeeDesignation = responseJson.get("employee").getAsJsonObject().get("details").getAsJsonObject()
                .get("designation").toString().replaceAll("^\"|\"$", "");
        String employeeSalary = responseJson.get("employee").getAsJsonObject().get("details").getAsJsonObject()
                .get("salary").toString().replaceAll("^\"|\"$", "");
        int stockOptionsTotal = responseJson.get("employee").getAsJsonObject().get("stocks").getAsJsonObject()
                .get("options").getAsJsonObject().get("total").getAsInt();
        int stockOptionsVested = responseJson.get("employee").getAsJsonObject().get("stocks").getAsJsonObject()
                .get("options").getAsJsonObject().get("vestedAmount").getAsInt();
        // Validate data returned
        Assert.assertEquals(employeeId, "0410", "Employee id is not as expected");
        Assert.assertEquals(employeeDesignation, "Senior Software Engineer", "Employee designation is not as expected");
        Assert.assertEquals(employeeSalary, "$1500", "Employee salary is not as expected");
        Assert.assertEquals(stockOptionsTotal, 120, "Stock options total is not as expected");
        Assert.assertEquals(stockOptionsVested, 105, "Stock options vested is not as expected");
    }
}
