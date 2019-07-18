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
import io.cellery.integration.scenario.tests.petstore.domain.Cart;
import io.cellery.integration.scenario.tests.petstore.domain.Order;
import io.cellery.integration.scenario.tests.petstore.domain.PetAccessory;
import io.cellery.integration.scenario.tests.petstore.domain.User;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.nio.file.Paths;

import static io.github.bonigarcia.wdm.DriverManagerType.CHROME;

/**
 * This includes the test cases related to hello world web scenario.
 */
public class PetStoreTestCase extends BaseTestCase {
    private static final String BACKEND_INSTANCE_NAME = "pet-be-inst";
    private static final String BACKEND_IMAGE_NAME = "petbe";
    private static final String FRONTEND_INSTANCE_NAME = "pet-fe-inst";
    private static final String FRONTEND_IMAGE_NAME = "petfe";
    private static final String VERSION = "1.0.0";
    private static final String LINK = "petstorebackend:pet-be-inst";
    private WebDriver webDriver;
    private User alice;
    private User bob;
    private Cart cart;
    private PetAccessory[] itemsOfAlice;
    private Order order;

    @BeforeClass
    public void setup() {
        WebDriverManager.getInstance(CHROME).setup();
        webDriver = new ChromeDriver(new ChromeOptions().setHeadless(true));
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, 120);

        // Create 2 users Alice and Bob with their information
        this.alice = new User("Alice", "Sanchez", "No 60, Regent street, " +
                "New York.", "alice", "alice123", webDriver, webDriverWait);
        this.bob = new User("Bob", "Dylan", "No 36, Mayfair street, " +
                "Los Angeles.", "bob", "bob123", webDriver, webDriverWait);

        this.cart = new Cart(webDriver);
        this.order = new Order(webDriver);

