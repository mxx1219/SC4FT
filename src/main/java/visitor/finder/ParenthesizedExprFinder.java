package visitor.finder;

import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;

public class ParenthesizedExprFinder extends Finder{

    public boolean visit(InfixExpression node){
        if(node.getLeftOperand() instanceof ParenthesizedExpression || node.getRightOperand() instanceof ParenthesizedExpression){
            nodes.add(node);
        }
        return true;
    }
}
