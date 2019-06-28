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

import io.cellery.integration.scenario.tests.models.Cell;
import io.cellery.integration.scenario.tests.models.SequenceDiagram;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class includes the selenium tests related to observability dashboard
 */
public class ObservabilityDashboard {

    private String webCellUrl;
    private List<Cell> cells;
    private SequenceDiagram diagram;
    private WebDriver webDriver;
    private WebDriverWait webDriverWait;
    private JavascriptExecutor jsExecutor;

    public ObservabilityDashboard(WebDriver webDriver, WebDriverWait webDriverWait) {
        this.cells = new ArrayList<>();
        this.diagram = new SequenceDiagram();
        this.webDriver = webDriver;
        this.webDriverWait = webDriverWait;
        this.jsExecutor = (JavascriptExecutor) webDriver;
    }

    public void setWebCellUrl(String webCellUrl) {
        this.webCellUrl = webCellUrl;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void overviewPage() {
        String overviewXPath = "//*[@id=\"root\"]/div/main/div[2]/div/h5";
        String overviewHeader =
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(overviewXPath))).getText();
        Assert.assertEquals(overviewHeader, "Overview");

        String cellDropdownButtonXPath = "//*[@id=\"root\"]/div/main/div[3]/div[3]/div/div[2]/div[2]/div" +
                "/div[1]/div[2]";
        clickOnObservabilityButton(cellDropdownButtonXPath);

