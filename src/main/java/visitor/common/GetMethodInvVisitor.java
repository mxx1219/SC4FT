package visitor.common;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.ArrayList;
import java.util.List;

public class GetMethodInvVisitor extends ASTVisitor {
    private final List<MethodInvocation> methodInvocations = new ArrayList<MethodInvocation>();

    public boolean visit(MethodInvocation node){
        if(node.getName().toString().equals("get")){
            methodInvocations.add(node);
        }
        return true;
    }

    public List<MethodInvocation> getMethodInvocations() {
        return methodInvocations;
    }

}
