package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MutateConditionalExpr3 extends Checker{

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
            if(buggyChildren.get(diffIndex) instanceof Expression && patchChildren.get(diffIndex) instanceof InfixExpression){
                InfixExpression infixExpression = (InfixExpression) patchChildren.get(diffIndex);
                Expression buggyExpr = (Expression) buggyChildren.get(diffIndex);
                ASTNode tmpParentNode = buggyExpr;
                while(tmpParentNode != null && ! (tmpParentNode instanceof Statement)){
                    tmpParentNode = tmpParentNode.getParent();
                }
                if(tmpParentNode != null){
                    if(tmpParentNode instanceof IfStatement){
                        focusType = "IfStatement";
                    } else if(tmpParentNode instanceof WhileStatement){
                        focusType = "WhileStatement";
                    } else if(tmpParentNode instanceof ForStatement){
                        focusType = "ForStatement";
                    } else if(tmpParentNode instanceof DoStatement){
                        focusType = "DoStatement";
                    }
                }
                finalFixedNode = infixExpression;
                if(infixExpression.getOperator().equals(InfixExpression.Operator.CONDITIONAL_OR) || infixExpression.getOperator().equals(InfixExpression.Operator.CONDITIONAL_AND)){
                    Expression leftOperand = infixExpression.getLeftOperand();
                    Expression rightOperand = infixExpression.getRightOperand();
                    if(leftOperand.toString().equals(buggyExpr.toString()) || rightOperand.toString().equals(buggyExpr.toString())){
                        buggyParentNode = buggyExpr;
                        while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                            buggyParentNode = buggyParentNode.getParent();
                        }
                        patchParentNode = infixExpression;
                        while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                            patchParentNode = patchParentNode.getParent();
                        }
                        return true;
                    }
                }
            }
            return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
        } else{
            return false;
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
