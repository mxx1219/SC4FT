package visitor.finder;

import org.eclipse.jdt.core.dom.InfixExpression;

public class InfixExprFinder extends Finder{

    public boolean visit(InfixExpression node){
        nodes.add(node);
        return true;
    }
}
