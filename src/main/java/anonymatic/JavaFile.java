package anonymatic;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JavaFile
{
    private File original;
    private String relativePath;
    private List<String> originalLines = new ArrayList<>();
    private List<String> anonymisedLines = new ArrayList<>();

    public JavaFile(File originalFile, File rootDir)
    {
        original = originalFile;
        relativePath = originalFile.getAbsolutePath().replace(rootDir.getAbsolutePath(),"");
        loadLines();
        anonymiseLines();
        if (anonymisedLines.isEmpty())
        {
            System.err.println("Warning: anonymised version of " + original.getPath() +
                    " is empty, anonymisation may have broken.");
        }
    }

    public void write(File newSolutionDir)
    {
        File newFile = new File(newSolutionDir.toPath() + File.separator +  relativePath);
        newFile.getParentFile().mkdirs();
        Path filePath = newFile.toPath();
        try
        {
            Files.write(filePath, anonymisedLines, StandardCharsets.UTF_8);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void loadLines()
    {
        originalLines.clear();
        try
        {
            Files.lines(original.toPath()).forEachOrdered(s -> originalLines.add(s));
        }
        catch (IOException e)
        {
            System.err.println("IO Exception from file " + original.getPath());
            e.printStackTrace();
        }
        catch (UncheckedIOException e)
        {
            System.err.println("IO Exception from file " + original.getPath());
            e.printStackTrace();
        }
    }

    private void anonymiseLines()
    {
        boolean classDetected = false;
        boolean blockComment = false;

        for (String l : originalLines)
        {
            // if a class has been hit, just add the line
            if (classDetected)
            {
                anonymisedLines.add(l);
                continue;
            }

            // not currently in a block comment
            if (!blockComment)
            {
                if (l.trim().startsWith("/*"))
                {
                    if (!l.trim().contains("*/"))
                    {
                        blockComment = true;
                    }
                    continue;
                }
                if (!l.trim().startsWith("//"))
                {
                    anonymisedLines.add(l);
                    if (l.trim().contains("class"))
                    {
                        classDetected = true;
                    }
                }
            }
            // in a block comment, wait until it ends
            else
            {
                if (l.contains("*/"))
                    blockComment = false;
            }
        }
    }
}
