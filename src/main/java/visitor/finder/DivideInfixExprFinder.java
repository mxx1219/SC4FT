package visitor.finder;

import org.eclipse.jdt.core.dom.InfixExpression;

public class DivideInfixExprFinder extends Finder{

    public boolean visit(InfixExpression node){
        if(node.getOperator().equals(InfixExpression.Operator.DIVIDE)){
            nodes.add(node);
        }
        return true;
    }
}
