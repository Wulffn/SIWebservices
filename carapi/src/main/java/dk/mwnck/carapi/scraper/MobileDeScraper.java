package dk.mwnck.carapi.scraper;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import dk.mwnck.carapi.constants.Currency;
import dk.mwnck.carapi.pojo.Car;

import java.util.ArrayList;
import java.util.List;

public class MobileDeScraper implements Scraper {
    @Override
    public List<Car> getCars(Car searchCar) {

        List<Car> cars = null;
        List<HtmlElement> items = (List<HtmlElement>) getPage(searchCar).getByXPath("//div[contains(@class,'cBox-body')]");
        System.out.println("Fandt items: " + items.size());
        if (items.isEmpty()) {
            return null;
        } else {
            cars = new ArrayList<>();

            for (HtmlElement htmlItem : items) {

                HtmlElement version = htmlItem.getFirstByXPath(".//span[contains(@class,'h3 u-text-break-word')]");
                System.out.print(version.asText());
                HtmlElement price = htmlItem.getFirstByXPath(".//span[contains(@class, 'h3 u-block')]");
                System.out.println(", " +  price.asText());
                cars.add(new Car(searchCar.getManufacturer(), searchCar.getModel(), version.asText(), searchCar.getYear(), price.asText(), Currency.EUR));
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
            System.out.println("Going to : " + searchUrl);
            //System.out.println(client.getPage(searchUrl));
            return client.getPage(searchUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String concatURL(Car car) {
        StringBuilder sb = new StringBuilder();
        //https://suchen.mobile.de/auto/opel-meriva-2012.html
        sb.append("https://suchen.mobile.de/auto/")
                .append(car.getManufacturer())
                .append("-")
                .append(car.getModel())
                .append("-")
                .append(car.getYear())
                .append(".html");

        return sb.toString();
    }

    public static void main(String[] args) {
        new MobileDeScraper().getCars(new Car("kia", "picanto", 2012));
    }
}


