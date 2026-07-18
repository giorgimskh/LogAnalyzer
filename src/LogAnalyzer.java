import java.io.File;

public class LogAnalyzer{
    public static void main(String[] args){
        //user may seek for help(understanding program)
        if(args.length>0 && (args[0].equals("-h") || args[0].equals("--help"))){
            printUsage();
            System.exit(0);
        }

        String inputPath;
        String outputPath;

        if (args.length == 2) {
            inputPath = args[0];
            outputPath = args[1];
        }else{
            inputPath="logs/system.log";
            outputPath="reports/summary_report.txt";
            System.out.println("Default arguments used");
        }


        File inputFile=new File(inputPath);
        File outputFile=new File(outputPath);

        //input validation
        if(!(inputFile.exists() || inputFile.isFile())){
            System.err.println("Path of input log or log File does not exist");
            System.exit(1);
        }

        //output validation
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs() && !parentDir.exists()) {
                System.err.println("❌ Failed to create output directory: " + parentDir.getPath());
                System.exit(1);
            }
        }

        //permission verification
        System.out.println("🔒 Checking system permissions...");
        if (!PermissionChecker.verifyPermissions(inputFile, outputFile)) {
            // Permission check message is printed inside the PermissionChecker
            System.exit(1);
        }

        System.out.println("✅ Security check passed.");




    }


    private static void printUsage(){
        System.out.println("Secure OS-Level Log Analyzer");
        System.out.println("Usage:");
        System.out.println("  java LogAnalyzer [input_log_file] [output_report_file]");
        System.out.println();
        System.out.println("Defaults:");
        System.out.println("  input_log_file:     logs/server.log");
        System.out.println("  output_report_file: reports/summary_report.txt");
    }
}
