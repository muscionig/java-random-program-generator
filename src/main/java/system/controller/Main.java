package system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FileUtils;
import system.controller.parser.Parser;
import system.model.ScopeTable;
import system.model.nodes.Node;
import system.model.nodes.classes.NormalClassDeclaration;
import system.model.nodes.interfaces.InterfaceDeclaration;
import utils.Config;
import utils.Logger;
import utils.RandomGen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main class
 */
public class Main {

    public static Config config;

    public static void main(String[] args){

        Long interfaceTime;
        Long classTime;
        Long compileTime;
        Long executionTime;
        Long totalTime;

        Long start = System.currentTimeMillis();
        initConfig();
        Parser parser = new Parser();
        new File("generatedSrc/out").mkdirs();
        new File("generatedSrc/main/java").mkdirs();
        try {
            FileUtils.cleanDirectory(new File("generatedSrc/main/java"));
            FileUtils.cleanDirectory(new File("generatedSrc/out"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String basePath = "generatedSrc/main/java/";

        Long startTime = System.currentTimeMillis();
        // INTERFACES GENERATION
        int maxNumberOfInterfaces = config.getInterfaces().get("max");
        int minNumberOfInterfaces = config.getInterfaces().get("min");

        List<String> interfaceNameList = new ArrayList<>();

        for (int i = 0; i < RandomGen.getNextInt(maxNumberOfInterfaces-minNumberOfInterfaces) + minNumberOfInterfaces; i++) {

            InterfaceDeclaration interf = new InterfaceDeclaration();
            String interfaceName = interf.getNormalInterfaceDeclaration().getIdentifier().produce();

            interfaceNameList.add(interfaceName);

            save(interf, basePath + interfaceName +".java");
            Logger.log("INTERFACE GENERATOR", "Generation successful: " + interfaceName);
        }

        Map<String, ScopeTable> interfaceTables = new HashMap<>();
        for(String name: interfaceNameList){
            ScopeTable table = parser.getClassScopeTable(new File(basePath + name +".java"));
            interfaceTables.put(name, table);
        }

        Long endTime = System.currentTimeMillis();
        interfaceTime = endTime - startTime;

        // CLASSES GENERATION
        startTime = System.currentTimeMillis();
        NormalClassDeclaration cl = null;
        String className = "";
        ScopeTable classScopeTable = null;

        List<String> javaInterfaceNames = new ArrayList<>();
        for (String name: interfaceNameList) {
            javaInterfaceNames.add(name + ".java");
        }

        List<String> classNames = new ArrayList<>(javaInterfaceNames);

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
                    cl = new NormalClassDeclaration(className, produceMain, interfaceTables);
                } catch (Exception e) {
                    Logger.logError("CLASS GENERATOR", "Generation failed: " + className);
                    e.printStackTrace();
                    return;
                }
            } else {
                classNames.add(className+".java");
                classScopeTable = parser.getClassScopeTable(new File(basePath + oldClassName +".java"), classScopeTable);
                try {

                    cl = new NormalClassDeclaration(className, produceMain, interfaceTables, classScopeTable);
                } catch (Exception e) {
                    Logger.logError("CLASS GENERATOR", "Generation failed: " + className);
                    e.printStackTrace();
                    return;
                }
            }
            Logger.log("CLASS GENERATOR", "Generation successful: " + className);

            if (cl != null) {
                save(cl, basePath + className +".java");
            }

        }
        endTime = System.currentTimeMillis();
        classTime = endTime - startTime;

        startTime = System.currentTimeMillis();

        if (CompileChecker.compileCheck(classNames) == 0) {

            Logger.log("compiler", "Compilation successful");
            endTime = System.currentTimeMillis();
            compileTime = endTime - startTime;

            startTime = System.currentTimeMillis();
            String mainClass = className;
            Runner runner = new Runner(config.isRun(), config.getTimeout());
            runner.execute(mainClass);
            endTime = System.currentTimeMillis();
            executionTime = endTime - startTime;
            Long end = System.currentTimeMillis();
            totalTime = end -start;

            Logger.log("STATS", "Interface generation time: "+  interfaceTime.toString() + " ms");
            Logger.log("STATS",  "Class generation time: "+ classTime.toString()+ " ms");
            Logger.log("STATS", "Compilation time: " + compileTime.toString()+ " ms");
            Logger.log("STATS", "Execution time: " + executionTime.toString()+ " ms");
            Logger.log("STATS", "Total time: " + totalTime.toString()+ " ms");
        } else {
            Logger.logError("compiler","Compilation failed \n Exit from random program generator");
            Logger.log("STATS: ", "Interface generation time: " + interfaceTime.toString() + " ms");
            Logger.log("STATS", "Class generation time: " + classTime.toString()+ " ms");
        }

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
