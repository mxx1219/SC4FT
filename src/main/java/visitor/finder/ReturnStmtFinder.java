package visitor.finder;

import org.eclipse.jdt.core.dom.ReturnStatement;

public class ReturnStmtFinder extends Finder{
    public boolean visit(ReturnStatement node){
        nodes.add(node);
        return true;
    }
}
