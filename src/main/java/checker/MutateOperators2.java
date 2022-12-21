package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.ParenthesizedExprFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MutateOperators2 extends Checker{

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

                finalFixedNode = patchInfixExpr;

                if(buggyOperator.equals(patchOperator)){
                    if(buggyLeftOperand instanceof ParenthesizedExpression && patchRightOperand instanceof ParenthesizedExpression){
                        Expression buggyThirdExpr = buggyRightOperand;
                        Expression patchFirstExpr = patchLeftOperand;
                        Expression buggyInnerExpr = ((ParenthesizedExpression) buggyLeftOperand).getExpression();
                        Expression patchInnerExpr = ((ParenthesizedExpression) patchRightOperand).getExpression();
                        if(buggyInnerExpr instanceof InfixExpression && patchInnerExpr instanceof InfixExpression){
                            Expression buggyFirstExpr = ((InfixExpression) buggyInnerExpr).getLeftOperand();
                            Expression buggySecondExpr = ((InfixExpression) buggyInnerExpr).getRightOperand();
                            Expression patchSecondExpr = ((InfixExpression) patchInnerExpr).getLeftOperand();
                            Expression patchThirdExpr = ((InfixExpression) patchInnerExpr).getRightOperand();
                            if(buggyFirstExpr.toString().equals(patchFirstExpr.toString()) && buggySecondExpr.toString().equals(patchSecondExpr.toString()) && buggyThirdExpr.toString().equals(patchThirdExpr.toString())){
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
                    } else if(patchLeftOperand instanceof ParenthesizedExpression && buggyRightOperand instanceof ParenthesizedExpression){
                        Expression patchThirdExpr = patchRightOperand;
                        Expression buggyFirstExpr = buggyLeftOperand;
                        Expression patchInnerExpr = ((ParenthesizedExpression) patchLeftOperand).getExpression();
                        Expression buggyInnerExpr = ((ParenthesizedExpression) buggyRightOperand).getExpression();
                        if(patchInnerExpr instanceof InfixExpression && buggyInnerExpr instanceof InfixExpression){
                            Expression patchFirstExpr = ((InfixExpression) patchInnerExpr).getLeftOperand();
                            Expression patchSecondExpr = ((InfixExpression) patchInnerExpr).getRightOperand();
                            Expression buggySecondExpr = ((InfixExpression) buggyInnerExpr).getLeftOperand();
                            Expression buggyThirdExpr = ((InfixExpression) buggyInnerExpr).getRightOperand();
                            if(buggyFirstExpr.toString().equals(patchFirstExpr.toString()) && buggySecondExpr.toString().equals(patchSecondExpr.toString()) && buggyThirdExpr.toString().equals(patchThirdExpr.toString())){
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
        ParenthesizedExprFinder visitor = new ParenthesizedExprFinder();
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
