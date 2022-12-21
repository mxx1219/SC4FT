package visitor.common;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class StmtVisitor extends ASTVisitor {

    private String buggyString = "";
    private final List<Statement> statements = new ArrayList<>();

    public boolean parse(ASTNode node){
        if(node.toString().length() >= buggyString.length()){
            if(node.toString().equals(buggyString)){
                if(node instanceof Statement){
                    statements.add((Statement) node);
                }
            }
            return true;
        }
        return false;
    }

    public void setBuggyString(String string){
        buggyString = string;
    }
    public List<Statement> getStatements() {
        return statements;
    }

    public boolean visit(AssertStatement node){
        return parse(node);
    }

    public boolean visit(Block node){
        return parse(node);
    }

    public boolean visit(BreakStatement node){
        return parse(node);
    }

    public boolean visit(ConstructorInvocation node){
        return parse(node);
    }

    public boolean visit(ContinueStatement node){
        return parse(node);
    }

    public boolean visit(DoStatement node){
        return parse(node);
    }

    public boolean visit(EmptyStatement node){
        return parse(node);
    }

    public boolean visit(EnhancedForStatement node){
        return parse(node);
    }

    public boolean visit(ExpressionStatement node){
        return parse(node);
    }

    public boolean visit(ForStatement node){
        return parse(node);
    }

    public boolean visit(IfStatement node){
        return parse(node);
    }

    public boolean visit(LabeledStatement node){
        return parse(node);
    }

    public boolean visit(ReturnStatement node){
        return parse(node);
    }

    public boolean visit(SuperConstructorInvocation node){
        return parse(node);
    }

    public boolean visit(SwitchCase node){
        return parse(node);
    }

    public boolean visit(SwitchStatement node){
        return parse(node);
    }

    public boolean visit(SynchronizedStatement node){
        return parse(node);
    }

    public boolean visit(ThrowStatement node){
        return parse(node);
    }

    public boolean visit(TryStatement node){
        return parse(node);
    }

    public boolean visit(TypeDeclarationStatement node){
        return parse(node);
    }

    public boolean visit(VariableDeclarationStatement node){
        return parse(node);
    }

    public boolean visit(WhileStatement node){
        return parse(node);
    }
}
