import java.io.*;
import java.nio.charset.StandardCharsets;

public class LogParser {
    public class AnalysisResult {
        public final long totalLines;
        public final long infoCount;
        public final long warningCount;
        public final long errorCount;
        public final long unclassifiedCount;

        public AnalysisResult(long totalLines, long infoCount, long warningCount, long errorCount, long unclassifiedCount) {
            this.totalLines = totalLines;
            this.infoCount = infoCount;
            this.warningCount = warningCount;
            this.errorCount = errorCount;
            this.unclassifiedCount = unclassifiedCount;
        }


    }

    public AnalysisResult logParser(File inputFile, ReportWriter reportWriter) throws IOException{
        long totalLines = 0;
        long infoCount = 0;
        long warningCount = 0;
        long errorCount = 0;
        long unclassifiedCount=0;

        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            String line;

            while ((line = reader.readLine()) != null) {
                totalLines++;

                line = line.toUpperCase();

                if (line.contains("INFO:")) {
                    infoCount++;
                } else if (line.contains("WARNING:")) {
                    warningCount++;
                } else if (line.contains("EROR:")) {
                    errorCount++;
                    reportWriter.writeErrorLine(line);
                } else {
                    unclassifiedCount++;
                }
            }
        }
        return new AnalysisResult(totalLines,infoCount,warningCount,errorCount,unclassifiedCount);
    }
}
