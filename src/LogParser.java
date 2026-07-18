import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;

public class LogParser {
    public class AnalysisResult{
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



}
