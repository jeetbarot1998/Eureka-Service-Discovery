package io.javabrains.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class MovieInfo {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "getFallBackCatalogItem",
    commandProperties = {
//            Execution Timeout: Defines the timeout duration for the command execution.
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
//            Circuit Breaker Request Volume Threshold: Specifies the minimum number of requests needed before the circuit breaker will consider tripping.
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "20"),
//            Circuit Breaker Sleep Window: Determines how long the circuit breaker should stay open before retrying.
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
//            Error Threshold Percentage: The error percentage at which the circuit breaker will trip open.
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
    })
    public CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
        return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
    }

    public CatalogItem getFallBackCatalogItem(Rating rating){
        return new CatalogItem("Movie not found","", rating.getRating());
    }
}
