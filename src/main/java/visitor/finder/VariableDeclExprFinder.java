package visitor.finder;

import org.eclipse.jdt.core.dom.VariableDeclarationExpression;

public class VariableDeclExprFinder extends Finder{

    public boolean visit(VariableDeclarationExpression node){
        nodes.add(node);
        return true;
    }
}
