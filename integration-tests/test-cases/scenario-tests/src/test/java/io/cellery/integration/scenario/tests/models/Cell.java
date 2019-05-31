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

package io.cellery.integration.scenario.tests.models;

import java.util.List;
import java.util.Objects;

public class Cell {

    private String cellName;
    private String instanceName;
    private List<String> components;

    public Cell(String cellName, String instanceName, List<String> components) {
        this.cellName = cellName;
        this.instanceName = instanceName;
        this.components = components;
    }

    public String getCellName() {
        return cellName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public List<String> getComponents() {
        return components;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cell cell = (Cell) o;
        return cellName.equals(cell.cellName) &&
                instanceName.equals(cell.instanceName) &&
                components.equals(cell.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellName, instanceName, components);
    }

    @Override
    public String toString() {
        return "Cell{" +
                "cellName='" + cellName + '\'' +
                ", instanceName='" + instanceName + '\'' +
                ", components=" + components +
                '}';
    }
}
