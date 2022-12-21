package visitor.finder;

import org.eclipse.jdt.core.dom.MethodInvocation;

public class GetMethodInvFinder extends Finder {

    public boolean visit(MethodInvocation node){
        if(node.getName().toString().equals("get")){
            nodes.add(node);
        }
        return true;
    }
}
