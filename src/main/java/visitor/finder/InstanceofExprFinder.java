package visitor.finder;

import org.eclipse.jdt.core.dom.InstanceofExpression;

public class InstanceofExprFinder extends Finder{

    public boolean visit(InstanceofExpression node){
        nodes.add(node);
        return true;
    }
}
