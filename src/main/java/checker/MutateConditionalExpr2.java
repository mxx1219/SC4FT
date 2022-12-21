package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.CondInfixExprFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MutateConditionalExpr2 extends Checker{

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
            if(buggyChildren.get(diffIndex) instanceof InfixExpression && patchChildren.get(diffIndex) instanceof Expression){
                InfixExpression infixExpression = (InfixExpression) buggyChildren.get(diffIndex);
                Expression patchExpr = (Expression) patchChildren.get(diffIndex);
                finalFixedNode = patchExpr;
                if(infixExpression.getOperator().equals(InfixExpression.Operator.CONDITIONAL_OR) || infixExpression.getOperator().equals(InfixExpression.Operator.CONDITIONAL_AND)){
                    Expression leftOperand = infixExpression.getLeftOperand();
                    Expression rightOperand = infixExpression.getRightOperand();
                    if(leftOperand.toString().equals(patchExpr.toString()) || rightOperand.toString().equals(patchExpr.toString())){
                        buggyParentNode = infixExpression;
                        while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                            buggyParentNode = buggyParentNode.getParent();
                        }
                        patchParentNode = patchExpr;
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
        CondInfixExprFinder visitor = new CondInfixExprFinder();
        visitor.setBuggyMethod(buggyMethodAst.toString());
        visitor.setInMethod(inMethod);
        cUnit.accept(visitor);
        nodes = visitor.getNodes();
        collect(cUnit, nodes, negSamples);
        return negSamples;
    }
}
