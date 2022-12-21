package visitor.finder;

import org.eclipse.jdt.core.dom.NumberLiteral;

public class NumberLiteralFinder extends Finder{

    public boolean visit(NumberLiteral node){
        nodes.add(node);
        return true;
    }
}
