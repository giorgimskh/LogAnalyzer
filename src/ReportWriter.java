import java.io.*;
import java.nio.charset.StandardCharsets;

public class ReportWriter implements AutoCloseable{
    private final FileOutputStream fos;
    private final BufferedWriter writer;

    public ReportWriter(File outputFile) throws IOException {
        this.fos=new FileOutputStream(outputFile);

        //The writer keeps text inside a quick-access temporary memory block
        //OutputStreamWriter(..., StandardCharsets.UTF_8) (The Translator):
        // It translates your Java strings into raw binary data (0s and 1s) using the UTF-8 character map.
        //This is the low-level utility that takes those raw bytes from the translator and
        // delivers them straight to the operating system's file system handler.
        this.writer = new BufferedWriter(new OutputStreamWriter((fos), StandardCharsets.UTF_8));
    }

    public  void writeErrorLine(String line) throws IOException {
        writer.write("[CRITICAL ERROR] -> " + line);
        writer.newLine();

        //Empties Java's internal memory buffer down to the OS cache.
        //if power goes off,data lost still
        writer.flush();

        try{
            // 2. Grabs the low-level OS hook for this exact file.
            FileDescriptor fd=fos.getFD();

            // 3. Forces the Operating System to pause and physically write the cache to the disk
            //data is safe now
            fd.sync();
        }catch (SyncFailedException e){
            System.err.println("⚠️ Warning: Hardware disk sync failed (filesystem may not support sync): " + e.getMessage());
        }
    }

    public void writeSummary(long totalLines, long infoCount, long warningCount, long errorCount) throws IOException {
        writer.write("===============================");
        writer.newLine();
        writer.write("        LOG ANALYSIS SUMMARY");
        writer.newLine();
        writer.write("===============================");
        writer.newLine();
        writer.write("Total Log Lines Processed: " + totalLines);
        writer.newLine();
        writer.write("INFO Messages: " + infoCount);
        writer.newLine();
        writer.write("WARNING Messages: " + warningCount);
        writer.newLine();
        writer.write("ERROR Messages: " + errorCount);
        writer.newLine();
        writer.write("===============================");
        writer.newLine();
        writer.flush();
    }


    @Override
    public void close() throws IOException {
        if(writer!=null)
            writer.close();
    }
}
