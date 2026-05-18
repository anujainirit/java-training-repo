package com.javatraining.m00.intro;

import com.javatraining.m00.intro.Calculator;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 * ══════════════════════════════════════════════════════════════
 */
@DisplayName("M00-T1: Calculate Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CalculatorTest {

    @Test @Order(1)
    @DisplayName("Car toll = distance * 2")
    void carBasicToll() {
        Calculator calculator = new Calculator();
        assertThat(calculator.calculate(10,20)).isEqualTo(30);
    }
}
