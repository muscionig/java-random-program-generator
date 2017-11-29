package system.model.nodes.classes;

//variableDeclaratorList
//        :	variableDeclarator (',' variableDeclarator)*
//        ;

import system.model.nodes.Node;

public class VariableDeclaratorList implements Node {

    private VariableDeclarator variableDeclarator;

    VariableDeclaratorList() {
        this.variableDeclarator = new VariableDeclarator();
    }

    @Override
    public String produce() {
        return this.verify(this.variableDeclarator.produce());
    }
}