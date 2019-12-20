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
package io.cellery.integration.scenario.tests.employeeportal;

import com.google.common.net.HttpHeaders;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cellery.integration.scenario.tests.BaseTestCase;
import io.cellery.integration.scenario.tests.Constants;
import io.cellery.integration.scenario.tests.util.ApimHelper;
import io.cellery.integration.scenario.tests.util.HttpClient;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This includes build, run and termination of employee portal cells.
 */
public class EmployeePortal extends BaseTestCase {

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
    private static final String HR_URL = "https://wso2-apim-gateway/myorg/hr/1.0.1";
    private static final String HR_INST_API = "hr_inst_global_1_0_1_hr";
    private static final String AUTHENTICATION_TYPE_BEARER = "Bearer";

    private String employeeBuildSource;
    private String applicationName;
    private ApimHelper apimHelper = new ApimHelper();

    public void setEmployeeBuildSource(String employeeBalSource) {
        this.employeeBuildSource = employeeBalSource;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Build employee portal images.
     *
     * @throws Exception if cellery build fails
     */
    public void build() throws Exception {
        build("stocks.bal", Constants.TEST_CELL_ORG_NAME, STOCK_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "cellery", "stock")
                        .toFile().getAbsolutePath());
        build(employeeBuildSource, Constants.TEST_CELL_ORG_NAME, EMPLOYEE_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "cellery")
                        .toFile().getAbsolutePath());
        build("hr.bal", Constants.TEST_CELL_ORG_NAME, HR_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "cellery", "hr").toFile().getAbsolutePath());
    }

    /**
     * Run employee portal instances.
     *
     * @throws Exception if cellery run fails
     */
    public void run() throws Exception {
        String[] links = new String[]{LINK_HR_TO_STOCK, LINK_HR_TO_EMPLOYEE};
        // Run stock cell
        run(Constants.TEST_CELL_ORG_NAME, STOCK_IMAGE_NAME, VERSION, STOCK_INSTANCE_NAME, 600);
        // Run hr cell by defining link to stock cell and starting the dependent employee cell
        run(Constants.TEST_CELL_ORG_NAME, HR_IMAGE_NAME, VERSION, HR_INSTANCE_NAME, links, true);
    }

    /**
     * Send http requests and get the employee data.
     *
     * @throws NoSuchAlgorithmException if apimHelper method fails
     * @throws IOException              if apimHelper method fails
     * @throws KeyManagementException   if apimHelper method fails
     */
    public void sendRequest() throws NoSuchAlgorithmException, IOException, KeyManagementException,
            InterruptedException {
        // Todo: Update cell wait logic to check if job is published
        TimeUnit.SECONDS.sleep(60);
        // Get the access token to access apim store for user alice
        String accessTokenForApimStore = apimHelper.getAccessTokenForApiStore(ALICE_USERNAME, ALICE_PASSWORD);
        // Get the id of hr api
        String hrApiId = apimHelper.getApiId(HR_INST_API);
        // Create an application to subscribe
        String applicationDescription = "testing";
        String applicationCallBackUrl = "http://employee.server.com/callback";
        String applicationId = apimHelper.createApplication(this.applicationName, applicationDescription
                , applicationCallBackUrl, accessTokenForApimStore);
        // Subscribe for hr api default application
        apimHelper.subscribeForApplication(hrApiId, applicationId, accessTokenForApimStore);
        // Generate production keys for default application
        apimHelper.generateKeysForApplication(accessTokenForApimStore, applicationId);
        // Get base64 encoded consumer key consumer secret
        String consumerKeyConsumerSecret = apimHelper.getConsumerKeyConsumerSecretForApplication(
                accessTokenForApimStore, applicationId);
        // Get the token to access wso2-apim-gateway
        String gatewayToken = apimHelper.getWso2ApimGatewayToken(consumerKeyConsumerSecret, ALICE_USERNAME
                , ALICE_PASSWORD);
        // Todo: check if the cell instance is ready.
        TimeUnit.SECONDS.sleep(60);
        // Validate data
        validateData(gatewayToken);
        // Delete application
        apimHelper.deleteApplication(applicationId, accessTokenForApimStore);
    }

    /**
     * Validate employee data against the expected values.
     *
     * @param token An access token
     * @throws NoSuchAlgorithmException if sendGet method fails
     * @throws IOException              if sendGet method fails
     * @throws KeyManagementException   if sendGet method fails
     */
    private void validateData(String token) throws NoSuchAlgorithmException, IOException,
            KeyManagementException, InterruptedException {
        //Add headers
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, AUTHENTICATION_TYPE_BEARER + " " + token);
        String response = HttpClient.sendGet(HR_URL, headers, 30, 10);
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

    /**
     * Terminate employee portal instances.
     *
     * @throws Exception if cell termination fails
     */
    public void terminate() throws Exception {
        terminateCell(HR_INSTANCE_NAME);
        terminateCell(EMPLOYEE_INSTANCE_NAME);
        terminateCell(STOCK_INSTANCE_NAME);
    }

    /**
     * Delete cell images.
     *
     * @throws Exception if cell image deletion fails
     */
    public void deleteImages() throws Exception {
        delete(Constants.TEST_CELL_ORG_NAME + Constants.FORWARD_SLASH + STOCK_IMAGE_NAME + Constants.COLON + VERSION);
        delete(Constants.TEST_CELL_ORG_NAME + Constants.FORWARD_SLASH + EMPLOYEE_IMAGE_NAME + Constants.COLON +
                VERSION);
        delete(Constants.TEST_CELL_ORG_NAME + Constants.FORWARD_SLASH + HR_IMAGE_NAME + Constants.COLON + VERSION);
    }
}
