import checker.*;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import utils.Utils;
import visitor.common.AnnotationRemover;
import visitor.common.StringLiteralSolver;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String dataDir = null;
        boolean ifAllowMultiCategory = false;
        if(args.length != 2){
            System.out.println("Usage: java -jar <data_dir> <true/false>");
            System.exit(-1);
        } else{
            dataDir = args[0].trim();
            if(! args[1].trim().equals("true") && ! args[1].trim().equals("false")){
                System.out.println("Usage: java -jar <data_dir> <true/false>");
                System.exit(-1);
            } else if (args[1].trim().equals("true")){
                ifAllowMultiCategory = true;
            }
        }
        String methodDir = dataDir + "methods/";
        String fileDir = dataDir + "files/";
        String outputDir = dataDir + "output/";
        Map<String, Integer> patternCount = new HashMap<>();
        int FILE_NUMBER = Objects.requireNonNull(new File(fileDir).listFiles()).length;

        for(int i = 1; i <= FILE_NUMBER; i++) {
            String buggyMethodPath = methodDir + "buggy/" + String.format("%d.txt", i);
            String patchMethodPath = methodDir + "fixed/" + String.format("%d.txt", i);
            File buggyMethod = new File(buggyMethodPath);
            File patchMethod = new File(patchMethodPath);
            List<String> categories = classify(buggyMethod, patchMethod, ifAllowMultiCategory);
            if(categories == null){
                System.out.println("");
                continue;
            }
            System.out.println(String.join(",", categories));
            Checker checker;
            for(String category: categories) {
                String className = "checker." + category;
                Class<?> clazz = Class.forName(className);
                checker = (Checker) clazz.getConstructor().newInstance();
                String positiveSample;
                List<String> negativeSamples = new ArrayList<>();
                positiveSample = checker.getPositiveSample(buggyMethod, patchMethod, ASTParser.K_CLASS_BODY_DECLARATIONS);
                // just need to focus on the existence of the positive sample during building dataset_pr
                if (!ifAllowMultiCategory) {
                    if (positiveSample.equals("")) {
                        continue;
                    }
                    // the existence of the negative sample also needs to be determined
                } else {
                    String buggyFilePath = fileDir + String.format("%d.txt", i);
                    File buggyFile = new File(buggyFilePath);
                    CompilationUnit fileCUnit = (CompilationUnit) Utils.genAST(buggyFile, ASTParser.K_COMPILATION_UNIT);
                    StringLiteralSolver solver = new StringLiteralSolver();
                    fileCUnit.accept(solver);
                    AnnotationRemover remover = new AnnotationRemover();
                    fileCUnit.accept(remover);
                    // search negative samples in the same method
                    negativeSamples = checker.getNegativeSample(fileCUnit, true);
                    // search negative samples in the same file
                    if (negativeSamples.size() == 0) {
                        negativeSamples = checker.getNegativeSample(fileCUnit, false);
                    }
                    negativeSamples.remove(positiveSample);
                    if (positiveSample.equals("") || negativeSamples.size() == 0) {
                        continue;
                    }
                }

                String supperPattern;
                if (Character.isDigit(category.charAt(category.length() - 1))) {
                    supperPattern = category.substring(0, category.length() - 1);
                } else {
                    supperPattern = category;
                }

                // for saving positive sample
                if (!patternCount.containsKey(supperPattern)) {
                    patternCount.put(supperPattern, 1);
                }
                File positiveDir = new File(outputDir + "positive/" + supperPattern);
                if (!positiveDir.exists()) {
                    positiveDir.mkdirs();
                }
                File posFile = new File(positiveDir + "/" + patternCount.get(supperPattern) + ".txt");
                Utils.writeToFile(posFile, positiveSample);
                if (!ifAllowMultiCategory) {
                    patternCount.put(supperPattern, patternCount.get(supperPattern) + 1);
                } else {
                    // for saving negative sample
                    File negativeDir = new File(outputDir + "negative/" + supperPattern);
                    if (!negativeDir.exists()) {
                        negativeDir.mkdirs();
                    }
                    //randomly select one as the negative sample
                    int random_num = (int) (Math.random() * negativeSamples.size());
                    String negativeSample = negativeSamples.get(random_num);
                    File negFile = new File(negativeDir + "/" + patternCount.get(supperPattern) + ".txt");
                    Utils.writeToFile(negFile, negativeSample);
                    patternCount.put(supperPattern, patternCount.get(supperPattern) + 1);
                }
            }
        }
    }

    public static List<String> classify(File buggyMethod, File patchMethod, boolean ifAllowMultiCategory) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String[] checker_list = new String[]{
                "InsertCastChecker", "InsertMissedStmt1", "InsertMissedStmt2", "InsertMissedStmt3", "InsertMissedStmt4",
                "MutateDataType1", "MutateDataType2", "InsertNullPointerChecker1", "InsertNullPointerChecker2",
                "InsertNullPointerChecker3", "InsertNullPointerChecker4", "InsertNullPointerChecker5", "InsertRangeChecker1",
                "InsertRangeChecker2", "MutateClassInstanceCreation", "MutateReturnStmt", "MutateConditionalExpr1",
                "MutateConditionalExpr2", "MutateConditionalExpr3", "MutateOperators1", "MutateOperators2",
                "MutateOperators3", "MutateVariable1", "MutateVariable2", "MutateMethodInvExpr1", "MutateMethodInvExpr2",
                "MutateMethodInvExpr3", "MutateMethodInvExpr4", "MutateLiteralExpr1", "MutateLiteralExpr2",
                "MutateIntegerDivisionOperation1", "MutateIntegerDivisionOperation2", "MutateIntegerDivisionOperation3",
                "MoveStmt", "RemoveBuggyStmt"
        };

        List<String> categories = new ArrayList<>();
        int maxNodeDepth = 0;
        Checker checker;
        for (String checker_name : checker_list) {
            String className = "checker." + checker_name;
            Class<?> clazz = Class.forName(className);
            checker = (Checker) clazz.getConstructor().newInstance();
            if(checker.check(buggyMethod, patchMethod, ASTParser.K_CLASS_BODY_DECLARATIONS)) {
                if (ifAllowMultiCategory) {
                    categories.add(checker_name);
                } else {
                    maxNodeDepth = compareDepth(checker, maxNodeDepth, categories, checker_name);
                }
            }
        }
        solveContainRelation(categories);
        if(ifAllowMultiCategory){
            return categories;
        } else {
            solveSomeSpecialRelation(categories);
            return categories.size() <= 1? categories: null;
        }
    }

    public static void solveContainRelation(List<String> categories){
        if(categories.contains("MutateLiteralExpr1")){
            categories.remove("MutateLiteralExpr2");
        }
        if(categories.contains("MutateVariable1")){
            categories.remove("MutateVariable2");
        }
        if(categories.contains("MutateConditionalExpr2") || categories.contains("MutateConditionalExpr3")){
            categories.remove("MutateConditionalExpr1");
        }
        if(categories.contains("InsertCastChecker") || categories.contains("InsertRangeChecker1")
                || categories.contains("InsertRangeChecker2") || categories.contains("InsertNullPointerChecker1")){
            categories.remove("InsertMissedStmt4");
        }
    }

    public static void solveSomeSpecialRelation(List<String> categories){
        if(categories.contains("MutateConditionalExpr2") || categories.contains("MutateConditionalExpr3")){
            categories.remove("MutateMethodInvExpr2");
        }
    }

    public static int compareDepth(Checker checker, int maxDepth, List<String> categories, String category) {
        checker.calculateDepth();
        if (checker.getNodeDepth() == maxDepth) {
            categories.add(category);
        } else if (checker.getNodeDepth() > maxDepth) {
            categories.clear();
            maxDepth = checker.getNodeDepth();
            categories.add(category);
        }
        return maxDepth;
    }
}
