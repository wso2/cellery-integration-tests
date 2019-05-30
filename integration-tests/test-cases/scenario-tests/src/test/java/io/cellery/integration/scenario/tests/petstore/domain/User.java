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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

/**
 * This includes functions related to pet-store scenario.
 */
public class User {
    private String firstName;
    private String lastName;
    private String address;
    private String userName;
    private String password;
    private WebDriver webDriver;
    private WebDriverWait wait;
    private String petStoreSignInButtonXpath = "//*[@id=\"app\"]/div/header/div/button";
    private String petStoreSignOutButtonXpath = "//*[@id=\"user-info-appbar\"]/div[2]/ul/li[2]";
    private String idpSignInButtonXpath = "//*[@id=\"loginForm\"]/div[6]/div/button";
    private String userButtonXpath = "//*[@id=\"app\"]/div/header/div/div/button";
    private String personalInfoNexButtonXpath = "//*[@id=\"app\"]/div/main/div/div/div/div[1]/div/div/div/div/div[4]/" +
            "button[2]";
    public String preferenceSubmitButtonXpath = "//*[@id=\"app\"]/div/main/div/div/div/div[3]/div/div/div/div/div[2]/" +
            "button[2]";
    public String preferenceCheckBoxDogXpath = "//*[@id=\"app\"]/div/main/div/div/div/div[3]/div/div/div/div/div[1]/" +
            "label[1]/span[1]/span[1]/input";
    public String preferenceCheckBoxCatXpath = "//*[@id=\"app\"]/div/main/div/div/div/div[3]/div/div/div/div/div[1]/" +
            "label[2]/span[1]/span[1]/input";

    public User(String firstName, String lastName, String address, String userName, String password
            , WebDriver webDriver, WebDriverWait webDriverWait) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.userName = userName;
        this.password = password;
        this.webDriver = webDriver;
        this.wait = webDriverWait;
    }

    /**
     * Get the user name of user.
     * @return username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Get the password of user.
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the first name of user.
     * @return password
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get the last name of the user.
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Get the address of user.
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Click the sign in button of pet-store web page.
     * @return header of sign in web page
     */
    public String clickSignIn() {
        webDriver.findElement(By.xpath(petStoreSignInButtonXpath)).click();
        String signInHeader = webDriver.findElement(By.cssSelector("H2")).getText();
        return signInHeader;
    }

    /**
     * Submit username and password of the user.
     * @return header of personal information web page
     */
    public String submitCredentials() {
        WebElement username = webDriver.findElement(By.id("username"));
        WebElement password = webDriver.findElement(By.id("password"));
        username.sendKeys(this.getUserName());
        password.sendKeys(this.getPassword());
        webDriver.findElement(By.xpath(idpSignInButtonXpath)).click();
        String personalInfoHeader = webDriver.findElement(By.cssSelector("H2")).getText();
        return personalInfoHeader;
    }

    /**
     * Accept the privacy policy.
     */
    public void acceptPrivacyPolicy() {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        webDriver.findElement(By.id("approveCb")).click();
        Boolean isPresent = webDriver.findElements(By.id("consent_select_all")).size() > 0;
        if (isPresent) {
            WebElement element = webDriver.findElement(By.id("consent_select_all"));
            js.executeScript("arguments[0].click()", element);
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        }
        webDriver.findElement(By.id("approve")).click();
    }

    /**
     * Submit the user information and preferences and proceed.
     * @return header of pet store web page
     * @throws InterruptedException
     */
    public String submitInformation() throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("first-name")));
        WebElement firstName = webDriver.findElement(By.id("first-name"));
        WebElement lastName = webDriver.findElement(By.id("last-name"));
        WebElement address = webDriver.findElement(By.id("address"));
        firstName.sendKeys(this.getFirstName());
        lastName.sendKeys(this.getLastName());
        address.sendKeys(this.getAddress());
        webDriver.findElement(By.xpath(personalInfoNexButtonXpath)).click();
        // Submit pet preferences
        // Putting an explicit sleep of 15 seconds because test is failing in jenkins server.
        TimeUnit.SECONDS.sleep(15);
        webDriver.findElement(By.xpath(preferenceCheckBoxDogXpath)).click();
        webDriver.findElement(By.xpath(preferenceCheckBoxCatXpath)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(preferenceSubmitButtonXpath)));
        webDriver.findElement(By.xpath(preferenceSubmitButtonXpath)
        ).click();
        String petAccessoriesHeader = webDriver.findElement(By.cssSelector("H6")).getText();

        return petAccessoriesHeader;
    }

    /**
     * Sign out from pet-store.
     * @return header of idp logout screen
     */
    public String signOut() {
        webDriver.findElement(By.xpath(userButtonXpath)).click();
        webDriver.findElement(By.xpath(petStoreSignOutButtonXpath)).click();
        String idpLogoutHeader = webDriver.findElement(By.cssSelector("H2")).getText();
        return idpLogoutHeader;
    }

    /**
     *  Confirm sign out.
     */
    public void signOutConfirm() {
        webDriver.findElement(By.id("approve")).click();
    }
}
