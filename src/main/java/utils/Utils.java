package utils;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {

    public static ASTNode genAST(String source, int type){
        ASTParser astParser = ASTParser.newParser(AST.JLS4);
        Map<String, String> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_4, options);
        astParser.setCompilerOptions(options);
        astParser.setSource(source.toCharArray());
        astParser.setKind(type);
        astParser.setResolveBindings(true);
        return astParser.createAST(null);
    }

    public static ASTNode genAST(File file, int type){
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(file));
            while((line = br.readLine()) != null){
                sb.append(line);
                sb.append("\n");
            }
            br.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return genAST(sb.toString(), type);
    }

    public static List<ASTNode> getChildren(ASTNode node){
        List<ASTNode> childNodes = new ArrayList<>();
        List listProperty = node.structuralPropertiesForType();
        for(Object obj: listProperty){
            StructuralPropertyDescriptor propertyDescriptor = (StructuralPropertyDescriptor) obj;
            if (propertyDescriptor instanceof ChildListPropertyDescriptor) {
                ChildListPropertyDescriptor childListPropertyDescriptor = (ChildListPropertyDescriptor) propertyDescriptor;
                Object children = node.getStructuralProperty(childListPropertyDescriptor);
                List<ASTNode> childrenNodes = (List<ASTNode>) children;
                for (ASTNode childNode : childrenNodes) {
                    if(childNode instanceof Javadoc || childNode instanceof Annotation){
                        continue;
                    }
                    if (childNode == null) {
                        continue;
                    }
                    childNodes.add(childNode);
                }
            } else if(propertyDescriptor instanceof ChildPropertyDescriptor){
                ChildPropertyDescriptor childPropertyDescriptor = (ChildPropertyDescriptor) propertyDescriptor;
                Object child = node.getStructuralProperty(childPropertyDescriptor);
                ASTNode childNode = (ASTNode) child;
                if(childNode instanceof Javadoc || childNode instanceof Annotation){
                    continue;
                }
                if(childNode == null){
                    continue;
                }
                childNodes.add(childNode);
            }
        }
        return childNodes;
    }

    public static int parseLineNumber(String methodString, int stmtStartPos){
        methodString = methodString.substring(0, stmtStartPos);
        int enterCount = 0;
        char[] chars = methodString.toCharArray();
        for(char character: chars) {
            if (character == '\n') {
                enterCount++;
            }
        }
        return enterCount + 1;
    }

    public static boolean isVariable(SimpleName node){
        boolean ifInStmt = false;
        ASTNode parent = node.getParent();
        while(parent != null){
            if(parent instanceof Statement){
                ifInStmt = true;
                break;
            }
            parent = parent.getParent();
        }
        if(ifInStmt){
            if(node.getParent() instanceof MethodInvocation) {
                String property = node.getLocationInParent().getId();
                if (! property.equals("name")){
                    return !node.isDeclaration();
                }
            } else{
                if(! (node.getParent() instanceof Type)){
                    return !node.isDeclaration();
                }
            }
        }
        return false;
    }

    public static void writeToFile(File file, String content) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
