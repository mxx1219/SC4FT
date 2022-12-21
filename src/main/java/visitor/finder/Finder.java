package visitor.finder;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public abstract class Finder extends ASTVisitor {

    protected String buggyMethod = "";
    protected boolean inMethod = true;
    protected final List<ASTNode> nodes = new ArrayList<>();

    public boolean visit(MethodDeclaration node){
        if(inMethod){
            return node.toString().equals(buggyMethod);
        } else{
            return !node.toString().equals(buggyMethod);
        }
    }

    public void setBuggyMethod(String buggyMethod) {
        this.buggyMethod = buggyMethod;
    }

    public void setInMethod(boolean inMethod){
        this.inMethod = inMethod;
    }

    public List<ASTNode> getNodes() {
        return nodes;
    }
}
