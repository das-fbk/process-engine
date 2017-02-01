package eu.fbk.das.process.engine.api.domain;

public class Variable {
    

	private String name;

	private String type;
	
	private String value;
	
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	
	
	public void setType(String type) {
		this.type = type;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void fillInAttributes(Variable clone) {
		clone.name = this.name;
		clone.type = this.type;
		clone.value = this.value;
		
	}
	
     @Override
	public Variable clone() {
		
		Variable clone = new Variable();
		fillInAttributes(clone);
		return clone;
	}
}
