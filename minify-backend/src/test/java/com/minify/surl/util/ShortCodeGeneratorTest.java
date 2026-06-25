package com.minify.surl.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ShortCodeGeneratorTest {

    @Test
    void generate_lengthIsAlwaysSeven() {
        assertThat(ShortCodeGenerator.generate()).hasSize(7);
    }

    @RepeatedTest(20)
    void generate_onlyBase62Characters() {
        String code = ShortCodeGenerator.generate();
        assertThat(code).matches("[0-9A-Za-z]{7}");
    }

    @Test
    void generate_neverNullOrBlank() {
        String code = ShortCodeGenerator.generate();
        assertThat(code).isNotNull().isNotBlank();
    }

    @Test
    void generate_producesDistinctValues() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            codes.add(ShortCodeGenerator.generate());
        }
        assertThat(codes.size()).isGreaterThan(1);
    }
}
