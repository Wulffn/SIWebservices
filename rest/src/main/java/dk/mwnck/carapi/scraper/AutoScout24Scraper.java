package dk.mwnck.carapi.scraper;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import dk.mwnck.carapi.constants.Currency;
import dk.mwnck.carapi.pojo.Car;

import java.util.ArrayList;
import java.util.List;

public class AutoScout24Scraper implements Scraper {


    public List<Car> getCars(Car searchCar) {
        List<Car> cars = null;
        List<HtmlElement> items = (List<HtmlElement>) getPage(searchCar).getByXPath("//div[@class='cldt-summary-full-item-main']");
        if (items.isEmpty()) {
            return null;
        } else {
            cars = new ArrayList<>();
            for (HtmlElement htmlItem : items) {
                HtmlElement version = ((HtmlElement) htmlItem.getFirstByXPath(".//h2[@class='cldt-summary-version sc-ellipsis']"));
                HtmlElement price = ((HtmlElement) htmlItem.getFirstByXPath(".//span[@class='cldt-price sc-font-xl sc-font-bold']"));
                HtmlElement km = (HtmlElement) htmlItem.getFirstByXPath(".//li[@data-type='mileage']");

                int intKm = Integer.parseInt(km.asText().replaceAll("[^0-9]+", ""));

                cars.add(new Car(searchCar.getManufacturer(), searchCar.getModel(), version.asText(), searchCar.getYear(), price.asText(), intKm, Currency.EUR));
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
            return client.getPage(searchUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String concatURL(Car car) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://www.autoscout24.com/lst/")
                .append(car.getManufacturer())
                .append("/")
                .append(car.getModel())
                .append("?sort=standard&desc=0&ustate=N%2CU&size=20&page=1&fregto=")
                .append(car.getYear())
                .append("&fregfrom=")
                .append(car.getYear())
                .append("&atype=C&")
                .append("&kmto=")
                .append(car.getKm()+20000)
                .append("&kmfrom=")
                .append(car.getKm()-20000);
        return sb.toString();
    }
}
