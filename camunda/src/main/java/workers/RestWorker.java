package workers;

import com.google.gson.reflect.TypeToken;
import constants.Country;
import org.camunda.bpm.client.ExternalTaskClient;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.gson.*;
import pojo.Car;

public class RestWorker {
    private static final Logger LOGGER = Logger.getLogger(RestWorker.class.getName());

    public static void main(String[] args) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8082/engine-rest")
                .asyncResponseTimeout(10000)
                .build();


        // subscribe to "parse-cars"
        client.subscribe("parse-cars")
            .lockDuration(1000)
            .handler((externalTask, externalTaskService) -> {
                StringBuffer cars = externalTask.getVariable("cars");

                Gson gson = new Gson();
                Type listOfCarObjects = new TypeToken<List<Car>>(){}.getType();
                List<Car> carList = gson.fromJson(cars.toString(), listOfCarObjects);

                // split the cars into taxated (domestic) and import cars.
                List<Car> taxatedCars = carList.stream().filter(c -> c.getCountry().equals(Country.DK)).collect(Collectors.toList());
                List<Car> importCars = carList.stream().filter(c -> c.getCountry().equals(Country.DE)).collect(Collectors.toList());

                String envelope = createXmlEnvelope(carsToXml(taxatedCars, true), carsToXml(importCars, false));
                System.out.println(envelope);
                String duty = "";
                // call SOAP here.
                try
                {
                    URL url = new URL("http://localhost:8080/ws");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "text/xml");
                    OutputStream os = con.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                    osw.write(envelope);
                    osw.flush();
                    osw.close();
                    os.close();

                    con.connect();

                    String result;
                    BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    int result2 = bis.read();
                    while(result2 != -1)
                    {
                        buf.write((byte)result2);
                        result2 = bis.read();
                    }
                    result = buf.toString();
                    // Q&D grab the <ns2:duty> tag.
                    duty = result.substring(result.indexOf("<ns2:duty>")+10, result.indexOf("</ns2:duty>"));
                    System.out.println("Amount to pay: " + duty);

                    con.disconnect();
                }
                catch(Exception e)
                {
                    System.out.println("BURN, no SOAP for you: " + e.getMessage());
                }

                HashMap<String, Object> variables = new HashMap<>();
                variables.put("duty", duty);
                externalTaskService.complete(externalTask,variables);

            }).open();


        // subscribe to external task topic "scrape-cars" as specified in BPMN
        client.subscribe("scrape-cars")
                .lockDuration(1000)
                .handler((externalTask, externalTaskService) -> {
                    System.out.println("SCRAPING...");
                    Map<String, Object> variables = new HashMap<String, Object>();


                    // call REST here.
                    try {
                        URL url = new URL("http://localhost:8081/car/kia/picanto/2012/50000/DK/DE");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");
                        con.setRequestProperty("Content-Type", "application/json");
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer content = new StringBuffer();
                        while((inputLine = in.readLine()) != null)
                            content.append(inputLine);
                        in.close();
                        con.disconnect();

                        System.out.println(content);
                        variables.put("count", 1);
                        variables.put("cars", content);

                    }
                    catch(Exception e)
                    {
                        System.out.println("BURN: " + e.getMessage());
                        variables.clear();
                        variables.put("count", 0);
                    }
                    System.out.println("Submitting result to camunda");

                    externalTaskService.complete(externalTask, variables);
                    System.out.println("DONE");

                }).open();
    }

    private static String createXmlEnvelope(String fromCars, String toCars) {
        StringBuilder sb = new StringBuilder();
        sb.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <Body>\n" +
                "        <getCarDutyRequest xmlns=\"http://www.mwnck.dk/springsoap/gen\">\n");
        sb.append(fromCars).append(toCars);
        sb.append("      </getCarDutyRequest>\n" +
                "    </Body>\n" +
                "</Envelope>");
        return sb.toString();
    }

    private static String carsToXml(List<Car> cars, boolean isTaxated)
    {
        StringBuilder sb = new StringBuilder();
        for(Car car : cars) {

            if ((isTaxated)) {
                sb.append("<taxatedCar><price>" + car.getPriceInWholeNumber() + "</price></taxatedCar>");
            } else {
                sb.append("<car><price>" + car.getPriceInWholeNumber() + "</price></car>");
            }
        }
        return sb.toString();

    }

}
