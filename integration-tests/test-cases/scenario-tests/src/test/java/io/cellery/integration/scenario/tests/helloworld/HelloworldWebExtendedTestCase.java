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

import io.cellery.integration.scenario.tests.ObservabilityDashboard;
import io.cellery.integration.scenario.tests.models.Cell;
import io.cellery.integration.scenario.tests.models.SequenceDiagram;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This includes the test cases related to optional features of hello world web scenario such as observability and API
 * Manager. This test class should be used to test the complete setup and the basic setup with modifications.
 */
public class HelloworldWebExtendedTestCase extends HelloworldWebBaseTestCase {

    private static final String IMAGE_NAME = "hello-world-web";
    private static final String VERSION = "1.0.0";
    private static final String HELLO_WORLD_INSTANCE = "hello-world-inst";
    private ObservabilityDashboard observabilityDashboard;

    @BeforeClass
    public void setupExtended() {
        observabilityDashboard = new ObservabilityDashboard(webDriver, webDriverWait);
        observabilityDashboard.setWebCellUrl("http://hello-world.com/");
        List<String> componentList = new ArrayList<>(Arrays.asList("gateway", "hello"));
        Cell cell = new Cell(IMAGE_NAME, HELLO_WORLD_INSTANCE, componentList);
        observabilityDashboard.getCells().add(cell);

        SequenceDiagram diagram = new SequenceDiagram();
        diagram.getComponents().put(HELLO_WORLD_INSTANCE, 2);
    }

    @Test (description = "Validates the login flow of the dashboard", dependsOnMethods = "invoke")
    public void observabilityLogin() {
        observabilityDashboard.loginObservability();
    }

    @Test (description = "Validates the overview of the dashboard", dependsOnMethods = "observabilityLogin")
    public void overviewPage() {
        observabilityDashboard.overviewPage();
    }

    @Test (description = "Validates Cell instances and components of the dashboard", dependsOnMethods = "overviewPage")
    public void cellsPage() {
        observabilityDashboard.cellsPage();
    }

    @Test (description = "Validates tracing page of the dashboard", dependsOnMethods = "cellsPage")
    public void tracingPage() {
        observabilityDashboard.tracingPage();
    }

    @Test (description = "Validates logout functionality of the observability portal", dependsOnMethods = "tracingPage")
    public void observabilityLogout() {
        observabilityDashboard.logoutObservability();
    }

    @Override
    @Test(dependsOnMethods = "observabilityLogout")
    public void terminate() throws Exception {
        terminateCell(HELLO_WORLD_INSTANCE);
    }

    @AfterClass
    public void cleanupExtended() {
        try {
            observabilityDashboard.cleanupCelleryDashboard();
        } catch (Exception ignored) {

        }
    }
}
