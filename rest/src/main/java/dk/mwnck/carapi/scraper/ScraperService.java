package dk.mwnck.carapi.scraper;

import dk.mwnck.carapi.constants.Country;
import dk.mwnck.carapi.pojo.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ScraperService {

    static Scraper[] scrapers = {new AutoScout24Scraper(), new MobileDeScraper(), new BilbasenScraper()};

    public static List<Car> getCars(Car searchCar, Country from, Country to) throws InterruptedException {
        List<Callable<Boolean>> tasks = new ArrayList<>();
        List<Car> cars = new ArrayList();

        for (int i = 0; i < scrapers.length; i++) {
            int finalI = i;
            tasks.add(() -> cars.addAll(scrapers[finalI].getCars(searchCar)));
        }

//        tasks.add(() -> cars.put("dk", bilbasenScraper.getCars(searchCar)));
//        tasks.add(()-> cars.put("de", autoScout24Scraper.getCars(searchCar)));
//        tasks.add(()-> cars.put("de", mobileScraper.getCars(searchCar)));
        ExecutorService executor = Executors.newFixedThreadPool(3);

        List<Future<Boolean>> results = executor.invokeAll(tasks);
        return cars;
    }
}
