package visitor.finder;

import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInvFinder extends Finder{

    public boolean visit(MethodInvocation node){
        nodes.add(node);
        return true;
    }
}
