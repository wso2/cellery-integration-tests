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
    private User alice;
    private User bob;
    private Cart cart;
    private PetAccessory[] itemsOfAlice;
    private Order order;

    @BeforeClass
    public void setup() {
        WebDriverManager.getInstance(CHROME).setup();
        webDriver = new ChromeDriver(new ChromeOptions().setHeadless(false));
        wait = new WebDriverWait(webDriver, 120);

        this.alice = new User("Alice", "Sanchez", "No 60, Regent street, " +
                "New York.", "alice39", "alice123", webDriver, wait);
        this.bob = new User("Bob", "Dylan", "No 36, Mayfair street, " +
                "Los Angeles.", "bob39", "bob123", webDriver, wait);

        this.cart = new Cart(webDriver);
        this.order = new Order(webDriver);

        PetAccessory aliceItem1 = new PetAccessory("Pet carrier cage",  50.00, 12
                , Constants.PET_STORE_XPATH_PET_CARRIER_CAGE, webDriver);
        PetAccessory aliceItem2 = new PetAccessory("Bone shaped toy",  5.00, 15
                , Constants.PET_STORE_XPATH_BONE_SHAPED_TOY, webDriver);
        itemsOfAlice = new PetAccessory[]{aliceItem1, aliceItem2};
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

    /**
     * This tests sign in for user Alice.
     * @throws InterruptedException
     */
    @Test
    public void signInAlice() throws InterruptedException {
        this.signIn(this.alice);
    }

    /**
     * This tests add to cart for user Alice.
     * @throws InterruptedException
     */
    @Test
    public void addToCartAlice() throws InterruptedException {
        for (int i = 0; i < itemsOfAlice.length; i++) {
            TimeUnit.SECONDS.sleep(10);
            this.cart.addToCart(itemsOfAlice[i]);
        }
        validateWebPage("2", this.cart.getNumberOfItems(), "Pet store cart content before checkout is " +
                "not as expected");
    }

    /**
     * This tests checkout cart for user Alice.
     * @throws InterruptedException
     */
    @Test
    public void checkoutCartAlice() throws InterruptedException {
        this.cart.checkout();
        TimeUnit.SECONDS.sleep(10);
        validateWebPage("0", this.cart.getNumberOfItems(), "Pet store cart content after checkout is " +
                "not as expected");
    }

    /**
     * This tests check order in for user Alice.
     * @throws InterruptedException
     */
    @Test
    public void checkOrdersAlice() throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
        validateWebPage("$ 675.00", this.order.getOrderValue(), "Pet store cart content after checkout is " +
                "not as expected");
    }

    /**
     * This tests sign out for user Alice.
     */
    @Test
    public void signOutAlice() {
        this.signOut(this.alice);
    }

    /**
     * This tests sign in for user Bob.
     * @throws InterruptedException
     */
    @Test
    public void signInBob() throws InterruptedException {
        this.signIn(this.bob);
    }

    /**
     * This tests check order in for user Bob.
     * @throws InterruptedException
     */
    @Test
    public void checkOrdersBob() throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
        validateWebPage(Constants.PET_STORE_NO_ORDERS_PLACED, this.order.getOrderValue(),
                "Pet store cart content after checkout is not as expected");
    }

    /**
     * This tests sign out for user Bob.
     */
    @Test
    public void signOutBob() {
        this.signOut(this.bob);
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

    public void signIn(User user) throws InterruptedException {
        String singInHeader = user.clickSignIn();
        validateWebPage(singInHeader, Constants.PET_STORE_SIGN_IN_WEB_CONTENT, "Pet store sign in web page " +
                "content is not as expected");

        String personalInfoHeader = user.submitCredentials();
        validateWebPage(personalInfoHeader, Constants.IDENTITY_SERVER_HEADER, "Identity server web page " +
                "content is not as expected");

        user.acceptPrivacyPolicy();

        String petAccessoriesHeader = user.submitInformation();
        validateWebPage(petAccessoriesHeader, Constants.PET_STORE_PERSONAL_INFORMATION_HEADER, "Pet store sign " +
                "in web page content is not as expected");
    }

    public void signOut(User user) {
        String idpLogoutHeader = user.signOut();
        validateWebPage(idpLogoutHeader, Constants.PET_STORE_IDENTITY_SERVER_LOGOUT_HEADER, "IDP logout header " +
                "is not as expected");
        user.signOutConfirm();
    }
}
