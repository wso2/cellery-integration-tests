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

import com.google.common.net.HttpHeaders;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cellery.integration.scenario.tests.util.ApimHelper;
import io.cellery.integration.scenario.tests.util.HttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * This includes the test cases related to employee portal.
 */
public class EmployeePortalTestCase extends BaseTestCase {

    private static final String STOCK_INSTANCE_NAME = "stock-inst";
    private static final String STOCK_IMAGE_NAME = "stock";
    private static final String EMPLOYEE_INSTANCE_NAME = "employee-inst";
    private static final String EMPLOYEE_IMAGE_NAME = "employee";
    private static final String HR_INSTANCE_NAME = "hr-inst";
    private static final String HR_IMAGE_NAME = "hr";
    private static final String VERSION = "1.0.0";
    private static final String ALICE_USERNAME = "alice";
    private static final String ALICE_PASSWORD = "alice123";
    private static final String LINK_HR_TO_STOCK = "stockCellDep:stock-inst";
    private static final String LINK_HR_TO_EMPLOYEE = "employeeCellDep:employee-inst";
    private static final String HR_URL = "https://wso2-apim-gateway/hr-inst/hr-api";
    private static final String HR_INST_API = "hr_inst_global_1_0_0_hr_api";
    private static final String DEFAULT_APPLICATION = "DefaultApplication";
    private static final String AUTHENTICATION_TYPE_BEARER = "Bearer";

    private HttpClient httpClient;
    private ApimHelper apimHelper;

    @BeforeClass
    public void setup() {
        this.httpClient = new HttpClient();
        this.apimHelper = new ApimHelper();
    }

    @Test(description = "Tests the building of all related cell images.")
    public void build() throws Exception {
        build("stocks.bal", Constants.TEST_CELL_ORG_NAME, STOCK_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "cellery", "stock")
                        .toFile().getAbsolutePath());
        build("employee.bal", Constants.TEST_CELL_ORG_NAME, EMPLOYEE_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "cellery", "employee")
                        .toFile().getAbsolutePath());
        build("hr.bal", Constants.TEST_CELL_ORG_NAME, HR_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "cellery", "hr").toFile().getAbsolutePath());
    }

    @Test(description = "Tests the running of all the related cell instances.")
    public void run() throws Exception {
        String[] links = new String[]{LINK_HR_TO_STOCK, LINK_HR_TO_EMPLOYEE};
        // Run stock cell
        run(Constants.TEST_CELL_ORG_NAME, STOCK_IMAGE_NAME, VERSION, STOCK_INSTANCE_NAME, 600);
        // Run hr cell by defining link to stock cell and starting the dependent employee cell
        run(Constants.TEST_CELL_ORG_NAME, HR_IMAGE_NAME, VERSION, HR_INSTANCE_NAME, links, true,
                600);
    }

    @Test(description = "Sends http requests and asserts response with expected employee data")
    public void validateData() throws Exception {
        // Get the access token to access apim store for user alice
        String accessTokenForApimStore = apimHelper.getAccessTokenForApiStore(ALICE_USERNAME, ALICE_PASSWORD);
        // Get the id of hr api needed to subscribe
        String hrApiId = apimHelper.getApiId(HR_INST_API);
        // Get the id of DefaultApplication needed to subscribe
        String defaultApplicationId = apimHelper.getApplicationId(accessTokenForApimStore,
                DEFAULT_APPLICATION);
        // Subscribe for hr api default application
        apimHelper.subscribeForApplication(hrApiId, defaultApplicationId, accessTokenForApimStore);
        // Get base64 encoded consumer key consumer secret
        String consumerKeyConsumerSecret =
                apimHelper.getConsumerKeyConsumerSecretForApplication(accessTokenForApimStore
                        , defaultApplicationId);
        // Get the token to access wso2-apim-gateway
        String gatewayToken = apimHelper.getWso2ApimGatewayToken(consumerKeyConsumerSecret, ALICE_USERNAME
                , ALICE_PASSWORD);
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
        delete(Constants.TEST_CELL_ORG_NAME + "/" + STOCK_IMAGE_NAME + ":" + VERSION);
        delete(Constants.TEST_CELL_ORG_NAME + "/" + EMPLOYEE_IMAGE_NAME + ":" + VERSION);
        delete(Constants.TEST_CELL_ORG_NAME + "/" + HR_IMAGE_NAME + ":" + VERSION);
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
     * Send an http request and get the employee data and validate against the expected values.
     *
     * @param token An access token
     * @throws NoSuchAlgorithmException if sendGet method fails
     * @throws IOException              if sendGet method fails
     * @throws KeyManagementException   if sendGet method fails
     */
    private void validateData(String token) throws NoSuchAlgorithmException, IOException,
            KeyManagementException {
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, AUTHENTICATION_TYPE_BEARER + " " + token);
        String response = httpClient.sendGet(HR_URL, headers);
        JsonObject responseJson = new JsonParser().parse(response).getAsJsonObject();
        Assert.assertTrue(responseJson.isJsonObject());
        String employeeId =
                responseJson.get("employee").getAsJsonObject().get("details").getAsJsonObject().get("id")
                        .toString().replaceAll("^\"|\"$", "");
        String employeeDesignation =
                responseJson.get("employee").getAsJsonObject().get("details").getAsJsonObject()
                        .get("designation").toString().replaceAll("^\"|\"$", "");
        String employeeSalary =
                responseJson.get("employee").getAsJsonObject().get("details").getAsJsonObject()
                        .get("salary").toString().replaceAll("^\"|\"$", "");
        int stockOptionsTotal = responseJson.get("employee").getAsJsonObject().get("stocks").getAsJsonObject()
                .get("options").getAsJsonObject().get("total").getAsInt();
        int stockOptionsVested =
                responseJson.get("employee").getAsJsonObject().get("stocks").getAsJsonObject()
                        .get("options").getAsJsonObject().get("vestedAmount").getAsInt();
        // Validate data returned
        Assert.assertEquals(employeeId, "0410", "Employee id is not as expected");
        Assert.assertEquals(employeeDesignation, "Senior Software Engineer", "Employee designation is not " +
                "as expected");
        Assert.assertEquals(employeeSalary, "$1500", "Employee salary is not as expected");
        Assert.assertEquals(stockOptionsTotal, 120, "Stock options total is not as expected");
        Assert.assertEquals(stockOptionsVested, 105, "Stock options vested is not as expected");
    }
}
