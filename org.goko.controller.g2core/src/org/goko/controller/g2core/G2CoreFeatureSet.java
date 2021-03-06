/**
 * 
 */
package org.goko.controller.g2core;

import org.goko.controller.g2core.controller.G2CoreCommunicator;
import org.goko.controller.g2core.controller.G2CoreControllerService;
import org.goko.controller.g2core.controller.IG2CoreControllerService;
import org.goko.core.common.applicative.logging.IApplicativeLogService;
import org.goko.core.common.exception.GkException;
import org.goko.core.connection.serial.ISerialConnectionService;
import org.goko.core.controller.IControllerConfigurationFileExporter;
import org.goko.core.controller.IControllerConfigurationFileImporter;
import org.goko.core.controller.IControllerService;
import org.goko.core.controller.ICoordinateSystemAdapter;
import org.goko.core.controller.IFourAxisControllerAdapter;
import org.goko.core.controller.IGCodeContextProvider;
import org.goko.core.controller.IJogService;
import org.goko.core.controller.IProbingService;
import org.goko.core.controller.IWorkVolumeProvider;
import org.goko.core.execution.IGCodeExecutionTimeService;
import org.goko.core.feature.IFeatureSet;
import org.goko.core.feature.TargetBoard;
import org.goko.core.gcode.rs274ngcv3.IRS274NGCService;
import org.goko.core.gcode.service.IExecutionService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;

/**
 * @author Psyko
 * @date 8 janv. 2017
 */
public class G2CoreFeatureSet implements IFeatureSet {
	/** Target board definition for this feature set */
	private static final TargetBoard G2CORE_TARGET_BOARD = new TargetBoard("g2core.v099", "G2 Core v0.99");
				
	/** (inheritDoc)
	 * @see org.goko.core.feature.IFeatureSet#getTargetBoard()
	 */
	@Override
	public TargetBoard getTargetBoard() {
		return G2CORE_TARGET_BOARD;
	}

	/** (inheritDoc)
	 * @see org.goko.core.feature.IFeatureSet#start(org.osgi.framework.BundleContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void start(BundleContext context) throws GkException {
		G2CoreCommunicator communicator = new G2CoreCommunicator();
		communicator.setConnectionService(findService(context, ISerialConnectionService.class));
		
		G2CoreControllerService service = new G2CoreControllerService(communicator);
		context.registerService(IControllerService.class, service, null);
		context.registerService(IG2CoreControllerService.class, service, null);		
		context.registerService(IProbingService.class, service, null);
		context.registerService(IFourAxisControllerAdapter.class, service, null);
		context.registerService(ICoordinateSystemAdapter.class, service, null);
		context.registerService(IJogService.class, service, null);
		context.registerService(IJogService.class, service, null);		
		context.registerService(IWorkVolumeProvider.class, service, null);		
		context.registerService(IGCodeContextProvider.class, service, null);		
		context.registerService(IControllerConfigurationFileExporter.class, service, null);		
		context.registerService(IControllerConfigurationFileImporter.class, service, null);		
				
		service.setGCodeService(findService(context, IRS274NGCService.class));
		service.setEventAdmin(findService(context, EventAdmin.class));		
		service.setExecutionService(findService(context, IExecutionService.class));
		service.setGcodeExecutionTimeService(findService(context, IGCodeExecutionTimeService.class));
		
		IApplicativeLogService applicativeLogService = findService(context, IApplicativeLogService.class);
		service.setApplicativeLogService(findService(context, IApplicativeLogService.class));
		communicator.setApplicativeLogService(applicativeLogService);
		
		service.start();
	}

	protected <S> S findService( BundleContext context, Class<S> clazz){
		ServiceReference<S> ref = context.getServiceReference(clazz);
		if(ref != null){
			return context.getService(ref);
		}
		return null;
	}
	/** (inheritDoc)
	 * @see org.goko.core.feature.IFeatureSet#stop()
	 */
	@Override
	public void stop() throws GkException {
		
	}	

}
