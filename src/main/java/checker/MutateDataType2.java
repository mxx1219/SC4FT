package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.CastExprFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MutateDataType2 extends Checker{

    public boolean recursiveCheck(ASTNode buggyNode, ASTNode patchNode, Type returnType) {
        if (buggyNode.toString().equals(patchNode.toString())) {
            return false;
        } else {
            List<ASTNode> buggyChildren = Utils.getChildren(buggyNode);
            List<ASTNode> patchChildren = Utils.getChildren(patchNode);
            if (buggyChildren.size() == patchChildren.size()) {
                int diffChildNum = 0;
                int diffIndex = -1;
                for (int i = 0; i < patchChildren.size(); i++) {
                    if (!buggyChildren.get(i).toString().equals(patchChildren.get(i).toString())) {
                        diffChildNum++;
                        diffIndex = i;
                    }
                }
                if (diffChildNum != 1) {
                    return false;
                } else {
                    if(buggyChildren.get(diffIndex).getNodeType() != patchChildren.get(diffIndex).getNodeType()){
                        return false;
                    }
                    if(buggyChildren.get(diffIndex) instanceof CastExpression && patchChildren.get(diffIndex) instanceof CastExpression){
                        CastExpression buggyCastExpr = (CastExpression) buggyChildren.get(diffIndex);
                        CastExpression patchCastExpr = (CastExpression) patchChildren.get(diffIndex);
                        List<ASTNode> buggyExprChildren = Utils.getChildren(buggyCastExpr);
                        List<ASTNode> patchExprChildren = Utils.getChildren(patchCastExpr);
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
                            return true;
                        } else{  // the expression in CastExpression may contains another child CastExpression node
                            return recursiveCheck(buggyExprChildren.get(diffExprIndex), patchExprChildren.get(diffExprIndex), returnType);
                        }
                    }
                    else if(! (buggyChildren.get(diffIndex) instanceof CastExpression) && ! (patchChildren.get(diffIndex) instanceof CastExpression)){
                        return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
                    } else{
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
    }

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }

    public List<String> getNegativeSample(CompilationUnit cUnit, boolean inMethod){
        List<ASTNode> nodes = null;
        List<String> negSamples = new ArrayList<>();
        CastExprFinder visitor = new CastExprFinder();
        visitor.setBuggyMethod(buggyMethodAst.toString());
        visitor.setInMethod(inMethod);
        cUnit.accept(visitor);
        nodes = visitor.getNodes();
        collect(cUnit, nodes, negSamples);
        return negSamples;
    }

}
