package visitor.finder;

import org.eclipse.jdt.core.dom.SuperConstructorInvocation;

public class SuperConstructorInvFinder extends Finder{

    public boolean visit(SuperConstructorInvocation node){
        nodes.add(node);
        return true;
    }
}
