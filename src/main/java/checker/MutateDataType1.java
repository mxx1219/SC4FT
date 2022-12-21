package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MutateDataType1 extends Checker{

    private String focusType = "";

    public boolean recursiveCheck(ASTNode buggyNode, ASTNode patchNode, Type returnType){
        if(buggyNode.toString().equals(patchNode.toString())) {
            return false;
        } else{
            List<ASTNode> buggyChildren = Utils.getChildren(buggyNode);
            List<ASTNode> patchChildren = Utils.getChildren(patchNode);
            if(buggyChildren.size() == patchChildren.size()){
                int diffChildNum = 0;
                int diffIndex = -1;
                for(int i = 0; i < patchChildren.size(); i++){
                    if(! buggyChildren.get(i).toString().equals(patchChildren.get(i).toString())){
                        diffChildNum ++;
                        diffIndex = i;
                    }
                }
                if(diffChildNum != 1) {
                    return false;
                } else{
                    if(buggyChildren.get(diffIndex) instanceof VariableDeclarationStatement && patchChildren.get(diffIndex) instanceof VariableDeclarationStatement){
                        focusType = "VariableDeclarationStatement";
                        VariableDeclarationStatement buggyVarDeclStmt = (VariableDeclarationStatement) buggyChildren.get(diffIndex);
                        VariableDeclarationStatement patchVarDeclStmt = (VariableDeclarationStatement) patchChildren.get(diffIndex);
                        List<ASTNode> buggyStmtChildren = Utils.getChildren(buggyVarDeclStmt);
                        List<ASTNode> patchStmtChildren = Utils.getChildren(patchVarDeclStmt);
                        if(buggyStmtChildren.size() != patchStmtChildren.size()){
                            return false;
                        }
                        int diffCount = 0;
                        int diffExprIndex = -1;
                        for(int i = 0; i < patchStmtChildren.size(); i++){
                            if(! buggyStmtChildren.get(i).toString().equals(patchStmtChildren.get(i).toString())){
                                diffCount ++;
                                diffExprIndex = i;
                            }
                        }
                        if(diffCount != 1) {
                            return false;
                        }
                        if(buggyStmtChildren.get(diffExprIndex) instanceof Type && patchStmtChildren.get(diffExprIndex) instanceof Type){

                            finalFixedNode = patchStmtChildren.get(diffExprIndex);
                            buggyParentNode = buggyStmtChildren.get(diffExprIndex);
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                            patchParentNode = patchStmtChildren.get(diffExprIndex);
                            while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                                patchParentNode = patchParentNode.getParent();
                            }
                            return buggyParentNode != null && patchParentNode != null; // filter the situation not in statement
                        } else{
                            return false;
                        }
                    } else if(buggyChildren.get(diffIndex) instanceof VariableDeclarationExpression && patchChildren.get(diffIndex) instanceof VariableDeclarationExpression){
                        focusType = "VariableDeclarationExpression";
                        VariableDeclarationExpression buggyVarDeclExpr = (VariableDeclarationExpression) buggyChildren.get(diffIndex);
                        VariableDeclarationExpression patchVarDeclExpr = (VariableDeclarationExpression) patchChildren.get(diffIndex);
                        List<ASTNode> buggyExprChildren = Utils.getChildren(buggyVarDeclExpr);
                        List<ASTNode> patchExprChildren = Utils.getChildren(patchVarDeclExpr);
                        if(buggyExprChildren.size() != patchExprChildren.size()){
                            return false;
                        }
                        int diffCount = 0;
                        int diffExprIndex = -1;
                        for(int i = 0; i < patchExprChildren.size(); i++){
                            if(! buggyExprChildren.get(i).toString().equals(patchExprChildren.get(i).toString())){
                                diffCount ++;
                                diffExprIndex = i;
                            }
                        }
                        if(diffCount != 1) {
                            return false;
                        }
                        if(buggyExprChildren.get(diffExprIndex) instanceof Type && patchExprChildren.get(diffExprIndex) instanceof Type){
                            finalFixedNode = patchExprChildren.get(diffExprIndex);
                            buggyParentNode = buggyExprChildren.get(diffExprIndex);
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                            patchParentNode = patchExprChildren.get(diffExprIndex);
                            while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                                patchParentNode = patchParentNode.getParent();
                            }
                            return buggyParentNode != null && patchParentNode != null; // filter the situation not in statement
                        } else{
                            return false;
                        }
                    } else if(buggyChildren.get(diffIndex) instanceof SingleVariableDeclaration && patchChildren.get(diffIndex) instanceof SingleVariableDeclaration){
                        focusType = "SingleVariableDeclaration";
                        SingleVariableDeclaration buggySingleVarDecl = (SingleVariableDeclaration) buggyChildren.get(diffIndex);
                        SingleVariableDeclaration patchSingleVarDecl = (SingleVariableDeclaration) patchChildren.get(diffIndex);
                        List<ASTNode> buggySingleVarChildren = Utils.getChildren(buggySingleVarDecl);
                        List<ASTNode> patchSingleVarChildren = Utils.getChildren(patchSingleVarDecl);
                        if(buggySingleVarChildren.size() != patchSingleVarChildren.size()){
                            return false;
                        }
                        int diffCount = 0;
                        int diffExprIndex = -1;
                        for(int i = 0; i < patchSingleVarChildren.size(); i++){
                            if(! buggySingleVarChildren.get(i).toString().equals(patchSingleVarChildren.get(i).toString())){
                                diffCount ++;
                                diffExprIndex = i;
                            }
                        }
                        if(diffCount != 1) {
                            return false;
                        }
                        if(buggySingleVarChildren.get(diffExprIndex) instanceof Type && patchSingleVarChildren.get(diffExprIndex) instanceof Type){
                            finalFixedNode = patchSingleVarChildren.get(diffExprIndex);
                            buggyParentNode = buggySingleVarChildren.get(diffExprIndex);
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                            patchParentNode = patchSingleVarChildren.get(diffExprIndex);
                            while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                                patchParentNode = patchParentNode.getParent();
                            }
                            return buggyParentNode != null && patchParentNode != null; // filter the situation not in statement
                        } else{
                            return false;
                        }
                    } else if(! (buggyChildren.get(diffIndex) instanceof VariableDeclarationStatement) &&
                            ! (patchChildren.get(diffIndex) instanceof VariableDeclarationStatement)) {
                        return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
                    } else if(! (buggyChildren.get(diffIndex) instanceof VariableDeclarationExpression) &&
                            ! (patchChildren.get(diffIndex) instanceof VariableDeclarationExpression)) {
                        return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
                    } else if(! (buggyChildren.get(diffIndex) instanceof SingleVariableDeclaration)
                            && ! (patchChildren.get(diffIndex) instanceof SingleVariableDeclaration)) {
                        return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
                    } else{
                        return false;
                    }
                }
            } else{
                return false;
            }
        }
    }

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }

    public List<String> getNegativeSample(CompilationUnit cUnit, boolean inMethod) {
        List<ASTNode> nodes;
        List<String> negSamples = new ArrayList<>();
        Finder visitor;
        switch (focusType) {
            case "VariableDeclarationStatement":
                visitor = new VariableDeclStmtFinder();
                break;
            case "VariableDeclarationExpression":
                visitor = new VariableDeclExprFinder();
                break;
            case "SingleVariableDeclaration":
                visitor = new SVDFinder();
                break;
            default:
                visitor = null;
        }
        if (visitor == null) {
            return negSamples;
        }
        visitor.setBuggyMethod(buggyMethodAst.toString());
        visitor.setInMethod(inMethod);
        cUnit.accept(visitor);
        nodes = visitor.getNodes();
        collect(cUnit, nodes, negSamples);
        return negSamples;
    }
}
