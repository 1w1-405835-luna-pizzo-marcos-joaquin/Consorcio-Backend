package ar.edu.utn.frc.tup.lc.iv.client;


import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class OwnerRestClient {
    private final RestTemplate restTemplate;
    private static final String ROOT_URL = "https://my-json-server.typicode.com/EbeltramoUtn/demoTP";
    @Autowired
    public OwnerRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<OwnerDto[]> getOwnerPlot() {
        String url = ROOT_URL + "/owners";
        return restTemplate.getForEntity(url, OwnerDto[].class);
    }
}