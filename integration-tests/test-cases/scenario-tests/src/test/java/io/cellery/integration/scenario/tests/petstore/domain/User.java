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
    private WebDriverWait webDriverWait;

    /**
     * Initializes a user object that would be used to perform actions on the pet-store web site.
     * @param firstName
     *        First name of the user
     * @param lastName
     *        Last name if the user
     * @param address
     *        Address of the user
     * @param userName
     *        User name of the user
     * @param password
     *        Password of the user
     * @param webDriver
     *        A selenium web driver to interact with pet store web page.
     * @param webDriverWait
     *        A web driver wait which would be used to check the availability of pet store web page attributes
     */
    public User(String firstName, String lastName, String address, String userName, String password
            , WebDriver webDriver, WebDriverWait webDriverWait) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.userName = userName;
        this.password = password;
        this.webDriver = webDriver;
        this.webDriverWait = webDriverWait;
    }

    /**
     * Click the sign in button of pet-store web page.
     * @return header of sign in web page
     */
    public String clickSignIn() {
        String petStoreSignInButtonXpath = "//*[@id=\"app\"]/div/header/div/button";
        webDriver.findElement(By.xpath(petStoreSignInButtonXpath)).click();
        return webDriver.findElement(By.cssSelector("H2")).getText();
    }

    /**
     * Submit username and password of the user.
     * @return header of personal information web page
     */
    public String submitCredentials() {
        WebElement username = webDriver.findElement(By.id("username"));
        WebElement password = webDriver.findElement(By.id("password"));
        username.sendKeys(this.userName);
        password.sendKeys(this.password);
        String idpSignInButtonXpath = "//*[@id=\"loginForm\"]/div[6]/div/button";
        webDriver.findElement(By.xpath(idpSignInButtonXpath)).click();
        return webDriver.findElement(By.cssSelector("H2")).getText();
    }

    /**
     * Accept the privacy policy.
     */
    public void acceptPrivacyPolicy() {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        webDriver.findElement(By.id("approveCb")).click();
        boolean isPresent = webDriver.findElements(By.id("consent_select_all")).size() > 0;
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
     * @throws InterruptedException if fails to submit information
     */
    public String submitInformation() throws InterruptedException {
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("first-name")));
        WebElement firstName = webDriver.findElement(By.id("first-name"));
        WebElement lastName = webDriver.findElement(By.id("last-name"));
        WebElement address = webDriver.findElement(By.id("address"));
        firstName.sendKeys(this.firstName);
        lastName.sendKeys(this.lastName);
        address.sendKeys(this.address);
        String personalInfoNexButtonXpath = "//*[@id=\"app\"]/div/main/div/div/div/div[1]/div/div/div/div/div[4]/" +
                "button[2]";
        webDriver.findElement(By.xpath(personalInfoNexButtonXpath)).click();
        // Submit pet preferences
        // Putting an explicit sleep of 15 seconds because test is failing in jenkins server.
        TimeUnit.SECONDS.sleep(15);
        String preferenceCheckBoxDogXpath = "//*[@id=\"app\"]/div/main/div/div/div/div[3]/div/div/div/div/div[1]/" +
                "label[1]/span[1]/span[1]/input";
        webDriver.findElement(By.xpath(preferenceCheckBoxDogXpath)).click();
        String preferenceCheckBoxCatXpath = "//*[@id=\"app\"]/div/main/div/div/div/div[3]/div/div/div/div/div[1]/" +
                "label[2]/span[1]/span[1]/input";
        webDriver.findElement(By.xpath(preferenceCheckBoxCatXpath)).click();
        String preferenceSubmitButtonXpath = "//*[@id=\"app\"]/div/main/div/div/div/div[3]/div/div/div/div/div[2]" +
                "/button[2]";
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(preferenceSubmitButtonXpath)));
        webDriver.findElement(By.xpath(preferenceSubmitButtonXpath)
        ).click();

        return webDriver.findElement(By.cssSelector("H6")).getText();
    }

    /**
     * Sign out from pet-store.
     * @return header of idp logout screen
     */
    public String signOut() {
        String userButtonXpath = "//*[@id=\"app\"]/div/header/div/div/button";
        webDriver.findElement(By.xpath(userButtonXpath)).click();
        String petStoreSignOutButtonXpath = "//*[@id=\"user-info-appbar\"]/div[2]/ul/li[2]";
        webDriver.findElement(By.xpath(petStoreSignOutButtonXpath)).click();
        return webDriver.findElement(By.cssSelector("H2")).getText();
    }

    /**
     *  Confirm sign out.
     */
    public void signOutConfirm() {
        webDriver.findElement(By.id("approve")).click();
    }
}
