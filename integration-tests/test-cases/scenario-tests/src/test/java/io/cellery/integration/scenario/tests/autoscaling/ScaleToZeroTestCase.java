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

import io.cellery.integration.scenario.tests.EmployeePortalTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.util.concurrent.TimeUnit;

/**
 * This test includes the test cases related to scale to zero.
 */
public class ScaleToZeroTestCase extends EmployeePortalTestCase {

    private static final String KUBECTL_GET_DEPLOYEMENT_EMPLOYEE_SERVICE = "kubectl get deployment employee-inst" +
            "--employee-service-rev-deployment";
    private static final String EMPLOYEE_POD_RUNNING_OUTPUT = "employee-inst--employee-service-rev-deployment   1/1";
    private static final String EMPLOYEE_POD_TERMINATED_OUTPUT = "employee-inst--employee-service-rev-deployment   0/0";
    private static final String CELLERY_MODIFY_SCALE_TO_ZERO = "cellery setup modify --scale-to-zero=";

    @BeforeClass
    public void setup() {
        this.employeeBalFile = "employee-zero.bal";
        this.applicationName = "EmployeePortalZero";
    }

    @Test(description = "Tests the runtime modification of enabling scale-to-zero")
    public void enableScaleToZero() throws Exception {
        Process process = Runtime.getRuntime().exec(CELLERY_MODIFY_SCALE_TO_ZERO + "enable");
        // Check if knative is successfully enabled
        String expectedOut = "Runtime status (Knative Serving)...OK";
        String errorString = "Failed to enable zero scaling in the runtime";
        readOutputResult(process, expectedOut, errorString);
    }

    @Test(description = "Tests the termination of employee pod after receiving no requests")
    public void checkPodTermination() throws Exception {
        // Wait 3 minutes for employee pod to be terminated
        TimeUnit.SECONDS.sleep(180);
        Process process = Runtime.getRuntime().exec(KUBECTL_GET_DEPLOYEMENT_EMPLOYEE_SERVICE);
        String errorString = "employee-service pod is not terminated";
        readOutputResult(process, EMPLOYEE_POD_TERMINATED_OUTPUT, errorString);
    }

    @Test(description = "Tests the starting of a pod after receiving a request")
    public void checkPodStart() throws Exception {
        Process process = Runtime.getRuntime().exec(KUBECTL_GET_DEPLOYEMENT_EMPLOYEE_SERVICE);
        String errorString = "employee-service pod is not running";
        readOutputResult(process, EMPLOYEE_POD_RUNNING_OUTPUT, errorString);
    }

    @Test(description = "Tests the runtime modification of disabling scale-to-zero")
    public void disableScaleToZero() throws Exception {
        Process process = Runtime.getRuntime().exec(CELLERY_MODIFY_SCALE_TO_ZERO + "disable");
        // Check if knative is successfully disabled
        String expectedOut = "Runtime status (Cellery)...OK";
        String errorString = "Failed to disable zero scaling in the runtime";
        readOutputResult(process, expectedOut, errorString);
    }
}
