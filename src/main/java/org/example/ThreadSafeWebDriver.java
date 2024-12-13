package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ThreadSafeWebDriver {
    private static ThreadLocal<WebDriver> threadLocalDriver = ThreadLocal.withInitial(() -> {
        return new ChromeDriver(); // Create a new WebDriver instance per thread
    });

    public static WebDriver getDriver() {
        return threadLocalDriver.get();
    }

    public static void quitDriver() {
        getDriver().quit();
        threadLocalDriver.remove(); // Clean up to prevent memory leaks
    }
}