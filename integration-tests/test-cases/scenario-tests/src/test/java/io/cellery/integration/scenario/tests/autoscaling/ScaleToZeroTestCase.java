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
package io.cellery.integration.scenario.tests.autoscaling;

import io.cellery.integration.scenario.tests.BaseTestCase;
import io.cellery.integration.scenario.tests.employeeportal.EmployeePortal;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * This test includes the test cases related to scale to zero.
 */
public class ScaleToZeroTestCase extends BaseTestCase {

    private static final String KUBECTL_GET_DEPLOYEMENT_EMPLOYEE_SERVICE = "kubectl get deployment employee-inst" +
            "--employee-service-deployment";
    private static final String CELLERY_MODIFY_SCALE_TO_ZERO = "cellery setup modify --scale-to-zero=";
    private String employeePodRunningOutput = "employee-inst--employee-service-deployment   1/1";
    private String employeePodTerminatedOutput = "employee-inst--employee-service-deployment   0/0";

    private EmployeePortal employeePortal = new EmployeePortal();

    @BeforeClass
    public void setup() {
        employeePortal.setEmployeeBalFile("employee-zero.bal");
        employeePortal.setApplicationName("EmployeePortalZero");
        if (Boolean.parseBoolean(System.getenv("IS_GCP"))) {
            employeePodRunningOutput = "employee-inst--employee-service-deployment   1";
            employeePodTerminatedOutput = "employee-inst--employee-service-deployment   0";
        }
    }

    @Test(description = "Tests the runtime modification of enabling scale-to-zero")
    public void enableScaleToZero() throws Exception {
        Process process = Runtime.getRuntime().exec(CELLERY_MODIFY_SCALE_TO_ZERO + "enable");
        // Check if knative is successfully enabled
        String expectedOut = "Runtime status (Knative Serving)...OK";
        String errorString = "Failed to enable zero scaling in the runtime";
        readOutputResult(process, expectedOut, errorString);
    }

    @Test(description = "Tests the building of all related cell images.", dependsOnMethods = "enableScaleToZero")
    public void buildEmpPortal() throws Exception {
        employeePortal.build();
    }

    @Test(description = "Tests the running of all the related cell instances.", dependsOnMethods = "buildEmpPortal")
    public void runEmpPortal() throws Exception {
        employeePortal.run();
    }

    @Test(description = "Tests the termination of employee pod after receiving no requests",
            dependsOnMethods = "runEmpPortal")
    public void checkPodTermination() throws Exception {
        // Wait 3 minutes for employee pod to be terminated
        TimeUnit.SECONDS.sleep(180);
        Process process = Runtime.getRuntime().exec(KUBECTL_GET_DEPLOYEMENT_EMPLOYEE_SERVICE);
        String errorString = "employee-service pod is not terminated";
        readOutputResult(process, employeePodTerminatedOutput, errorString);
    }

    @Test(description = "Sends http requests and asserts response with expected employee data",
            dependsOnMethods = "checkPodTermination")
    public void validateData() throws Exception {
        employeePortal.sendRequest();
    }

    @Test(description = "Tests the starting of a pod after receiving a request",
            dependsOnMethods = "validateData")
    public void checkPodStart() throws Exception {
        Process process = Runtime.getRuntime().exec(KUBECTL_GET_DEPLOYEMENT_EMPLOYEE_SERVICE);
        String errorString = "employee-service pod is not running";
        readOutputResult(process, employeePodRunningOutput, errorString);
    }

    @Test(description = "This tests the termination of hr, employee and stock cell instances",
            dependsOnMethods = "checkPodStart")
    public void terminate() throws Exception {
        employeePortal.terminate();
    }

    @Test(description = "This tests the deletion hr, employee and stock cell images", dependsOnMethods = "terminate")
    public void deleteImages() throws Exception {
        employeePortal.deleteImages();
    }

    @Test(description = "Tests the runtime modification of disabling scale-to-zero", dependsOnMethods = "deleteImages")
    public void disableScaleToZero() throws Exception {
        Process process = Runtime.getRuntime().exec(CELLERY_MODIFY_SCALE_TO_ZERO + "disable");
        // Check if knative is successfully disabled
        String expectedOut = "Runtime status (Cellery)...OK";
        String errorString = "Failed to disable zero scaling in the runtime";
        readOutputResult(process, expectedOut, errorString);
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
