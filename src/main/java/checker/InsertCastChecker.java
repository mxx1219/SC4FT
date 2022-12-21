package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.CastExprFinder;
import visitor.common.CastExprVisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InsertCastChecker extends Checker{

    public boolean recursiveCheck(ASTNode buggyNode, ASTNode patchNode, Type returnType){
        if(buggyNode.toString().equals(patchNode.toString())){
            return false;
        } else{
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
                if(diffChildNum != 1){
                    return false;
                } else{
                    if(patchChildren.get(diffIndex) instanceof IfStatement){
                        IfStatement ifStmt = (IfStatement) patchChildren.get(diffIndex);
                        if(ifStmt.getExpression() instanceof InstanceofExpression){
                            finalFixedNode = ifStmt;
                            Type castType = ((InstanceofExpression) ifStmt.getExpression()).getRightOperand();
                            Expression expression = ((InstanceofExpression) ifStmt.getExpression()).getLeftOperand();
                            Statement thenStmt = ifStmt.getThenStatement();
                            Statement elseStmt = ifStmt.getElseStatement();
                            List<Statement> blockStmt = new ArrayList<Statement>();
                            if(elseStmt == null){
                                if (thenStmt instanceof Block) {
                                    blockStmt = ((Block) thenStmt).statements();
                                } else {
                                    blockStmt.add(thenStmt);
                                }
                                if(blockStmt.size() == 1){
                                    if (checkCastExpr(blockStmt, castType, expression)) {
                                        if(blockStmt.get(0).toString().equals(buggyChildren.get(diffIndex).toString())){
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
                }
            } else if(buggyChildren.size() > patchChildren.size()){
                int startIndex = 0;
                int endIndex = 0;
                int i = 0;
                boolean existDifference = false;
                while(i < patchChildren.size()){
                    if(existDifference){
                        if(! buggyChildren.get(i + endIndex - startIndex - 1).toString().equals(patchChildren.get(i).toString())){
                            return false;
                        } else{
                            i ++;
                        }
                    } else {
                        if (!buggyChildren.get(i).toString().equals(patchChildren.get(i).toString())) {
                            startIndex = i;
                            if (!(patchChildren.get(i) instanceof IfStatement)) {
                                return false;
                            } else {
                                IfStatement ifStmt = (IfStatement) patchChildren.get(i);
                                if (!(ifStmt.getExpression() instanceof InstanceofExpression)) {
                                    return false;
                                } else {
                                    finalFixedNode =ifStmt;
                                    Type castType = ((InstanceofExpression) ifStmt.getExpression()).getRightOperand();
                                    Expression expression = ((InstanceofExpression) ifStmt.getExpression()).getLeftOperand();
                                    Statement thenStmt = ifStmt.getThenStatement();
                                    Statement elseStmt = ifStmt.getElseStatement();
                                    if(elseStmt != null){
                                        return false;
                                    }
                                    List<Statement> blockStmt = new ArrayList<>();
                                    if (thenStmt instanceof Block) {
                                        blockStmt = ((Block) thenStmt).statements();
                                    } else {
                                        blockStmt.add(thenStmt);
                                    }
                                    if (!checkCastExpr(blockStmt, castType, expression)) {
                                        return false;
                                    }
                                    int tmp = i;
                                    for (Statement stmt : blockStmt) {
                                        if(tmp >= buggyChildren.size()){
                                            return false;
                                        }
                                        if (!stmt.toString().equals(buggyChildren.get(tmp).toString())) {
                                            return false;
                                        }
                                        tmp++;
                                    }
                                    endIndex = tmp;
                                    existDifference = true;
                                }
                            }
                        }
                        if (existDifference && patchChildren.size() + endIndex - startIndex - 1 != buggyChildren.size()) {
                            return false;
                        }
                        i ++;
                    }
                }
                return existDifference;
            } else{
                return false;
            }
        }
    }

    public boolean checkCastExpr(List<Statement> nodes, Type castType, Expression castExpr){
        boolean existMatchedCastExpr = false;
        for(Statement node: nodes){
            CastExprVisitor visitor = new CastExprVisitor();
            node.accept(visitor);
            List<CastExpression> castExpressions = visitor.getCastExpressions();
            for (CastExpression castExpression : castExpressions) {
                if (castExpression.getType().toString().equals(castType.toString()) && castExpression.getExpression().toString().equals(castExpr.toString())) {
                    existMatchedCastExpr = true;
                    buggyParentNode = castExpression;
                    while (!(buggyParentNode instanceof Statement) && buggyParentNode != null) {
                        buggyParentNode = buggyParentNode.getParent();
                    }
                    break;
                }
            }
            if(existMatchedCastExpr){
                break;
            }
        }
        return existMatchedCastExpr;
    }

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample2(buggyFile, patchFile, astParserType);
    }

    public List<String> getNegativeSample(CompilationUnit cUnit, boolean inMethod){
        List<ASTNode> nodes;
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
