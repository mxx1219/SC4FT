package visitor.finder;

import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class VariableDeclStmtFinder extends Finder{

    public boolean visit(VariableDeclarationStatement node){
        nodes.add(node);
        return true;
    }
}
