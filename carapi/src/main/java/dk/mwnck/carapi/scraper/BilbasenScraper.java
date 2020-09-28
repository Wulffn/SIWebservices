package dk.mwnck.carapi.scraper;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import dk.mwnck.carapi.constants.Currency;
import dk.mwnck.carapi.pojo.Car;

import java.util.ArrayList;
import java.util.List;

public class BilbasenScraper implements Scraper {
    @Override
    public List<Car> getCars(Car searchCar) {

        List<Car> cars = null;
        List<HtmlElement> items = (List<HtmlElement>) getPage(searchCar).getByXPath("//div[contains(@class,'bb-listing-clickable')]");
        System.out.println("Fandt items: " + items.size());
        if (items.isEmpty()) {
            return null;
        } else {
            cars = new ArrayList<>();

            for (HtmlElement htmlItem : items) {

                HtmlElement version = htmlItem.getFirstByXPath(".//a[contains(@class,'listing-heading')]");
                System.out.print(version.asText());
                HtmlElement price = htmlItem.getFirstByXPath(".//div[contains(@class, 'col-xs-3 listing-price')]");
                System.out.println(", " +  price.asText());
                cars.add(new Car(searchCar.getManufacturer(), searchCar.getModel(), version.asText(), searchCar.getYear(), price.asText(), Currency.DKK));
            }
        }

        return cars;

    }

    private HtmlPage getPage(Car car) {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        try {
            String searchUrl = concatURL(car);
            //System.out.println(client.getPage(searchUrl));
            return client.getPage(searchUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String concatURL(Car car) {
        StringBuilder sb = new StringBuilder();
        // https://www.bilbasen.dk/brugt/bil/Kia/Picanto?YearFrom=2012&YearTo=2012
        sb.append("https://www.bilbasen.dk/brugt/bil/")
                .append(car.getManufacturer())
                .append("/")
                .append(car.getModel())
                .append("?yearFrom=")
                .append(car.getYear())
                .append("&yearTo=")
                .append(car.getYear());
        return sb.toString();
    }

    public static void main(String[] args) {
        new BilbasenScraper().getCars(new Car("kia", "picanto", 2012));
    }
}


