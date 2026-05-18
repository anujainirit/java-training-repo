package com.javatraining.m01.oop;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 * ══════════════════════════════════════════════════════════════
 */
@DisplayName("M01-T2: Vehicle Toll Calculator — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VehicleTollTest {

    // ── Car toll ────────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("Car toll = distance * 2")
    void carBasicToll() {
        Vehicle car = new Car("KA01AB1234", false);
        assertThat(car.computeToll(10)).isEqualTo(20.0);
    }

    @Test @Order(2)
    @DisplayName("Electric car gets 20% discount")
    void electricCarDiscount() {
        Vehicle car = new Car("KA01AB5678", true);
        // 10km * ₹2 = ₹20, 20% off = ₹16
        assertThat(car.computeToll(10)).isEqualTo(16.0);
    }

    // ── Truck toll ──────────────────────────────────────────────────────────

    @Test @Order(3)
    @DisplayName("Truck with 2 axles: no surcharge, rate ₹5/km")
    void truckTwoAxlesNoSurcharge() {
        Vehicle truck = new Truck("MH12CD9999", false, 2);
        assertThat(truck.computeToll(10)).isEqualTo(50.0);
    }

    @Test @Order(4)
    @DisplayName("Truck with 4 axles: ₹50 surcharge per extra axle (2 extra)")
    void truckFourAxlesSurcharge() {
        Vehicle truck = new Truck("MH12CD8888", false, 4);
        // 10km * ₹5 = ₹50 + (4-2)*₹50 = ₹50+₹100 = ₹150
        assertThat(truck.computeToll(10)).isEqualTo(150.0);
    }

    @Test @Order(5)
    @DisplayName("Electric truck gets 20% discount including surcharge")
    void electricTruckDiscount() {
        Vehicle truck = new Truck("MH12CD7777", true, 4);
        // base = ₹150, 20% off = ₹120
        assertThat(truck.computeToll(10)).isEqualTo(120.0);
    }

    @Test @Order(6)
    @DisplayName("Truck with < 2 axles throws IllegalArgumentException")
    void truckInvalidAxles() {
        assertThatThrownBy(() -> new Truck("XX00XX0000", false, 1))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Truck("XX00XX0001", false, 0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // ── Motorcycle toll ─────────────────────────────────────────────────────

    @Test @Order(7)
    @DisplayName("Motorcycle toll = distance * 1")
    void motorcycleBasicToll() {
        Vehicle moto = new Motorcycle("TN09EF3333", false);
        assertThat(moto.computeToll(50)).isEqualTo(50.0);
    }

    @Test @Order(8)
    @DisplayName("Electric motorcycle gets 20% discount")
    void electricMotorcycleDiscount() {
        Vehicle moto = new Motorcycle("TN09EF4444", true);
        // 50km * ₹1 = ₹50, 20% off = ₹40
        assertThat(moto.computeToll(50)).isEqualTo(40.0);
    }

    // ── Rounding ────────────────────────────────────────────────────────────

    @Test @Order(9)
    @DisplayName("Toll is rounded to 2 decimal places")
    void tollRoundedToTwoDecimals() {
        Vehicle car = new Car("KA01ZZ0001", true);
        // 3km * ₹2 = ₹6, 20% off = ₹4.8 (already 2dp)
        // Use distance that forces rounding
        double toll = car.computeToll(3.333);
        // 3.333 * 2 = 6.666, * 0.8 = 5.333 → rounded to 5.33
        assertThat(toll).isEqualTo(5.33);
    }

    // ── Validation ──────────────────────────────────────────────────────────

    @ParameterizedTest @Order(10)
    @DisplayName("computeToll() throws for non-positive distance")
    @ValueSource(doubles = {0, -1, -100.5})
    void computeTollRejectsNonPositiveDistance(double badDistance) {
        Vehicle car = new Car("KA01AB0000", false);
        assertThatThrownBy(() -> car.computeToll(badDistance))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest @Order(11)
    @DisplayName("registrationNumber must not be null or blank")
    void nullOrBlankRegThrows(String blank) {
        // constructor should throw for null/blank
    }

    // ── TollBooth ───────────────────────────────────────────────────────────

    @Test @Order(12)
    @DisplayName("TollBooth.process() returns correct receipt")
    void boothProcessReturnsReceipt() {
        TollBooth booth = new TollBooth("BOOTH-01");
        Vehicle car = new Car("KA01AB1234", false);
        TollReceipt receipt = booth.process(car, 20.0);

        assertThat(receipt.registrationNumber).isEqualTo("KA01AB1234");
        assertThat(receipt.boothId).isEqualTo("BOOTH-01");
        assertThat(receipt.distanceKm).isEqualTo(20.0);
        assertThat(receipt.amountCharged).isEqualTo(40.0); // 20 * ₹2
    }

    @Test @Order(13)
    @DisplayName("TollBooth with blank ID throws IllegalArgumentException")
    void boothBlankIdThrows() {
        assertThatThrownBy(() -> new TollBooth(""))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TollBooth(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // ── Pattern matching ────────────────────────────────────────────────────

    @Test @Order(14)
    @DisplayName("Vehicle type identified via sealed interface pattern matching")
    void patternMatchingVehicleType() {
        Vehicle v = new Truck("DL09GH1111", false, 3);
        String type = switch (v) {
            case Car c -> "Car";
            case Truck t -> "Truck";
            case Motorcycle m -> "Motorcycle";
        };
        assertThat(type).isEqualTo("Truck");
    }

    @Test @Order(15)
    @DisplayName("isElectric() returns correct value for each vehicle type")
    void isElectricFlag() {
        assertThat(new Car("A", true).isElectric()).isTrue();
        assertThat(new Car("B", false).isElectric()).isFalse();
        assertThat(new Truck("C", true, 2).isElectric()).isTrue();
        assertThat(new Motorcycle("D", false).isElectric()).isFalse();
    }
}
