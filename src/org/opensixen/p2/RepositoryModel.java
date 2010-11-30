/**
 * 
 */
package org.opensixen.p2;

import java.net.URI;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class RepositoryModel {
	
	private String name;
	
	private URI location;
	
	
	public RepositoryModel(String name, URI location) {
		super();
		this.name = name;
		this.location = location;
	}

	public RepositoryModel() {
		super();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the location
	 */
	public URI getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(URI location) {
		this.location = location;
	}
	
	

}
