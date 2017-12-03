package system.model.nodes.classes;

//methodDeclaration
//        :	methodModifier* methodHeader methodBody
//        ;

import system.model.ScopeTable;

public class MethodDeclaration implements IClassMemberDeclaration {

    private ScopeTable scopeTable;

    private MethodModifier methodModifier;
    private MethodHeader methodHeader;
    private MethodBody methodBody;

    MethodDeclaration(ScopeTable outerScopeTable) {
        this.scopeTable = new ScopeTable(outerScopeTable);

        this.methodModifier = new MethodModifier();
        this.methodHeader = new MethodHeader();
        this.methodBody = new MethodBody(this.methodHeader.getResult().getType(), this.scopeTable);

        outerScopeTable.addMethod(this.methodHeader.getResult().getType(), this.methodHeader.getMethodDeclarator().getIdentifier().toString());
    }

    public MethodModifier getMethodModifier() {
        return methodModifier;
    }

    public MethodHeader getMethodHeader() {
        return methodHeader;
    }

    @Override
    public String produce() {
        String b = methodModifier.produce() +
                " " +
                methodHeader.produce() +
                " " +
                methodBody.produce();
        return this.verify(b);
    }
}
