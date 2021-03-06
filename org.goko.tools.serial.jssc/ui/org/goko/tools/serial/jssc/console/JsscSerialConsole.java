/*
 *
 *   Goko
 *   Copyright (C) 2013  PsyKo
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.goko.tools.serial.jssc.console;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.goko.common.GkUiComponent;
import org.goko.common.GkUiUtils;
import org.goko.common.elements.combo.GkCombo;
import org.goko.common.elements.combo.LabeledValue;
import org.goko.core.common.exception.GkException;
import org.goko.tools.serial.jssc.console.internal.JsscSerialConsoleController;
import org.goko.tools.serial.jssc.console.internal.JsscSerialConsoleModel;

public class JsscSerialConsole extends GkUiComponent<JsscSerialConsoleController, JsscSerialConsoleModel> {
	private static final String CONSOLE_ENABLED = "org.goko.tools.serial.jssc.consoleEnabled";
	private static final String CONSOLE_SCROLL_LOCKED =  "org.goko.tools.serial.jssc.consoleScrollLock";
	private static final String CONSOLE_END_LINE_TOKEN =  "org.goko.tools.serial.jssc.consoleEndLineToken";
	private Text commandTxt;
	private GkCombo<LabeledValue<String>> endLineCombo;

	/**
	 * Constructor
	 * @param context
	 */
	@Inject
	public JsscSerialConsole(IEclipseContext context) {
		super(context, new JsscSerialConsoleController());		
		context.set(JsscSerialConsoleController.class, getController());		
	}
	/**
	 * Create contents of the view part.
	 *
	 * @throws GkException
	 *             GkException
	 */
	@PostConstruct
	public void createControls(final Composite parent, MPart part) throws GkException {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new GridLayout(4, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		commandTxt = new Text(composite_2, SWT.BORDER);
		commandTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.CR || e.keyCode == SWT.LF || e.keyCode == SWT.KEYPAD_CR){
					try {
						getController().sendCurrentCommand();
					} catch (GkException e1) {
						displayMessage(e1);
					}
				}else if(e.keyCode == SWT.ARROW_UP){
					getController().climbHistoryUp();
				}else if(e.keyCode == SWT.ARROW_DOWN){
					getController().climbHistoryDown();
				}

			}
		});
		commandTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnSend = new Button(composite_2, SWT.NONE);
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				try {
					getController().sendCurrentCommand();
				} catch (GkException e1) {
					displayMessage(e1);
				}
			}
		});
		GridData gd_btnSend = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSend.widthHint = 50;
		btnSend.setLayoutData(gd_btnSend);
		btnSend.setText("Send");
				
		Label lblNewLabel = new Label(composite_2, SWT.NONE);
		lblNewLabel.setText("Line end ");
								
		endLineCombo = new GkCombo<LabeledValue<String>>(composite_2, SWT.NONE | SWT.READ_ONLY);
		Combo combo = endLineCombo.getCombo();
		combo.select(2);


		StyledText styledText = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		styledText.setEditable(false);
		styledText.setFont(SWTResourceManager.getFont("Consolas", 9, SWT.NORMAL));
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		getController().setTextDisplay(styledText);
		initBindings();
		initFromPersistedState(part);
	}

	private void initFromPersistedState(MPart part){
		Map<String, String> state = part.getPersistedState();
		String consoleEnabledStr = state.get(CONSOLE_ENABLED);
		if(StringUtils.isNotEmpty(consoleEnabledStr)){
			getDataModel().setConsoleEnabled(BooleanUtils.toBoolean(consoleEnabledStr));
		}else{
			getDataModel().setConsoleEnabled(true); // Default is enabled
		}
		String consoleScrollStr = state.get(CONSOLE_SCROLL_LOCKED);
		if(StringUtils.isNotEmpty(consoleScrollStr)){
			getDataModel().setScrollLock(BooleanUtils.toBoolean(consoleScrollStr));
		}

		String endLineToken = state.get(CONSOLE_END_LINE_TOKEN);
		if(StringUtils.isNotEmpty(consoleScrollStr)){
			try {
				getDataModel().setEndLineToken( GkUiUtils.getLabelledValueByKey(endLineToken, getDataModel().getEndLineChars()));
			} catch (GkException e) {
				displayMessage(e);
			}
		}
	}
	private void initBindings() throws GkException{
		getController().addItemsBinding(endLineCombo, "endLineChars");
		getController().addItemSelectionBinding(endLineCombo, "endLineToken");
		getController().addTextModifyBinding(commandTxt, "command");
	}

	@PersistState
	public void persistState(MPart part){
		if(getDataModel() != null){
			part.getPersistedState().put(CONSOLE_ENABLED, String.valueOf(getDataModel().isConsoleEnabled()));
			part.getPersistedState().put(CONSOLE_SCROLL_LOCKED, String.valueOf(getDataModel().isScrollLock()));
			part.getPersistedState().put(CONSOLE_END_LINE_TOKEN, String.valueOf(getDataModel().getEndLineToken().getValue()));
		}
	}

	@PreDestroy
	public void dispose(){
		getController().destroy();
	}
}
