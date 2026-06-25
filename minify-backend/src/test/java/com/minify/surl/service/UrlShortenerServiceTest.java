package com.minify.surl.service;

import com.minify.surl.entity.UrlEntity;
import com.minify.surl.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlShortenerService service;

    @Test
    void createShortUrl_newUrl_savesAndReturnsEntity() {
        when(urlRepository.findByOriginalUrl(anyString())).thenReturn(Optional.empty());
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);

        UrlEntity saved = UrlEntity.builder()
                .originalUrl("https://example.com")
                .shortCode("abc1234")
                .build();
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(saved);

        UrlEntity result = service.createShortUrl("https://example.com");

        verify(urlRepository).save(any(UrlEntity.class));
        assertThat(result.getShortCode()).isNotBlank();
        assertThat(result.getOriginalUrl()).isEqualTo("https://example.com");
    }

    @Test
    void createShortUrl_duplicateUrl_returnsExistingEntityWithoutSaving() {
        UrlEntity existing = UrlEntity.builder()
                .originalUrl("https://example.com")
                .shortCode("existing")
                .build();
        when(urlRepository.findByOriginalUrl("https://example.com")).thenReturn(Optional.of(existing));

        UrlEntity result = service.createShortUrl("https://example.com");

        verify(urlRepository, never()).save(any());
        assertThat(result).isSameAs(existing);
    }

    @Test
    void createShortUrl_shortCodeCollision_retriesUntilUnique() {
        when(urlRepository.findByOriginalUrl(anyString())).thenReturn(Optional.empty());
        // First two codes collide, third is free
        when(urlRepository.existsByShortCode(anyString()))
                .thenReturn(true, true, false);

        UrlEntity saved = UrlEntity.builder()
                .originalUrl("https://example.com")
                .shortCode("newcode")
                .build();
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(saved);

        UrlEntity result = service.createShortUrl("https://example.com");

        verify(urlRepository, times(3)).existsByShortCode(anyString());
        verify(urlRepository).save(any(UrlEntity.class));
        assertThat(result).isNotNull();
    }

    @Test
    void resolveShortUrl_existingCode_returnsEntity() {
        UrlEntity entity = UrlEntity.builder()
                .originalUrl("https://example.com")
                .shortCode("abc1234")
                .build();
        when(urlRepository.findByShortCode("abc1234")).thenReturn(Optional.of(entity));

        UrlEntity result = service.resolveShortUrl("abc1234");

        assertThat(result).isSameAs(entity);
    }

    @Test
    void resolveShortUrl_unknownCode_throwsNotFound() {
        when(urlRepository.findByShortCode("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resolveShortUrl("unknown"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(NOT_FOUND);
    }

    @Test
    void createShortUrl_invalidUrl_throwsBadRequest() {
        assertThatThrownBy(() -> service.createShortUrl("not-a-url"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(BAD_REQUEST);

        verify(urlRepository, never()).save(any());
    }
}
