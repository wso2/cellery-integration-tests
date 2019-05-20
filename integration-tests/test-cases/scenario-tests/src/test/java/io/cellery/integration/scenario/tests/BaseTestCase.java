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

package io.cellery.integration.scenario.tests;

import org.testng.Assert;

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

    private static final String CELLERY_BUILD = "cellery build";
    private static final String CELLERY_RUN = "cellery run";
    private static final String CELLERY_STATUS = "cellery status";
    private static final String CELLERY_TERM = "cellery term";

    private static final String TEST_CASES_DIR = "test-cases";
    private static final String CELLS_DIR = "cells";
    private static final String SCENARIO_TEST_DIR = "scenario-tests";
    private static final String TARGET = "target";

    protected static final String CELLERY_SCENARIO_TEST_ROOT = System.getenv(CELLERY_AUTOMATION_TESTS_ROOT_ENV) +
            File.separator + TEST_CASES_DIR + File.separator + CELLS_DIR;
    protected static final String CELLERY_ROOT_TARGET = System.getenv(CELLERY_AUTOMATION_TESTS_ROOT_ENV)
            + File.separator + TEST_CASES_DIR + File.separator + SCENARIO_TEST_DIR + File.separator + TARGET;

    protected void build(String cellFileName, String orgName, String imageName, String version, String executionDirPath)
            throws Exception {
        String cellImageName = getCellImageName(orgName, imageName, version);
        Process process = Runtime.getRuntime().exec(CELLERY_BUILD + " " + cellFileName + " "
                + cellImageName, null, new File(executionDirPath));
        String errorString = "Unable to build cell: " + cellFileName + " , with image name: " + cellImageName;
        readOutputResult(process, SUCCESSFUL_BUILD_MSG, errorString);
    }

    protected String run(String orgName, String imageName, String version, String instanceName, int timeoutSec)
            throws Exception {
        String cellImageName = getCellImageName(orgName, imageName, version);
        String command = CELLERY_RUN + " " + cellImageName + " -y";
        if (instanceName != null && !instanceName.isEmpty()) {
            command += " -n " + instanceName;
        }
        Process process = Runtime.getRuntime().exec(command);
        String result = readOutputResult(process, SUCCESSFUL_RUN_MSG, "Unable to run cell: "
                + cellImageName + " , with instance name: " + instanceName, timeoutSec);
        String instancesResult = result.substring(result.indexOf(INSTANCE_NAME_HEADING));
        if (instanceName != null && !instanceName.isEmpty()) {
            if (!instancesResult.contains(instanceName + " ")) {
                throw new Exception("Cell instance is not started with the instance name specified : " + instanceName +
                        " , result output is: " + result);
            }
        } else {
            int index = instancesResult.indexOf(getInstanceNamePrefix(imageName, version));
            instanceName = instancesResult.substring(index).split(" ")[0];
        }
        return instanceName;
    }

    protected String run(String orgName, String imageName, String version, String instanceName,
                                       String link, boolean startDependencies, int timeoutSec)
            throws Exception {
        String cellImageName = getCellImageName(orgName, imageName, version);
        String command = CELLERY_RUN + " " + cellImageName + " -y";
        if (instanceName != null && !instanceName.isEmpty()) {
            command += " -n " + instanceName;
        }
        if (link != null && !link.isEmpty()) {
            command += " -l " + link;
        }
        if (startDependencies) {
            command += " -d";
        }
        Process process = Runtime.getRuntime().exec(command);
        String result = readOutputResult(process, SUCCESSFUL_RUN_MSG, "Unable to run cell: "
                + cellImageName + " , with instance name: " + instanceName, timeoutSec);
        String instancesResult = result.substring(result.indexOf(INSTANCE_NAME_HEADING));
        if (instanceName != null && !instanceName.isEmpty()) {
            if (!instancesResult.contains(instanceName + " ")) {
                throw new Exception("Cell instance is not started with the instance name specified : " + instanceName +
                        " , result output is: " + result);
            }
        } else {
            int index = instancesResult.indexOf(getInstanceNamePrefix(imageName, version));
            instanceName = instancesResult.substring(index).split(" ")[0];
        }
        return instanceName;
    }

    protected void terminateCell(String cellInstanceName) throws Exception {
        Process process = Runtime.getRuntime().exec(CELLERY_TERM + " " + cellInstanceName);
        readOutputResult(process, "",
                "Error while terminating the instance :" + cellInstanceName);
        process = Runtime.getRuntime().exec(CELLERY_STATUS + " " + cellInstanceName);
        String expectedOutput = "cannot find cell " + cellInstanceName;
        try {
            String errorMessage = "Cell instance is not terminated properly:" + cellInstanceName;
            readOutputResult(process, expectedOutput, errorMessage);
        } catch (Exception ex) {
            if (!ex.getMessage().contains(expectedOutput)) {
                throw ex;
            }
        }
    }

    protected String readOutputResult(Process process, String successOutput, String errorMessage) throws Exception {
        return readOutputResult(process, successOutput, errorMessage, 600);
    }

    protected String readOutputResult(Process process, String successOutput, String errorMessage, int timeout)
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

    private String getInstanceNamePrefix(String image, String version) {
        return image + "-" + version;
    }

    protected void validateWebPage(String expected, String actual, String error) {
        Assert.assertEquals(expected, actual, error);
    }
}
