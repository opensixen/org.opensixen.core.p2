 /******* BEGIN LICENSE BLOCK *****
 * Versión: GPL 2.0/CDDL 1.0/EPL 1.0
 *
 * Los contenidos de este fichero están sujetos a la Licencia
 * Pública General de GNU versión 2.0 (la "Licencia"); no podrá
 * usar este fichero, excepto bajo las condiciones que otorga dicha 
 * Licencia y siempre de acuerdo con el contenido de la presente. 
 * Una copia completa de las condiciones de de dicha licencia,
 * traducida en castellano, deberá estar incluida con el presente
 * programa.
 * 
 * Adicionalmente, puede obtener una copia de la licencia en
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Este fichero es parte del programa opensiXen.
 *
 * OpensiXen es software libre: se puede usar, redistribuir, o
 * modificar; pero siempre bajo los términos de la Licencia 
 * Pública General de GNU, tal y como es publicada por la Free 
 * Software Foundation en su versión 2.0, o a su elección, en 
 * cualquier versión posterior.
 *
 * Este programa se distribuye con la esperanza de que sea útil,
 * pero SIN GARANTÍA ALGUNA; ni siquiera la garantía implícita 
 * MERCANTIL o de APTITUD PARA UN PROPÓSITO DETERMINADO. Consulte 
 * los detalles de la Licencia Pública General GNU para obtener una
 * información más detallada. 
 *
 * TODO EL CÓDIGO PUBLICADO JUNTO CON ESTE FICHERO FORMA PARTE DEL 
 * PROYECTO OPENSIXEN, PUDIENDO O NO ESTAR GOBERNADO POR ESTE MISMO
 * TIPO DE LICENCIA O UNA VARIANTE DE LA MISMA.
 *
 * El desarrollador/es inicial/es del código es
 *  FUNDESLE (Fundación para el desarrollo del Software Libre Empresarial).
 *  Indeos Consultoria S.L. - http://www.indeos.es
 *
 * Contribuyente(s):
 *  Eloy Gómez García <eloy@opensixen.org> 
 *
 * Alternativamente, y a elección del usuario, los contenidos de este
 * fichero podrán ser usados bajo los términos de la Licencia Común del
 * Desarrollo y la Distribución (CDDL) versión 1.0 o posterior; o bajo
 * los términos de la Licencia Pública Eclipse (EPL) versión 1.0. Una 
 * copia completa de las condiciones de dichas licencias, traducida en 
 * castellano, deberán de estar incluidas con el presente programa.
 * Adicionalmente, es posible obtener una copia original de dichas 
 * licencias en su versión original en
 *  http://www.opensource.org/licenses/cddl1.php  y en  
 *  http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * Si el usuario desea el uso de SU versión modificada de este fichero 
 * sólo bajo los términos de una o más de las licencias, y no bajo los 
 * de las otra/s, puede indicar su decisión borrando las menciones a la/s
 * licencia/s sobrantes o no utilizadas por SU versión modificada.
 *
 * Si la presente licencia triple se mantiene íntegra, cualquier usuario 
 * puede utilizar este fichero bajo cualquiera de las tres licencias que 
 * lo gobiernan,  GPL 2.0/CDDL 1.0/EPL 1.0.
 *
 * ***** END LICENSE BLOCK ***** */

package org.opensixen.p2;

import java.util.List;

import org.compiere.util.CLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
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
		//UpdateOperation operation = new UpdateOperation(session);
		List<IInstallableUnit> toBeUpdated = P2.get().getInstalled();
		UpdateOperation operation = new UpdateOperation(session, toBeUpdated);
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