        for (int i = 0; i < cells.size(); i++) {
            String componentXPath = "//*[@id=\"MUIDataTableBodyRow-" + i + "\"]/td[4]/a";
            String componentInstanceName =
                    webDriverWait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath(componentXPath)))).getText();
            Assert.assertEquals(componentInstanceName, cells.get(i).getInstanceName());
        }
        String refreshButtonXPath = "//*[@id=\"root\"]/div/main/div[2]/div/div[2]/div/div[2]/div";
        clickOnObservabilityButton(refreshButtonXPath);
        String refreshOffButtonXPath = "//*[@id=\"menu-refresh-interval\"]/div[2]/ul/li[1]";
        clickOnObservabilityButton(refreshOffButtonXPath);
    }

    public void cellsPage() {
        String cellPageButtonXPath = "//*[@id=\"root\"]/div/div/div/ul/div[2]";
        clickOnObservabilityButton(cellPageButtonXPath);
        String cellPageHeaderXPath = "//*[@id=\"root\"]/div/main/div[2]/div/h5";
        String cellsHeader =
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(cellPageHeaderXPath))).getText();
        Assert.assertEquals(cellsHeader, "Cells");
        List<Cell> cellCopy = new ArrayList<>(cells);
        int cellAmount = cellCopy.size();
        for (int i = 0; i < cellAmount; i++) {
            String instanceNameXPath = "//*[@id=\"MUIDataTableBodyRow-" + i + "\"]/td[4]/a";
            String instanceName =
                    webDriverWait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath(instanceNameXPath)))).getText();
            Cell cell = getCellForInstance(cellCopy, instanceName);
            Assert.assertNotNull(cell); //Instance name is not found in test scenario
            Assert.assertEquals(instanceName, cell.getInstanceName());

            String incomingReqXPath = "//*[@id=\"MUIDataTableBodyRow-" + i + "\"]/td[12]";
            int incomingRequestCount =
                    Integer.parseInt(webDriver.findElement(By.xpath(incomingReqXPath)).getText());
            Assert.assertTrue(incomingRequestCount > 0);

            testCellInstance(instanceNameXPath, cell.getInstanceName(), cell.getComponents());
        }
    }

    private Cell getCellForInstance(List<Cell> i, String instanceName) {
        for (Cell cell : i) {
            if (cell.getInstanceName().equals(instanceName)) {
                i.remove(cell);
                return cell;
            }
        }
        return null;
    }

    private void testCellInstance(String instanceNameXPath, String instanceName, List<String> componentList) {
        clickOnObservabilityButton(instanceNameXPath);
        String cellHeaderXPath = "//*[@id=\"root\"]/div/main/div[2]/div/h5";
        String cellHeader =
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(cellHeaderXPath))).getText();
        Assert.assertEquals(cellHeader, instanceName);

        String componentButtonXPath = "//*[@id=\"root\"]/div/main/div[3]/div[1]/div/div" +
                "/div/div/button[2]";
        clickOnObservabilityButton(componentButtonXPath);
        testComponents(componentList, instanceName);
    }

    private void testComponents(List<String> expectedComponents, String instanceName) {
        int numberOfComponents = expectedComponents.size();
        for (int i = 0; i < numberOfComponents; i++) {
            String componenetXPath = "//*[@id=\"MUIDataTableBodyRow-" + i + "\"]/td[4]/a";
            String componentName =
                    webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(componenetXPath))).getText();
            Assert.assertTrue(checkComponenetExist(expectedComponents, componentName));
            testComponent(componenetXPath, instanceName, componentName);
        }
        String backButtonXPath = "//*[@id=\"root\"]/div/main/div[2]/div/button[1]";
        clickOnObservabilityButton(backButtonXPath);
    }

    private void testComponent(String componentXpath, String cellInstanceName, String componentName) {
        clickOnObservabilityButton(componentXpath);
        String headerComponenetXPath = "//*[@id=\"root\"]/div/main/div[2]/div/h5";
        String headerComponentName =
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(headerComponenetXPath))).getText();
        Assert.assertEquals(headerComponentName, componentName);
        String instanceNameXPath = "//*[@id=\"root\"]/div/main/div[3]/table/tbody/tr[2]/td[2]/a";
        String cellInstName =
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(instanceNameXPath))).getText();
        Assert.assertEquals(cellInstName, cellInstanceName);

        String k8sPodButtonXPath = "//*[@id=\"root\"]/div/main/div[3]/div[1" +
                "]/div/div/div/div/button[2]";
        clickOnObservabilityButton(k8sPodButtonXPath);
        String podNameFieldXPath = "//*[@id=\"MUIDataTableBodyRow-0\"]/td[2]";
        String podName =
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath(podNameFieldXPath)))).getText();
        Assert.assertTrue(podName.startsWith(cellInstanceName + "--" + componentName));

        if (!componentName.equals("gateway")) { //Since Web Cell gateway componenet is not showing data
            validateMetricsData();
        }

        String backButtonXPath = "//*[@id=\"root\"]/div/main/div[2]/div/button[1]";
        clickOnObservabilityButton(backButtonXPath);
    }

    private void validateMetricsData() {
        String metricsButtonXPath = "//*[@id=\"root\"]/div/main/div[3]/div[1]/div/div/div/div/button[3]";
        clickOnObservabilityButton(metricsButtonXPath);
        String responseTimeXPath = "//*[@id=\"root\"]/div/main/div[3]/div[3]/div/div/div[2]/div[1]/div/div" +
                "[2]/p[1]";
        int avgResponseTime = Integer.parseInt(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(responseTimeXPath))).getText());
        Assert.assertTrue(avgResponseTime > 0);
        String requestCountXPath = "//*[@id=\"root\"]/div/main/div[3]/div[3]/div/div/div[2]/div[2]/div/div" +
                "[2]/p[1]";
        double requestCount = Double.parseDouble(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(requestCountXPath))).getText());
        Assert.assertTrue(requestCount > 0);

        String sucessRateSvgXPath = "//*[@id=\"root\"]/div/main/div[3]/div[3]/div/div/div[1]/div/div[2]/div" +
                "[1]";
        checkSvgExists(sucessRateSvgXPath);
        String httpReqXPath = "//*[@id=\"root\"]/div/main/div[3]/div[3]/div/div/div[3]/div/div[2]/div/div[1" +
                "]/div";
        checkSvgExists(httpReqXPath);
        String requestVolumeXPath = "//*[@id=\"root\"]/div/main/div[3]/div[3]/div/div/div[4]/div/div[2]/div" +
                "/div[1]/div";
        checkSvgExists(requestVolumeXPath);
        String requestDurationXPath = "//*[@id=\"root\"]/div/main/div[3]/div[3]/div/div/div[5]/div/div[2" +
                "]/div/div[1]/div";
        checkSvgExists(requestDurationXPath);
        String requestResponseXPath = "//*[@id=\"root\"]/div/main/div[3]/div[3]/div/div/div[6]/div/div[2" +
                "]/div/div[1]/div";
        checkSvgExists(requestResponseXPath);
    }

    private boolean checkComponenetExist(List<String> array, String targetString) {
        if (array.contains(targetString)) {
            array.remove(targetString);
            return true;
        } else {
            return false;
        }
    }

    public void tracingPage() {
        String tracingPageButtonXPath = "//*[@id=\"root\"]/div/div/div/ul/div[3]";
        clickOnObservabilityButton(tracingPageButtonXPath);

        String searchTextXPath = "//*[@id=\"root\"]/div/main/div[3]/div[2]/div[1]/div" +
                "/div/div/div/input";
        WebElement searchInput =
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(searchTextXPath)));
        searchInput.sendKeys("http.url=" + webCellUrl);
        searchInput.sendKeys(Keys.RETURN);

        Actions act = new Actions(webDriver);
        String searchButtonXPath = "//*[@id=\"root\"]/div/main/div[3]/button";
        act.moveToElement(webDriver.findElement(By.xpath(searchButtonXPath))).click().perform();

        String firstTraceHeader = "//*[@id=\"root\"]/div/main/div[3]/div[3]/div[1]";
        clickOnObservabilityButton(firstTraceHeader);

        By traceData = By.cssSelector(".vis-foreground > .vis-group");
        int traceCount =
                webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(traceData)).size();
        Assert.assertTrue(traceCount > 1);
        WebElement tracePage = webDriver.findElements(traceData).get(0);
        tracePage.click();
        WebElement refreshedPage = webDriver.findElements(traceData).get(0);
        String componentFieldXPath = "//*[@class=\"vis-item-content\"]/div/div" +
                "/div/table/tbody/tr[1]/td/div";
        WebElement component = refreshedPage.findElement(By.xpath(componentFieldXPath));
        Assert.assertEquals(component.getText(), "proxy");

        String sequenceDiagramButtonXPath = "//*[@id=\"root\"]/div/main/div[3]/div[1]/div/div/div/button[2]";
        clickOnObservabilityButton(sequenceDiagramButtonXPath);
        checkSeqDiagram();

        String dependancyDiagramButton = "//*[@id=\"root\"]/div/main/div[3]/div[1]/div/div/div/button[3]";
        clickOnObservabilityButton(dependancyDiagramButton);

        String dependancyDiagramXPath = "//*[@id=\"root\"]/div/main/div[3]/div[2]/div/div/div/canvas";
        webDriver.findElement(By.xpath(dependancyDiagramXPath));

    }

    private void checkSeqDiagram() {
        for (Map.Entry<String, Integer> entry : diagram.getCalls().entrySet()) {
            checkSeqDiagramCell(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Integer> entry : diagram.getComponents().entrySet()) {
            webDriver.findElement(By.xpath("//*[name()='svg']/*[name()='g'][" + entry.getValue() +
                    "]/*[name()" +
                    "='text']")).click();
            String firstComp =
                    webDriver.findElement(By.xpath("//*[name()='svg']/*[name()='g'][2]/*[name()" +
                            "='text']/*[name()" +
                            "='tspan']")).getText();
            Assert.assertEquals(firstComp, entry.getKey());
        }

    }

    private void checkSeqDiagramCell(String instanceName, int cellSlotNumber) {
        String cellName =
                webDriver.findElement(By.xpath("//*[name()='svg']/*[name()='g'][" + cellSlotNumber +
                        "]/*[name()" +
                        "='text']/*[name()" +
                        "='tspan']")).getText();
        Assert.assertEquals(cellName, instanceName.replace("-", "_"));
    }

    public void loginObservability() {
        webDriver.get(Constants.DEFAULT_OBSERVABILITY_URL);
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/header/div/div/a/img")));
        WebElement username = webDriver.findElement(By.id("username"));
        WebElement password = webDriver.findElement(By.id("password"));
        username.sendKeys("admin");
        password.sendKeys("admin");
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"loginForm\"]/div[6" +
                "]/div/button"))).click();
        try {
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approveCb"))).click();
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("consent_select_all"))).click();
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approve"))).click();
        } catch (Exception ignored) {

        }
        String personalInfoHeader = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//*[@id=\"root" +
                "\"]/div/header/div/h6"))).getText();
        Assert.assertEquals(personalInfoHeader, Constants.OBSERVABILITY_WEB_CONTENT, "Cellery Observability" +
                " Dashboard content is not as expected");
    }

    public void logoutObservability() {
        String profileButtonXPath = "//*[@id=\"root\"]/div/header/div/div/button[3]";
        clickOnObservabilityButton(profileButtonXPath);
        String signoutButtonXPath = "//*[@id=\"user-info-appbar\"]/div[2]/ul/li[2]";
        clickOnObservabilityButton(signoutButtonXPath);
        WebElement approveButton =
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approve")));
        jsExecutor.executeScript("arguments[0].click();", approveButton);
        String signinHeader = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body" +
                "/div/div/div/div/div[1]/h2"))).getText();
        Assert.assertEquals(signinHeader, "SIGN IN");
    }

    public void cleanupCelleryDashboard() throws InterruptedException, IOException {
        String spWorkerPod = getPodsAssociatedWithLabel("app", "wso2sp-worker");
        new ProcessBuilder().command("kubectl", "delete", "pod", "-n", "cellery" +
                "-system", spWorkerPod).start();
        Thread.sleep(30000); //Wait till sp worker pod is restarted

        String dbPodName = getPodsAssociatedWithLabel("deployment", "wso2apim-with-analytics-mysql");
        String celleryDashboardCleanupSqlQuery = "delete from DependencyModelTable;" +
                " delete from DistributedTracingTable;" +
                " delete from K8sPodInfoTable;" +
                " delete from RequestAggregation_DAYS;" +
                " delete from RequestAggregation_HOURS;" +
                " delete from RequestAggregation_MINUTES;" +
                " delete from RequestAggregation_MONTHS;" +
                " delete from RequestAggregation_SECONDS;" +
                " delete from RequestAggregation_YEARS;";

        new ProcessBuilder().command("kubectl", "exec", "-it", "-n", "cellery" +
                        "-system", dbPodName, "--", "mysql", "-u", "root",
                "--password=root", "CELLERY_OBSERVABILITY_DB", "-e", celleryDashboardCleanupSqlQuery).start();
        Thread.sleep(30000); //Wait till sp worker pod is restarted
    }

    private String getPodsAssociatedWithLabel(String key, String value) throws IOException {
        Process getDashboardDbPod = new ProcessBuilder().command("kubectl", "get", "pod", "-n", "cellery" +
                        "-system", "-l",
                key + "=" + value, "-o", "jsonpath='{.items[0].metadata.name}'").start();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(getDashboardDbPod.getInputStream()));
        String podName = reader.readLine();
        podName = podName.substring(1, podName.length() - 1);
        return podName;
    }

    private void clickOnObservabilityButton(String buttonXPath) {
        WebElement button = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(buttonXPath)));
        jsExecutor.executeScript("arguments[0].click();", button);
    }

    private void checkSvgExists(String svgDivXPath) {
        WebElement svgElementDiv =
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(svgDivXPath)));
        WebElement svgElement = svgElementDiv.findElement(By.xpath("//*[local-name() = 'svg']"));
        Assert.assertNotNull(svgElement);
    }
}
