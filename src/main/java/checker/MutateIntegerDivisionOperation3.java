package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;

import java.util.List;

public class MutateIntegerDivisionOperation3 extends MutateIntegerDivisionOperation{

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

            if(buggyChildren.get(diffIndex) instanceof InfixExpression && patchChildren.get(diffIndex) instanceof InfixExpression){
                InfixExpression buggyInfixExpr = (InfixExpression) buggyChildren.get(diffIndex);
                InfixExpression patchInfixExpr = (InfixExpression) patchChildren.get(diffIndex);
                Expression buggyLeftOperand = buggyInfixExpr.getLeftOperand();
                Expression buggyRightOperand = buggyInfixExpr.getRightOperand();
                InfixExpression.Operator buggyOperator = buggyInfixExpr.getOperator();
                Expression patchLeftOperand = patchInfixExpr.getLeftOperand();
                Expression patchRightOperand = patchInfixExpr.getRightOperand();
                InfixExpression.Operator patchOperator = patchInfixExpr.getOperator();
                if(buggyOperator.equals(InfixExpression.Operator.DIVIDE) && patchOperator.equals(InfixExpression.Operator.TIMES)){
                    finalFixedNode = patchInfixExpr;
                    if(buggyLeftOperand.toString().equals(patchRightOperand.toString())){
                        boolean flag = false;
                        InfixExpression innerDivision = null;
                        if(patchLeftOperand instanceof ParenthesizedExpression) {
                            if(((ParenthesizedExpression) patchLeftOperand).getExpression() instanceof InfixExpression) {
                                flag = true;
                                innerDivision = (InfixExpression) ((ParenthesizedExpression) patchLeftOperand).getExpression();
                            }
                        } else if(patchLeftOperand instanceof InfixExpression) {
                            flag = true;
                            innerDivision = (InfixExpression) patchLeftOperand;
                        }
                        if(flag && innerDivision.getOperator().equals(InfixExpression.Operator.DIVIDE)) {
                            if(innerDivision.getLeftOperand() instanceof NumberLiteral && ((NumberLiteral)innerDivision.getLeftOperand()).toString().equals("1.0") && innerDivision.getRightOperand().toString().equals(buggyRightOperand.toString())) {
                                buggyParentNode = buggyInfixExpr;
                                while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                    buggyParentNode = buggyParentNode.getParent();
                                }
                                patchParentNode = patchInfixExpr;
                                while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                                    patchParentNode = patchParentNode.getParent();
                                }
                                return true;
                            }
                        }
                    }
                }
            }
            return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
        } else {
            return false;
        }
    }
}
