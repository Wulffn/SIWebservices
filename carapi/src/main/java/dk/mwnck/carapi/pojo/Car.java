package dk.mwnck.carapi.pojo;

public class Car {

    private String manufacturer, model, version;
    private int year;
    private String price;

    public Car(String manufacturer, String model, int year) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
    }

    public Car(String manufacturer, String model, String version, int year, String price) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.version = version;
        this.year = year;
        this.price = price;
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

    public void setVersion(String version) {
        this.version = version;
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
