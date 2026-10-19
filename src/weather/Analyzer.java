package weather;

import java.time.Month;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/// Analysis functions for a list of [Recorder]s.
///
///
///
/// List<Recorder> data = DataLoader.load(Path.of("weatherdata.csv"));
/// Analyzer.averageTemperature(data, Month.AUGUST); // OptionalDouble[29.2...]
/// Analyzer.countRainyDays(data);                   // 28
///
public interface Analyzer {

    /// Calculates the average temperature for one month.
    ///
    ///
    ///
    /// @param data  the weather records to search
    /// @param month the month to average, e.g. `Month.AUGUST`
    /// @return the average, or `OptionalDouble.empty()` if that month has no data
    static OptionalDouble averageTemperature(List<Recorder> data, Month month) {
        return data.stream()
                .filter(r -> r.date().getMonth() == month)
                .mapToDouble(Recorder::temperature)
                .average();
    }

    /// Finds every day whose temperature is strictly above the threshold.
    ///
    ///
    /// @param data      the weather records to search
    /// @param threshold the temperature in °C to compare against
    /// @return the matching days, in date order
    static List<Recorder> daysAbove(List<Recorder> data, double threshold) {
        return data.stream()
                .filter(r -> r.temperature() > threshold)
                .toList();
    }

    /// Counts the rainy days in the whole data set.
    ///
    ///
    /// @param data the weather records
    /// @return how many days had precipitation greater than 0
    static long countRainyDays(List<Recorder> data) {
        return data.stream()
                .filter(Recorder::isRainy)
                .count();
    }

    /// Counts the rainy days in one month.
    ///
    ///
    ///
    /// @param data  the weather records
    /// @param month the month to check
    /// @return how many days in that month had rain
    static long countRainyDays(List<Recorder> data, Month month) {
        return data.stream()
                .filter(r -> r.date().getMonth() == month)
                .filter(Recorder::isRainy)
                .count();
    }

    /// Counts how many days fall into each [Category].
    ///
    ///
    ///
    ///
    /// Map<Category, Long> counts = Analyzer.categoryCounts(data);
    /// counts.forEach((cat, n) -> System.out.println(cat.label() + ": " + n));
    ///
    ///
    /// @param data the weather records
    /// @return a map from category to number of days
    static Map<Category, Long> categoryCounts(List<Recorder> data) {
        Map<Category, Long> counts = data.stream()
                .collect(Collectors.groupingBy(Recorder::category,
                        () -> new EnumMap<>(Category.class),
                        Collectors.counting()));
        // make sure every category shows up, even with 0 days
        for (Category c : Category.values()) {
            counts.putIfAbsent(c, 0L);
        }
        return counts;
    }

    /// Finds the hottest day in the data.
    ///
    ///
    ///
    /// @param data the weather records
    /// @return the day with the highest temperature, or empty if there is no data
    static Optional<Recorder> hottestDay(List<Recorder> data) {
        return data.stream().max(Comparator.comparingDouble(Recorder::temperature));
    }

    /// Finds the coldest day in the data.
    ///
    ///
    ///
    /// @param data the weather records
    /// @return the day with the lowest temperature, or empty if there is no data
    static Optional<Recorder> coldestDay(List<Recorder> data) {
        return data.stream().min(Comparator.comparingDouble(Recorder::temperature));
    }

    /// Lists the months that appear in the data, in calendar order.
    ///
    ///
    ///
    /// @param data the weather records
    /// @return the distinct months, sorted
    static List<Month> months(List<Recorder> data) {
        return data.stream()
                .map(r -> r.date().getMonth())
                .distinct()
                .sorted()
                .toList();
    }
}
