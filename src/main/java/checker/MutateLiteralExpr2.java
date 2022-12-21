package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;

import java.util.List;

public class MutateLiteralExpr2 extends MutateLiteralExpr{

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
            if((buggyChildren.get(diffIndex) instanceof BooleanLiteral || buggyChildren.get(diffIndex) instanceof CharacterLiteral
                    || buggyChildren.get(diffIndex) instanceof NumberLiteral || buggyChildren.get(diffIndex) instanceof StringLiteral)
                    && patchChildren.get(diffIndex) instanceof Expression) {
                if(! buggyChildren.get(diffIndex).toString().equals(patchChildren.get(diffIndex).toString())){
                    if(buggyChildren.get(diffIndex) instanceof BooleanLiteral){
                        focusType = "BooleanLiteral";
                    } else if(buggyChildren.get(diffIndex) instanceof CharacterLiteral){
                        focusType = "CharacterLiteral";
                    } else if(buggyChildren.get(diffIndex) instanceof NumberLiteral){
                        focusType = "NumberLiteral";
                    } else if(buggyChildren.get(diffIndex) instanceof StringLiteral){
                        focusType = "StringLiteral";
                    }
                    finalFixedNode = patchChildren.get(diffIndex);
                    buggyParentNode = buggyChildren.get(diffIndex);
                    while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                        buggyParentNode = buggyParentNode.getParent();
                    }
                    patchParentNode = patchChildren.get(diffIndex);
                    while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                        patchParentNode = patchParentNode.getParent();
                    }
                    if(buggyNode.getClass() != patchNode.getClass()){
                        return false;
                    }
                    return true;
                }
            }
            return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
        } else{
            return false;
        }
    }
}
