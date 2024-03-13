package Application.api;

import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.apache.http.client.config.RequestConfig;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class MusicBrainzSearchRoute {

    /*  @Value("${musicBrainz.protocol}")
      private String PROTOCOL;
      @Value("${musicBrainz.host}")
      private String HOST;
      @Value("${musicBrainz.host}")
      private int PORT;
      @Value("${musicBrainz.servicePath}")

     */
    private String protocol = "http";
    private String host = "musicbrainz.org";
    private int port = 80;

    private static final String BASE_PATH = "/ws/2/";

    private static final String END_PATH = "?inc=aliases";
    public final String MUSIC_BRAINZ_URI = new URIBuilder()
            .setScheme(protocol)
            .setHost(host)
            .setPort(port)
            //  .setPath(SERVICEPATH)
            .toString();

    @GetMapping("/MBArtist/{artistId}")
    public String searchArtist(@PathVariable String artistId) throws URISyntaxException {
        String fullPath = constructUrl(BASE_PATH + "artist/" + artistId + END_PATH);
        RestTemplate restTemplate = restTemplate();
        URI uri = new URI(fullPath);
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        return response.getBody();
    }

    private String constructUrl(String path) {
        return protocol + "://" + host + ":" + port + path;
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