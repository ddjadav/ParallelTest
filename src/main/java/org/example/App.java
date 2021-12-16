package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.runners.Parameterized.*;

/**
 * Hello world!
 *
 */
public class App 
{
    private static String build;
    public WebDriver driver;

//    @Rule
//    public SauceTestWatcher resultReportingTestWatcher = new SauceTestWatcher();

    @Rule
    public TestName testName = new TestName() {
        public String getMethodName() {
            return String.format("%s", super.getMethodName());
        }
    };

//    @BeforeClass
//    public static void beforeClass() {
//        build = setBuildName();
//    }


    @Parameters(name = "{0},{1}")
    public static Collection<Object[]> crossBrowserData() {
        return Arrays.asList(new Object[][] {
                { "Chrome", "macOS 10.14", "latest" },
                { "Chrome", "macOS 10.15", "latest" },
                { "Chrome", "macOS 10.14", "latest-1" },
                { "Chrome", "macOS 10.15", "latest-1" },
                { "Safari", "macOS 10.14", "latest" },
                { "Safari", "macOS 10.15", "latest" },
        });
    }

    @Parameter // first data value (0) is default
    public String browser;

    @Parameter(1)
    public String platform;

    @Parameter(2)
    public String browserVersion;

    @Before
    public void setup() throws MalformedURLException {
        createDriver();
//        resultReportingTestWatcher.setDriver(driver);
    }
    private void createDriver() throws MalformedURLException {
        String sauceUsername = System.getenv("SAUCE_USERNAME");
        String sauceAccessKey = System.getenv("SAUCE_ACCESS_KEY");

        MutableCapabilities sauceOpts = new MutableCapabilities();
        sauceOpts.setCapability("username", sauceUsername);
        sauceOpts.setCapability("accessKey", sauceAccessKey);
        sauceOpts.setCapability("name", testName.getMethodName());
        sauceOpts.setCapability("build", build);
        sauceOpts.setCapability("commandTimeout", "30");

//        MutableCapabilities browserOptions = new MutableCapabilities();
//        browserOptions.setCapability(CapabilityType.PLATFORM_NAME, platform);
//        browserOptions.setCapability(CapabilityType.BROWSER_VERSION, browserVersion);
//        browserOptions.setCapability(CapabilityType.BROWSER_NAME, browser);
//        browserOptions.setCapability("sauce:options", sauceOpts);
        driver = getDriver();

        String sauceUrl = "https://ondemand.saucelabs.com/wd/hub";
        URL url = new URL(sauceUrl);
        driver.get(sauceUrl);
//        driver = new RemoteWebDriver(url, browserOptions);

    }

    public WebDriver getDriver(){
        driver = null;
        if (browser.equalsIgnoreCase("Chrome")) {
            WebDriverManager.chromedriver().setup();
//                System.setProperty("webdriver.chrome.driver","src/test/resources/BrowserDriver/chromedriver");

            ChromeOptions options = new ChromeOptions();
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.default_directory", "/home/atliq/Downloads");
            chromePrefs.put("safebrowsing.enabled", true);

            options.setExperimentalOption("prefs", chromePrefs);
            options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
            options.setExperimentalOption("useAutomationExtension", false);
            options.setAcceptInsecureCerts(true);

            options.addArguments("--safebrowsing-disable-download-protection");
            options.addArguments("--test-type");
            options.addArguments("--incognito");
            options.addArguments("--disable-extensions");
            options.addArguments("disable-infobars");
//                options.addArguments("--window-size=1920,1080");
//                options.addArguments("--headless");


            driver = new ChromeDriver(options);
        } else if ((browser.equalsIgnoreCase("edge"))) {
            WebDriverManager.edgedriver().setup();
            EdgeOptions op = new EdgeOptions();
            op.setCapability("InPrivate", true);
            //            System.setProperty("webdriver.edge.driver", "src\\test\\Resources\\BrowserDriver\\IEDriverServer.exe");
            driver = new EdgeDriver(op);

        } else if ((browser.equalsIgnoreCase("ie"))) {
            WebDriverManager.iedriver().setup();

            //            System.setProperty("webdriver.ie.driver", "src\\test\\Resources\\BrowserDriver\\IEDriverServer.exe");
            driver = new InternetExplorerDriver();

        } else {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        }
        return driver;
    }

}
