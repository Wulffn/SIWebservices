package workers;

import org.camunda.bpm.client.ExternalTaskClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RestWorker {
    private static final Logger LOGGER = Logger.getLogger(RestWorker.class.getName());

    public static void main(String[] args) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8082/engine-rest")
                .asyncResponseTimeout(10000)
                .build();

        // subscribe to external task topic as specified in BPMN
        client.subscribe("scrape-cars")
                .lockDuration(1000)
                .handler((externalTask, externalTaskService) -> {
                    System.out.println("SCRAPING...");
                    Map<String, Object> variables = new HashMap<String, Object>();

                    // call rest here.
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
}
