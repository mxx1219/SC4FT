package checker;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import visitor.finder.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class MutateMethodInvExpr extends Checker{

    protected String focusType = "";

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
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
            case "ConstructorInvocation":
                visitor = new ConstructorInvFinder();
                break;
            case "SuperConstructorInvocation":
                visitor = new SuperConstructorInvFinder();
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
