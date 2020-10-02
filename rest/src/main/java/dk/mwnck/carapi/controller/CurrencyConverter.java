package dk.mwnck.carapi.controller;

import dk.mwnck.carapi.constants.Currency;
import dk.mwnck.carapi.pojo.Car;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CurrencyConverter {

    public static void change(Map<String, List<Car>> cars, Currency currency)
    {
        Map<String, Double> rates = null;
        try
        {
            rates = getRates(currency);
        }
        catch(Exception e)
        {
            System.out.println("BURN, no rates for you.");
        }

        if (rates != null) {
            for (String key : cars.keySet()) {
                for (Car car : cars.get(key)) {
                    System.out.println(car.getPrice() + ", " + car.getCurrency());
                    float amount = Float.parseFloat(car.getPrice().replaceAll("[^0-9]", ""));
                    System.out.println(amount);
                    if (car.getCurrency() != currency) {
                        car.setPrice(String.valueOf(amount / rates.get(car.getCurrency().toString())));
                        car.setCurrency(currency);
                    }
                    else
                    {
                        car.setPrice(String.valueOf(amount));
                    }
                }
            }
        }
        else
            System.out.println("BURN, no conversion for you!");



    }

    private static Map<String, Double> getRates(Currency base) throws Exception
    {

        // https://api.exchangeratesapi.io/latest
        JSONObject json = new JSONObject(IOUtils.toString(new URL("https://api.exchangeratesapi.io/latest?base=" + base.toString()), Charset.forName("UTF-8")));
        JSONObject rates = json.getJSONObject("rates");

        Iterator<String> keys = rates.keys();
        Map<String, Double> result = new HashMap<>();
        while(keys.hasNext())
        {
            String key = keys.next();
            result.put(key, rates.getDouble(key));
        }

        System.out.println(result.toString());
        return result;
    }

    public static void main(String[] args) throws Exception{
        getRates(Currency.DKK);
    }

}
