package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.InstanceofExprFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MutateOperators3 extends Checker{

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

            if(buggyChildren.get(diffIndex) instanceof InstanceofExpression && patchChildren.get(diffIndex) instanceof InfixExpression){
                InstanceofExpression instanceofExpression = (InstanceofExpression) buggyChildren.get(diffIndex);
                InfixExpression infixExpression = (InfixExpression) patchChildren.get(diffIndex);

                Expression buggyLeftOperand = instanceofExpression.getLeftOperand();

                Expression patchLeftOperand = infixExpression.getLeftOperand();
                Expression patchRightOperand = infixExpression.getRightOperand();
                InfixExpression.Operator patchOperator = infixExpression.getOperator();

                if(patchOperator.equals(InfixExpression.Operator.NOT_EQUALS) && patchRightOperand instanceof NullLiteral && buggyLeftOperand.toString().equals(patchLeftOperand.toString())){
                    finalFixedNode = infixExpression;
                    buggyParentNode = instanceofExpression;
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
        InstanceofExprFinder visitor = new InstanceofExprFinder();
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
