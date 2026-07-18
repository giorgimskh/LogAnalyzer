import java.io.File;

public class PermissionChecker {
    public static boolean verifyPermissions(File inputFile, File outputFile) {
        if(!inputFile.canRead()){
            System.err.println("❌ Permission check failed: Input file is not readable: " + inputFile.getPath());
            return false;
        }

        if(!outputFile.canWrite() && outputFile.exists()){
            System.err.println("❌ Permission check failed: Output file is not writable: " + outputFile.getPath());
            return false;
        }else {
           File existingParent= findExistingParent(outputFile);

           if(!existingParent.canWrite()){
               System.err.println("❌ Permission check failed: Parent directory is not writable: " + existingParent.getPath());
               return false;
           }
        }

        return true;
    }

    private static File findExistingParent(File file){
        File parent=file.getParentFile();

        if(parent==null){
            return  new File(".");
        }

        while(parent!=null && !parent.exists()){
            parent=parent.getParentFile();
        }

        return (parent!=null) ? parent:new File(".");
    }
}
