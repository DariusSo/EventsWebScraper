package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class Scraper {

    private WebDriver driver;
    private String home = "https://kakava.lt";
    private ArrayList<String> months = new ArrayList<>(Arrays.asList("Sau", "Vas", "Kov", "Bal", "Geg", "Bir", "Lie", "Rugpj", "Rugs", "Spa", "Lap", "Gruod"));
    private String category = "";

    public void test() throws InterruptedException {

       init();
       List<WebElement> categoryList = driver.findElements(By.xpath("/html/body/div[2]/div[1]/div/header/div[2]/div/nav/ul/li"));
        System.out.println(categoryList.size());
       for(int i = 1; i < categoryList.size(); i++){
           WebElement webElement = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/header/div[2]/div/nav/ul/li[" + i + "]/a"));
           category = webElement.getText();
           webElement.click();
           //popUpHandle();
           boolean rekomenduojama = false;
           try{
               driver.findElement(By.xpath("/html/body/div[2]/section/div/div[2]/div[1]/div[1]/h2"));
               rekomenduojama = true;
           }catch (Exception e){

           }
           if(category.equals("Koncertai") || category.equals("Teatras")){
               goThroughElements("/html/body/div[2]/section/div/div[2]/div[1]/div[2]");
               driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/header/div[2]/div/nav/ul/li[" + i + "]/a")).click();
               goThroughElements("/html/body/div[2]/section/div/div[2]/div[2]/div[1]/div");
           }else{
               goThroughElements("/html/body/div[2]/section/div/div[2]/div/div[1]/div");
           }
//           try{
//               driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div[1]/button")).click();
//           }catch (Exception e){
//
//           }
           //driver.navigate().back();
           //driver.navigate().to(home);
       }
    }

    public void goThroughElements(String path){
        int i = 1;
        boolean flag = true;
        while (flag){

            try{
                driver.findElement(By.xpath(path + "/div[" + i + "]" + "/a")).click();
                try{
                    createEvent();
                }catch (Exception e){
                    System.out.println("Tickets sold out or event canceled.");
                }
                driver.navigate().back();

            }catch (NoSuchWindowException e){
                e.printStackTrace();
                System.exit(0);
            }catch (Exception e){
                System.out.println("Viskas");
                e.printStackTrace();
                driver.navigate().back();
                flag = false;
            }
            i++;
        }
    }

    public void createEvent(){
        String title = titleSelect();
        String description = buildDescription("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[5]");
        String priceString = driver.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[2]/div/div[1]/div/div/div[3]/div/a/div[2]/div/div[2]/div[2]/span/span")).getText();
        String[] splittedPriceString = priceString.split(" ");
        String priceReplaced = splittedPriceString[0].replace(',', '.');
        double price = Double.parseDouble(priceReplaced);
        LocalDateTime dateAndTime = buildDateAndTime("/html/body/div[2]/section/div/div[1]/div/div/div[2]/div/div[1]/div/div/div[3]/div/a/div[1]/div/div[1]");
        String place;
        try{
            place = driver.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/div[1]/div")).getText();
        }catch (Exception e){
            driver.findElement(By.xpath("/html/body/div[2]/section/div/div/div/div/div[2]/div/div[1]/div[2]/div/div[3]/div[1]/a")).click();
            try{
                place = driver.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/div[1]/div")).getText();
            }catch (Exception ex){
                place = driver.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/div[2]/div")).getText();
            }
        }
        String imageUrl = driver.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[1]/img")).getAttribute("src");


        Event event = new Event(title, description, price, category, dateAndTime, place, imageUrl);
        System.out.println("Event acquired.");
    }

    public String placeSelect(){
        try{
            return driver.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/div[1]/div")).getText();
        }catch (Exception e){
            return driver.findElement(By.xpath("/html/body/div[2]/section/div/div/div/div/div[1]/div[4]/div[1]/div[2]/div/h1")).getText();
        }
    }

    public String titleSelect(){
        try{
            return driver.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/h1")).getText();
        }catch (Exception e){
            return driver.findElement(By.xpath("/html/body/div[2]/section/div/div/div/div/div[1]/div[4]/div[1]/div[2]/div/h1")).getText();
        }
    }

    public String buildDescription(String pathToDescription){
        String description = "";
        int i = 1;
        boolean flag = true;
        while (flag){
            try{
                description += driver.findElement(By.xpath(pathToDescription + "/p[" + i + "]")).getText() + "\n\n";
            }catch (Exception e){
                flag = false;
            }
            i++;
        }
        System.out.println("Description ready.");
        return description;
    }

    public LocalDateTime buildDateAndTime(String pathDate){
        String[] dayAndMonth = driver.findElement(By.xpath(pathDate)).getText().split(" ");
        String month = convertMonthToNumberString(dayAndMonth[0]);
        String day = dayAndMonth[1];
        String year = "";
        if(Integer.parseInt(month) < 12){
            year = "2024";
        }else{
            year = "2025";
        }
        String timeString = driver.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[2]/div/div[1]/div/div/div[3]/div/a/div[2]/div/div[2]/div[1]")).getText();
        String[] splittedTimeString = timeString.split(": ");

        String date = year + "-" + month + "-" + day + " " + splittedTimeString[1] + ":00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(date, formatter);

    }

    public String convertMonthToNumberString(String monthString){
        for(String month : months){
            if(month.equals(monthString)){
                if(months.indexOf(month) + 1 > 9){
                    return String.valueOf(months.indexOf(month) + 1);
                }
                return "0" + String.valueOf(months.indexOf(month) + 1);
            }
        }
        return "";
    }

    public void init() throws InterruptedException {
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--start-maximized");
        //options.addArguments("--headless=new");
        options.addArguments("--disable-gpu"); // Reduces GPU overhead
        options.addArguments("--no-sandbox"); // Improves performance
        options.addArguments("--disable-dev-shm-usage"); // Uses disk instead of shared memory
        //options.addArguments("--window-size=1280,800"); // Smaller window size
        options.addArguments("--disable-extensions"); // Disables Chrome extensions
        options.addArguments("--disable-background-networking"); // Reduces background resource consumption
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-client-side-phishing-detection");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-hang-monitor");
        options.addArguments("--disable-prompt-on-repost");
        options.addArguments("--disable-sync");
        options.addArguments("--metrics-recording-only");
        options.addArguments("--no-first-run");
        options.addArguments("--mute-audio");
        driver = new ChromeDriver();
        driver.get(home);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/iframe[2]")));

        driver.switchTo().frame(element);
        driver.findElement(By.className("ml-popup-close")).click();

        driver.switchTo().defaultContent();
        driver.findElement(By.xpath("/html/body/div[2]/footer/div[1]/div[3]/button[2]")).click();

        try {
            // Switch to the iframe
            WebElement iframe3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/iframe[3]")));
            driver.switchTo().frame(iframe3);

            // Wait for the popup to be clickable
            WebElement annoyingPopup = wait.until(ExpectedConditions.elementToBeClickable(By.className("close")));

            // Ensure the element is in the viewport and use JavaScript to click
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", annoyingPopup);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", annoyingPopup);

        } catch (ElementClickInterceptedException e) {
            System.out.println("Click intercepted. Retrying...");
            WebElement annoyingPopup = driver.findElement(By.className("close"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", annoyingPopup);
        } finally {
            // Switch back to the main content
            driver.switchTo().defaultContent();
        }
    }

    public void popUpHandle(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
                    WebElement newsletter = wait.until(
                            ExpectedConditions.visibilityOfElementLocated(By.id("omnisend-form-63ff1f31b40d6530aba59a6d-action-627932485028ebd8c6660c51"))
                    );
                    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/iframe[3]")));
                    driver.switchTo().frame(element);
                    driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div[1]/button")).click();
                    driver.switchTo().defaultContent();

                }catch (Exception e){

                }
            }
        });
        thread.start();
    }

}
