package dk.mwnck.springsoap.gen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@CrossOrigin
public class DutyEndpoint
{
    private static final String NAMESPACE_URI = "http://www.mwnck.dk/springsoap/gen";

    /*@Autowired
    public DutyEndpoint(){}*/

    @PayloadRoot(namespace=NAMESPACE_URI, localPart = "getCarDutyRequest")
    @ResponsePayload
    public GetCarDutyResponse getCarDuty(@RequestPayload GetCarDutyRequest request)
    {
        GetCarDutyResponse response = new GetCarDutyResponse();
        response.setDuty(DutyCalculator.calculate(request.getTaxatedCars(), request.getCars()));

        return response;
    }
}
