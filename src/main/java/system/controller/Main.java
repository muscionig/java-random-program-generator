package system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FileUtils;
import system.controller.parser.Parser;
import system.model.ScopeTable;
import system.model.nodes.Node;
import system.model.nodes.classes.NormalClassDeclaration;
import utils.Config;
import utils.Logger;
import utils.RandomGen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Main {

    public static Config config;

    public static void main(String[] args){

        initConfig();
        Parser parser = new Parser();
        new File("generatedSrc/out").mkdirs();
        try {
            FileUtils.cleanDirectory(new File("generatedSrc/main/java"));
            FileUtils.cleanDirectory(new File("generatedSrc/out"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        NormalClassDeclaration cl = null;
        String className = "";
        String basePath = "generatedSrc/main/java/";
        ScopeTable classScopeTable = null;
        List<String> classNames = new ArrayList<>();

        int maxNumberOfClasses = config.getClasses().get("max");
        int minNumberOfClasses = config.getClasses().get("min");
        int numOfClasses = RandomGen.getNextInt(maxNumberOfClasses-minNumberOfClasses) + minNumberOfClasses;

        for(int i = 0; i < numOfClasses; i++){

            boolean produceMain = false;
            if(i == (numOfClasses - 1)){

                produceMain = true;
            }
            String oldClassName = className;
            className = "Main" + i;
            if(cl == null) {
                className = "Main";
                classNames.add(className+".java");
                try {
                    cl = new NormalClassDeclaration(className, produceMain);
                } catch (Exception e) {
                    Logger.logError("CLASS: "+ className, "Generation failed");
                    e.printStackTrace();
                    return;
                }
            }else{
                classNames.add(className+".java");
                classScopeTable = parser.getClassScopeTable(new File(basePath + oldClassName +".java"), classScopeTable);
                try {

                    cl = new NormalClassDeclaration(className, classScopeTable, produceMain);
                } catch (Exception e) {
                    Logger.logError("CLASS: "+ className, "Generation failed");
                    e.printStackTrace();
                    return;
                }
            }
            Logger.log("CLASS: "+ className, "Generation successful");

            if (cl != null) {
                save(cl, basePath + className +".java");
            }

        }
        if (CompileChecker.compileCheck(classNames) == 0) {
            Logger.log("compiler", "Compilation successful");
        } else {
            Logger.logError("compiler","Compilation failed");
        }


        String mainClass = className;
        Runner runner = new Runner(config.isRun(), config.getTimeout());
        runner.execute(mainClass);
    }

    private static void initConfig() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            config = mapper.readValue(new File("src/main/resources/conf.yaml"), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void save(Node node, String path) {

        List<String> sourceLines = new ArrayList<>();
        sourceLines.add(node.produce());

        File f = new File(path);
        try {
            Files.write(f.toPath(), sourceLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
