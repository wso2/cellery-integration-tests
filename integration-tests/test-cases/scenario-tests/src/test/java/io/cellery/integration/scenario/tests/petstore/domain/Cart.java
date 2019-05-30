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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * This includes functions related to pet-store scenario.
 */
public class Cart {
    private WebDriver webDriver;
    private String numberOfItemsXpath = "//*[@id=\"app\"]/div/header/div/div/div/span/span";
    private String addToCartButtonXpath = "/html/body/div[2]/div[2]/div/div[3]/button[2]";
    private String cartButtonXpath = "//*[@id=\"app\"]/div/header/div/div/div/span/button";
    private String checkoutButtonXpath = "//*[@id=\"app\"]/div/main/div/div/div[2]/button";
    private WebDriverWait wait;

    public Cart(WebDriver webDriver) {
        this.webDriver = webDriver;
        wait = new WebDriverWait(webDriver, 120);
    }

    /**
     * Adds a pet store item to the cart.
     * @param petAccessory
     */
    public void addToCart(PetAccessory petAccessory) {
        WebElement amountInputFiled;
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(petAccessory.getXpath())));
        webDriver.findElement(By.xpath(petAccessory.getXpath())).click();
        amountInputFiled = webDriver.findElement(By.id("amount"));
        amountInputFiled.sendKeys(Keys.BACK_SPACE);
        amountInputFiled.sendKeys(Integer.toString(petAccessory.getAmount()));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(addToCartButtonXpath)));
        webDriver.findElement(By.xpath(addToCartButtonXpath)).click();
    }

    /**
     * Checks out cart.
     */
    public void checkout() {
        webDriver.findElement(By.xpath(cartButtonXpath)).click();
        webDriver.findElement(By.xpath(checkoutButtonXpath)).click();
    }

    /**
     * Get the number of items in the cart.
     * @return the number of items.
     */
    public String getNumberOfItems() {
        return webDriver.findElement(By.xpath(numberOfItemsXpath)).getText();
    }
}
