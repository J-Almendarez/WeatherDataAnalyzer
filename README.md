# Weather Data Analyzer

A Java application that reads daily weather data from a CSV file and analyzes it
using modern Java features (Java 15–23). The project has **no explicit classes**.
Everything is built from records, interfaces, an enum, and functional code
(lambdas and streams).

It runs as a menu in the console and can also open a **GUI dashboard** in the
browser, styled with CSS.

## Features

| # | Feature | Where |
|---|---------|-------|
| 1 | Parse temperature, humidity, and precipitation from `weatherdata.csv` | `DataLoader`, `Recorder.fromCsv` |
| 2 | Average temperature for a specific month | `Analyzer.averageTemperature` |
| 3 | Days with temperatures above a given threshold | `Analyzer.daysAbove` |
| 4 | Count of rainy days (total and per month) | `Analyzer.countRainyDays` |
| 5 | Weather categories: Hot, Warm, Cold | `Category.of` |
| 6 | Summary report | `Report.summary` |
| 7 | GUI dashboard with CSS (extra credit) | `Report.html`, `style.css` |

### Weather categories

| Category | Temperature |
|----------|-------------|
| Hot      | 30 °C and above |
| Warm     | 18 °C up to 30 °C |
| Cold     | below 18 °C |

## Modern Java features used

| Feature | Java version | Where it is used |
|---------|--------------|------------------|
| Records | 16 | `Recorder` holds one day of data, with a compact constructor for validation |
| Enhanced `switch` (arrow syntax) | 14 | The menu in `WeatherApp.main`, `Category.description()` |
| Pattern matching for `switch` + record patterns with `when` guards | 21 | `Category.of()` picks Hot/Warm/Cold |
| Text blocks (`"""`) | 15 | The menu, the summary report, and the HTML page in `Report` |
| `String.formatted()` | 15 | Filling values into the text blocks |
| `Stream.toList()` | 16 | `DataLoader.load`, `Analyzer.daysAbove`, `Analyzer.months` |
| Sequenced collections `getFirst()` / `getLast()` | 21 | First and last date in `Report` |
| Markdown Javadoc (`///`) | 23 | Every file and method is documented with markdown and code examples |
| `var` | 10 | `DataLoader.load`, record patterns |
| Lambdas and streams | 8 | All of `Analyzer`, `DataLoader`, and `Report` |
| Static methods in interfaces | 8 | `Analyzer`, `DataLoader`, `Report`, `WeatherApp` hold logic without classes |

### How it works without classes

- `Recorder` is a **record**.
- `Category` is an **enum**.
- `DataLoader`, `Analyzer`, `Report`, and `WeatherApp` are **interfaces** with only
  `static` methods. Java lets an interface have a `static void main`, so even the
  entry point is not a class.

## Requirements

- JDK 21 or newer to compile and run
- JDK 23 or newer to generate the Javadoc, because `///` markdown comments were added in JDK 23

## How to run

### IntelliJ
1. Open the project folder.
2. Make sure `weatherdata.csv` and `style.css` are in the main project folder, not inside `src`.
3. Right-click `WeatherApp` and choose **Run**.

### Command line
From the project folder:
```bash
javac -d out src/weather/*.java
java -cp out weather.WeatherApp
```

### Generate the Javadoc
```bash
javadoc -d docs -sourcepath src weather
```
Then open `docs/index.html`.

## Using the program

```
Loaded 92 days of weather data from weatherdata.csv

===== Weather Data Analyzer =====
1. Average temperature for a month
2. Days above a temperature
3. Count rainy days
4. Weather category for each day
5. Summary report
6. Open GUI dashboard
0. Exit
Choose an option: 1
Enter a month (name or number): august
Average temperature in August: 29.2 C
```

For a month, you can type a name (`august`, `aug`) or a number (`8`).

Option 5 prints this report:
```
==============================
     WEATHER SUMMARY REPORT
==============================
Days of data:   92  (2023-08-01 to 2023-10-31)
Hottest day:    2023-08-21  36.5 C
Coldest day:    2023-10-05  10.4 C
Rainy days:     33

By month:
  August     avg  29.2 C   rainy days: 15
  September  avg  22.7 C   rainy days: 11
  October    avg  16.5 C   rainy days: 7

Categories:
  Hot:  15 days
  Warm: 56 days
  Cold: 21 days
```

Option 6 creates `weather_report.html` and opens it in your browser. It shows
summary cards, a bar chart of the categories, monthly averages, and a color-coded
table of every day.

## Data file

`weatherdata.csv` has one row per day, from 2023-08-01 to 2023-10-31:

```
Date,Temperature,Humidity,Precipitation
2023-08-01,32.5,65,0.0
2023-08-02,35.0,60,0.2
```

| Column        | Unit   |
|---------------|--------|
| Date          | YYYY-MM-DD |
| Temperature   | °C     |
| Humidity      | %      |
| Precipitation | inches (0.0 means no rain) |

## Project structure

```
weatherdata.csv           - the weather data
style.css                 - CSS for the GUI dashboard
README.md
src/weather/
  Recorder.java           - record for one day of data, parses a CSV line
  Category.java           - enum Hot/Warm/Cold, pattern matching switch
  DataLoader.java         - interface that loads the CSV with streams
  Analyzer.java           - interface with all the analysis functions
  Report.java             - interface that builds the text report and HTML page with text blocks
  WeatherApp.java         - interface with main() and the menu
```
