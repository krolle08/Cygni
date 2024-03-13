package service;

import Application.api.MusicBrainzSearchRoute;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class testMockMusicBrainzSearchRoute {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private MusicBrainzSearchRoute musicBrainzSearchRoute;
    @Test
    public void testSearchArtist() throws URISyntaxException {
        // Mock the response from the external service
        String artistId = "5b11f4ce-a62d-471e-81fc-a69a8278c7da";
        String responseBody = "Nirvana";

        // Create a mock ResponseEntity
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        // Stub the behavior of RestTemplate.getForEntity to return the mock ResponseEntity
        Mockito.lenient().when(restTemplate.getForEntity(new URI("http://musicbrainz.org/ws/2/artist/" + artistId + "?inc=aliases"), String.class))
                .thenReturn(responseEntity);


        // Create a mock ResponseEntity
        ResponseEntity<String> result = musicBrainzSearchRoute.searchArtist("5b11f4ce-a62d-471e-81fc-a69a8278c7da");

        // Verify the result
            assertThat(result.getBody()).contains(responseBody);
            assertThat(result.getStatusCode().equals(HttpStatus.OK));
    }


}