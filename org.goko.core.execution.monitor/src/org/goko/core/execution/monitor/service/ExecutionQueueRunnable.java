package org.goko.core.execution.monitor.service;

import org.goko.core.common.exception.GkException;
import org.goko.core.gcode.execution.ExecutionQueue;
import org.goko.core.gcode.execution.ExecutionState;
import org.goko.core.gcode.execution.IExecutionControl;
import org.goko.core.gcode.execution.IExecutionToken;
import org.goko.core.gcode.execution.IExecutionTokenState;
import org.goko.core.gcode.execution.IExecutor;
import org.goko.core.gcode.service.IExecutionService;
import org.goko.core.log.GkLog;

/**
 * Describes a runnable performing the execution of an ExecutionQueue
 *
 * @author Psyko
 *
 */
public class ExecutionQueueRunnable<S extends IExecutionTokenState, T extends IExecutionToken<S>>  implements Runnable,IExecutionControl{
	/** LOG */
	private static final GkLog LOG = GkLog.getLogger(ExecutionQueueRunnable.class);
	/** The current executor */
	private IExecutor<S, T> executor;
	/** The execution queue*/
	private ExecutionQueue<S, T> executionQueue;
	/** The underlying execution service */
	private IExecutionService<S, T> executionService;
	/** The state of the execution */
	private ExecutionState state;

	/**
	 * Constructor
	 * @param executionService the referencing execution service
	 */
	public ExecutionQueueRunnable(IExecutionService<S, T> executionService) {
		this.executionService = executionService;
		this.state = ExecutionState.IDLE;
	}

	/** (inheritDoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		LOG.info("Starting ExecutionQueueRunnable");
		try{
			setState(ExecutionState.RUNNING);
			executionService.notifyQueueExecutionStart(executionQueue.getType());

			while(executionQueue.hasNext() && state != ExecutionState.STOPPED){				
				executionQueue.beginNextTokenExecution();
				runExecutionToken();					
				executionQueue.endCurrentTokenExecution();									
			}
						
			if(state == ExecutionState.STOPPED){
				executionService.notifyQueueExecutionCanceled(executionQueue.getType());
			}else if(state == ExecutionState.ERROR){
				executionService.notifyQueueExecutionCanceled(executionQueue.getType());
			}else{
				setState(ExecutionState.COMPLETE);
				executionService.notifyQueueExecutionComplete(executionQueue.getType());	
				LOG.info("Queue complete");
			}			
		}catch(GkException e){
			LOG.error(e);
			setState(ExecutionState.FATAL_ERROR);
		}
	}

	protected void isValidState(){

	}
	/**
	 * Execute the given execution token
	 * @param token the token to execute
	 * @throws GkException GkException
	 */
	protected void runExecutionToken() throws GkException{
		executor.setExecutionService(executionService);
		T token = executionQueue.getCurrentToken();
		executor.executeToken(token);
	}

	/**
	 * @return the executor
	 */
	public IExecutor<S, T> getExecutor() {
		return executor;
	}

	/**
	 * @param executor the executor to set
	 */
	public void setExecutor(IExecutor<S, T> executor) {
		this.executor = executor;
	}

	/**
	 * @return the executionQueue
	 */
	public ExecutionQueue<S, T> getExecutionQueue() {
		return executionQueue;
	}

	/**
	 * @param executionQueue the executionQueue to set
	 */
	public void setExecutionQueue(ExecutionQueue<S, T> executionQueue) {
		this.executionQueue = executionQueue;
	}

	/**
	 * @return the state
	 */
	@Override
	public ExecutionState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(ExecutionState state) {
		this.state = state;
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutionControl#start()
	 */
	@Override
	public void start() throws GkException {
		executor.start();
		setState(ExecutionState.RUNNING);
		executionService.notifyExecutionStart(executionQueue.getCurrentToken());
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutionControl#resume()
	 */
	@Override
	public void resume() throws GkException {
		if(getState() == ExecutionState.PAUSED){
			setState(ExecutionState.RUNNING);
			executor.resume();		
			executionService.notifyExecutionResume(executionQueue.getCurrentToken());
		}
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutionControl#pause()
	 */
	@Override
	public void pause() throws GkException {
		if(getState() == ExecutionState.RUNNING){
			setState(ExecutionState.PAUSED);
			executor.pause();		
			executionService.notifyExecutionPause(executionQueue.getCurrentToken());
		}
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutionControl#stop()
	 */
	@Override
	public void stop() throws GkException {
		if(getState() == ExecutionState.RUNNING || getState() == ExecutionState.PAUSED){			
			T token = executionQueue.getCurrentToken();			
			setState(ExecutionState.STOPPED);
			executor.stop();		
			executionService.notifyExecutionCanceled(token);			
		}
	}

}
