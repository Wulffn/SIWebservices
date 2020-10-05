package dk.mwnck.carapi.scraper;

import dk.mwnck.carapi.constants.Country;
import dk.mwnck.carapi.pojo.Car;

import java.util.List;

public interface Scraper {

    Country getCountry();
    List<Car> getCars(Car searchCar);
}
