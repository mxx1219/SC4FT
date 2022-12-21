package visitor.common;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.StringLiteral;

public class StringLiteralSolver extends ASTVisitor {

    public boolean visit(StringLiteral node){
        node.setLiteralValue("STRING");
        return false;
    }
}
