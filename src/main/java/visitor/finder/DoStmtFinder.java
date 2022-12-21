package visitor.finder;

import org.eclipse.jdt.core.dom.IfStatement;

public class DoStmtFinder extends Finder{

    public boolean visit(IfStatement node){
        nodes.add(node);
        return true;
    }
}
