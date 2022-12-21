package visitor.finder;

import org.eclipse.jdt.core.dom.IfStatement;

public class IfStmtFinder extends Finder{

    public boolean visit(IfStatement node){
        nodes.add(node);
        return true;
    }
}
