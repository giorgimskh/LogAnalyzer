import java.io.File;

public class PermissionChecker {
    public static boolean verifyPermissions(File inputFile, File outputFile) {
        if(!inputFile.canRead()){
            System.err.println("❌ Permission check failed: Input file is not readable: \" + inputFile.getPath()");
            return false;
        }

        if(!outputFile.canWrite() && outputFile.exists()){
            System.err.println("❌ Permission check failed: Output file is not writable: " + outputFile.getPath());
            return false;
        }else {
            File parent = outputFile.getParentFile();
            if (parent == null) {
                parent = new File(".");
            }
            // Traverse up to find the first parent directory that exists
            File existingParent = parent;
            while (existingParent != null && !existingParent.exists()) {
                existingParent = existingParent.getParentFile();
            }
            if (existingParent == null) {
                existingParent = new File(".");
            }
            if (!existingParent.canWrite()) {
                System.err.println("❌ Permission check failed: Output directory/parent is not writable: " + existingParent.getPath());
                return false;
            }
        }

        return true;
    }
}
