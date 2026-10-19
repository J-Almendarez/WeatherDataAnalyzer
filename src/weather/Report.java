package weather;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

///
/// - [#summary(List)] makes the plain-text report printed in the console
/// - [#html(List)] makes the HTML dashboard (the GUI), styled by `style.css`
///
/// Text blocks are the `"""` strings. They keep line breaks and indentation, so
/// multi-line output looks the same in the code as it does on screen.
///
///
public interface Report {

    /// Creates the console summary report.
    ///
    /// Uses a text block together with `String.formatted(...)`
    ///
    /// ```java
    /// System.out.println(Report.summary(data));
    /// // ==============================
    /// //      WEATHER SUMMARY REPORT
    /// // ==============================
    /// // Days of data:   92
    /// // ...
    /// ```
    ///
    /// @param data the weather records (must not be empty)
    /// @return the finished report as one string
    static String summary(List<Recorder> data) {
        Recorder hottest = Analyzer.hottestDay(data).orElseThrow();
        Recorder coldest = Analyzer.coldestDay(data).orElseThrow();
        Map<Category, Long> counts = Analyzer.categoryCounts(data);

        // one line per month, built with a stream
        String monthLines = Analyzer.months(data).stream()
                .map(m -> "  %-10s avg %5.1f C   rainy days: %d".formatted(
                        monthName(m),
                        Analyzer.averageTemperature(data, m).orElse(0),
                        Analyzer.countRainyDays(data, m)))
                .collect(Collectors.joining("\n"));

        return """
                ==============================
                     WEATHER SUMMARY REPORT
                ==============================
                Days of data:   %d  (%s to %s)
                Hottest day:    %s  %.1f C
                Coldest day:    %s  %.1f C
                Rainy days:     %d

                By month:
                %s

                Categories:
                  Hot:  %d days
                  Warm: %d days
                  Cold: %d days
                """.formatted(
                data.size(), data.getFirst().date(), data.getLast().date(),
                hottest.date(), hottest.temperature(),
                coldest.date(), coldest.temperature(),
                Analyzer.countRainyDays(data),
                monthLines,
                counts.get(Category.HOT), counts.get(Category.WARM), counts.get(Category.COLD));
    }

    /// Creates the full HTML dashboard page.
    ///
    /// The page links to `style.css`, which must be in the same folder as the
    /// HTML file. It shows summary cards, a bar per category, monthly averages,
    /// and a table of every day.
    ///
    /// ```java
    /// String page = Report.html(data);
    /// Files.writeString(Path.of("weather_report.html"), page);
    /// ```
    ///
    /// @param data the weather records (must not be empty)
    /// @return the HTML page as a string
    static String html(List<Recorder> data) {
        Map<Category, Long> counts = Analyzer.categoryCounts(data);

        // bars for Hot / Warm / Cold, width is the percent of all days
        String categoryBars = counts.entrySet().stream()
                .map(e -> """
                        <div class="bar-row">
                          <span class="bar-label">%s</span>
                          <div class="bar-track"><div class="bar %s" style="width: %.0f%%"></div></div>
                          <span class="bar-value">%d</span>
                        </div>
                        """.formatted(e.getKey().label(), cssClass(e.getKey()),
                        100.0 * e.getValue() / data.size(), e.getValue()))
                .collect(Collectors.joining());

        // one card per month
        String monthCards = Analyzer.months(data).stream()
                .map(m -> """
                        <div class="card">
                          <h3>%s</h3>
                          <p class="big">%.1f&deg;C</p>
                          <p>average temp</p>
                          <p>%d rainy days</p>
                        </div>
                        """.formatted(monthName(m),
                        Analyzer.averageTemperature(data, m).orElse(0),
                        Analyzer.countRainyDays(data, m)))
                .collect(Collectors.joining());

        // one table row per day
        String tableRows = data.stream()
                .map(r -> """
                        <tr>
                          <td>%s</td><td>%.1f</td><td>%d%%</td><td>%.1f</td>
                          <td><span class="badge %s">%s</span></td>
                        </tr>
                        """.formatted(r.date(), r.temperature(), r.humidity(), r.precipitation(),
                        cssClass(r.category()), r.category().label()))
                .collect(Collectors.joining());

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <title>Weather Data Analyzer</title>
                  <link rel="stylesheet" href="style.css">
                </head>
                <body>
                  <header>
                    <h1>Weather Data Analyzer</h1>
                    <p>%d days of data from %s to %s</p>
                  </header>

                  <section class="stats">
                    <div class="card"><p>Rainy days</p><p class="big">%d</p></div>
                    <div class="card"><p>Hottest</p><p class="big">%.1f&deg;C</p><p>%s</p></div>
                    <div class="card"><p>Coldest</p><p class="big">%.1f&deg;C</p><p>%s</p></div>
                  </section>

                  <h2>Categories</h2>
                  <section class="bars">
                %s
                  </section>

                  <h2>By month</h2>
                  <section class="stats">
                %s
                  </section>

                  <h2>All days</h2>
                  <table>
                    <thead>
                      <tr><th>Date</th><th>Temp (&deg;C)</th><th>Humidity</th><th>Rain (in)</th><th>Category</th></tr>
                    </thead>
                    <tbody>
                %s
                    </tbody>
                  </table>
                </body>
                </html>
                """.formatted(
                data.size(), data.getFirst().date(), data.getLast().date(),
                Analyzer.countRainyDays(data),
                Analyzer.hottestDay(data).orElseThrow().temperature(), Analyzer.hottestDay(data).orElseThrow().date(),
                Analyzer.coldestDay(data).orElseThrow().temperature(), Analyzer.coldestDay(data).orElseThrow().date(),
                categoryBars, monthCards, tableRows);
    }

    /// Writes the HTML dashboard to a file.
    ///
    /// ```java
    /// Path file = Report.writeHtml(data, Path.of("weather_report.html"));
    /// ```
    ///
    /// @param data the weather records
    /// @param file where to save the page
    /// @return the same path, for convenience
    /// @throws UncheckedIOException if the file cannot be written
    static Path writeHtml(List<Recorder> data, Path file) {
        try {
            return Files.writeString(file, html(data));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + file, e);
        }
    }

    /// Turns a month into a nice name, e.g. `AUGUST` becomes `"August"`.
    ///
    /// ```java
    /// Report.monthName(Month.AUGUST); // "August"
    /// ```
    ///
    /// @param month the month
    /// @return the full English name of the month
    static String monthName(Month month) {
        return month.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    /// The CSS class used to color a category in the dashboard.
    ///
    /// ```java
    /// Report.cssClass(Category.HOT); // "hot"
    /// ```
    ///
    /// @param category the category
    /// @return `"hot"`, `"warm"`, or `"cold"`
    static String cssClass(Category category) {
        return category.name().toLowerCase();
    }
}
