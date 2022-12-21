package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.ReturnStmtFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MutateReturnStmt extends Checker{

    public boolean recursiveCheck(ASTNode buggyNode, ASTNode patchNode, Type returnType) {
        if (buggyNode.toString().equals(patchNode.toString())) {
            return false;
        }
        List<ASTNode> buggyChildren = Utils.getChildren(buggyNode);
        List<ASTNode> patchChildren = Utils.getChildren(patchNode);
        int numStmtInBuggyChildren = 0;
        int numStmtInPatchChildren = 0;

        // None-statement level parse means illegal
        for(ASTNode buggyChild: buggyChildren){
            if(buggyChild instanceof Statement){
                numStmtInBuggyChildren ++;
            }
        }

        for(ASTNode patchChild: patchChildren){
            if(patchChild instanceof Statement){
                numStmtInPatchChildren ++;
            }
        }

        if(numStmtInBuggyChildren == 0 && numStmtInPatchChildren == 0){
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
            if (buggyChildren.get(diffIndex) instanceof ReturnStatement && patchChildren.get(diffIndex) instanceof ReturnStatement) {
                ReturnStatement buggyReturnStmt = (ReturnStatement) buggyChildren.get(diffIndex);
                ReturnStatement patchReturnStmt = (ReturnStatement) patchChildren.get(diffIndex);
                finalFixedNode = patchReturnStmt;
                buggyParentNode = buggyReturnStmt;
                patchParentNode = patchReturnStmt;
                if(buggyReturnStmt.getExpression() == null && patchReturnStmt.getExpression() == null){
                    return false;
                }else if(buggyReturnStmt.getExpression() == null || patchReturnStmt.getExpression() == null) {
                    return true;
                }else if(! buggyReturnStmt.getExpression().toString().equals(patchReturnStmt.getExpression().toString())) {
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
        ReturnStmtFinder visitor = new ReturnStmtFinder();
        visitor.setBuggyMethod(buggyMethodAst.toString());
        visitor.setInMethod(inMethod);
        cUnit.accept(visitor);
        nodes = visitor.getNodes();
        collect(cUnit, nodes, negSamples);
        return negSamples;
    }

}
