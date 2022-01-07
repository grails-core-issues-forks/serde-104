package com.example

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.uri.UriBuilder
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class ProductSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient httpClient
    void "problem is render to map with serde"() throws IOException {
        given:
        BlockingHttpClient client = httpClient.toBlocking()
        when:
        Argument<?> okArg = Argument.of(String.class)
        Argument<?> errorArg = Argument.of(Map.class)
        client.exchange(HttpRequest.GET(UriBuilder.of("/product").build()), okArg, errorArg)

        then:
        HttpClientResponseException e = thrown(HttpClientResponseException.class)

        then:
        HttpStatus.BAD_REQUEST ==  e.getStatus()
        e.getResponse().getContentType().isPresent()
        "application/problem+json" == e.getResponse().getContentType().get().toString()

        when:
        Optional<Map> bodyOptional = e.getResponse().getBody(Map.class);

        then:
        bodyOptional

        and:
        4 == bodyOptional.get().keySet().size()
        "Out of Stock"== bodyOptional.get().get("title")
        "Item B00027Y5QG is no longer available" == bodyOptional.get().get("detail")
        "https://example.org/out-of-stock" == bodyOptional.get().get("type")
        Collections.singletonMap("product", "B00027Y5QG") == bodyOptional.get().get("parameters")
    }
}
