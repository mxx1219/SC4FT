package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;

import java.io.File;
import java.util.List;

public class InsertMissedStmt1 extends Checker{

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
                    return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
                }
            } else if(buggyChildren.size() == patchChildren.size() - 1){
                int i = 0;
                boolean diffMatch = false;
                boolean diffAppears = false;
                int diffIndexInPatch = -1;
                int indexDiff = 0;
                while(i < patchChildren.size()){
                    if(i < buggyChildren.size()) {
                        if (!buggyChildren.get(i).toString().equals(patchChildren.get(i + indexDiff).toString())) {
                            if (diffAppears) {
                                return false;
                            }
                            diffAppears = true;
                            diffIndexInPatch = i;
                            if (patchChildren.get(i + indexDiff) instanceof ExpressionStatement) {
                                ExpressionStatement exprStmt = (ExpressionStatement) patchChildren.get(i + indexDiff);
                                finalFixedNode = exprStmt;
                                if (Utils.getChildren(exprStmt).size() == 1 && Utils.getChildren(exprStmt).get(0) instanceof MethodInvocation) {
                                    diffMatch = true;
                                }
                            }
                            indexDiff = 1;
                        } else {
                            i++;
                        }
                    } else{
                        if(!diffAppears){
                            diffAppears = true;
                            if (patchChildren.get(i) instanceof ExpressionStatement) {
                                diffIndexInPatch = i;
                                ExpressionStatement exprStmt = (ExpressionStatement) patchChildren.get(i);
                                finalFixedNode = exprStmt;
                                if (Utils.getChildren(exprStmt).size() == 1 && Utils.getChildren(exprStmt).get(0) instanceof MethodInvocation) {
                                    diffMatch = true;
                                }
                            }
                        }
                        i++;
                    }
                }
                if(diffAppears && diffMatch){
                    if(diffIndexInPatch < buggyChildren.size()){
                        buggyParentNode = buggyChildren.get(diffIndexInPatch);
                        while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                            buggyParentNode = buggyParentNode.getParent();
                        }
                    }else if(diffIndexInPatch == buggyChildren.size()){
                        if(diffIndexInPatch == 0){
                            buggyParentNode = buggyNode;
                        } else{
                            buggyParentNode = buggyChildren.get(diffIndexInPatch - 1);
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                        }
                    }
                    return true;
                }else{
                    return false;
                }
            } else{
                return false;
            }
        }
    }

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }
}
