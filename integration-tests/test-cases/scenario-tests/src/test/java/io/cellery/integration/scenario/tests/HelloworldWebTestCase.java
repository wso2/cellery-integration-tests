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

import io.cellery.integration.scenario.tests.models.Cell;
import io.cellery.integration.scenario.tests.models.SequenceDiagram;
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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.bonigarcia.wdm.DriverManagerType.CHROME;

/**
 * This includes the test cases related to hello world web scenario.
 */
public class HelloworldWebTestCase extends BaseTestCase {

    private static final String IMAGE_NAME = "hello-world-web";
    private static final String VERSION = "1.0.0";
    private static final String HELLO_WORLD_INSTANCE = "hello-world-inst";
    private WebDriver webDriver;
    private ObservabilityDashboard observabilityDashboard;

    @BeforeClass
    public void setup() {
        WebDriverManager.getInstance(CHROME).setup();
        webDriver = new ChromeDriver(new ChromeOptions().setHeadless(true));
        observabilityDashboard = new ObservabilityDashboard(webDriver, new WebDriverWait(webDriver, 120));
        observabilityDashboard.setWebCellUrl("http://hello-world.com/");
        List<String> componentList = new ArrayList<>(Arrays.asList("gateway", "hello"));
        Cell cell = new Cell(IMAGE_NAME, HELLO_WORLD_INSTANCE, componentList);
        observabilityDashboard.getCells().add(cell);

        SequenceDiagram diagram = new SequenceDiagram();
        diagram.getSequenceDiagramCells().put(HELLO_WORLD_INSTANCE, 2);
    }

    @Test
    public void build() throws Exception {
        build("web.bal", Constants.TEST_CELL_ORG_NAME, IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "hello-web").toFile().getAbsolutePath());
    }

    @Test
    public void run() throws Exception {
        run(Constants.TEST_CELL_ORG_NAME, IMAGE_NAME, VERSION, HELLO_WORLD_INSTANCE, 600);
    }

    @Test
    public void invoke() {
        webDriver.get(Constants.DEFAULT_HELLO_WORLD_URL);
        validateWebPage();
    }

    @Test (description = "Validates the login flow of the dashboard")
    public void observabilityLogin() {
        observabilityDashboard.loginObservability();
    }

    @Test (description = "Validates the overview of the dashboard")
    public void overviewPage() {
        observabilityDashboard.overviewPage();
    }

    @Test (description = "Validates Cell instances and components of the dashboard")
    public void cellsPage() {
        observabilityDashboard.cellsPage();
    }

    @Test (description = "Validates tracing page of the dashboard")
    public void tracingPage() {
        observabilityDashboard.tracingPage();
    }

    @Test (description = "Validates logout functionality of the observability portal")
    public void observabilityLogout() {
        observabilityDashboard.logoutObservability();
    }

    @Test
    public void terminate() throws Exception {
        terminateCell(HELLO_WORLD_INSTANCE);
    }

    @Test(expectedExceptions = Exception.class)
    public void repeatTerminate() throws Exception {
        terminateCell(HELLO_WORLD_INSTANCE);
    }

    @Test
    public void deleteImage() throws Exception {
        delete(Constants.TEST_CELL_ORG_NAME + "/" + IMAGE_NAME + ":" + VERSION);
    }

    private void validateWebPage() {
        String searchHeader = webDriver.findElement(By.cssSelector("H1")).getText().toLowerCase();
        Assert.assertEquals(searchHeader, Constants.HELLO_WORLD_WEB_CONTENT, "Web page is content is not as expected");
    }

    @AfterClass
    public void cleanup() throws InterruptedException, IOException {
        webDriver.close();
        try {
            terminateCell(HELLO_WORLD_INSTANCE);
        } catch (Exception ignored) {
        }
        observabilityDashboard.cleanupCelleryDashboard();
    }
}
