package dk.mwnck.carapi.pojo;

import dk.mwnck.carapi.constants.Country;
import dk.mwnck.carapi.constants.Currency;

public class Car {

    private String manufacturer, model, version;
    private int year;
    private String price;
    private Currency currency; // the original currency when scraped.
    private int km;
    private Country country;

    public Car(String manufacturer, String model, int year, int km) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
        this.km = km;
    }



    public Car(String manufacturer, String model, String version, int year, String price, int km, Currency currency, Country country) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.version = version;
        this.year = year;
        this.price = price;
        this.km = km;
        this.currency = currency;
        this.country = country;
    }


    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVersion() {
        return version;
    }

    public int getKm() { return km; }

    public void setKm(int km) {
        this.km = km;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Currency getCurrency() { return currency; }

    public void setCurrency(Currency currency) {this.currency = currency;}

    public Country getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "Car{" +
                "manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", version='" + version + '\'' +
                ", year=" + year +
                ", price='" + price + '\'' +
                '}';
    }
}
