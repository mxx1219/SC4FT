package visitor.finder;

import org.eclipse.jdt.core.dom.BooleanLiteral;

public class BooleanLiteralFinder extends Finder{

    public boolean visit(BooleanLiteral node){
        nodes.add(node);
        return true;
    }
}
