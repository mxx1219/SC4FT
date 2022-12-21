package checker;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MoveStmt extends Checker{

    private ASTNode leftDiffNode = null;
    private ASTNode rightDiffNode = null;

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
            List<Integer> diffIndex = new ArrayList<Integer>();
            for(int i = 0; i < patchChildren.size(); i++){
                if(! buggyChildren.get(i).toString().equals(patchChildren.get(i).toString())){
                    diffChildNum ++;
                    diffIndex.add(i);
                }
            }
            if(diffChildNum == 1) {
                return recursiveCheck(buggyChildren.get(diffIndex.get(0)), patchChildren.get(diffIndex.get(0)), returnType);
            } else if(diffChildNum > 1){
                for(int i = 0; i < buggyChildren.size(); i++){
                    for(int j = 0; j < patchChildren.size(); j++){
                        List<ASTNode> tmpBuggyChildren = new ArrayList<ASTNode>();
                        tmpBuggyChildren.addAll(buggyChildren);
                        List<ASTNode> tmpPatchChildren = new ArrayList<ASTNode>();
                        tmpPatchChildren.addAll(patchChildren);
                        if(tmpBuggyChildren.get(i).toString().equals(tmpPatchChildren.get(j).toString())){
                            tmpBuggyChildren.remove(i);
                            tmpPatchChildren.remove(j);
                            if(tmpBuggyChildren.toString().equals(tmpPatchChildren.toString())){
                                finalFixedNode = patchChildren.get(j);
                                buggyParentNode = buggyChildren.get(i);
                                while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                    buggyParentNode = buggyParentNode.getParent();
                                }
                                return true;
                            }
                        }
                    }
                }
                if(diffChildNum == 2){ // stmt adding in one node, and stmt deleting in another node
                    if(checkMoveStmtInTwoNodes(buggyChildren.get(diffIndex.get(0)), buggyChildren.get(diffIndex.get(1)), patchChildren.get(diffIndex.get(0)), patchChildren.get(diffIndex.get(1)))){
                        return true;
                    } else{
                        return false;
                    }
                } else{
                    return false;
                }
            } else{
                return false;
            }
        } else if(buggyChildren.size() == patchChildren.size() + 1){
            for(int i = 0; i < buggyChildren.size(); i++){
                List<ASTNode> tmpBuggyChildren = new ArrayList<ASTNode>();
                tmpBuggyChildren.addAll(buggyChildren);
                tmpBuggyChildren.remove(i);
                int diffNum = 0;
                int diffIndex = -1;
                for(int j = 0; j < patchChildren.size(); j++){
                    if(! patchChildren.get(j).toString().equals(tmpBuggyChildren.get(j).toString())){
                        diffNum ++;
                        diffIndex = j;
                    }
                }
                if(diffNum == 1){
                    if(checkAddOrDelete(tmpBuggyChildren.get(diffIndex), patchChildren.get(diffIndex),"left") == 2){
                        if(buggyChildren.get(i).toString().equals(leftDiffNode.toString())){
                            buggyParentNode = buggyChildren.get(i);
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        } else if(buggyChildren.size() == patchChildren.size() - 1){
            for(int i = 0; i < patchChildren.size(); i++){
                List<ASTNode> tmpPatchChildren = new ArrayList<ASTNode>();
                tmpPatchChildren.addAll(patchChildren);
                tmpPatchChildren.remove(i);
                int diffNum = 0;
                int diffIndex = -1;
                for(int j = 0; j < buggyChildren.size(); j++){
                    if(! buggyChildren.get(j).toString().equals(tmpPatchChildren.get(j).toString())){
                        diffNum ++;
                        diffIndex = j;
                    }
                }
                if(diffNum == 1){
                    if(checkAddOrDelete(buggyChildren.get(diffIndex), tmpPatchChildren.get(diffIndex),"left") == 1){
                        if(patchChildren.get(i).toString().equals(leftDiffNode.toString())){
                            finalFixedNode = patchChildren.get(i);
                            buggyParentNode = leftDiffNode;
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        } else{
            return false;
        }
    }

    public boolean checkMoveStmtInTwoNodes(ASTNode leftBuggyNode, ASTNode rightBuggyNode, ASTNode leftPatchNode, ASTNode rightPatchNode){
        int leftResultStatus;
        int rightResultStatus;
        leftResultStatus = checkAddOrDelete(leftBuggyNode, leftPatchNode, "left");
        rightResultStatus = checkAddOrDelete(rightBuggyNode, rightPatchNode, "right");
        if ((leftResultStatus == 1 && rightResultStatus == 2)){
            if(leftDiffNode != null && rightDiffNode != null && leftDiffNode.toString().equals(rightDiffNode.toString())){
                buggyParentNode = leftDiffNode;
                while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                    buggyParentNode = buggyParentNode.getParent();
                }
                return true;
            }
        } else if((leftResultStatus == 2 && rightResultStatus == 1)){
            if(leftDiffNode != null && rightDiffNode != null && leftDiffNode.toString().equals(rightDiffNode.toString())){
                buggyParentNode = rightDiffNode;
                while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                    buggyParentNode = buggyParentNode.getParent();
                }
                return true;
            }
        }
        return false;
    }

    public int checkAddOrDelete(ASTNode buggyNode, ASTNode patchNode, String direction){
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
            return 0;
        }

        if(buggyChildren.size() == patchChildren.size()){
            int diffChildNum = 0;
            int diffIndex = -1;
            for(int i = 0; i < buggyChildren.size(); i++){
                if(! buggyChildren.get(i).toString().equals(patchChildren.get(i).toString())){
                    diffChildNum ++;
                    diffIndex = i;
                }
            }
            if(diffChildNum != 1){
                return 0;
            }
            return checkAddOrDelete(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), direction);
        } else if(buggyChildren.size() == patchChildren.size() + 1){
            for(int i = 0; i < buggyChildren.size(); i++){
                List<ASTNode> tmpBuggyChildren = new ArrayList<ASTNode>();
                tmpBuggyChildren.addAll(buggyChildren);
                tmpBuggyChildren.remove(i);
                if(tmpBuggyChildren.toString().equals(patchChildren.toString())){
                    if(direction.equals("left")){
                        leftDiffNode = buggyChildren.get(i);
                    } else{
                        rightDiffNode = buggyChildren.get(i);
                    }
                    return 1;
                }
            }
            return 0;
        } else if(buggyChildren.size() == patchChildren.size() - 1){
            for(int i = 0; i < patchChildren.size(); i++){
                List<ASTNode> tmpPatchChildren = new ArrayList<ASTNode>();
                tmpPatchChildren.addAll(patchChildren);
                tmpPatchChildren.remove(i);
                if(tmpPatchChildren.toString().equals(buggyChildren.toString())){
                    finalFixedNode = patchChildren.get(i);
                    if(direction.equals("left")){
                        leftDiffNode = patchChildren.get(i);
                    } else{
                        rightDiffNode = patchChildren.get(i);
                    }
                    return 2;
                }
            }
            return 0;
        } else{
            return 0;
        }
    }

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }
}
