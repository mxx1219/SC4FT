package checker;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import visitor.finder.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class MutateLiteralExpr extends Checker{

    protected String focusType = "";

    public String getPositiveSample(File buggyFile, File patchFile, int astParserType){
        return getPositiveSample1(buggyFile, patchFile, astParserType);
    }

    public List<String> getNegativeSample(CompilationUnit cUnit, boolean inMethod) {
        List<ASTNode> nodes;
        List<String> negSamples = new ArrayList<>();
        Finder visitor;
        switch (focusType) {
            case "BooleanLiteral":
                visitor = new BooleanLiteralFinder();
                break;
            case "CharacterLiteral":
                visitor = new CharacterLiteralFinder();
                break;
            case "NumberLiteral":
                visitor = new NumberLiteralFinder();
                break;
            case "StringLiteral":
                visitor = new StringLiteralFinder();
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

    public void calculateDepth(){
        ASTNode parent = this.finalFixedNode;
        while(!(parent instanceof MethodDeclaration)){
            this.nodeDepth ++;
            parent = parent.getParent();
        }
        this.nodeDepth += 1;
    }
}
