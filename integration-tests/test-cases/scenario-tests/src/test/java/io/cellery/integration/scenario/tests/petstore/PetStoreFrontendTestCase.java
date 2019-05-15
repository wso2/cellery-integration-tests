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
package io.cellery.integration.scenario.tests.petstore;

import io.cellery.integration.scenario.tests.BaseTestCase;
import io.cellery.integration.scenario.tests.Constants;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static io.github.bonigarcia.wdm.DriverManagerType.CHROME;

/**
 * This includes the test cases related to hello world web scenario.
 */
public class PetStoreFrontendTestCase extends BaseTestCase {
    private static final String instanceName = "pet-fe-inst";
    private static final String dependentInstanceName = "pet-be-inst";
    private static final String imageName = "pet-fe-cell";
    private static final String version = "latest";
    private static final String link = "petStoreBackend:pet-be-inst";
    private WebDriver webDriver;

    @BeforeClass
    public void setup() {
        WebDriverManager.getInstance(CHROME).setup();
        webDriver = new ChromeDriver(new ChromeOptions().setHeadless(true));
    }

    @Test
    public void build() throws Exception {
        build("pet-fe.bal", Constants.TEST_CELL_ORG_NAME, imageName, version,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "pet-store", "pet-fe").toFile().getAbsolutePath());
    }

    @Test
    public void run() throws Exception {
        run(Constants.TEST_CELL_ORG_NAME, imageName, version, instanceName, link, true,
                600);
    }

    @Test
    public void invoke() {
        webDriver.get(Constants.DEFAULT_PET_STORE_URL);
        validateWebPage();
    }

    @Test
    public void terminate() throws Exception {
        terminateCell(instanceName);
        terminateCell(dependentInstanceName);
    }

    private void validateWebPage() {
        String searchHeader = webDriver.findElement(By.cssSelector("H1")).getText().toLowerCase();
        Assert.assertEquals(searchHeader, Constants.PET_STORE_WEB_CONTENT,
                "Web page is content is not as expected");
    }

    @AfterClass
    public void cleanup() {
        webDriver.close();
        try {
            terminateCell(instanceName);
            terminateCell(dependentInstanceName);
        } catch (Exception ignored) {
        }
    }
}
