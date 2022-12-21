package visitor.finder;

import org.eclipse.jdt.core.dom.ArrayAccess;

public class ArrayAccessFinder extends Finder {

    public boolean visit(ArrayAccess node){
        nodes.add(node);
        return true;
    }
}
