package visitor.finder;

import org.eclipse.jdt.core.dom.CastExpression;

public class CastExprFinder extends Finder {

    public boolean visit(CastExpression node){
        nodes.add(node);
        return true;
    }
}
