package visitor.finder;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

public class SVDFinder extends Finder{
    public boolean visit(SingleVariableDeclaration node){
        nodes.add(node);
        return true;
    }
}
