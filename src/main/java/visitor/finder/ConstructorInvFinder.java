package visitor.finder;

import org.eclipse.jdt.core.dom.ConstructorInvocation;

public class ConstructorInvFinder extends Finder{

    public boolean visit(ConstructorInvocation node){
        nodes.add(node);
        return true;
    }
}
