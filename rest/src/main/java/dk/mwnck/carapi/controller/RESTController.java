package dk.mwnck.carapi.controller;

import dk.mwnck.carapi.constants.Currency;
import dk.mwnck.carapi.pojo.Car;
import dk.mwnck.carapi.scraper.BilbasenScraper;
import dk.mwnck.carapi.scraper.Scraper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RESTController {

    //AutoScout24Scraper scraper = new AutoScout24Scraper();
    Scraper scraper = new BilbasenScraper();

    @GetMapping("car/{manu}/{model}/{year}")
    public ResponseEntity<List<Car>> getCars(@PathVariable String manu, @PathVariable String model, @PathVariable int year) {
        Car searchCar = new Car(manu, model, year);
        List<Car> cars = scraper.getCars(searchCar);
        return new ResponseEntity<>(cars, HttpStatus.ACCEPTED);
    }

    @GetMapping("car/{manu}/{model}/{year}/{currency}")
    public ResponseEntity<List<Car>> getCars(@PathVariable String manu, @PathVariable String model, @PathVariable int year, @PathVariable String currency) {
        ResponseEntity<List<Car>> response = getCars(manu, model, year);
        List<Car> cars = response.getBody();
        CurrencyConverter.change(cars, Currency.DKK);
        return new ResponseEntity<>(cars, HttpStatus.ACCEPTED);
    }
}
