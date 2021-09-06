package com.joe.app;

import org.apache.commons.cli.*;

/**
 * @author Joe
 *
 * 2021/6/22 11:56
 */
public class PdfCombiner {

    public static void main(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true, "input folder path");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output pdf");
        output.setRequired(true);
        options.addOption(output);

        Option outline = new Option("otl", false, "outline");
        outline.setRequired(false);
        options.addOption(outline);

        Option toc = new Option("toc", false, "table of contents");
        toc.setRequired(false);
        options.addOption(toc);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            PdfOperator operator = new PdfOperator(cmd.getOptionValue("output"));
            // 遍历源文件夹，执行合并
            System.out.println("---Traversing " + cmd.getOptionValue("input") + ", and Merging into " + cmd.getOptionValue("output") + "...");
            operator.startTraverse(cmd.getOptionValue("input"));
            System.out.println("---Successfully Merged!");

            if (cmd.hasOption("otl")) {
                System.out.println("---Adding Outline...");
                operator.addOutline();
                System.out.println("---Outline Added Successfully!");
            }

            if (cmd.hasOption("toc")) {
                System.out.println("---Adding Table of Contents...");
                operator.addTableOfContents();
                System.out.println("---Table of Contents Added Successfully!");
            }

            // 关闭文件流
            operator.close();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java -jar app-1.0.jar -i <input path> -o <output path> [-otl] [-toc]", options);
            System.exit(1);
        }
    }

}
