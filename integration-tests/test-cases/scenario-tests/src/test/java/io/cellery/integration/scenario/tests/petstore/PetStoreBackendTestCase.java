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
import org.testng.annotations.Test;

import java.nio.file.Paths;
/**
 * This includes the test cases related to hello world web scenario.
 */
public class PetStoreBackendTestCase extends BaseTestCase {
    private static final String instanceName = "pet-be-inst";
    private static final String imageName = "pet-be-cell";
    private static final String version = "latest";

    @Test
    public void build() throws Exception {
        build("pet-be.bal", Constants.TEST_CELL_ORG_NAME, imageName, version,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "pet-store", "pet-be").toFile().getAbsolutePath());
    }

    @Test
    public void run() throws Exception {
        run(Constants.TEST_CELL_ORG_NAME, imageName, version, instanceName, 180);
    }
}
