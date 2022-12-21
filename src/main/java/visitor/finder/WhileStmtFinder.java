package visitor.finder;

import org.eclipse.jdt.core.dom.WhileStatement;

public class WhileStmtFinder extends Finder{

    public boolean visit(WhileStatement node){
        nodes.add(node);
        return true;
    }
}
