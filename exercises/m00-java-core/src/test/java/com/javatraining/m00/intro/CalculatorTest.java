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

    @Test @Order(2)
    @DisplayName("Multiply two numbers")
    void multiplyTest() {
        Calculator calculator = new Calculator();
        assertThat(calculator.multiply(3, 4)).isEqualTo(12);
        assertThat(calculator.multiply(-2, 5)).isEqualTo(-10);
        assertThat(calculator.multiply(0, 100)).isEqualTo(0);
    }

    @Test @Order(3)
    @DisplayName("Divide two numbers")
    void divideTest() {
        Calculator calculator = new Calculator();
        assertThat(calculator.divide(20, 4)).isEqualTo(5);
        assertThat(calculator.divide(-15, 3)).isEqualTo(-5);
    }

    @Test @Order(4)
    @DisplayName("Divide by zero throws ArithmeticException")
    void divideByZeroTest() {
        Calculator calculator = new Calculator();
        assertThatThrownBy(() -> calculator.divide(10, 0))
            .isInstanceOf(ArithmeticException.class);
    }
}
