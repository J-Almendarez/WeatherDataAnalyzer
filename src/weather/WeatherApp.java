package weather;

import java.awt.Desktop;
import java.nio.file.Path;
import java.time.Month;
import java.util.List;
import java.util.Scanner;


public interface WeatherApp {

    /// The CSV file that is read
    Path DATA_FILE = Path.of("weatherdata.csv");

    /// Where the HTML dashboard is saved
    Path REPORT_FILE = Path.of("weather_report.html");

    /// The menu
    String MENU = """

            ===== Weather Data Analyzer =====
            1. Average temperature for a month
            2. Days above a temperature
            3. Count rainy days
            4. Weather category for each day
            5. Summary report
            6. Open GUI dashboard
            0. Exit
            """;

    /// Loads the data, then keeps showing the menu until the user picks `0`.
    ///
    ///
    /// 1. Average temperature for a month
    ///
    ///
    ///
    /// @param args not used
    static void main(String[] args) {
        List<Recorder> data = DataLoader.load(DATA_FILE);
        System.out.println("Loaded " + data.size() + " days of weather data from " + DATA_FILE);

        Scanner in = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.print(MENU);
            System.out.print("Choose an option: ");
            String choice = in.nextLine().strip();

            // enhanced switch with arrows, no break needed
            switch (choice) {
                case "1" -> showAverage(data, in);
                case "2" -> showDaysAbove(data, in);
                case "3" -> showRainyDays(data);
                case "4" -> showCategories(data);
                case "5" -> System.out.println(Report.summary(data));
                case "6" -> openDashboard(data);
                case "0" -> running = false;
                default -> System.out.println("Not a valid option, try again.");
            }
        }
        System.out.println("Bye!");
    }

    /// Menu option 1. Asks for a month and prints its average temperature.
    ///
    ///
    /// Enter a month (name or number): august
    /// Average temperature in August: 29.2 C
    ///
    ///
    /// @param data the weather records
    /// @param in   the scanner to read the month from
    static void showAverage(List<Recorder> data, Scanner in) {
        System.out.print("Enter a month (name or number): ");
        Month month = parseMonth(in.nextLine());
        if (month == null) {
            System.out.println("No month found");
            return;
        }
        Analyzer.averageTemperature(data, month).ifPresentOrElse(
                avg -> System.out.printf("Average temperature in %s: %.1f C%n", Report.monthName(month), avg),
                () -> System.out.println("No data for " + Report.monthName(month)));
    }

    /// Menu option 2. Asks for a threshold and lists the days above it.
    ///
    ///
    /// Enter a temperature in C: 33
    /// Days above 33.0 C: 7
    ///   2023-08-02  35.0 C
    ///
    ///
    ///
    /// @param data the weather records
    /// @param in   the scanner to read the threshold from
    static void showDaysAbove(List<Recorder> data, Scanner in) {
        System.out.print("Enter a temperature in C: ");
        double threshold;
        try {
            threshold = Double.parseDouble(in.nextLine().strip());
        } catch (NumberFormatException e) {
            System.out.println("That is not a number.");
            return;
        }
        List<Recorder> hotDays = Analyzer.daysAbove(data, threshold);
        System.out.printf("Days above %.1f C: %d%n", threshold, hotDays.size());
        hotDays.forEach(d -> System.out.printf("  %s  %.1f C%n", d.date(), d.temperature()));
    }

    /// Menu option 3. Prints the number of rainy days, total and per month.
    ///
    ///
    /// Rainy days in total: 28
    ///   August: 14
    ///
    ///
    ///
    /// @param data the weather records
    static void showRainyDays(List<Recorder> data) {
        System.out.println("Rainy days in total: " + Analyzer.countRainyDays(data));
        Analyzer.months(data).forEach(m ->
                System.out.printf("  %s: %d%n", Report.monthName(m), Analyzer.countRainyDays(data, m)));
    }

    /// Menu option 4. Prints the category of every day plus the totals.
    ///
    ///
    ///
    ///
    ///
    ///
    ///
    /// @param data the weather records
    static void showCategories(List<Recorder> data) {
        data.forEach(d -> System.out.printf("%s  %5.1f C  %-5s (%s)%n",
                d.date(), d.temperature(), d.category().label(), d.category().description()));
        System.out.println();
        Analyzer.categoryCounts(data).forEach((cat, count) ->
                System.out.println(cat.label() + ": " + count + " days"));
    }

    /// Menu option 6. Saves the HTML dashboard and opens it in the browser.
    ///
    ///
    ///
    /// @param data the weather records
    static void openDashboard(List<Recorder> data) {
        Path file = Report.writeHtml(data, REPORT_FILE);
        System.out.println("Dashboard saved to " + file.toAbsolutePath());
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(file.toUri());
            } else {
                System.out.println("Open that file in your browser to see it.");
            }
        } catch (Exception e) {
            System.out.println("Could not open the browser, open the file yourself.");
        }
    }

    /// Reads a month from text
    ///
    ///
    ///
    ///
    ///
    ///
    /// @param text what the user typed
    /// @return the month, or `null` if it could not be read
    static Month parseMonth(String text) {
        String t = text.strip().toUpperCase();
        if (t.isEmpty()) {
            return null;
        }
        try {
            return Month.of(Integer.parseInt(t));
        } catch (NumberFormatException e) {
            // not a number, try matching the name instead
        } catch (Exception e) {
            return null; // number outside 1-12
        }
        for (Month m : Month.values()) {
            if (m.name().startsWith(t)) {
                return m;
            }
        }
        return null;
    }
}
