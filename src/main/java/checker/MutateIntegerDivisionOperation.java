package checker;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import visitor.finder.CastExprFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class MutateIntegerDivisionOperation extends Checker{

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }

    public List<String> getNegativeSample(CompilationUnit cUnit, boolean inMethod){
        List<ASTNode> nodes = null;
        List<String> negSamples = new ArrayList<>();
        CastExprFinder visitor = new CastExprFinder();
        visitor.setBuggyMethod(buggyMethodAst.toString());
        visitor.setInMethod(inMethod);
        cUnit.accept(visitor);
        nodes = visitor.getNodes();
        collect(cUnit, nodes, negSamples);
        return negSamples;
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
