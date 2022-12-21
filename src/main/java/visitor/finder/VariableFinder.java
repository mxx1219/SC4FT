package visitor.finder;

import org.eclipse.jdt.core.dom.SimpleName;
import utils.Utils;

public class VariableFinder extends Finder{
    public boolean visit(SimpleName node){
        if(Utils.isVariable(node)) {
            nodes.add(node);
        }
        return true;
    }
}
