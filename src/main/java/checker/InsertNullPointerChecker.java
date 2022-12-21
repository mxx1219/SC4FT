package checker;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class InsertNullPointerChecker extends Checker{
    public boolean checkExprExistInBlock(List<Statement> nodes, Expression leftOperand){
        List<Expression> expressions = new ArrayList<Expression>();
        for(Statement node: nodes){
            expressions.addAll(getAllExpressions(node));
        }
        for(Expression expr: expressions){
            if(expr.toString().equals(leftOperand.toString())){
                buggyParentNode = expr;
                while(!(buggyParentNode instanceof Statement) && buggyParentNode != null){
                    buggyParentNode = buggyParentNode.getParent();
                }
                return true;
            }
        }
        return false;
    }

    public static List<Expression> getAllExpressions(ASTNode node){
        List<Expression> expressions = new ArrayList<>();
        if(node instanceof Expression) {
            expressions.add((Expression) node);
        }
        List<ASTNode> children = Utils.getChildren(node);
        for(ASTNode child: children){
            expressions.addAll(getAllExpressions(child));
        }
        return expressions;
    }
}
