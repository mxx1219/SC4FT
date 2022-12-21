package visitor.finder;

import org.eclipse.jdt.core.dom.Assignment;

public class AssignmentFinder extends Finder{
    public boolean visit(Assignment node){
        nodes.add(node);
        return true;
    }
}
