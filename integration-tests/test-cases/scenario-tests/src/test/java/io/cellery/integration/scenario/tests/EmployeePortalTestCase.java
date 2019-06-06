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

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.nio.file.Paths;

/**
 * This includes the test cases related to employee portal.
 */
public class EmployeePortalTestCase extends BaseTestCase {
    private static final String STOCK_INSTANCE_NAME = "stock-inst";
    private static final String STOCK_IMAGE_NAME = "stock-cell";
    private static final String EMPLOYEE_INSTANCE_NAME = "employee-inst";
    private static final String EMPLOYEE_IMAGE_NAME = "employee-cell";
    private static final String HR_INSTANCE_NAME = "hr-inst";
    private static final String HR_IMAGE_NAME = "hr-cell";
    private static final String VERSION = "1.0.0";
    private static final String LINK_STOCK = "stockCellDep:stock-inst";
    private static final String LINK_EMPLOYEE = "employeeCellDep:employee-inst";

    @Test
    public void buildStock() throws Exception {
        build("stocks.bal", Constants.TEST_CELL_ORG_NAME, STOCK_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "stock").toFile().getAbsolutePath());
    }

    @Test
    public void runStock() throws Exception {
        run(Constants.TEST_CELL_ORG_NAME, STOCK_IMAGE_NAME, VERSION, STOCK_INSTANCE_NAME, 120);
    }

    @Test
    public void buildEmployee() throws Exception {
        build("employee.bal", Constants.TEST_CELL_ORG_NAME, EMPLOYEE_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "employee").toFile().getAbsolutePath());
    }

    @Test
    public void runEmployee() throws Exception {
        run(Constants.TEST_CELL_ORG_NAME, EMPLOYEE_IMAGE_NAME, VERSION, EMPLOYEE_INSTANCE_NAME, 120);
    }

    @Test
    public void buildHr() throws Exception {
        build("hr.bal", Constants.TEST_CELL_ORG_NAME, HR_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "employee-portal", "hr").toFile().getAbsolutePath());
    }

    @Test
    public void runHr() throws Exception {
        String[] links = new String[]{LINK_STOCK, LINK_EMPLOYEE};
        run(Constants.TEST_CELL_ORG_NAME, HR_IMAGE_NAME, VERSION, HR_INSTANCE_NAME, links, false,
                120);
    }

    @Test
    public void deleteImages() throws Exception {
        delete(Constants.TEST_CELL_ORG_NAME + "/" + HR_IMAGE_NAME + ":" + VERSION);
        delete(Constants.TEST_CELL_ORG_NAME + "/" + EMPLOYEE_IMAGE_NAME + ":" + VERSION);
        delete(Constants.TEST_CELL_ORG_NAME + "/" + STOCK_IMAGE_NAME + ":" + VERSION);
    }

    @Test
    public void terminate() throws Exception {
        terminateCell(HR_INSTANCE_NAME);
        terminateCell(EMPLOYEE_INSTANCE_NAME);
        terminateCell(STOCK_INSTANCE_NAME);
    }

    @AfterClass
    public void cleanup() {
        try {
            terminateCell(HR_INSTANCE_NAME);
            terminateCell(EMPLOYEE_INSTANCE_NAME);
            terminateCell(STOCK_INSTANCE_NAME);
        } catch (Exception ignored) {
        }
    }
}
