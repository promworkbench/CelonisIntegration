package org.processmining.celonisintegration.objects.studio;

public class Space {
	private String name;
	private String id;

	public Space(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
	
	public String toString() {
		return this.name;
	}
	
	public boolean equals(Object o) {
		if (o == this) {
            return true;
        }
		
		if (!(o instanceof Space)) {
            return false;
        }
		
		Space s = (Space) o;
		
		return (this.getName() == s.getName()) && (this.getId() == s.getId());
	}
}
