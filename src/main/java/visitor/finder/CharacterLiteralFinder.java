package visitor.finder;

import org.eclipse.jdt.core.dom.CharacterLiteral;

public class CharacterLiteralFinder extends Finder{

    public boolean visit(CharacterLiteral node){
        nodes.add(node);
        return true;
    }
}
