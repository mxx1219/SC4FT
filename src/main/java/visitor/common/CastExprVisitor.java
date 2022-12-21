package visitor.common;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CastExpression;

import java.util.ArrayList;
import java.util.List;

public class CastExprVisitor extends ASTVisitor {

    private final List<CastExpression> castExpressions = new ArrayList<>();

    public boolean visit(CastExpression node){
        castExpressions.add(node);
        return true;
    }

    public List<CastExpression> getCastExpressions() {
        return castExpressions;
    }
}
