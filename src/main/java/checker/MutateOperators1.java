package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MutateOperators1 extends Checker{

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

            if(buggyChildren.get(diffIndex) instanceof InfixExpression && patchChildren.get(diffIndex) instanceof InfixExpression) {
                focusType = "InfixExpression";
                InfixExpression buggyInfixExpr = (InfixExpression) buggyChildren.get(diffIndex);
                InfixExpression patchInfixExpr = (InfixExpression) patchChildren.get(diffIndex);

                Expression buggyLeftOperand = buggyInfixExpr.getLeftOperand();
                Expression buggyRightOperand = buggyInfixExpr.getRightOperand();
                InfixExpression.Operator buggyOperator = buggyInfixExpr.getOperator();

                Expression patchLeftOperand = patchInfixExpr.getLeftOperand();
                Expression patchRightOperand = patchInfixExpr.getRightOperand();
                InfixExpression.Operator patchOperator = patchInfixExpr.getOperator();

                if (!buggyOperator.equals(patchOperator)) {
                    if(! buggyInfixExpr.hasExtendedOperands() && ! patchInfixExpr.hasExtendedOperands()){
                        if (buggyLeftOperand.toString().equals(patchLeftOperand.toString()) && buggyRightOperand.toString().equals(patchRightOperand.toString())) {
                            finalFixedNode = patchInfixExpr;
                            buggyParentNode = buggyInfixExpr;
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                            patchParentNode = patchInfixExpr;
                            while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                                patchParentNode = patchParentNode.getParent();
                            }
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            } else if(buggyChildren.get(diffIndex) instanceof Assignment && patchChildren.get(diffIndex) instanceof Assignment){
                focusType = "Assignment";
                Assignment buggyAssignment = (Assignment) buggyChildren.get(diffIndex);
                Assignment patchAssignment = (Assignment) patchChildren.get(diffIndex);

                Expression buggyLeftOperand = buggyAssignment.getLeftHandSide();
                Expression buggyRightOperand = buggyAssignment.getRightHandSide();
                Assignment.Operator buggyOperator = buggyAssignment.getOperator();

                Expression patchLeftOperand = patchAssignment.getLeftHandSide();
                Expression patchRightOperand = patchAssignment.getRightHandSide();
                Assignment.Operator patchOperator = patchAssignment.getOperator();

                if(!buggyOperator.equals(patchOperator)){
                    if (buggyLeftOperand.toString().equals(patchLeftOperand.toString()) && buggyRightOperand.toString().equals(patchRightOperand.toString())) {
                        finalFixedNode = patchAssignment;
                        buggyParentNode = buggyAssignment;
                        while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                            buggyParentNode = buggyParentNode.getParent();
                        }
                        patchParentNode = patchAssignment;
                        while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                            patchParentNode = patchParentNode.getParent();
                        }
                        return true;
                    } else{
                        return false;
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

    public List<String> getNegativeSample(CompilationUnit cUnit, boolean inMethod) {
        List<ASTNode> nodes;
        List<String> negSamples = new ArrayList<>();
        Finder visitor;
        switch (focusType) {
            case "InfixExpression":
                visitor = new InfixExprFinder();
                break;
            case "Assignment":
                visitor = new AssignmentFinder();
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

    public void calculateDepth(){
        ASTNode parent = this.finalFixedNode;
        while(!(parent instanceof MethodDeclaration)){
            this.nodeDepth ++;
            parent = parent.getParent();
        }
        this.nodeDepth += 1;
    }
}
