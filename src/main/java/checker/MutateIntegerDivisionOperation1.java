package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;

import java.util.List;

public class MutateIntegerDivisionOperation1 extends MutateIntegerDivisionOperation{

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
                if(buggyOperator.equals(InfixExpression.Operator.DIVIDE) && patchOperator.equals(InfixExpression.Operator.DIVIDE)){
                    if(buggyLeftOperand.toString().equals(patchLeftOperand.toString())){
                        if(patchRightOperand instanceof CastExpression){
                            CastExpression castExpression = (CastExpression) patchRightOperand;
                            Type castType = castExpression.getType();
                            finalFixedNode = castType;
                            Expression expr = castExpression.getExpression();
                            if(buggyRightOperand.toString().equals(expr.toString())){
                                if(castType instanceof PrimitiveType){
                                    PrimitiveType primitiveType = (PrimitiveType) castType;
                                    if(primitiveType.getPrimitiveTypeCode().equals(PrimitiveType.DOUBLE) || primitiveType.getPrimitiveTypeCode().equals(PrimitiveType.FLOAT)){
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
                }
            }
            return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
        } else {
            return false;
        }
    }
}
