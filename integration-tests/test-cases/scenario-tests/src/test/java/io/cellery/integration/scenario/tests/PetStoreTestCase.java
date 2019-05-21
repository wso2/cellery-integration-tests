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
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static io.github.bonigarcia.wdm.DriverManagerType.CHROME;

/**
 * This includes the test cases related to hello world web scenario.
 */
public class PetStoreTestCase extends BaseTestCase {
    private static final String backEndInstanceName = "pet-be-inst";
    private static final String backEndImageName = "pet-be-cell";
    private static final String frontEndInstanceName = "pet-fe-inst";
    private static final String frontEndImageName = "pet-fe-cell";
    private static final String version = "latest";
    private static final String link = "petStoreBackend:pet-be-inst";
    private WebDriver webDriver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        WebDriverManager.getInstance(CHROME).setup();
        webDriver = new ChromeDriver(new ChromeOptions().setHeadless(true));
        wait = new WebDriverWait(webDriver, 120);
    }

    @Test
    public void buildBackEnd() throws Exception {
        build("pet-be.bal", Constants.CELL_ORG_NAME, backEndImageName, version,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "pet-store", "pet-be").toFile().getAbsolutePath());
    }

    @Test
    public void runBackEnd() throws Exception {
        run(Constants.CELL_ORG_NAME, backEndImageName, version, backEndInstanceName, 600);
    }

    @Test
    public void buildFrontEnd() throws Exception {
        build("pet-fe.bal", Constants.CELL_ORG_NAME, frontEndImageName, version,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "pet-store", "pet-fe").toFile().getAbsolutePath());
    }

    @Test
    public void runFrontEnd() throws Exception {
        run(Constants.CELL_ORG_NAME, frontEndImageName, version, frontEndInstanceName, link, false,
                600);
    }

    @Test
    public void invoke() {
        webDriver.get(Constants.DEFAULT_PET_STORE_URL);
        String petAccessoriesHeader = webDriver.findElement(By.cssSelector("H1")).getText();
        validateWebPage(petAccessoriesHeader, Constants.PET_STORE_WEB_CONTENT, "Pet store web page content is not " +
                "as expected");
    }

    @Test
    public void signIn() throws InterruptedException {
        clickSignIn();
        submitCredentials();
        acceptPrivacyPolicy();
        submitUserInfo();
    }

    public void clickSignIn() {
        // Click sign in button
        webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/header/div/button")).click();
        String singInHeader = webDriver.findElement(By.cssSelector("H2")).getText();
        validateWebPage(singInHeader, Constants.PET_STORE_SIGN_IN_WEB_CONTENT, "Pet store sign in web page content "
                + "is not as expected");
    }

    public void submitCredentials() {
        // Submit username, password
        WebElement username = webDriver.findElement(By.id("username"));
        WebElement password = webDriver.findElement(By.id("password"));
        username.sendKeys("admin");
        password.sendKeys("admin");
        webDriver.findElement(By.xpath("//*[@id=\"loginForm\"]/div[6]/div/button")).click();
        String personalInfoHeader = webDriver.findElement(By.cssSelector("H2")).getText();
        validateWebPage(personalInfoHeader, Constants.IDENTITY_SERVER_HEADER, "Identity server web page " +
                "content is not as expected");
    }

    public void acceptPrivacyPolicy() {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        webDriver.findElement(By.id("approveCb")).click();
        webDriver.findElement(By.id("consent_select_all")).click();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        webDriver.findElement(By.id("approve")).click();
    }

    public void submitUserInfo() throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("first-name")));
        WebElement firstName = webDriver.findElement(By.id("first-name"));
        WebElement lastName = webDriver.findElement(By.id("last-name"));
        WebElement address = webDriver.findElement(By.id("address"));
        firstName.sendKeys("Alex");
        lastName.sendKeys("Sanchez");
        address.sendKeys("No 60, Regent street, New York.");
        webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/main/div/div/div/div[1]/div/div/div/div/div[4]/button[2]"))
                .click();
        // Submit pet preferences
        TimeUnit.SECONDS.sleep(5);
        webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/main/div/div/div/div[3]/div/div/div/div/div[1]/label[1]" +
                "/span[1]/span[1]/input")).click();
        webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/main/div/div/div/div[3]/div/div/div/div/div[1]/label[2]/" +
                "span[1]/span[1]/input")).click();
        TimeUnit.SECONDS.sleep(5);
        webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/main/div/div/div/div[3]/div/div/div/div/div[2]/button[2]")
        ).click();
        String petAccessoriesHeader = webDriver.findElement(By.cssSelector("H6")).getText();
        validateWebPage(petAccessoriesHeader, Constants.PET_STORE_PERSONAL_INFORMATION_HEADER, "Pet store sign in " +
                "web page content is not as expected");
    }

    @Test
    public void terminate() throws Exception {
        terminateCell(backEndInstanceName);
        terminateCell(frontEndInstanceName);
    }

    @AfterClass
    public void cleanup() {
        webDriver.close();
        try {
            terminateCell(backEndInstanceName);
            terminateCell(frontEndInstanceName);
        } catch (Exception ignored) {
        }
    }
}
