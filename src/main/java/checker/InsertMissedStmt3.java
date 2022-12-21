package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;

import java.io.File;
import java.util.List;

public class InsertMissedStmt3 extends Checker{

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
                if(diffChildNum != 1) {
                    return false;
                } else{
                    if(patchChildren.get(diffIndex) instanceof TryStatement){
                        TryStatement tryStmt = (TryStatement) patchChildren.get(diffIndex);
                        finalFixedNode = tryStmt;
                        Statement tryBody = tryStmt.getBody();
                        Statement finallBlock = tryStmt.getFinally();
                        List<Statement> blockStmt;
                        if(finallBlock == null) {
                            blockStmt = ((Block) tryBody).statements();
                            if (blockStmt.size() == 1) {
                                if (blockStmt.get(0).toString().equals(buggyChildren.get(diffIndex).toString())) {
                                    buggyParentNode = buggyChildren.get(diffIndex);
                                    while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                        buggyParentNode = buggyParentNode.getParent();
                                    }
                                    return true;
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
                while(i < patchChildren.size()) {
                    if (existDifference) {
                        if (!buggyChildren.get(i + endIndex - startIndex - 1).toString().equals(patchChildren.get(i).toString())) {
                            return false;
                        } else {
                            i++;
                        }
                    } else {
                        if (!buggyChildren.get(i).toString().equals(patchChildren.get(i).toString())) {
                            startIndex = i;
                            if (!(patchChildren.get(i) instanceof TryStatement)) {
                                return false;
                            } else {
                                TryStatement tryStmt = (TryStatement) patchChildren.get(i);
                                finalFixedNode = tryStmt;
                                buggyParentNode = buggyChildren.get(i);
                                while (!(buggyParentNode instanceof Statement) && buggyParentNode != null) {
                                    buggyParentNode = buggyParentNode.getParent();
                                }
                                Statement tryBody = tryStmt.getBody();
                                Statement finallBlock = tryStmt.getFinally();
                                if (finallBlock != null) {  // Do not want else part
                                    return false;
                                }
                                List<Statement> blockStmt;
                                blockStmt = ((Block) tryBody).statements();
                                int tmp = i;
                                for (Statement stmt : blockStmt) {
                                    if (tmp >= buggyChildren.size()) {
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
                        if (existDifference && patchChildren.size() + endIndex - startIndex - 1 != buggyChildren.size()) {
                            return false;
                        }
                        i++;
                    }
                }
                return existDifference;
            } else{
                return false;
            }
        }
    }

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }
}
