package com.javatraining.m01.oop;

/**
 * EXERCISE M01-T2: Vehicle Toll Calculator
 *
 * Model a vehicle hierarchy and compute toll fees.
 * See EXERCISE.md for the full specification.
 *
 * Rules:
 *  - Base toll = distance * rate_per_km
 *  - Car: ₹2/km,  axle surcharge = 0
 *  - Truck: ₹5/km, axle surcharge = ₹50 per axle beyond 2
 *  - Motorcycle: ₹1/km, no surcharge
 *  - Electric vehicles of any type get a 20% discount on the final toll
 *
 * DO NOT change method signatures, class names, or package.
 */

// ── Sealed interface — implement all permitted subtypes ──────────────────────
public sealed interface Vehicle permits Car, Truck, Motorcycle {

    /**
     * Return the vehicle's registration number (non-null, non-blank).
     */
    String registrationNumber();

    /**
     * Return whether this vehicle runs on electricity.
     */
    boolean isElectric();

    /**
     * Compute the toll fee for the given distance in km.
     *
     * @param distanceKm positive distance
     * @return toll amount in rupees (rounded to 2 decimal places)
     * @throws IllegalArgumentException if distanceKm <= 0
     */
    double computeToll(double distanceKm);
}

// ── TODO: implement Car ──────────────────────────────────────────────────────
// record Car(String registrationNumber, boolean isElectric) implements Vehicle { ... }

// ── TODO: implement Truck ────────────────────────────────────────────────────
// record Truck(String registrationNumber, boolean isElectric, int axles) implements Vehicle { ... }
// axles must be >= 2; throw IllegalArgumentException otherwise

// ── TODO: implement Motorcycle ───────────────────────────────────────────────
// record Motorcycle(String registrationNumber, boolean isElectric) implements Vehicle { ... }


/**
 * Toll booth that processes vehicles.
 */
class TollBooth {

    private final String boothId;

    public TollBooth(String boothId) {
        if (boothId == null || boothId.isBlank()) {
            throw new IllegalArgumentException("boothId must not be blank");
        }
        this.boothId = boothId;
    }

    public String getBoothId() { return boothId; }

    /**
     * Process a vehicle and return a TollReceipt.
     *
     * @param vehicle     the vehicle being processed
     * @param distanceKm  distance for which toll is charged
     * @return receipt with vehicle reg, booth id, distance, and amount
     */
    public TollReceipt process(Vehicle vehicle, double distanceKm) {
        // TODO: implement — call vehicle.computeToll(), build receipt
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

/**
 * Immutable receipt — implement as a record.
 * Fields: registrationNumber, boothId, distanceKm, amountCharged
 */
// TODO: record TollReceipt(...) { }
class TollReceipt {
    // placeholder — replace with a proper record
    public final String registrationNumber;
    public final String boothId;
    public final double distanceKm;
    public final double amountCharged;

    public TollReceipt(String registrationNumber, String boothId,
                       double distanceKm, double amountCharged) {
        this.registrationNumber = registrationNumber;
        this.boothId = boothId;
        this.distanceKm = distanceKm;
        this.amountCharged = amountCharged;
    }
}
