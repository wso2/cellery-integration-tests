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
package io.cellery.integration.scenario.tests.cli;

import io.cellery.integration.scenario.tests.BaseTestCase;
import io.cellery.integration.scenario.tests.Constants;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Paths;

/**
 * This test includes the test cases related to the CLI and images.
 */
public class ImageBasedTestCase extends BaseTestCase {
    private final String orgName = "wso2cellery";
    private final String imageName = "hello-world-cell";
    private final String employeeImage = "employee-test-cell";
    private final String employeeVersion = "latest";

    @Test
    public void describeImage() throws Exception {
        String cellImageName = getCellImageName(orgName, imageName, Constants.SAMPLE_CELLS_VERSION);
        Process process = Runtime.getRuntime().exec("cellery describe " + cellImageName);
        String errorString = "Unable to describe cell image: " + cellImageName;
        String expectedOut = "name: " + imageName;
        readOutputResult(process, expectedOut, errorString);
    }

    @Test(expectedExceptions = Exception.class)
    public void describeNonExistingImage() throws Exception {
        String cellImageName = "wso2/test-foo:1.2.3";
        Process process = Runtime.getRuntime().exec("cellery describe " + cellImageName);
        String errorString = "Unable to describe non existing cell: " + cellImageName;
        readOutputResult(process, "", errorString);
    }

    @Test
    public void extractNonResourceImage() throws Exception {
        String cellImageName = getCellImageName(orgName, imageName, Constants.SAMPLE_CELLS_VERSION);
        Process process = Runtime.getRuntime().exec("cellery extract-resources " + cellImageName);
        String errorString = "Unable to extract non resource cell image: " + cellImageName;
        String expectedOut = "No resources available in " + cellImageName;
        readOutputResult(process, expectedOut, errorString);
    }

    @Test
    public void extractNonExistingImage() throws Exception {
        String cellImageName = getCellImageName(orgName, "test-1234", Constants.SAMPLE_CELLS_VERSION);
        Process process = Runtime.getRuntime().exec("cellery extract-resources " + cellImageName);
        try {
            readOutputResult(process, "", "");
        } catch (Exception ex) {
            if (!ex.getMessage().contains("Failed to extract resources for image " + cellImageName) ||
                    !ex.getMessage().contains("Image not Found")) {
                throw ex;
            }
        }
    }

    @Test
    public void buildResourceImage() throws Exception {
        build("employee.bal", Constants.TEST_CELL_ORG_NAME, employeeImage, employeeVersion,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal",
                        "employee").toFile().getAbsolutePath());
    }

    @Test
    public void extractResourceImage() throws Exception {
        extractImage(null);
    }

    @Test
    public void extractResourceImageWithProvidedOutput() throws Exception {
        extractImage(CELLERY_ROOT_TARGET);
        File file = new File(CELLERY_ROOT_TARGET + File.separator + "employee.swagger.json");
        Assert.assertTrue(file.exists(), "The resource wasn't extracted in the specified directory");
    }

    private void extractImage(String outputPath) throws Exception {
        String cellImageName = getCellImageName(Constants.TEST_CELL_ORG_NAME, employeeImage,
                employeeVersion);
        String command = "cellery extract-resources " + cellImageName;
        if (outputPath != null) {
            File file = new File(outputPath);
            file.mkdirs();
            command = command + " -o " + file.getAbsolutePath();
        }
        Process process = Runtime.getRuntime().exec(command);
        String errorString = "Unable to extract non resource cell image: " + cellImageName;
        //TODO: there is a typo in the success message, CLI message should be fixed. Then, this should be updated
        String expectedOut = "Successfully extracred cell image resources: " + cellImageName;
        readOutputResult(process, expectedOut, errorString);
    }

}
