package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InsertNullPointerChecker2 extends InsertNullPointerChecker{

    public boolean recursiveCheck(ASTNode buggyNode, ASTNode patchNode, Type returnType) {
        if (buggyNode.toString().equals(patchNode.toString())) {
            return false;
        }
        List<ASTNode> buggyChildren = Utils.getChildren(buggyNode);
        List<ASTNode> patchChildren = Utils.getChildren(patchNode);
        if(! simpleCheck(buggyChildren, patchChildren)){
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
            return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
        } else if(buggyChildren.size() == patchChildren.size() - 1){
            int diffChildNum = 0;
            int diffIndex = -1;
            boolean existDiff = false;
            List<Statement> subsequentStmts = new ArrayList<Statement>();
            int i = 0;
            while(i < buggyChildren.size()){
                if(! buggyChildren.get(i).toString().equals(patchChildren.get(i + diffChildNum).toString())){
                    if(existDiff){  // one more difference
                        return false;
                    }
                    existDiff = true;
                    diffChildNum ++;
                    diffIndex = i;
                } else{
                    if(existDiff && buggyChildren.get(i) instanceof Statement){
                        subsequentStmts.add((Statement) buggyChildren.get(i));
                    }
                    i ++;
                }
            }
            if(! (existDiff && (patchChildren.get(diffIndex) instanceof IfStatement))) {
                return false;
            }
            IfStatement ifStmt = (IfStatement) patchChildren.get(diffIndex);
            finalFixedNode = ifStmt;
            if(! (ifStmt.getExpression() instanceof InfixExpression)){
                return false;
            }
            InfixExpression condExpr = (InfixExpression) ifStmt.getExpression();
            Expression leftOperand = condExpr.getLeftOperand();
            Expression rightOperand = condExpr.getRightOperand();
            InfixExpression.Operator operator = condExpr.getOperator();
            Statement thenStmt = ifStmt.getThenStatement();
            Statement elseStmt = ifStmt.getElseStatement();
            if(elseStmt != null){
                return false;
            }
            if(! (operator.equals(InfixExpression.Operator.EQUALS) && rightOperand instanceof NullLiteral)){
                return false;
            }
            ReturnStatement returnStmt;
            if(thenStmt instanceof ReturnStatement) {
                returnStmt = (ReturnStatement) thenStmt;
            } else if(thenStmt instanceof Block && ((Block) thenStmt).statements().size() == 1 && ((Block) thenStmt).statements().get(0) instanceof ReturnStatement) {
                returnStmt = (ReturnStatement) ((Block) thenStmt).statements().get(0);
            } else{
                return false;
            }
            if(checkExprExistInBlock(subsequentStmts, leftOperand)){
                return checkReturnType(returnStmt, returnType);
            } else{
                return false;
            }
        } else{
            return false;
        }
    }

    public boolean checkReturnType(ReturnStatement returnStmt, Type returnType){
        // for constructor
        if(returnType == null){
            return returnStmt.getExpression() == null;
        }
        else if(returnType instanceof PrimitiveType){
            PrimitiveType primitiveType = (PrimitiveType) returnType;
            if(primitiveType.getPrimitiveTypeCode().equals(PrimitiveType.BOOLEAN)){
                return returnStmt.getExpression() instanceof BooleanLiteral && returnStmt.getExpression().toString().equals("false");
            }
            else if(primitiveType.getPrimitiveTypeCode().equals(PrimitiveType.VOID)){
                return returnStmt.getExpression() == null;
            } else{
                return returnStmt.getExpression() instanceof NumberLiteral && returnStmt.getExpression().toString().equals("0");
            }
        } else if(returnType.toString().equals("String")){
            return returnStmt.getExpression() instanceof ClassInstanceCreation && returnStmt.getExpression().toString().equals("new String()");
        } else{
            return returnStmt.getExpression() instanceof NullLiteral;
        }
    }

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }
}
