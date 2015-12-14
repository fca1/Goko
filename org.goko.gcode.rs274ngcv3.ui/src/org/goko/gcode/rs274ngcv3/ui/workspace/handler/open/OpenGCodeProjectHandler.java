package org.goko.gcode.rs274ngcv3.ui.workspace.handler.open;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;
import org.goko.core.common.exception.GkException;
import org.goko.core.log.GkLog;
import org.goko.core.workspace.service.IWorkspaceService;

public class OpenGCodeProjectHandler {
	/** Log */
	private static final GkLog LOG = GkLog.getLogger(OpenGCodeProjectHandler.class);
	
	@Inject
	private IWorkspaceService workspaceService;
	
	
	@Execute
	public void executeOpenFile(Shell shell) throws GkException{
		workspaceService.loadProject(null);
	}
}