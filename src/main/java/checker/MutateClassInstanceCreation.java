package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.finder.ClassInsCreationFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MutateClassInstanceCreation extends Checker{

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
            if(buggyChildren.get(diffIndex) instanceof ClassInstanceCreation && patchChildren.get(diffIndex) instanceof CastExpression){
                ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) buggyChildren.get(diffIndex);
                CastExpression castExpression = (CastExpression) patchChildren.get(diffIndex);
                finalFixedNode = castExpression;
                if(classInstanceCreation.getType().toString().equals(castExpression.getType().toString())){
                    if(castExpression.getExpression() instanceof SuperMethodInvocation){
                        SuperMethodInvocation methodInvocation = (SuperMethodInvocation) castExpression.getExpression();
                        if(methodInvocation.getName().toString().equals("clone")){
                            buggyParentNode = classInstanceCreation;
                            while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                                buggyParentNode = buggyParentNode.getParent();
                            }
                            patchParentNode =castExpression;
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

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }

    public List<String> getNegativeSample(CompilationUnit cUnit, boolean inMethod){
        List<ASTNode> nodes;
        List<String> negSamples = new ArrayList<>();
        ClassInsCreationFinder visitor = new ClassInsCreationFinder();
        visitor.setBuggyMethod(buggyMethodAst.toString());
        visitor.setInMethod(inMethod);
        cUnit.accept(visitor);
        nodes = visitor.getNodes();
        collect(cUnit, nodes, negSamples);
        return negSamples;
    }

}
