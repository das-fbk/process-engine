package eu.fbk.das.process.engine.api.composition.element;

import java.util.List;
import java.util.Map;

/**
 * class for ACTION-clause in control-flow requirements
 * 
 * @author Heorhi
 *
 */
public class CFClauseAction extends CFClause {
    final private String sid;
    final private String action;
    final private Map<String, List<String>> condition;

    public CFClauseAction(String sid, String action,
	    Map<String, List<String>> condition) {
	super(CFClauseType.ACTION);
	this.sid = sid;
	this.action = action;
	this.condition = condition;
    }

    public String getSid() {
	return sid;
    }

    public String getAction() {
	return action;
    }

    public Map<String, List<String>> getCondition() {
	return condition;
    }

    @Override
    public String toString() {
	String str = "ACTION<<(" + sid + "." + action + ")";
	if (condition != null)
	    str += "--->C(" + condition + ")";
	str += ">>";
	return str;
    }

}
