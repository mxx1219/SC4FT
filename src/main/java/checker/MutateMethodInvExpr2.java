package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MutateMethodInvExpr2 extends MutateMethodInvExpr{

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
            if(buggyChildren.get(diffIndex) instanceof MethodInvocation && patchChildren.get(diffIndex) instanceof MethodInvocation){
                focusType = "MethodInvocation";
                MethodInvocation buggyMethodInv = (MethodInvocation) buggyChildren.get(diffIndex);
                MethodInvocation patchMethodInv = (MethodInvocation) patchChildren.get(diffIndex);
                if(buggyMethodInv.getName().toString().equals(patchMethodInv.getName().toString())
                        && ((buggyMethodInv.getExpression() == null && patchMethodInv.getExpression() == null)
                        || (buggyMethodInv.getExpression() != null && patchMethodInv.getExpression() != null && buggyMethodInv.getExpression().toString().equals(patchMethodInv.getExpression().toString())))
                        && buggyMethodInv.typeArguments().toString().equals(patchMethodInv.typeArguments().toString())){
                    if(! buggyMethodInv.arguments().toString().equals(patchMethodInv.arguments().toString())){
                        if(buggyMethodInv.arguments().size() == patchMethodInv.arguments().size()){
                            finalFixedNode = patchMethodInv;
                            buggyParentNode = buggyMethodInv;
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                            patchParentNode = patchMethodInv;
                            while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                                patchParentNode = patchParentNode.getParent();
                            }
                            return true;
                        }
                    }
                }
            } else if (buggyChildren.get(diffIndex) instanceof ConstructorInvocation && patchChildren.get(diffIndex) instanceof ConstructorInvocation){
                focusType = "ConstructorInvocation";
                ConstructorInvocation buggyConsInv = (ConstructorInvocation) buggyChildren.get(diffIndex);
                ConstructorInvocation patchConsInv = (ConstructorInvocation) patchChildren.get(diffIndex);
                if(buggyConsInv.typeArguments().toString().equals(patchConsInv.typeArguments().toString())){
                    if(! buggyConsInv.arguments().toString().equals(patchConsInv.arguments().toString())){
                        if(buggyConsInv.arguments().size() == patchConsInv.arguments().size()){
                            finalFixedNode = patchConsInv;
                            buggyParentNode = buggyConsInv;
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                            patchParentNode = patchConsInv;
                            while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                                patchParentNode = patchParentNode.getParent();
                            }
                            return true;
                        }
                    }
                }
            } else if (buggyChildren.get(diffIndex) instanceof SuperConstructorInvocation && patchChildren.get(diffIndex) instanceof SuperConstructorInvocation){
                focusType = "SuperConstructorInvocation";
                SuperConstructorInvocation buggySuperConsInv = (SuperConstructorInvocation) buggyChildren.get(diffIndex);
                SuperConstructorInvocation patchSuperConsInv = (SuperConstructorInvocation) patchChildren.get(diffIndex);
                if((buggySuperConsInv.getExpression() == null && patchSuperConsInv.getExpression() == null || buggySuperConsInv.getExpression() != null && patchSuperConsInv.getExpression() != null && buggySuperConsInv.getExpression().toString().equals(patchSuperConsInv.getExpression().toString()))
                        && buggySuperConsInv.typeArguments().toString().equals(patchSuperConsInv.typeArguments().toString())){
                    if(! buggySuperConsInv.arguments().toString().equals(patchSuperConsInv.arguments().toString())){
                        if(buggySuperConsInv.arguments().size() == patchSuperConsInv.arguments().size()){
                            finalFixedNode = patchSuperConsInv;
                            buggyParentNode = buggySuperConsInv;
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                            patchParentNode = patchSuperConsInv;
                            while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                                patchParentNode = patchParentNode.getParent();
                            }
                            return true;
                        }
                    }
                }
            } else if (buggyChildren.get(diffIndex) instanceof ClassInstanceCreation && patchChildren.get(diffIndex) instanceof ClassInstanceCreation){
                focusType = "ClassInstanceCreation";
                ClassInstanceCreation buggyClassInsCreation = (ClassInstanceCreation) buggyChildren.get(diffIndex);
                ClassInstanceCreation patchClassInsCreation = (ClassInstanceCreation) patchChildren.get(diffIndex);
                if(buggyClassInsCreation.getType().toString().equals(patchClassInsCreation.getType().toString())
                        && (buggyClassInsCreation.getExpression() == null && patchClassInsCreation.getExpression() == null || buggyClassInsCreation.getExpression() != null && patchClassInsCreation.getExpression() != null && buggyClassInsCreation.getExpression().toString().equals(patchClassInsCreation.getExpression().toString()))
                        && buggyClassInsCreation.typeArguments().toString().equals(patchClassInsCreation.typeArguments().toString())
                        && buggyClassInsCreation.getAnonymousClassDeclaration() == null && patchClassInsCreation.getAnonymousClassDeclaration() == null){
                    if(! buggyClassInsCreation.arguments().toString().equals(patchClassInsCreation.arguments().toString())){
                        if(buggyClassInsCreation.arguments().size() == patchClassInsCreation.arguments().size()){
                            finalFixedNode = patchClassInsCreation;
                            buggyParentNode = buggyClassInsCreation;
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                            patchParentNode = patchClassInsCreation;
                            while(!(patchParentNode instanceof Statement) && patchParentNode != null){
                                patchParentNode = patchParentNode.getParent();
                            }
                            return true;
                        }
                    }
                }
            }
            return recursiveCheck(buggyChildren.get(diffIndex), patchChildren.get(diffIndex), returnType);
        } else{
            return false;
        }
    }

    public void calculateDepth(){
        ASTNode parent = this.finalFixedNode;
        while(!(parent instanceof MethodDeclaration)){
            this.nodeDepth ++;
            parent = parent.getParent();
        }
        this.nodeDepth += 1;
    }
}
