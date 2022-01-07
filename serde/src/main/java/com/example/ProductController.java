package com.example;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/product")
class ProductController {

    @Get
    HttpResponse<?> index() {
        return HttpResponse
                .badRequest("{\"type\":\"https://example.org/out-of-stock\",\"title\":\"Out of Stock\",\"detail\":\"Item B00027Y5QG is no longer available\",\"parameters\":{\"product\":\"B00027Y5QG\"}}")
                .contentType("application/problem+json");
    }
}
