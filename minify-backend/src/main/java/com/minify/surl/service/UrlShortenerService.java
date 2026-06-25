package com.minify.surl.service;

import com.minify.surl.entity.UrlEntity;
import com.minify.surl.repository.UrlRepository;
import com.minify.surl.util.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^https?://[\\w\\-.]+(:\\d+)?(/[\\w\\-./?=%&+#]*)?$"
    );

    private final UrlRepository urlRepository;

    @Transactional
    public UrlEntity createShortUrl(String originalUrl) {
        validateUrl(originalUrl);
        return urlRepository.findByOriginalUrl(originalUrl)
                .orElseGet(() -> {
                    String code;
                    do {
                        code = ShortCodeGenerator.generate();
                    } while (urlRepository.existsByShortCode(code));

                    return urlRepository.save(
                            UrlEntity.builder()
                                    .originalUrl(originalUrl)
                                    .shortCode(code)
                                    .build()
                    );
                });
    }

    public UrlEntity resolveShortUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short code not found"));
    }

    @Async
    public void incrementClickCountAsync(String shortCode) {
        urlRepository.incrementClickCount(shortCode);
    }

    private void validateUrl(String url) {
        if (url == null || !URL_PATTERN.matcher(url).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL");
        }
    }
}
