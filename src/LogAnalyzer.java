import java.io.File;
import java.io.IOException;

public class LogAnalyzer {
    public static void main(String[] args) {
        // User may seek help (understanding program execution structures)
        if (args.length > 0 && (args[0].equals("-h") || args[0].equals("--help"))) {
            printUsage();
            System.exit(0);
        }

        String inputPath;
        String outputPath;

        if (args.length == 2) {
            inputPath = args[0];
            outputPath = args[1];
        } else {
            inputPath = "logs/system.log"; // Matches the main fallback behavior zone
            outputPath = "reports/summary_report.txt";
            System.out.println("Default arguments used");
        }

        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);

        // Fixed: Input validation logic now uses && to ensure it is an actual existing file
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.err.println("❌ Error: Path of input log file does not exist or is a directory.");
            System.err.println("   Expected location: " + inputFile.getAbsolutePath());
            System.exit(1);
        }

        // Output validation directory building
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs() && !parentDir.exists()) {
                System.err.println("❌ Failed to create output directory: " + parentDir.getPath());
                System.exit(1);
            }
        }

        // Low-level permission verification execution hook
        System.out.println("🔒 Checking system permissions...");
        if (!PermissionChecker.verifyPermissions(inputFile, outputFile)) {
            // Permission check message is printed cleanly inside the PermissionChecker class
            System.exit(1);
        }

        System.out.println("✅ Security check passed.");
        System.out.println("⚡ Starting log analysis on: " + inputPath);

        // Fixed: Using try-with-resources handles hardware flushes and automatic closing safely!
        try (ReportWriter reportWriter = new ReportWriter(outputFile)) {

            LogParser parser = new LogParser();
            LogParser.AnalysisResult result = parser.parseLogFile(inputFile, reportWriter);

            reportWriter.writeSummary(
                    result.totalLines,
                    result.infoCount,
                    result.warningCount,
                    result.errorCount
            );

            System.out.println("🎉 Safe analysis complete! Hardware sync executed. Report saved to: " + outputPath);

        } catch (IOException e) {
            System.err.println("❌ Critical I/O Error during processing:");
            System.err.println("   Input file:  " + inputFile.getAbsolutePath());
            System.err.println("   Output file: " + outputFile.getAbsolutePath());
            System.err.println("   Details:     " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Secure OS-Level Log Analyzer");
        System.out.println("Usage:");
        System.out.println("  java LogAnalyzer [input_log_file] [output_report_file]");
        System.out.println();
        System.out.println("Defaults:");
        System.out.println("  input_log_file:      logs/system.log"); // Fixed: Now matches actual assignment logic
        System.out.println("  output_report_file: reports/summary_report.txt");
    }
}