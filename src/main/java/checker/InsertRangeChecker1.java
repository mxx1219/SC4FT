package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.common.ArrayAccessVisitor;
import visitor.finder.ArrayAccessFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InsertRangeChecker1 extends Checker{

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
            if (patchChildren.get(diffIndex) instanceof IfStatement) {
                IfStatement ifStmt = (IfStatement) patchChildren.get(diffIndex);
                finalFixedNode = ifStmt;
                if(ifStmt.getExpression() instanceof InfixExpression) {
                    InfixExpression infixExpression = (InfixExpression) ifStmt.getExpression();
                    InfixExpression.Operator infixOperator = infixExpression.getOperator();
                    Expression leftOperand = infixExpression.getLeftOperand();
                    Expression rightOperand = infixExpression.getRightOperand();
                    Statement thenStmt = ifStmt.getThenStatement();
                    Statement elseStmt = ifStmt.getElseStatement();
                    List<Statement> blockStmt = new ArrayList<Statement>();
                    if (elseStmt == null) {
                        if (thenStmt instanceof Block) {
                            blockStmt = ((Block) thenStmt).statements();
                        } else {
                            blockStmt.add(thenStmt);
                        }
                        if (blockStmt.size() == 1) {
                            if (infixOperator.equals(InfixExpression.Operator.LESS)) {
                                Expression arrayExpr = null;
                                if (rightOperand instanceof QualifiedName) {
                                    QualifiedName qualifiedName = (QualifiedName) rightOperand;
                                    if (qualifiedName.getName().toString().equals("length")) {
                                        arrayExpr = qualifiedName.getQualifier();
                                    }
                                } else if (rightOperand instanceof FieldAccess) {
                                    FieldAccess fieldAccess = (FieldAccess) rightOperand;
                                    if (fieldAccess.getName().toString().equals("length")) {
                                        arrayExpr = fieldAccess.getExpression();
                                    }
                                }
                                if (arrayExpr != null) {
                                    if (checkArrayAccess(blockStmt, arrayExpr, leftOperand)) {
                                        if (blockStmt.get(0).toString().equals(buggyChildren.get(diffIndex).toString())) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
        } else if(buggyChildren.size() > patchChildren.size()){
            int startIndex = 0;
            int endIndex = 0;
            int i = 0;
            boolean existDifference = false;
            while(i < patchChildren.size()){
                if(existDifference){
                    if(! buggyChildren.get(i + endIndex - startIndex - 1).toString().equals(patchChildren.get(i).toString())) {
                        return false;
                    }
                    i ++;
                } else{
                    if (! buggyChildren.get(i).toString().equals(patchChildren.get(i).toString())) {
                        startIndex = i;
                        if (!(patchChildren.get(i) instanceof IfStatement)) {
                            return false;
                        }
                        IfStatement ifStmt = (IfStatement) patchChildren.get(i);
                        finalFixedNode = ifStmt;
                        if(! (ifStmt.getExpression() instanceof InfixExpression)) {
                            return false;
                        }
                        InfixExpression infixExpression = (InfixExpression) ifStmt.getExpression();
                        InfixExpression.Operator infixOperator = infixExpression.getOperator();
                        Expression leftOperand = infixExpression.getLeftOperand();
                        Expression rightOperand = infixExpression.getRightOperand();
                        Statement thenStmt = ifStmt.getThenStatement();
                        Statement elseStmt = ifStmt.getElseStatement();
                        List<Statement> blockStmt = new ArrayList<Statement>();
                        if (elseStmt != null) {
                            return false;
                        }
                        if (thenStmt instanceof Block) {
                            blockStmt = ((Block) thenStmt).statements();
                        } else {
                            blockStmt.add(thenStmt);
                        }
                        if (! infixOperator.equals(InfixExpression.Operator.LESS)) {
                            return false;
                        }
                        Expression arrayExpr = null;
                        if (rightOperand instanceof QualifiedName) {
                            QualifiedName qualifiedName = (QualifiedName) rightOperand;
                            if (qualifiedName.getName().toString().equals("length")) {
                                arrayExpr = qualifiedName.getQualifier();
                            }
                        } else if (rightOperand instanceof FieldAccess) {
                            FieldAccess fieldAccess = (FieldAccess) rightOperand;
                            if (fieldAccess.getName().toString().equals("length")) {
                                arrayExpr = fieldAccess.getExpression();
                            }
                        } else{
                            return false;
                        }
                        if (arrayExpr == null) {
                            return false;
                        }
                        if (! checkArrayAccess(blockStmt, arrayExpr, leftOperand)) {
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

    public boolean checkArrayAccess(List<Statement> statements, Expression arrayExpr, Expression indexExpr){
        List<ArrayAccess> arrayAccesses = new ArrayList<>();
        ArrayAccessVisitor visitor = new ArrayAccessVisitor();
        for(Statement stmt: statements){
            stmt.accept(visitor);
            arrayAccesses.addAll(visitor.getArrayAccesses());
        }
        for(ArrayAccess arrayAccess: arrayAccesses){
            if(arrayAccess.getArray().toString().equals(arrayExpr.toString()) && arrayAccess.getIndex().toString().equals(indexExpr.toString())){
                buggyParentNode = arrayAccess;
                while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                    buggyParentNode = buggyParentNode.getParent();
                }
                return true;
            }
        }
        return false;
    }

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample2(buggyFile, patchFile, astParserType);
    }

    public List<String> getNegativeSample(CompilationUnit cUnit, boolean inMethod){
        List<ASTNode> nodes;
        List<String> negSamples = new ArrayList<>();
        ArrayAccessFinder visitor = new ArrayAccessFinder();
        visitor.setBuggyMethod(buggyMethodAst.toString());
        visitor.setInMethod(inMethod);
        cUnit.accept(visitor);
        nodes = visitor.getNodes();
        collect(cUnit, nodes, negSamples);
        return negSamples;
    }
}
