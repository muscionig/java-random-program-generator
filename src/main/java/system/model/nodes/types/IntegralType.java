package system.model.nodes.types;

//integralType
//        :	'byte'
//        |	'short'
//        |	'int'
//        |	'long'
//        |	'char'
//        ;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IntegralType implements INumericType {

    private String type;

    IntegralType() {

        List<String> types = new ArrayList<String>() {{
            add("byte");
            add("short");
            add("int");
            add("long");
            add("char");
        }};

        this.type = types.get(new Random().nextInt(types.size()));
    }

    @Override
    public String produce() {
        return this.verify(this.type);
    }

    @Override
    public String getType() {
        return this.type;
    }
}
