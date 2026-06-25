package com.minify.surl.controller;

import com.minify.surl.entity.UrlEntity;
import com.minify.surl.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/api/shorten")
    public ResponseEntity<Map<String, String>> shorten(@RequestBody Map<String, String> body) {
        String originalUrl = body.get("url");
        UrlEntity entity = urlShortenerService.createShortUrl(originalUrl);
        return ResponseEntity.ok(Map.of(
                "shortCode", entity.getShortCode(),
                "shortUrl", "/r/" + entity.getShortCode()
        ));
    }

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        UrlEntity entity = urlShortenerService.resolveShortUrl(shortCode);
        urlShortenerService.incrementClickCountAsync(shortCode);
        return ResponseEntity.status(301)
                .location(URI.create(entity.getOriginalUrl()))
                .build();
    }
}
