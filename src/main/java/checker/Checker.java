package checker;

import org.eclipse.jdt.core.dom.*;
import utils.Utils;
import visitor.common.AnnotationRemover;
import visitor.common.StmtVisitor;
import visitor.common.StringLiteralSolver;
import visitor.finder.StmtFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Checker {

    ASTNode finalFixedNode = null;
    ASTNode buggyMethodAst = null;
    ASTNode patchMethodAst = null;
    ASTNode buggyParentNode = null;
    ASTNode patchParentNode = null;

    int nodeDepth = 0;
    int buggyStmtLineNumber = -1;

    public boolean check(File buggyFile, File patchFile, int astParserType) {
        ASTNode buggyUnit = Utils.genAST(buggyFile, astParserType);
        ASTNode patchUnit = Utils.genAST(patchFile, astParserType);

        // Filter the situation of method deletion
        if(patchUnit.toString().equals("")){
            return false;
        }

        List<ASTNode> buggyChildren = Utils.getChildren(buggyUnit);
        List<ASTNode> patchChildren = Utils.getChildren(patchUnit);

        if (buggyChildren.size() != 2 || patchChildren.size() != 2) {
            System.out.println("Error! The form of cUnit is not correct!");
            return false;
        } else if (!(buggyChildren.get(1) instanceof MethodDeclaration) || !(patchChildren.get(1) instanceof MethodDeclaration)) {
            System.out.println("Error! Method parsing failed!");
            return false;
        } else {
            buggyUnit = buggyChildren.get(1);
            patchUnit = patchChildren.get(1);
            StringLiteralSolver solver = new StringLiteralSolver();
            buggyUnit.accept(solver);
            patchUnit.accept(solver);
            AnnotationRemover remover = new AnnotationRemover();
            buggyUnit.accept(remover);
            patchUnit.accept(remover);

            String buggyString = buggyUnit.toString();
            String patchString = patchUnit.toString();
            buggyUnit = Utils.genAST(buggyString, astParserType);
            patchUnit = Utils.genAST(patchString, astParserType);

            buggyChildren = Utils.getChildren(buggyUnit);
            patchChildren = Utils.getChildren(patchUnit);
            buggyUnit = buggyChildren.get(1);
            patchUnit = patchChildren.get(1);

            this.buggyMethodAst = buggyUnit;
            this.patchMethodAst = patchUnit;
            Type returnType = ((MethodDeclaration) buggyUnit).getReturnType2();
            return recursiveCheck(buggyUnit, patchUnit, returnType);
        }
    }

    public abstract boolean recursiveCheck(ASTNode buggyNode, ASTNode patchNode, Type returnType);

    public ASTNode getFinalFixedNode() {
        return this.finalFixedNode;
    }

    public int getNodeDepth() {
        return this.nodeDepth;
    }

    public void calculateDepth(){
        ASTNode parent = this.finalFixedNode;
        while(!(parent instanceof MethodDeclaration)){
            this.nodeDepth ++;
            parent = parent.getParent();
        }
    }


    public abstract String getPositiveSample(File buggyFile, File patchFile, int astParserType);

    public String getPositiveSample1(File buggyFile, File patchFile, int astParserType){
        check(buggyFile, patchFile, astParserType);
        // buggy position not in one statement
        if(buggyParentNode == null) {
            return "";
        } else{
            int start = buggyParentNode.getStartPosition();
            buggyStmtLineNumber = Utils.parseLineNumber(buggyMethodAst.toString(), start);
            String[] stringSlices = buggyMethodAst.toString().split("\n");
            stringSlices[buggyStmtLineNumber - 1] = " rank2fixstart " + stringSlices[buggyStmtLineNumber - 1] + " rank2fixend ";
            return String.join("\n", stringSlices);
        }
    }

    public String getPositiveSample2(File buggyFile, File patchFile, int astParserType){
        check(buggyFile, patchFile, astParserType);
        // buggy position not in one statement
        if (this.buggyParentNode == null) {
            return "";
        } else {
            String buggyString = this.buggyParentNode.toString();
            StmtVisitor visitor1 = new StmtVisitor();
            visitor1.setBuggyString(buggyString);
            this.buggyMethodAst.accept(visitor1);
            List<Statement> buggyStmts = visitor1.getStatements();

            StmtVisitor visitor2 = new StmtVisitor();
            visitor2.setBuggyString(buggyString);
            this.patchMethodAst.accept(visitor2);
            List<Statement> patchStmts = visitor2.getStatements();

            int stmtPosInList = -1;

            for(int i = 0; i < patchStmts.size(); i++){
                if(patchStmts.get(i).getStartPosition() == this.buggyParentNode.getStartPosition()){
                    stmtPosInList = i;
                    break;
                }
            }
            int realStart = buggyStmts.get(stmtPosInList).getStartPosition();
            this.buggyStmtLineNumber = Utils.parseLineNumber(this.buggyMethodAst.toString(), realStart);
            String[] stringSlices = this.buggyMethodAst.toString().split("\n");
            stringSlices[this.buggyStmtLineNumber - 1] = " rank2fixstart " + stringSlices[this.buggyStmtLineNumber - 1] + " rank2fixend ";
            return String.join("\n", stringSlices);
        }
    }

    public boolean simpleCheck(List<ASTNode> buggyChildren, List<ASTNode> patchChildren){
        int numStmtInBuggyChildren = 0;
        int numStmtInPatchChildren = 0;

        // None-statement level parse means illegal
        for(ASTNode buggyChild: buggyChildren){
            if(buggyChild instanceof Statement){
                numStmtInBuggyChildren ++;
            }
        }

        for(ASTNode patchChild: patchChildren){
            if(patchChild instanceof Statement){
                numStmtInPatchChildren ++;
            }
        }

        return numStmtInBuggyChildren != 0 || numStmtInPatchChildren != 0;
    }

    public void collect(CompilationUnit cUnit, List<ASTNode> nodes, List<String> negSamples){
        for(ASTNode node: nodes){
            ASTNode parentStmt = node;
            while(parentStmt != null && !(parentStmt instanceof Statement)){
                parentStmt = parentStmt.getParent();
            }
            ASTNode parentMethod = node;
            while(parentMethod != null && !(parentMethod instanceof MethodDeclaration)){
                parentMethod = parentMethod.getParent();
            }
            if(parentStmt != null && parentMethod != null){
                int stmtStartLine = cUnit.getLineNumber(parentStmt.getStartPosition());
                int methodStartLine = cUnit.getLineNumber(parentMethod.getStartPosition());
                String[] stringSlices = parentMethod.toString().split("\n");
                if(stmtStartLine - methodStartLine < 0){
                    continue;
                }
                stringSlices[stmtStartLine - methodStartLine] = " rank2fixstart " + stringSlices[stmtStartLine - methodStartLine] + " rank2fixend ";
                String methodWithStmtPos = String.join("\n", stringSlices);
                if(! negSamples.contains(methodWithStmtPos)){
                    negSamples.add(methodWithStmtPos);
                }
            }
        }
    }

    public List<String> getNegativeSample(CompilationUnit cUnit, boolean inMethod){
        List<ASTNode> nodes;
        List<String> negSamples = new ArrayList<>();
        String focusType = buggyParentNode.getClass().toString();
        focusType = focusType.substring(focusType.lastIndexOf(".") + 1);
        StmtFinder visitor = new StmtFinder();
        visitor.setBuggyMethod(buggyMethodAst.toString());
        visitor.setInMethod(inMethod);
        visitor.setFocusType(focusType);
        cUnit.accept(visitor);
        nodes = visitor.getNodes();
        collect(cUnit, nodes, negSamples);
        return negSamples;
    }
}
