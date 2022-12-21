package checker;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import utils.Utils;

import java.io.File;
import java.util.List;

public class RemoveBuggyStmt extends Checker{

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
            return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
        } else if(buggyChildren.size() == patchChildren.size() + 1){
            int i = 0;
            boolean diffMatch = false;
            boolean diffAppears = false;
            int indexDiff = 0;
            while(i < patchChildren.size()){
                if(! buggyChildren.get(i + indexDiff).toString().equals(patchChildren.get(i).toString())) {
                    if (diffAppears) {
                        return false;
                    }
                    diffAppears = true;
                    buggyParentNode = buggyChildren.get(i + indexDiff);
                    while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                        buggyParentNode = buggyParentNode.getParent();
                    }
                    if (buggyChildren.get(i + indexDiff) instanceof Statement) {
                        diffMatch = true;
                    }
                    indexDiff = 1;
                } else{
                    i ++;
                }
            }
            if(diffAppears && diffMatch){
                finalFixedNode = patchNode;
                return true;
            } else{
                return false;
            }
        } else{
            return false;
        }
    }

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }
}
