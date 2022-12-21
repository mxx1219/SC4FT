package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.*;

import java.util.ArrayList;
import java.util.List;

public class MutateMethodInvExpr1 extends MutateMethodInvExpr{

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
                if(! buggyMethodInv.getName().toString().equals(patchMethodInv.getName().toString())){
                    finalFixedNode = patchMethodInv.getName();
                    List<ASTNode> buggyMethodChildren = Utils.getChildren(buggyMethodInv);
                    List<ASTNode> patchMethodChildren = Utils.getChildren(patchMethodInv);
                    if(buggyMethodChildren.size() == patchMethodChildren.size()){
                        int diffMethodChildNum = 0;
                        for(int i = 0; i < patchMethodChildren.size(); i++){
                            if(! buggyMethodChildren.get(i).toString().equals(patchMethodChildren.get(i).toString())){
                                diffMethodChildNum ++;
                            }
                        }
                        if(diffMethodChildNum == 1) {
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
            } else if(buggyChildren.get(diffIndex) instanceof ClassInstanceCreation && patchChildren.get(diffIndex) instanceof ClassInstanceCreation){
                focusType = "ClassInstanceCreation";
                ClassInstanceCreation buggyClassInsCreation = (ClassInstanceCreation) buggyChildren.get(diffIndex);
                ClassInstanceCreation patchClassInsCreation = (ClassInstanceCreation) patchChildren.get(diffIndex);
                if(! buggyClassInsCreation.getType().toString().equals(patchClassInsCreation.getType().toString())){
                    finalFixedNode = patchClassInsCreation.getType();
                    List<ASTNode> buggyMethodChildren = Utils.getChildren(buggyClassInsCreation);
                    List<ASTNode> patchMethodChildren = Utils.getChildren(patchClassInsCreation);
                    if(buggyMethodChildren.size() == patchMethodChildren.size()){
                        int diffMethodChildNum = 0;
                        for(int i = 0; i < patchMethodChildren.size(); i++){
                            if(! buggyMethodChildren.get(i).toString().equals(patchMethodChildren.get(i).toString())){
                                diffMethodChildNum ++;
                            }
                        }
                        if(diffMethodChildNum == 1) {
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

    public List<String> getNegativeSample(CompilationUnit cUnit, boolean inMethod) {
        List<ASTNode> nodes;
        List<String> negSamples = new ArrayList<>();
        Finder visitor;
        switch (focusType) {
            case "MethodInvocation":
                visitor = new MethodInvFinder();
                break;
            case "ClassInstanceCreation":
                visitor = new ClassInsCreationFinder();
                break;
            default:
                visitor = null;
        }
        if (visitor == null) {
            return negSamples;
        }
        visitor.setBuggyMethod(buggyMethodAst.toString());
        visitor.setInMethod(inMethod);
        cUnit.accept(visitor);
        nodes = visitor.getNodes();
        collect(cUnit, nodes, negSamples);
        return negSamples;
    }
}
