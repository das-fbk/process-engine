package eu.fbk.das.process.engine.api.composition.element;

import java.util.List;

/**
 * class for AND-clause in control-flow requirements
 * 
 * @author Heorhi
 *
 */
public class CFClauseAnd extends CFClause {
    final private List<CFClause> clauses;

    public CFClauseAnd(List<CFClause> clauses) {
	super(CFClauseType.AND);
	this.clauses = clauses;
    }

    public List<CFClause> getClauses() {
	return clauses;
    }

    @Override
    public String toString() {
	String str = "AND<<\n";
	for (CFClause cl : clauses) {
	    str += "(" + cl + ")\n";
	}
	str += ">>";
	return str;
    }

}
