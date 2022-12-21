package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MutateConditionalExpr1 extends Checker{

    private String focusType = "";

    public boolean recursiveCheck(ASTNode buggyNode, ASTNode patchNode, Type returnType) {
        if (buggyNode.toString().equals(patchNode.toString())) {
            return false;
        }
        List<ASTNode> buggyChildren = Utils.getChildren(buggyNode);
        List<ASTNode> patchChildren = Utils.getChildren(patchNode);
        if(buggyChildren.size() == 0 || patchChildren.size() == 0){
            return false;
        }
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
            }

            if(buggyChildren.get(diffIndex) instanceof IfStatement && patchChildren.get(diffIndex) instanceof IfStatement){
                focusType = "IfStatement";
                boolean flag = checkDiffNumInChildren(buggyChildren.get(diffIndex), patchChildren.get(diffIndex));
                if(flag){
                    Expression buggyCondExpr = ((IfStatement) buggyChildren.get(diffIndex)).getExpression();
                    Expression patchCondExpr = ((IfStatement) patchChildren.get(diffIndex)).getExpression();
                    if(! buggyCondExpr.toString().equals(patchCondExpr.toString())){
                        finalFixedNode = patchCondExpr;
                        buggyParentNode = buggyCondExpr;
                        while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                            buggyParentNode = buggyParentNode.getParent();
                        }
                        patchParentNode = patchCondExpr;
                        while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                            patchParentNode = patchParentNode.getParent();
                        }
                        return true;
                    }
                }

            } else if(buggyChildren.get(diffIndex) instanceof WhileStatement && patchChildren.get(diffIndex) instanceof WhileStatement){
                focusType = "WhileStatement";
                boolean flag = checkDiffNumInChildren(buggyChildren.get(diffIndex), patchChildren.get(diffIndex));
                if(flag){
                    Expression buggyCondExpr = ((WhileStatement) buggyChildren.get(diffIndex)).getExpression();
                    Expression patchCondExpr = ((WhileStatement) patchChildren.get(diffIndex)).getExpression();
                    if(! buggyCondExpr.toString().equals(patchCondExpr.toString())){
                        finalFixedNode = patchCondExpr;
                        buggyParentNode = buggyCondExpr;
                        while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                            buggyParentNode = buggyParentNode.getParent();
                        }
                        patchParentNode = patchCondExpr;
                        while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                            patchParentNode = patchParentNode.getParent();
                        }
                        return true;
                    }
                }
            } else if(buggyChildren.get(diffIndex) instanceof ForStatement && patchChildren.get(diffIndex) instanceof ForStatement){
                focusType = "ForStatement";
                boolean flag = checkDiffNumInChildren(buggyChildren.get(diffIndex), patchChildren.get(diffIndex));
                if(flag){
                    Expression buggyCondExpr = ((ForStatement) buggyChildren.get(diffIndex)).getExpression();
                    Expression patchCondExpr = ((ForStatement) patchChildren.get(diffIndex)).getExpression();
                    if(buggyCondExpr != null && patchCondExpr != null && ! buggyCondExpr.toString().equals(patchCondExpr.toString())){
                        finalFixedNode = patchCondExpr;
                        buggyParentNode = buggyCondExpr;
                        while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                            buggyParentNode = buggyParentNode.getParent();
                        }
                        patchParentNode = patchCondExpr;
                        while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                            patchParentNode = patchParentNode.getParent();
                        }
                        return true;
                    }
                }

            } else if(buggyChildren.get(diffIndex) instanceof DoStatement && patchChildren.get(diffIndex) instanceof DoStatement){
                focusType = "DoStatement";
                boolean flag = checkDiffNumInChildren(buggyChildren.get(diffIndex), patchChildren.get(diffIndex));
                if(flag){
                    Expression buggyCondExpr = ((DoStatement) buggyChildren.get(diffIndex)).getExpression();
                    Expression patchCondExpr = ((DoStatement) patchChildren.get(diffIndex)).getExpression();
                    if(! buggyCondExpr.toString().equals(patchCondExpr.toString())){
                        finalFixedNode = patchCondExpr;
                        buggyParentNode = buggyCondExpr;
                        while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                            buggyParentNode = buggyParentNode.getParent();
                        }
                        patchParentNode = patchCondExpr;
                        while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                            patchParentNode = patchParentNode.getParent();
                        }
                        return true;
                    }
                }
            }
            return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
        } else {
            return false;
        }
    }

    public static boolean checkDiffNumInChildren(ASTNode buggyNode, ASTNode patchNode){
        List<ASTNode> buggyChildren = Utils.getChildren(buggyNode);
        List<ASTNode> patchChildren = Utils.getChildren(patchNode);
        int diffNum = 0;
        if(buggyChildren.size() != patchChildren.size()){
            return false;
        } else{
            for(int i = 0; i < buggyChildren.size(); i++){
                if(! buggyChildren.get(i).toString().equals(patchChildren.get(i).toString())){
                    diffNum ++;
                }
            }
            return diffNum == 1;
        }
    }

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }

    public List<String> getNegativeSample(CompilationUnit cUnit, boolean inMethod){
        List<ASTNode> nodes;
        List<String> negSamples = new ArrayList<>();
        Finder visitor;
        switch (focusType) {
            case "IfStatement":
                visitor = new IfStmtFinder();
                break;
            case "WhileStatement":
                visitor = new WhileStmtFinder();
                break;
            case "ForStatement":
                visitor = new ForStmtFinder();
                break;
            case "DoStatement":
                visitor = new DoStmtFinder();
                break;
            default:
                visitor = null;
        }
        if(visitor == null){
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
