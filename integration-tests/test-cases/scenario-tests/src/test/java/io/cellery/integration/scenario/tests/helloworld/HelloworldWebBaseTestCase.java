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
package io.cellery.integration.scenario.tests.helloworld;

import io.cellery.integration.scenario.tests.BaseTestCase;
import io.cellery.integration.scenario.tests.Constants;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static io.github.bonigarcia.wdm.DriverManagerType.CHROME;

/**
 * This class includes the test cases related to basic functionality of hello world web scenario.
 * This test class should be used for testing all setups (basic, complete, modified).
 */
public class HelloworldWebBaseTestCase extends BaseTestCase {

    private static final String IMAGE_NAME = "hello-world-web";
    private static final String VERSION = "1.0.0";
    private static final String HELLO_WORLD_INSTANCE = "hello-world-inst";
    protected WebDriver webDriver;
    protected WebDriverWait webDriverWait;

    @BeforeClass
    public void setup() {
        WebDriverManager.getInstance(CHROME).setup();
        webDriver = new ChromeDriver(new ChromeOptions().setHeadless(true));
        webDriverWait = new WebDriverWait(webDriver, 120);
    }

    @Test
    public void buildHelloWorld() throws Exception {
        build("web.bal", Constants.TEST_CELL_ORG_NAME, IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "hello-web").toFile().getAbsolutePath());
    }

    @Test(dependsOnMethods = "buildHelloWorld")
    public void runHelloWorld() throws Exception {
        run(Constants.TEST_CELL_ORG_NAME, IMAGE_NAME, VERSION, HELLO_WORLD_INSTANCE, 600);
    }

    @Test(dependsOnMethods = "runHelloWorld")
    public void invoke() {
        webDriver.get(Constants.DEFAULT_HELLO_WORLD_URL);
        validateWebPage();
    }

    @Test(dependsOnMethods = "runHelloWorld")
    public void deleteImage() throws Exception {
        delete(Constants.TEST_CELL_ORG_NAME + Constants.FORWARD_SLASH + IMAGE_NAME + Constants.COLON + VERSION);
    }

    @Test(dependsOnMethods = "invoke")
    public void terminate() throws Exception {
        terminateCell(HELLO_WORLD_INSTANCE);
    }

    private void validateWebPage() {
        String searchHeader = webDriver.findElement(By.cssSelector("H1")).getText().toLowerCase();
        Assert.assertEquals(searchHeader, Constants.HELLO_WORLD_WEB_CONTENT, "Web page is content is not as expected");
    }

    @AfterClass
    public void cleanup() {
        webDriver.close();
        try {
            terminateCell(HELLO_WORLD_INSTANCE);
        } catch (Exception ignored) {

        }
    }
}
