package visitor.finder;

import org.eclipse.jdt.core.dom.ForStatement;

public class ForStmtFinder extends Finder{

    public boolean visit(ForStatement node){
        nodes.add(node);
        return true;
    }
}
