/**
 * 
 */
package org.opensixen.p2;

import java.net.URI;

import org.eclipse.equinox.p2.metadata.IInstallableUnit;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class IUnitModel {
	
	private URI location;
	
	private IInstallableUnit installableUnit;
	
	private boolean selected = false;

	public IUnitModel(URI location, IInstallableUnit installableUnit) {
		super();
		this.location = location;
		this.installableUnit = installableUnit;
	}

	public IUnitModel() {
		super();
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

	/**
	 * @return the installableUnit
	 */
	public IInstallableUnit getInstallableUnit() {
		return installableUnit;
	}

	/**
	 * @param installableUnit the installableUnit to set
	 */
	public void setInstallableUnit(IInstallableUnit installableUnit) {
		this.installableUnit = installableUnit;
	}
	
	
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getName()	{		
		return installableUnit.getProperty(IInstallableUnit.PROP_NAME);
	}
	
	
	public String getDescription()	{		
		return installableUnit.getProperty(IInstallableUnit.PROP_DESCRIPTION);
	}
	
	

}
