/**
 * 
 */
package org.opensixen.p2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.compiere.util.CLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.p2.engine.ProfileMetadataRepositoryFactory;
import org.eclipse.equinox.internal.p2.metadata.repository.SimpleMetadataRepositoryFactory;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.IRepository;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.opensixen.core.p2.Activator;

/**
 * 
 * 
 * @author Eloy Gomez Indeos Consultoria http://www.indeos.es
 * 
 */
public class P2 {

	private static P2 instance;
	private CLogger log = CLogger.getCLogger(getClass());

	private IRepositoryManager<IMetadataRepository> metadataManager;
	private final IProvisioningAgent agent;

	/**
	 * Get the current instance of P2
	 * 
	 * @return
	 */
	public static P2 get() {
		if (instance == null) {
			instance = new P2();
		}

		return instance;
	}

	@SuppressWarnings("unchecked")
	public P2() {
		agent = (IProvisioningAgent) Activator
				.getService(IProvisioningAgent.SERVICE_NAME);
		metadataManager = (IRepositoryManager<IMetadataRepository>) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
	}

	/**
	 * Add a repository to the _SELF_ profile
	 * 
	 * @param location
	 * @param name
	 */
	public void addRepository(URI location, String name) {
		if (!metadataManager.contains(location)) {
			metadataManager.addRepository(location);
			if (name != null) {
				metadataManager.setRepositoryProperty(location,
						IRepository.PROP_NICKNAME, name);
			}
		}

		IRepositoryManager<IArtifactRepository> artifactManager = (IRepositoryManager<IArtifactRepository>) agent.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (!artifactManager.contains(location)) {
			artifactManager.addRepository(location);
			if (name != null) {
				artifactManager.setRepositoryProperty(location,
						IRepository.PROP_NICKNAME, name);
			}
		}
	}

	/**
	 * Add a repository to the _SELF_ profile
	 * 
	 * @param location
	 * @param name
	 */
	public void removeRepository(URI location) {
		if (!metadataManager.contains(location)) {
			metadataManager.removeRepository(location);
		}

		IRepositoryManager<IArtifactRepository> artifactManager = (IRepositoryManager<IArtifactRepository>) agent.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (!artifactManager.contains(location)) {
			artifactManager.removeRepository(location);
		}
	}

	/**
	 * Gel all enabled repositories
	 * 
	 * @return
	 */
	public URI[] getRepositories() {
		return metadataManager
				.getKnownRepositories(metadataManager.REPOSITORIES_ALL);
	}

	public RepositoryModel[] getAllRepositoryModel() {
		URI[] locations = getRepositories();
		RepositoryModel[] model = new RepositoryModel[locations.length];
		for (int i = 0; i < locations.length; i++) {
			String name = metadataManager.getRepositoryProperty(locations[i],
					IMetadataRepository.PROP_NAME);
			model[i] = new RepositoryModel(name, locations[i]);
		}
		return model;
	}

	/**
	 * Get all InstallableUnit of this location
	 * 
	 * @param location
	 * @return
	 * @throws URISyntaxException
	 * @throws ProvisionException
	 */
	@SuppressWarnings("restriction")
	public List<IUnitModel> getAllIUnit(URI location) throws RuntimeException {
		ArrayList<IUnitModel> iunits = new ArrayList<IUnitModel>();
		SimpleMetadataRepositoryFactory factory = new SimpleMetadataRepositoryFactory();
		factory.setAgent(agent);
		IMetadataRepository repository;
		try {
			repository = factory.load(location, 0, new P2ProgressMonitor());
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error cargando el repositorio", e);
			return iunits;
		}

		IQueryResult<IInstallableUnit> result = repository.query(
				QueryUtil.createIUGroupQuery(), null);
		IInstallableUnit[] units = result.toArray(IInstallableUnit.class);

		for (IInstallableUnit installableUnit : units) {
			iunits.add(new IUnitModel(location, installableUnit));
		}
		return iunits;
	}

	/**
	 * Install instalableUnits into the _SELF_ profile
	 * 
	 * @param installableUnits
	 * @param monitor
	 * @return
	 */
	public boolean install(ArrayList<IInstallableUnit> installableUnits,
			IProgressMonitor monitor) {
		ProvisioningSession session = new ProvisioningSession(agent);
		InstallOperation op = new InstallOperation(session, installableUnits);

		IStatus resultado = op.resolveModal(monitor);

		if (resultado.isOK()) {
			log.info("Resultado OK");
			op.getProvisioningJob(monitor).schedule();

		} else {
			log.info("Resultado distinto de OK");
			log.info("Mensaje: " + op.getResolutionDetails());
		}

		return true;
	}

	/**
	 * Install instalableUnits into the _SELF_ profile
	 * 
	 * @param iunits
	 * @return
	 */
	public boolean install(ArrayList<IUnitModel> iunits) {
		ArrayList<IInstallableUnit> installableUnits = new ArrayList<IInstallableUnit>();
		for (IUnitModel iunit : iunits) {
			installableUnits.add(iunit.getInstallableUnit());
		}

		return install(installableUnits, new P2ProgressMonitor());
	}

}
