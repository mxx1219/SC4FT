package visitor.finder;

import org.eclipse.jdt.core.dom.*;

public class StmtFinder extends Finder{

    private String focusType = "";


    public boolean visit(AssertStatement node){
        if(focusType.equals("AssertStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(BreakStatement node){
        if(focusType.equals("BreakStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(ConstructorInvocation node){
        if(focusType.equals("ConstructorInvocation")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(ContinueStatement node){
        if(focusType.equals("ContinueStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(DoStatement node){
        if(focusType.equals("DoStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(EmptyStatement node){
        if(focusType.equals("EmptyStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(EnhancedForStatement node){
        if(focusType.equals("EnhancedForStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(ExpressionStatement node){
        if(focusType.equals("ExpressionStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(ForStatement node){
        if(focusType.equals("ForStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(IfStatement node){
        if(focusType.equals("IfStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(LabeledStatement node){
        if(focusType.equals("LabeledStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(ReturnStatement node){
        if(focusType.equals("ReturnStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(SuperConstructorInvocation node){
        if(focusType.equals("SuperConstructorInvocation")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(SwitchCase node){
        if(focusType.equals("SwitchCase")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(SwitchStatement node){
        if(focusType.equals("SwitchStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(SynchronizedStatement node){
        if(focusType.equals("SynchronizedStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(ThrowStatement node){
        if(focusType.equals("ThrowStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(TryStatement node){
        if(focusType.equals("TryStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(TypeDeclarationStatement node){
        if(focusType.equals("TypeDeclarationStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(VariableDeclarationStatement node){
        if(focusType.equals("VariableDeclarationStatement")){
            nodes.add(node);
        }
        return true;
    }

    public boolean visit(WhileStatement node){
        if(focusType.equals("WhileStatement")){
            nodes.add(node);
        }
        return true;
    }

    public void setFocusType(String focusType) {
        this.focusType = focusType;
    }

}
