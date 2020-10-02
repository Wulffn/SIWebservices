package dk.mwnck.carapi.controller;

import dk.mwnck.carapi.constants.Currency;
import dk.mwnck.carapi.constants.Scrapers;
import dk.mwnck.carapi.pojo.Car;
import dk.mwnck.carapi.scraper.AutoScout24Scraper;
import dk.mwnck.carapi.scraper.BilbasenScraper;
import dk.mwnck.carapi.scraper.MobileDeScraper;
import dk.mwnck.carapi.scraper.Scraper;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin
public class RESTController {

    //AutoScout24Scraper scraper = new AutoScout24Scraper();
    Scraper bilbasenScraper = new BilbasenScraper();
    Scraper mobileScraper = new MobileDeScraper();
    Scraper autoScout24Scraper = new AutoScout24Scraper();

    @GetMapping("car/")
    public ResponseEntity<List<String>> getSources()
    {
        // get name of all scrapers values and return as list of strings.
        return new ResponseEntity<List<String>>(Stream.of(Scrapers.values()).map(Scrapers::name).collect(Collectors.toList()), HttpStatus.OK);
    }
    @CrossOrigin
    @GetMapping("car/{manu}/{model}/{year}/{mileage}/{source}")
    public ResponseEntity<Map<String, List<Car>>> getCars(@PathVariable String manu, @PathVariable String model, @PathVariable int year, @PathVariable int mileage, @PathVariable String source) throws InterruptedException {
        Car searchCar = new Car(manu, model, year, mileage);
        Map<String, List<Car>> cars = new HashMap();
        Scrapers selectedScraper = Scrapers.valueOf(Scrapers.class, source.toUpperCase());

        switch(selectedScraper)
        {
            case AUTOSCOUT:
                cars.put("autoscout24", autoScout24Scraper.getCars(searchCar));
                break;
            case BILBASEN:
                cars.put("bilbasen", bilbasenScraper.getCars(searchCar));
                break;
            case MOBILEDE:
                cars.put("mobileDe", mobileScraper.getCars(searchCar));
                break;
            default:
                System.out.println("BURN, no cars for you.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        /*List<Callable<List<Car>>> tasks = new ArrayList<>();
        tasks.add(() -> cars.put("bilbasen", bilbasenScraper.getCars(searchCar)));
        tasks.add(()-> cars.put("autoscout24", autoScout24Scraper.getCars(searchCar)));
        tasks.add(()-> cars.put("mobileDe", mobileScraper.getCars(searchCar)));
        ExecutorService executor = Executors.newFixedThreadPool(3);

        List<Future<List<Car>>> results = executor.invokeAll(tasks);*/

        return new ResponseEntity<>(cars, HttpStatus.ACCEPTED);
    }

    @CrossOrigin
    @GetMapping("car/{manu}/{model}/{year}/{mileage}/{source}/{currency}")
    public ResponseEntity<Map<String, List<Car>>> getCars(@PathVariable String manu, @PathVariable String model, @PathVariable int year, @PathVariable int mileage, @PathVariable String source, @PathVariable String currency) throws InterruptedException {
        ResponseEntity<Map<String, List<Car>>> response = getCars(manu, model, year, mileage, source);
        Map<String, List<Car>> cars = response.getBody();
        CurrencyConverter.change(cars, Currency.valueOf(currency));
        return new ResponseEntity<>(cars, HttpStatus.ACCEPTED);
    }
}
