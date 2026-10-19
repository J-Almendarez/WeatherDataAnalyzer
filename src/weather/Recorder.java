package weather;

import java.time.LocalDate;

/// One day of weather data, read from a single line of `weatherdata.csv`.
///
///
/// | Field           | Meaning                          | Example      |
/// |-----------------|----------------------------------|--------------|
/// | `date`          | the day the reading was taken    | `2023-08-01` |
/// | `temperature`   | temperature in °C                | `32.5`       |
/// | `humidity`      | relative humidity in percent     | `65`         |
/// | `precipitation` | rain in inches (0 = no rain)     | `0.2`        |
///
/// Example:
///
/// var day = Recorder.fromCsv("2023-08-01,32.5,65,0.0");
/// day.temperature();     // 32.5
/// day.date().getMonth(); // AUGUST
/// day.isRainy();         // false
///
///
/// @param date          the date of the reading
/// @param temperature   the temperature in degrees Celsius
/// @param humidity      relative humidity, 0–100 %
/// @param precipitation amount of rain, must not be negative
public record Recorder(LocalDate date, double temperature, int humidity, double precipitation) {

    /// Compact constructor that validates the values before the record is created.
    ///
    /// ```java
    /// new Recorder(LocalDate.now(), 20.0, 150, 0.0); // throws, humidity > 100
    /// ```
    ///
    /// @throws IllegalArgumentException if humidity is outside 0–100
    ///         or precipitation is negative
    public Recorder {
        if (humidity < 0 || humidity > 100) {
            throw new IllegalArgumentException("Humidity must be 0-100, got " + humidity);
        }
        if (precipitation < 0) {
            throw new IllegalArgumentException("Precipitation cannot be negative, got " + precipitation);
        }
    }

    /// Builds a `Recorder` from one CSV line.
    ///
    /// The line must have four comma-separated values in this order:
    /// `Date,Temperature,Humidity,Precipitation`
    ///
    /// ```java
    /// Recorder r = Recorder.fromCsv("2023-08-02,35.0,60,0.2");
    /// // r = Recorder[date=2023-08-02, temperature=35.0, humidity=60, precipitation=0.2]
    /// ```
    ///
    /// @param line a line from the CSV file (not the header)
    /// @return the parsed record
    /// @throws IllegalArgumentException if the line does not have exactly 4 values
    public static Recorder fromCsv(String line) {
        String[] parts = line.strip().split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Expected 4 values but got " + parts.length + ": " + line);
        }
        return new Recorder(
                LocalDate.parse(parts[0].strip()),
                Double.parseDouble(parts[1].strip()),
                Integer.parseInt(parts[2].strip()),
                Double.parseDouble(parts[3].strip()));
    }

    /// Tells whether it rained on this day.
    ///
    /// A day counts as rainy when precipitation is greater than `0.0`.
    ///
    /// ```java
    /// Recorder.fromCsv("2023-08-02,35.0,60,0.2").isRainy(); // true
    /// Recorder.fromCsv("2023-08-01,32.5,65,0.0").isRainy(); // false
    /// ```
    ///
    /// @return `true` if there was any precipitation
    public boolean isRainy() {
        return precipitation > 0.0;
    }

    /// Shortcut for [Category#of(Recorder)].
    ///
    /// ```java
    /// Recorder.fromCsv("2023-08-01,32.5,65,0.0").category(); // Category.HOT
    /// ```
    ///
    /// @return the weather category for this day
    public Category category() {
        return Category.of(this);
    }
}
