package visitor.common;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;

import java.util.ArrayList;
import java.util.List;

public class ArrayAccessVisitor extends ASTVisitor {
    private final List<ArrayAccess> arrayAccesses = new ArrayList<ArrayAccess>();

    public boolean visit(ArrayAccess node){
        arrayAccesses.add(node);
        return true;
    }

    public List<ArrayAccess> getArrayAccesses() {
        return arrayAccesses;
    }
}