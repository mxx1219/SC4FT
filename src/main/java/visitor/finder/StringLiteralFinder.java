package visitor.finder;

import org.eclipse.jdt.core.dom.StringLiteral;

public class StringLiteralFinder extends Finder{

    public boolean visit(StringLiteral node){
        nodes.add(node);
        return true;
    }

}
