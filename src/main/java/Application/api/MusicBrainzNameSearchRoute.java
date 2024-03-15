package Application.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MusicBrainzNameSearchRoute {

    /*  @Value("${musicBrainz.protocol}")
      private String PROTOCOL;
      @Value("${musicBrainz.host}")
      private String HOST;
      @Value("${musicBrainz.host}")
      private int PORT;
      @Value("${musicBrainz.servicePath}")

     */
    private Log log  = LogFactory.getLog(MusicBrainzNameSearchRoute.class);
    private String protocol = "http";
    private String schemeDelimiter ="://";
    private String version = "/2";
    private String pathPrefix = "/ws";
    private String annotation = "/annotation";
    private String query = "/?query=";
    private String json = "fmt=json";
    private String host = "musicbrainz.org";
    private Integer port = 80;

    @GetMapping("/MBID/{Id}")
    public Map<String, String> getMBIDAndDescription(@PathVariable Map<String,String> filterParams) throws URISyntaxException {
        String url = constructUrl(filterParams).toString();
        RestTemplate restTemplate = restTemplate();
        URI uri = new URI(url);
        restTemplate.getForEntity(uri, String.class);
        Map<String, String> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(restTemplate.getForEntity(uri, String.class).getBody());
            // Iterate through the array of objects
            for (JsonNode node : rootNode) {
                // Check if the object has type "artist"
                if ("artist".equals(node.get("type").asText())) {
                    // Extract the entity field
                    result.put("MBID", node.get("entity").asText());
                    result.put("Description", node.get("text").asText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private StringBuffer constructUrl(Map<String, String> filterParams) throws URISyntaxException {
        StringBuffer url = new StringBuffer();
        url.append(protocol).append(schemeDelimiter).append(host);
        if(port != null){
            url.append(":").append(port);
        }

        url.append(pathPrefix).append(version).append(annotation).append(query);
        if(filterParams == null){
            log.info("No parameter for the search has been provided");
        } else{
            // Append each parameter from filterParams
            for(Map.Entry<String, String> entry : filterParams.entrySet()){
                String paramName = entry.getKey();
                String paramValue = entry.getValue();
                // Append parameter name and value to URL
                url.append(paramName).append(":").append(paramValue);
            }
        }
        url.append("&").append(json);
        return url;
    }

    public RestTemplate restTemplate() {
        HttpHost proxy = new HttpHost(host, port);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        return new RestTemplateBuilder().requestFactory(() ->
                new HttpComponentsClientHttpRequestFactory(HttpClientBuilder
                        .create()
                        .setDefaultRequestConfig(config)
                        .build()))
                .build();
    }
}
















