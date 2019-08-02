package anonymatic;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Solution
{
    private String anonymisedName;
    private File originalDir;
    List<JavaFile> javaFiles = new ArrayList<JavaFile>();


    public Solution(File solutionDir)
    {
        // Load java files
        for (File f : locateFilesRecursive(solutionDir, ".java"))
            javaFiles.add(new JavaFile(f, solutionDir));

        anonymisedName = String.valueOf(this.hashCode());
    }

    private List<File> locateFilesRecursive(File directory, String extension)
    {
        List<File> identifiedFiles = new ArrayList<File>();
        try
        {
            for (File f : directory.listFiles())
            {
                if (f.isDirectory())
                {
                    identifiedFiles.addAll(locateFilesRecursive(f, extension));
                }
                else if (f.getName().endsWith(extension))
                {
                    identifiedFiles.add(f);
                }
            }
        }
        catch (NullPointerException npe)
        {
            // Directory has no files / is not a directory, skip
        }


        return identifiedFiles;
    }

    public void anonymise(File rootOutputDir)
    {
        // Create directory for solution inside solutionDir
        // TODO implement
        File solutionDir = new File(rootOutputDir + File.separator + anonymisedName);
        solutionDir.mkdirs();
        // Write anonymised version of each file in place
        for (JavaFile f : javaFiles)
        {
            f.write(new File(solutionDir.getPath()));
        }
    }
}
