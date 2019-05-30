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
package io.cellery.integration.scenario.tests.petstore.domain;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

/**
 * This includes functions related to pet-store scenario.
 */
public class Order {
    private WebDriver webDriver;
    private String orderValueXpath = "//*[@id=\"app\"]/div/main/div/div[2]/div/div[1]/div[1]/div[1]/p[3]";
    private String checkOrdersButtonXpath = "//*[@id=\"app\"]/div/main/div/div[1]/div/div/div/button";
    private String ordersHeaderXpath = "//*[@id=\"app\"]/div/main/div/div[1]/h1";
    private String noOrdersXpath = "//*[@id=\"app\"]/div/main/div/div[2]/p";
    private WebDriverWait wait;
    public Order(WebDriver webDriver) {
        this.webDriver = webDriver;
        wait = new WebDriverWait(webDriver, 120);
    }

    /**
     * Get the value of order.
     * @return order value.
     * @throws InterruptedException
     */
    public String getOrderValue() throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(checkOrdersButtonXpath)));
        webDriver.findElement(By.xpath(checkOrdersButtonXpath)).click();
        // Putting an explicit sleep of 10 seconds because test is failing in jenkins server.
        TimeUnit.SECONDS.sleep(10);
        Boolean orderExists = webDriver.findElements(By.xpath(orderValueXpath)).size() > 0;
        if (orderExists) {
            return webDriver.findElement(By.xpath(orderValueXpath)).getText();
        }
        return webDriver.findElement(By.xpath(noOrdersXpath)).getText();
    }
}
