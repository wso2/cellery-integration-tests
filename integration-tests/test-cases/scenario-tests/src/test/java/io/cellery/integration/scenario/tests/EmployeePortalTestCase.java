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

import io.cellery.integration.scenario.tests.employeeportal.EmployeePortal;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This includes the test cases related to employee portal.
 */
public class EmployeePortalTestCase extends BaseTestCase {

    private EmployeePortal employeePortal = new EmployeePortal();

    @BeforeClass
    public void setup() {
        employeePortal.setEmployeeBalFile("employee.bal");
        employeePortal.setApplicationName("EmployeePortal");
    }

    @Test(description = "Tests the building of all related cell images.")
    public void buildEmpPortal() throws Exception {
        employeePortal.build();
    }

    @Test(description = "Tests the running of all the related cell instances.", dependsOnMethods = "buildEmpPortal")
    public void runEmpPortal() throws Exception {
        employeePortal.run();
    }

    @Test(description = "Sends http requests and asserts response with expected employee data",
            dependsOnMethods = "runEmpPortal")
    public void validateData() throws Exception {
        employeePortal.sendRequest();
    }

    @Test(description = "This tests the termination of hr, employee and stock cell instances",
            dependsOnMethods = "validateData")
    public void terminate() throws Exception {
        employeePortal.terminate();
    }

    @Test(description = "This tests the deletion hr, employee and stock cell images", dependsOnMethods = "terminate")
    public void deleteImages() throws Exception {
        employeePortal.deleteImages();
    }

    @AfterClass
    public void cleanup() {
        try {
            employeePortal.terminate();
            employeePortal.deleteImages();
        } catch (Exception ignored) {
        }
    }
}
