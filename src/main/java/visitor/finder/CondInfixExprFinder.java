package visitor.finder;

import org.eclipse.jdt.core.dom.InfixExpression;

public class CondInfixExprFinder extends Finder{

    public boolean visit(InfixExpression node){
        if(node.getOperator().equals(InfixExpression.Operator.CONDITIONAL_OR)
                || node.getOperator().equals(InfixExpression.Operator.CONDITIONAL_AND)){
            nodes.add(node);
        }
        return true;
    }
}
