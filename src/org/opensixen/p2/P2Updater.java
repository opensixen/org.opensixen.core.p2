/**
 * 
 */
package org.opensixen.p2;

import org.compiere.util.CLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.opensixen.core.p2.Activator;




/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class P2Updater {
	
	private static CLogger s_log = CLogger.getCLogger(P2Updater.class);
	
	public static void startupUpdater()	{
		final IProvisioningAgent agent = (IProvisioningAgent) Activator.getService(IProvisioningAgent.SERVICE_NAME);		
		P2ProgressMonitor monitor = new P2ProgressMonitor();
		
		IStatus updateStatus = checkForUpdates(agent, monitor);
		if (updateStatus.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
			s_log.info("Nada que actualizar.");
			
		} else if (updateStatus.getSeverity() != IStatus.ERROR) {
			s_log.info("Actualizaciones instaladas, es necesario reiniciar.");
		} else {
			s_log.warning(updateStatus.getMessage());
		}
	}
	
	
	/**
	 * Check for updates
	 * @param agent
	 * @param monitor
	 * @return
	 * @throws OperationCanceledException
	 */
	private static IStatus checkForUpdates(IProvisioningAgent agent, IProgressMonitor monitor) throws OperationCanceledException {
		ProvisioningSession session = new ProvisioningSession(agent);

		// the default update operation looks for updates to the currently
		// running profile, using the default profile root marker. To change
		// which installable units are being updated, use the more detailed
		// constructors.
		UpdateOperation operation = new UpdateOperation(session);
		SubMonitor sub = SubMonitor.convert(monitor,
				"Checking for application updates...", 200);
		IStatus status = operation.resolveModal(sub.newChild(100));
		if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
			return status;
		}
		if (status.getSeverity() == IStatus.CANCEL)
			throw new OperationCanceledException();
		
		if (status.getSeverity() != IStatus.ERROR) {
			// More complex status handling might include showing the user what updates
			// are available if there are multiples, differentiating patches vs. updates, etc.
			// In this example, we simply update as suggested by the operation.
			ProvisioningJob job = operation.getProvisioningJob(null);
			if (job == null)	{
				s_log.info("No provisioning job: " + status.getCode());
				return status;
			}
			
			status = job.runModal(sub.newChild(100));
			if (status.getSeverity() == IStatus.CANCEL)
				throw new OperationCanceledException();
		}
		return status;
	}
	
}
