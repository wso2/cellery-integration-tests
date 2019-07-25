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
 * This includes the test cases related to hello world web scenario.
 */
public class PetStoreExtendedTestCase extends PetStoreBaseTestCase {

    private static final String BACKEND_INSTANCE_NAME = "pet-be-inst";
    private static final String BACKEND_IMAGE_NAME = "petbe";
    private static final String FRONTEND_INSTANCE_NAME = "pet-fe-inst";
    private static final String FRONTEND_IMAGE_NAME = "petfe";
    private ObservabilityDashboard observabilityDashboard;

    @BeforeClass
    public void setup() {
        super.setup();
        observabilityDashboard = new ObservabilityDashboard(webDriver, webDriverWait);
        observabilityDashboard.setWebCellUrl("http://pet-store.com/");
        List<String> backendComponentList = new ArrayList<>(Arrays.asList("controller", "catalog", "gateway",
                "customers", "orders"));
        Cell backendCell = new Cell(BACKEND_IMAGE_NAME, BACKEND_INSTANCE_NAME, backendComponentList);
        observabilityDashboard.getCells().add(backendCell);

        List<String> frontendComponentList = new ArrayList<>(Arrays.asList("gateway", "portal"));
        Cell frontendCell = new Cell(FRONTEND_IMAGE_NAME, FRONTEND_INSTANCE_NAME, frontendComponentList);
        observabilityDashboard.getCells().add(frontendCell);

        SequenceDiagram diagram = new SequenceDiagram();
        diagram.getComponents().put(FRONTEND_INSTANCE_NAME, 2);
        diagram.getComponents().put(BACKEND_INSTANCE_NAME, 3);

        diagram.getCalls().put("gateway", 5);
    }

    @Test(description = "Validates the login flow of the dashboard", dependsOnMethods = "signOutBob")
    public void observabilityLogin() {
        observabilityDashboard.loginObservability();
    }

    @Test(description = "Validates the overview of the dashboard", dependsOnMethods = "observabilityLogin")
    public void overviewPage() {
        observabilityDashboard.overviewPage();
    }

    @Test(description = "Validates Cell instances and components of the dashboard", dependsOnMethods = "overviewPage")
    public void cellsPage() {
        observabilityDashboard.cellsPage();
    }

    @Test(description = "Validates tracing page of the dashboard", dependsOnMethods = "cellsPage")
    public void tracingPage() {
        observabilityDashboard.tracingPage();
    }

    @Test(description = "Validates logout functionality of the observability portal", dependsOnMethods = "tracingPage")
    public void observabilityLogout() {
        observabilityDashboard.logoutObservability();
    }

    @Override
    @Test(description = "This tests the termination of pet-store backend and frontend cells",
            dependsOnMethods = "observabilityLogout")
    public void terminate() throws Exception {
        terminateCell(BACKEND_INSTANCE_NAME);
        terminateCell(FRONTEND_INSTANCE_NAME);
    }

    @AfterClass
    public void cleanup() {
        super.cleanup();
        try {
            observabilityDashboard.cleanupCelleryDashboard();
        } catch (Exception ignored) {

        }
    }
}
