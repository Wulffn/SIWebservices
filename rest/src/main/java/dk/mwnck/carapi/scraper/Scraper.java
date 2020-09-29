package dk.mwnck.carapi.scraper;

import dk.mwnck.carapi.pojo.Car;

import java.util.List;

public interface Scraper {

    List<Car> getCars(Car searchCar);
}
