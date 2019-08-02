package anonymatic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Anonymatic
{
    public Anonymatic(String masterDirPath, String outputDirPath)
    {
        // Load main directory
        File masterDir = new File(masterDirPath);
        if(!masterDir.isDirectory())
        {
            System.err.println(masterDirPath + " is not a directory!");
            System.exit(3);
        }

        // Load solutions from their directories
        List<Solution> solutions = new ArrayList<Solution>();
        for (File f : masterDir.listFiles())
        {
            if (f.isDirectory())
                solutions.add(new Solution(f));
        }
        if (solutions.isEmpty())
        {
            System.err.println(masterDirPath + " contains no solution directories!");
        }


        File outputDir = new File(outputDirPath);

        for (Solution s : solutions)
        {
            s.anonymise(outputDir);
        }

        File mapCsvFile = new File(outputDirPath + File.separator + "keymap.csv");
        List<String> csvLines = new ArrayList<>();
        csvLines.add("Student Name,Anonymised Key");
        for (Solution s : solutions)
        {
            csvLines.add(s.getOriginalName() + "," + s.getAnonymisedName());
        }
        try
        {
            Files.write(mapCsvFile.toPath(), csvLines, StandardCharsets.UTF_8);
            System.out.println("Written de-anonymisation keymap to " + mapCsvFile.getPath());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        if (args.length < 1 || args.length > 2)
        {
            System.err.println("Usage: java -jar anonymatic.jar " +
                    "<path to directory containing submission directories> " +
                    "<path to output directory>");
            System.exit(2);
        }

        Anonymatic anonymatic = new Anonymatic(args[0], args[1]);
    }
}
