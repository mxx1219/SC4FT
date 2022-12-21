package visitor.common;

import org.eclipse.jdt.core.dom.*;

public class AnnotationRemover extends ASTVisitor {

    public boolean visit(Javadoc node){
        node.delete();
        return false;
    }

    public boolean visit(SingleMemberAnnotation node){
        node.delete();
        return false;
    }

    public boolean visit(MarkerAnnotation node){
        node.delete();
        return false;
    }

    public boolean visit(NormalAnnotation node){
        node.delete();
        return false;
    }
}
