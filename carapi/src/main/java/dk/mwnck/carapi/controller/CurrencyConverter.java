package dk.mwnck.carapi.controller;

import dk.mwnck.carapi.constants.Currency;
import dk.mwnck.carapi.pojo.Car;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;



public class CurrencyConverter {

    public static void change(List<Car> cars, Currency currency)
    {
        for(Car car : cars)
        {
            System.out.println(car.getPrice() + ", " +  car.getCurrency());
            float amount = Float.parseFloat(car.getPrice().replaceAll("[^0-9]",""));
            System.out.println(amount);
            try
            {
                getRates();
            }
            catch(Exception e){}

        }



    }

    private static void getRates() throws Exception
    {

        // https://api.exchangeratesapi.io/latest
        JSONObject json = new JSONObject(IOUtils.toString(new URL("https://api.exchangeratesapi.io/latest"), Charset.forName("UTF-8")));
        System.out.println(json.toString());
    }

}
