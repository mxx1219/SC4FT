package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InsertMissedStmt4 extends Checker{

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
                    if(patchChildren.get(diffIndex) instanceof IfStatement){
                        IfStatement ifStmt = (IfStatement) patchChildren.get(diffIndex);
                        finalFixedNode = ifStmt;
                        Statement thenStmt = ifStmt.getThenStatement();
                        Statement elseStmt = ifStmt.getElseStatement();
                        List<Statement> blockStmt = new ArrayList<Statement>();
                        if(elseStmt == null) {
                            if (thenStmt instanceof Block) {
                                blockStmt = ((Block) thenStmt).statements();
                            } else {
                                blockStmt.add(thenStmt);
                            }
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
                                finalFixedNode = ifStmt;
                                buggyParentNode = buggyChildren.get(i);
                                while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                    buggyParentNode = buggyParentNode.getParent();
                                }
                                Statement thenStmt = ifStmt.getThenStatement();
                                Statement elseStmt = ifStmt.getElseStatement();
                                if(elseStmt != null){  // Do not want else part
                                    return false;
                                }
                                List<Statement> blockStmt = new ArrayList<Statement>();
                                if (thenStmt instanceof Block) {
                                    blockStmt = ((Block) thenStmt).statements();
                                } else {
                                    blockStmt.add(thenStmt);
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

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }
}
