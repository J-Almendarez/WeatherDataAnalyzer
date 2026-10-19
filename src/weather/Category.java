package weather;

/// The weather category of a day: **Hot**, **Warm**, or **Cold**.
///
///
/// | Category | Temperature      |
/// |----------|------------------|
/// | `HOT`    | 30 °C and above  |
/// | `WARM`   | 18 °C up to 30 °C|
/// | `COLD`   | below 18 °C      |
///
///
/// Category c = Category.of(Recorder.fromCsv("2023-09-05,17.2,45,0.0"));
/// System.out.println(c.label()); // Cold
///
public enum Category {
    HOT("Hot"),
    WARM("Warm"),
    COLD("Cold");

    /// Temperature (°C) at or above which a day is [#HOT].
    public static final double HOT_LIMIT = 30.0;

    /// Temperature (°C) at or above which a day is [#WARM].
    public static final double WARM_LIMIT = 18.0;

    private final String label;

    Category(String label) {
        this.label = label;
    }

    /// The display name, e.g. `"Hot"`.
    ///
    ///
    /// Category.WARM.label(); // "Warm"
    ///
    ///
    /// @return the human-readable name
    public String label() {
        return label;
    }

    /// Decides the category of a day using **pattern matching for `switch`**.
    ///
    /// The `Recorder(...)` record pattern pulls the temperature out of the record,
    /// and the `when` guard checks it. The cases are checked from top to bottom.
    ///
    ///
    /// @param day the day to categorize
    /// @return `HOT`, `WARM`, or `COLD`
    public static Category of(Recorder day) {
        return switch (day) {
            case Recorder(var date, double temp, var humidity, var rain) when temp >= HOT_LIMIT -> HOT;
            case Recorder(var date, double temp, var humidity, var rain) when temp >= WARM_LIMIT -> WARM;
            case Recorder r -> COLD;
        };
    }

    /// A short description of what the category feels like
    ///
    ///
    /// Category.COLD.description(); // "Grab a jacket"
    ///
    ///
    /// @return a short description
    public String description() {
        return switch (this) {
            case HOT -> "Stay hydrated";
            case WARM -> "Nice weather";
            case COLD -> "Grab a jacket";
        };
    }
}
