package visitor.finder;

import org.eclipse.jdt.core.dom.ClassInstanceCreation;

public class ClassInsCreationFinder extends Finder {

    public boolean visit(ClassInstanceCreation node){
        nodes.add(node);
        return true;
    }
}