        // Create 2 pet accessories for user Alice.
        String petCarrierCageXpath = "//*[@id=\"app\"]/div/main/div/div[2]/div/div[1]/div/div[4]/div[2]/button";
        PetAccessory aliceItem1 = new PetAccessory(12, petCarrierCageXpath);
        String boneShapedToyXpath = "//*[@id=\"app\"]/div/main/div/div[2]/div/div[4]/" +
                "div/div[4]/div[2]/button";
        PetAccessory aliceItem2 = new PetAccessory(15, boneShapedToyXpath);
        itemsOfAlice = new PetAccessory[]{aliceItem1, aliceItem2};
    }

    @Test(description = "Tests the building of pet store backend image.")
    public void buildBackEnd() throws Exception {
        build("pet-be.bal", Constants.CELL_ORG_NAME, BACKEND_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "pet-care-store", "pet-be").toFile().getAbsolutePath());
    }

    @Test(description = "Tests the running of pet store back end instance.", dependsOnMethods = "buildBackEnd")
    public void runBackEnd() throws Exception {
        run(Constants.CELL_ORG_NAME, BACKEND_IMAGE_NAME, VERSION, BACKEND_INSTANCE_NAME, 600);
    }

    @Test(description = "Tests the building of pet store frontend image.", dependsOnMethods = "runBackEnd")
    public void buildFrontEnd() throws Exception {
        build("pet-fe.bal", Constants.CELL_ORG_NAME, FRONTEND_IMAGE_NAME, VERSION,
                Paths.get(CELLERY_SCENARIO_TEST_ROOT, "pet-care-store", "pet-fe").toFile().getAbsolutePath());
    }

    @Test(description = "Tests the running of pet store front end instance.", dependsOnMethods = "buildFrontEnd")
    public void runFrontEnd() throws Exception {
        String[] links = new String[]{LINK};
        run(Constants.CELL_ORG_NAME, FRONTEND_IMAGE_NAME, VERSION, FRONTEND_INSTANCE_NAME, links, false);
    }

    @Test(description = "Tests invoking of pet store web page.", dependsOnMethods = "runFrontEnd")
    public void invoke() {
        webDriver.get(Constants.DEFAULT_PET_STORE_URL);
        String petAccessoriesHeader = webDriver.findElement(By.cssSelector("H1")).getText();
        validateWebPage(petAccessoriesHeader, Constants.PET_STORE_WEB_CONTENT, "Pet store web page content is not " +
                "as expected");
    }

    @Test(description = "This tests sign in for user Alice.", dependsOnMethods = "invoke")
    public void signInAlice() throws InterruptedException {
        this.signIn(this.alice);
    }

    @Test(description = "This tests add to cart for user Alice.", dependsOnMethods = "signInAlice")
    public void addToCartAlice() {
        for (PetAccessory anItemsOfAlice : itemsOfAlice) {
            this.cart.addToCart(anItemsOfAlice);
        }
        validateWebPage("2", this.cart.getNumberOfItems(), "Pet store cart content before checkout is " +
                "not as expected");
    }

    @Test(description = "This tests checkout cart for user Alice.", dependsOnMethods = "addToCartAlice")
    public void checkoutCartAlice() throws InterruptedException {
        this.cart.checkout();
        validateWebPage("$ 675.00", this.order.getOrderValue(), "Pet store cart content after checkout is " +
                "not as expected");
    }

    @Test(description = "This tests sign out for user Alice.", dependsOnMethods = "checkoutCartAlice")
    public void signOutAlice() throws InterruptedException {
        this.signOut(this.alice);
    }

    @Test(description = "This tests sign in for user Bob.", dependsOnMethods = "signOutAlice")
    public void signInBob() throws InterruptedException {
        this.signIn(this.bob);
    }

    @Test(description = "This tests check order for user Bob.", dependsOnMethods = "signInBob")
    public void checkOrdersBob() throws InterruptedException {
        String noOrdersPlaced = "No Orders Placed";
        validateWebPage(noOrdersPlaced, this.order.getOrderValue(),
                "Pet store cart content after checkout is not as expected");
    }

    @Test(description = "This tests sign out for user Bob.", dependsOnMethods = "checkOrdersBob")
    public void signOutBob() throws InterruptedException {
        this.signOut(this.bob);
    }

    @Test(description = "This tests the termination of pet-store backend and frontend cells",
            dependsOnMethods = "signOutBob")
    public void terminate() throws Exception {
        terminateCell(BACKEND_INSTANCE_NAME);
        terminateCell(FRONTEND_INSTANCE_NAME);
    }

    @Test(description = "This tests the deletion of pet-store backend and frontend cell images",
            dependsOnMethods = "terminate")
    public void deleteImages() throws Exception {
        delete(Constants.CELL_ORG_NAME + "/" + BACKEND_IMAGE_NAME + ":" + VERSION);
        delete(Constants.CELL_ORG_NAME + "/" + FRONTEND_IMAGE_NAME + ":" + VERSION);
    }

    @AfterClass
    public void cleanup() {
        webDriver.close();
        try {
            terminateCell(BACKEND_INSTANCE_NAME);
            terminateCell(FRONTEND_INSTANCE_NAME);
        } catch (Exception ignored) {
        }
    }

    /**
     * Signs in test user to pet-store web page.
     *
     * @param user An instance of User
     * @throws InterruptedException if fails to sign in
     */
    private void signIn(User user) throws InterruptedException {
        String signInHeader = user.clickSignIn();
        String petStoreSignInWebContent = "SIGN IN";
        validateWebPage(signInHeader, petStoreSignInWebContent, "Pet store sign in web page " +
                "content is not as expected");

        String personalInfoHeader = user.submitCredentials();
        String identityServerHeader = "OPENID USER CLAIMS";
        validateWebPage(personalInfoHeader, identityServerHeader, "Identity server web page " +
                "content is not as expected");

        user.acceptPrivacyPolicy();

        String petAccessoriesHeader = user.submitInformation();
        String personalInformationHeader = "Pet Store";
        validateWebPage(petAccessoriesHeader, personalInformationHeader, "Pet store sign " +
                "in web page content is not as expected");
    }

    /**
     * Signs out test user to pet-store web page.
     *
     * @param user An instance of user
     * @throws InterruptedException if fails to sign out
     */
    private void signOut(User user) throws InterruptedException {
        String idpLogoutHeaderActual = user.signOut();
        String idpLogoutHeader = "OPENID CONNECT LOGOUT";
        validateWebPage(idpLogoutHeaderActual, idpLogoutHeader, "IDP logout header " +
                "is not as expected");
        user.signOutConfirm();
    }
}
