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
       //driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/header/div[2]/div/nav/ul/li[8]")).click();

       List<WebElement> categoryList = driver.findElements(By.xpath("/html/body/div[2]/div[1]/div/header/div[2]/div/nav/ul/li"));
       for(WebElement webElement : categoryList){
           category = webElement.getText();
           webElement.click();
           int i = 1;
           boolean flag = true;
           while (flag){
               try{
                   driver.findElement(By.xpath("/html/body/div[2]/section/div/div[2]/div[1]/div[2]/div[" + i + "]" + "/a")).click();
                   createEvent();
                   driver.navigate().back();

               }catch (NoSuchWindowException e){
                   e.printStackTrace();
                   System.exit(0);
               }catch (NoSuchElementException e){
                   System.out.println("Viskas");
                   e.printStackTrace();
                   //flag = false;
               }
               i++;
           }
           driver.navigate().back();
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
        try{
            String place = driver.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/div[1]/div")).getText();
        }catch (Exception e){
            driver.findElement(By.xpath("/html/body/div[2]/section/div/div/div/div/div[2]/div/div[1]/div[1]/form/div/div")).click();

            String place = driver.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[4]/div[1]/div[2]/div[3]/div[1]/div")).getText();
        }
        String imageUrl = driver.findElement(By.xpath("/html/body/div[2]/section/div/div[1]/div/div/div[1]/div[1]/img")).getAttribute("src");


        Event event = new Event(title, description, price, category, dateAndTime, place, imageUrl);
        System.out.println(event);
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
        options.addArguments("--start-maximized");
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
    }

}
