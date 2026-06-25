package com.minify.surl.repository;

import com.minify.surl.entity.UrlEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    private UrlEntity save(String originalUrl, String shortCode) {
        return urlRepository.save(
                UrlEntity.builder()
                        .originalUrl(originalUrl)
                        .shortCode(shortCode)
                        .build()
        );
    }

    @Test
    void findByShortCode_returnsEntity() {
        save("https://example.com", "abc1234");

        Optional<UrlEntity> result = urlRepository.findByShortCode("abc1234");

        assertThat(result).isPresent();
        assertThat(result.get().getOriginalUrl()).isEqualTo("https://example.com");
    }

    @Test
    void findByShortCode_unknownCode_returnsEmpty() {
        assertThat(urlRepository.findByShortCode("unknown")).isEmpty();
    }

    @Test
    void existsByShortCode_trueWhenPresent() {
        save("https://example.com", "abc1234");

        assertThat(urlRepository.existsByShortCode("abc1234")).isTrue();
        assertThat(urlRepository.existsByShortCode("missing")).isFalse();
    }

    @Test
    void findByOriginalUrl_returnsEntity() {
        save("https://example.com", "abc1234");

        Optional<UrlEntity> result = urlRepository.findByOriginalUrl("https://example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getShortCode()).isEqualTo("abc1234");
    }
}
