package dk.mwnck.carapi.controller;

import dk.mwnck.carapi.constants.Country;
import dk.mwnck.carapi.constants.Currency;
import dk.mwnck.carapi.constants.Scrapers;
import dk.mwnck.carapi.pojo.Car;
import dk.mwnck.carapi.scraper.*;
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
    @GetMapping("car/{manu}/{model}/{year}/{mileage}/{fromCountry}/{toCountry}")
    public ResponseEntity<List<Car>> getCars(@PathVariable String manu, @PathVariable String model, @PathVariable int year, @PathVariable int mileage, @PathVariable String fromCountry, @PathVariable String toCountry) throws InterruptedException {
        Car searchCar = new Car(manu, model, year, mileage);
        Country from = Country.valueOf(Country.class, fromCountry.toUpperCase());
        Country to = Country.valueOf(Country.class, toCountry.toUpperCase());

//        Map<String, List<Car>> cars = new HashMap();
//        Scrapers selectedScraper = Scrapers.valueOf(Scrapers.class, source.toUpperCase());
//
//        switch(selectedScraper)
//        {
//            case AUTOSCOUT:
//                cars.put("autoscout24", autoScout24Scraper.getCars(searchCar));
//                break;
//            case BILBASEN:
//                cars.put("bilbasen", bilbasenScraper.getCars(searchCar));
//                break;
//            case MOBILEDE:
//                cars.put("mobileDe", mobileScraper.getCars(searchCar));
//                break;
//            default:
//                System.out.println("BURN, no cars for you.");
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }



        return new ResponseEntity<>(ScraperService.getCars(searchCar, from, to), HttpStatus.ACCEPTED);
    }

    @CrossOrigin
    @GetMapping("car/{manu}/{model}/{year}/{mileage}/{fromCountry}/{toCountry}/{currency}")
    public ResponseEntity<List<Car>> getCars(@PathVariable String manu, @PathVariable String model, @PathVariable int year, @PathVariable int mileage, @PathVariable String fromCountry, @PathVariable String toCountry, @PathVariable String currency) throws InterruptedException {
        ResponseEntity<List<Car>> response = getCars(manu, model, year, mileage, fromCountry, toCountry);
        List<Car> cars = response.getBody();
        CurrencyConverter.change(cars, Currency.valueOf(currency.toUpperCase()));
        return new ResponseEntity<>(cars, HttpStatus.ACCEPTED);
    }
}
