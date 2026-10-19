package weather;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/// Reads weather data from a CSV file.
///
///
/// Expected file format (first line is the header and is skipped):
/// ```
/// Date,Temperature,Humidity,Precipitation
/// 2023-08-01,32.5,65,0.0
/// 2023-08-02,35.0,60,0.2
/// ```
public interface DataLoader {

    /// Loads every row of the CSV file into a list of [Recorder]s.
    ///
    ///
    ///
    /// List<Recorder> data = DataLoader.load(Path.of("weatherdata.csv"));
    /// System.out.println(data.size() + " days loaded");
    ///
    ///
    /// @param file path to the CSV file
    /// @return an unmodifiable list of records, in file order
    /// @throws UncheckedIOException if the file cannot be read
    static List<Recorder> load(Path file) {
        try (var lines = Files.lines(file)) {
            return lines
                    .skip(1)                          // skip the header row
                    .filter(line -> !line.isBlank())  // ignore empty lines
                    .map(Recorder::fromCsv)      // text -> record
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + file, e);
        }
    }
}
