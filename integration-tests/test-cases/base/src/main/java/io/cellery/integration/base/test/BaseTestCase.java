/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package io.cellery.integration.base.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Base Test Case Integration Tests.
 */
public class BaseTestCase {
    private static final String SUCCESSFUL_BUILD_MSG = "Successfully built cell image";
    private static final String SUCCESSFUL_RUN_MSG = "Successfully deployed cell image";

    private static final String INSTANCE_NAME_HEADING = "INSTANCE NAME";
    private static final String CELLERY_AUTOMATION_TESTS_ROOT_ENV = "CELLERY_AUTOMATION_TESTS_ROOT";
    private static final String READY_STATUS = " Ready";

    private static final String CELLERY_BUILD = "cellery build";
    private static final String CELLERY_RUN = "cellery run";
    private static final String CELLERY_STATUS = "cellery status";
    private static final String CELLERY_TERM = "cellery term";

    private static final String CELLERY_TEST_ROOT = System.getenv(CELLERY_AUTOMATION_TESTS_ROOT_ENV) +
            File.separator + "test-cases";


    protected void build(String cellFileName, String orgName, String imageName, String version, String executionDirPath)
            throws Exception {
        String cellImageName = getCellImageName(orgName, imageName, version);
        Process process = Runtime.getRuntime().exec(CELLERY_BUILD + " " + cellFileName + " "
                + cellImageName, null, new File(executionDirPath));
        String errorString = "Unable to build cell: " + cellFileName + " , with image name: " + cellImageName;
        readOutputResult(process, SUCCESSFUL_BUILD_MSG, errorString);
    }

    protected void run(String orgName, String imageName, String version, String instanceName, int timeoutSec)
            throws Exception {
        String cellImageName = getCellImageName(orgName, imageName, version);
        String command = CELLERY_RUN + " " + cellImageName + " -y";
        if (instanceName != null && !instanceName.isEmpty()) {
            command += " -n " + instanceName;
        }
        Process process = Runtime.getRuntime().exec(command);
        String result = readOutputResult(process, SUCCESSFUL_RUN_MSG, "Unable to run cell: "
                + cellImageName + " , with instance name: " + instanceName, timeoutSec);
        if (instanceName != null && !instanceName.isEmpty()) {
            int index = result.indexOf(INSTANCE_NAME_HEADING);
            String instancesResult = result.substring(index);
            if (!instancesResult.contains(instanceName + " ")) {
                throw new Exception("Cell instance is not started with the instance name specified : " + instanceName +
                        " , result output is: " + result);
            }
        }
    }

    protected void waitForCellReady(String cellInstanceName, long timeout, TimeUnit timeUnit,
                                    long pollIntervalInMilliSec) throws Exception {
        long endTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
        Exception lastException = null;
        while (System.currentTimeMillis() < endTime) {
            Process process = Runtime.getRuntime().exec(CELLERY_STATUS + " " + cellInstanceName);
            try {
                readOutputResult(process, READY_STATUS, "Error while reading the status of cell instance :"
                        + cellInstanceName);
                Thread.sleep(pollIntervalInMilliSec);
                return;
            } catch (Exception ex) {
                lastException = ex;
            }
        }
        if (lastException != null) {
            throw lastException;
        }
    }

    protected void terminateCell(String cellInstanceName) throws Exception {
        Process process = Runtime.getRuntime().exec(CELLERY_TERM + " " + cellInstanceName);
        readOutputResult(process, "",
                "Error while terminating the instance :" + cellInstanceName);
        process = Runtime.getRuntime().exec(CELLERY_STATUS + " " + cellInstanceName);
        try {
            String errorMessage = "Cell instance is not terminated properly:" + cellInstanceName;
            readOutputResult(process, "Cannot find cell", errorMessage);
            throw new Exception(errorMessage);
        } catch (Exception ex) {
            if (!ex.getMessage().contains("(NotFound): cells.mesh.cellery.io \"" + cellInstanceName + "\"")) {
                throw ex;
            }
        }
    }

    private String readOutputResult(Process process, String successOutput, String errorMessage) throws Exception {
        return readOutputResult(process, successOutput, errorMessage, 10);
    }

    private String readOutputResult(Process process, String successOutput, String errorMessage, int timeout)
            throws Exception {
        try (BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream(),
                Charset.defaultCharset().name()));
             BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream(),
                     Charset.defaultCharset().name()))) {
            boolean terminated = process.waitFor(timeout, TimeUnit.SECONDS);
            int exitCode = process.exitValue();
            if (terminated && exitCode == 0) {
                String output = stdOutput.lines().map(String::valueOf).collect(Collectors.joining());
                if ((!successOutput.isEmpty() && !output.contains(successOutput)) ||
                        (successOutput.isEmpty() && !output.isEmpty())) {
                    throw new Exception("Expected output '" + successOutput + "' is missing in the build output: "
                            + output);
                }
                return output;
            } else {
                String output = stdError.lines().map(String::valueOf).collect(Collectors.joining());
                if (output == null || output.isEmpty()) {
                    output = stdOutput.lines().map(String::valueOf).collect(Collectors.joining());
                }
                throw new Exception(errorMessage + " ." + output);
            }
        }
    }

    protected String getCellImageName(String orgName, String imageName, String version) {
        return orgName + "/" + imageName + ":" + version;
    }

    protected String getCelleryTestRoot() {
        return CELLERY_TEST_ROOT;
    }

}
