package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AddEventsThread implements Runnable {

    private WebDriver driver2;
    private String home = "https://kakava.lt";
    private ArrayList<String> months = new ArrayList<>(Arrays.asList("Sau", "Vas", "Kov", "Bal", "Geg", "Bir", "Lie", "Rugpj", "Rugs", "Spa", "Lap", "Gruod"));
    private String category = "";
    private WebElement webElement;

    public AddEventsThread(WebElement webElement) {
        this.webElement = webElement;
    }

    @Override
    public void run() {
        try {
            init();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        category = webElement.getText();
        webElement.click();
        //popUpHandle();
        boolean rekomenduojama = false;
        try {
            driver2.findElement(By.xpath("/html/body/div[2]/section/div/div[2]/div[1]/div[1]/h2"));
            rekomenduojama = true;
        } catch (Exception e) {

        }
        if (category.equals("Koncertai") || category.equals("Teatras")) {
            goThroughElements("/html/body/div[2]/section/div/div[2]/div[1]/div[2]");
            goThroughElements("/html/body/div[2]/section/div/div[2]/div[2]/div[1]/div");
        } else {
            goThroughElements("/html/body/div[2]/section/div/div[2]/div/div[1]/div");
        }
//           try{
//               driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div[1]/button")).click();
//           }catch (Exception e){
//
//           }
        //driver.navigate().back();
    }
        public void goThroughElements (String path){
            int i = 1;
            boolean flag = true;
            while (flag) {

                try {
                    driver2.findElement(By.xpath(path + "/div[" + i + "]" + "/a")).click();
                    try {
                        createEvent();
                    } catch (Exception e) {
                        System.out.println("Tickets sold out or event canceled.");
                    }
                    driver2.navigate().back();

                } catch (NoSuchWindowException e) {
                    e.printStackTrace();
                    System.exit(0);
                } catch (Exception e) {
                    System.out.println("Viskas");
                    e.printStackTrace();
                    driver2.navigate().back();
                    flag = false;
                }
                i++;
            }
        }

        public void createEvent () {
            String title = titleSelect();
            String description = buildDescription("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[5]");
            String priceString = driver2.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[2]/div/div[1]/div/div/div[3]/div/a/div[2]/div/div[2]/div[2]/span/span")).getText();
            String[] splittedPriceString = priceString.split(" ");
            String priceReplaced = splittedPriceString[0].replace(',', '.');
            double price = Double.parseDouble(priceReplaced);
            LocalDateTime dateAndTime = buildDateAndTime("/html/body/div[2]/section/div/div[1]/div/div/div[2]/div/div[1]/div/div/div[3]/div/a/div[1]/div/div[1]");
            String place;
            try {
                place = driver2.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/div[1]/div")).getText();
            } catch (Exception e) {
                driver2.findElement(By.xpath("/html/body/div[2]/section/div/div/div/div/div[2]/div/div[1]/div[2]/div/div[3]/div[1]/a")).click();
                try {
                    place = driver2.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/div[1]/div")).getText();
                } catch (Exception ex) {
                    place = driver2.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/div[2]/div")).getText();
                }
            }
            String imageUrl = driver2.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[1]/img")).getAttribute("src");


            Event event = new Event(title, description, price, category, dateAndTime, place, imageUrl);
            System.out.println(event);
        }

        public String placeSelect () {
            try {
                return driver2.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/div[1]/div")).getText();
            } catch (Exception e) {
                return driver2.findElement(By.xpath("/html/body/div[2]/section/div/div/div/div/div[1]/div[4]/div[1]/div[2]/div/h1")).getText();
            }
        }

        public String titleSelect () {
            try {
                return driver2.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/h1")).getText();
            } catch (Exception e) {
                return driver2.findElement(By.xpath("/html/body/div[2]/section/div/div/div/div/div[1]/div[4]/div[1]/div[2]/div/h1")).getText();
            }
        }

        public String buildDescription (String pathToDescription){
            String description = "";
            int i = 1;
            boolean flag = true;
            while (flag) {
                try {
                    description += driver2.findElement(By.xpath(pathToDescription + "/p[" + i + "]")).getText() + "\n\n";
                } catch (Exception e) {
                    flag = false;
                }
                i++;
            }
            System.out.println("Description ready.");
            return description;
        }

        public LocalDateTime buildDateAndTime (String pathDate){
            String[] dayAndMonth = driver2.findElement(By.xpath(pathDate)).getText().split(" ");
            String month = convertMonthToNumberString(dayAndMonth[0]);
            String day = dayAndMonth[1];
            String year = "";
            if (Integer.parseInt(month) < 12) {
                year = "2024";
            } else {
                year = "2025";
            }
            String timeString = driver2.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[2]/div/div[1]/div/div/div[3]/div/a/div[2]/div/div[2]/div[1]")).getText();
            String[] splittedTimeString = timeString.split(": ");

            String date = year + "-" + month + "-" + day + " " + splittedTimeString[1] + ":00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            return LocalDateTime.parse(date, formatter);

        }

        public String convertMonthToNumberString (String monthString){
            for (String month : months) {
                if (month.equals(monthString)) {
                    if (months.indexOf(month) + 1 > 9) {
                        return String.valueOf(months.indexOf(month) + 1);
                    }
                    return "0" + String.valueOf(months.indexOf(month) + 1);
                }
            }
            return "";
        }

        public void init () throws InterruptedException {
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
            driver2 = ThreadSafeWebDriver.getDriver();
            driver2.get(home);
            driver2.manage().window().maximize();
            driver2.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

            WebDriverWait wait = new WebDriverWait(driver2, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/iframe[2]")));

            driver2.switchTo().frame(element);
            driver2.findElement(By.className("ml-popup-close")).click();

            driver2.switchTo().defaultContent();
            driver2.findElement(By.xpath("/html/body/div[2]/footer/div[1]/div[3]/button[2]")).click();
        }

        public void popUpHandle () {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        WebDriverWait wait = new WebDriverWait(driver2, Duration.ofSeconds(100));
                        WebElement newsletter = wait.until(
                                ExpectedConditions.visibilityOfElementLocated(By.id("omnisend-form-63ff1f31b40d6530aba59a6d-action-627932485028ebd8c6660c51"))
                        );
                        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/iframe[3]")));
                        driver2.switchTo().frame(element);
                        driver2.findElement(By.xpath("/html/body/div[1]/div/div/div/div[1]/button")).click();
                        driver2.switchTo().defaultContent();

                    } catch (Exception e) {

                    }
                }
            });
            thread.start();
        }
    }

