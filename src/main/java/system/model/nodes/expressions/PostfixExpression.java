package system.model.nodes.expressions;

import system.model.STKey;
import system.model.ScopeTable;
import system.model.nodes.Node;
import utils.RandomGen;

//postfixExpression
//        :	(	primary
//        |	expressionName
//        )
//        (	postIncrementExpression_lf_postfixExpression
//        |	postDecrementExpression_lf_postfixExpression
//        )*
//        ;

//TODO postIncrementExpression_lf_postfixExpression
//TODO postDecrementExpression_lf_postfixExpression
//TODO WARNING!! * after post increment decrement postfix expression
public class PostfixExpression implements Node{

    private IPostfixExpression postfixExpression;

    PostfixExpression(STKey key, ScopeTable scopeTable) {
        if (RandomGen.getNextInt(10) == 0) {
            this.postfixExpression = new Primary(key, scopeTable);
        } else {
            this.postfixExpression = new ExpressionName(key, scopeTable);
            if (this.postfixExpression.produce() == null) {
                this.postfixExpression = new Primary(key, scopeTable);
            }
        }


    }

    @Override
    public String produce() {
        return this.verify(postfixExpression.produce());
    }

}
