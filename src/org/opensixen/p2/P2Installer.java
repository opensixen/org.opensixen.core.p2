package org.opensixen.p2;

import java.net.URI;
import java.util.ArrayList;

import org.compiere.util.CLogger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;

import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.IRepository;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

import org.opensixen.core.p2.Activator;
import org.opensixen.osgi.interfaces.ICommand;

@SuppressWarnings("restriction")
public class P2Installer implements ICommand {

	private CLogger log = CLogger.getCLogger(getClass());
	private IRepositoryManager<IMetadataRepository> metadataManager;
	public P2Installer() {
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("restriction")
	@Override
	public String doIt() throws Exception {
		/*
		 * Installer installer = new Installer(); URL url = new
		 * URL("file:///tmp/monteloeder.zip"); String iuname = "monteloder";
		 * String profile = "monteloeder"; String targetDir = "/tmp/client";
		 * 
		 * installer.install(iuname, url, profile, targetDir);
		 */

		final IProvisioningAgent agent = (IProvisioningAgent) Activator
				.getService(IProvisioningAgent.SERVICE_NAME);
		metadataManager =  (IRepositoryManager<IMetadataRepository>) agent.getService(IMetadataRepositoryManager.SERVICE_NAME);
		// Añado los repositorios
		addRepositories(agent, new URI("file:///tmp/monteloeder/"), "Monteloeder Site");
						
		P2ProgressMonitor monitor = new P2ProgressMonitor();
		ProvisioningSession session = new ProvisioningSession(agent);
/*
		SimpleMetadataRepositoryFactory factory = new SimpleMetadataRepositoryFactory();
		factory.setAgent(agent);
		IMetadataRepository repository = factory.load(new URI("file:///tmp/monteloeder/"), 0, new P2ProgressMonitor());
		
	
		SimpleArtifactRepositoryFactory artifactFactory = new SimpleArtifactRepositoryFactory();
		IArtifactRepository artifactRepository = artifactFactory.load(new URI("file:///tmp/monteloeder/"), 0, new P2ProgressMonitor());
		
			
	
		  IQueryResult<IInstallableUnit> result =
		  repository.query(QueryUtil.createIUAnyQuery(), new
		  P2ProgressMonitor()); IInstallableUnit[] units =
		  result.toArray(IInstallableUnit.class); for (IInstallableUnit unit :
		  units) { log.info( unit.getId()); }
		  
		  InstallableUnitDescription description = new
		  InstallableUnitDescription(); //description.set
		  
		  
		  
		  IInstallableUnit instalableUnit =
		  MetadataFactory.createInstallableUnit(description);
		
		
		IQueryResult<IInstallableUnit> result = repository.query(QueryUtil
				.createIUQuery("feature.monteloeder.custom.feature.group"),
				new P2ProgressMonitor());
		
		
		
		ArrayList<IInstallableUnit> iu = new ArrayList<IInstallableUnit>();
		iu.addAll(result.toUnmodifiableSet());

		InstallOperation op = new InstallOperation(session, iu);

		IStatus resultado = op.resolveModal(monitor);
		
		
		if (resultado.isOK()) {
			log.info("Resultado OK");
			op.getProvisioningJob(monitor).schedule();
		
		} else {
			log.info("Resultado distinto de OK");
			log.info("Mensaje: " + op.getResolutionDetails());
		}
	*/
		return "";

	}
	
	
	private void addRepositories(IProvisioningAgent agent, URI location, String name)	{
	/*
		IProfileRegistry registry = (IProfileRegistry) agent.getService(IProfileRegistry.SERVICE_NAME);
		IAgentLocation agentLocation = (IAgentLocation) agent.getService(IAgentLocation.SERVICE_NAME);
		final RepositoryEvent event = createEvent(parameters);
		IProfile profile = (IProfile) parameters.get(ActionConstants.PARM_PROFILE);
		if (profile != null)
			addRepositoryToProfile(agentLocation, profile, event.getRepositoryLocation(), event.getRepositoryNickname(), event.getRepositoryType(), event.isRepositoryEnabled());
		//if provisioning into the current profile, broadcast an event to add this repository directly to the current repository managers.
		if (isSelfProfile(registry, profile))
			addToSelf(agent, agentLocation, event);
		return Status.OK_STATUS;
	*/
				
		if (!metadataManager.contains(location))	{
			metadataManager.addRepository(location);
			if (name != null)	{
				metadataManager.setRepositoryProperty(location, IRepository.PROP_NICKNAME, name);
			}
		}
		
		IRepositoryManager<IArtifactRepository> artifactManager = (IRepositoryManager<IArtifactRepository>) agent.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (!artifactManager.contains(location))	{
			artifactManager.addRepository(location);
			if (name != null)	{
				artifactManager.setRepositoryProperty(location, IRepository.PROP_NICKNAME, name);
			}
		}
	}
		
}
