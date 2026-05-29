package com.javatraining.m00.intro;

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

    @Test
    @Order(1)
    @DisplayName("Car toll = distance * 2")
    void carBasicToll() {
        Calculator calculator = new Calculator();
        assertThat(calculator.calculate(10,20)).isEqualTo(30);
        assertThat(calculator.multiply(10,20)).isEqualTo(200);
        assertThat(calculator.divide(10,20)).isEqualTo(0);
    }

    @Test
    @Order(2)
    @DisplayName("Multiply Test")
    void multiplyTest() {
        Calculator calculator = new Calculator();
        assertThat(calculator.multiply(10, 5)).isEqualTo(50);
    }

    @Test
    @Order(3)
    @DisplayName("Divide Test")
    void divideTest() {
        Calculator calculator = new Calculator();
        assertThat(calculator.divide(20, 5)).isEqualTo(4);
    }

    @Test
    @Order(4)
    @DisplayName("Divide by One Test")
    void divideByOneTest() {
        Calculator calculator = new Calculator();
        assertThat(calculator.divide(10, 1)).isEqualTo(10);

    }

    @Test @Order(4)
    @DisplayName("Divide by zero throws ArithmeticException")
    void divideByZeroTest() {
        Calculator calculator = new Calculator();
        assertThatThrownBy(() -> calculator.divide(10, 0))
            .isInstanceOf(ArithmeticException.class);
    }
    @Test
    @Order(2)
    @DisplayName("Multiply Test")
    void multiplyTest() {
        Calculator calculator = new Calculator();
        assertThat(calculator.multiply(10, 5)).isEqualTo(50);
    }

    @Test
    @Order(3)
    @DisplayName("Divide Test")
    void divideTest() {
        Calculator calculator = new Calculator();
        assertThat(calculator.divide(20, 5)).isEqualTo(4);
    }

    @Test
    @Order(4)
    @DisplayName("Divide by One Test")
    void divideByOneTest() {
        Calculator calculator = new Calculator();
        assertThat(calculator.divide(10, 1)).isEqualTo(10);
    }
}
