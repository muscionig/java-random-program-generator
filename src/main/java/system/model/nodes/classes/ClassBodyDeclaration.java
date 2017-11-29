package system.model.nodes.classes;

import system.model.nodes.Node;

//classBodyDeclaration
//        :	classMemberDeclaration
//        |	instanceInitializer
//        |	staticInitializer
//        |	constructorDeclaration
//        ;

public class ClassBodyDeclaration implements Node {

    private ClassMemberDeclaration classMemberDeclaration;

    ClassBodyDeclaration() {
        this.classMemberDeclaration = new ClassMemberDeclaration();
    }

    @Override
    public String produce() {
        return this.verify(this.classMemberDeclaration.produce());
    }
}
