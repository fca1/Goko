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
package org.goko.base.dro.controller;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.goko.base.dro.IDROService;
import org.goko.common.bindings.AbstractController;
import org.goko.core.common.exception.GkException;
import org.goko.core.controller.IControllerService;
import org.goko.core.controller.bean.MachineValueDefinition;

public class DROPreferencesPageController extends AbstractController<DROPreferencesPageModel> {

	@Inject @Preference
	IEclipsePreferences preferences;

	@Inject
	IControllerService controllerService;

	@Inject
	IDROService droService;

	public DROPreferencesPageController(DROPreferencesPageModel binding) {
		super(binding);
	}

	@Override
	public void initialize() throws GkException {
		refreshDisplayedValuesList();

	}

	/*
	 * READ/WRITE PREFERENCES METHODS
	 */
	protected List<MachineValueDefinition> getSavedValues() throws GkException{


		/*List<String> lstString = new ArrayList<String>();
		List<MachineValueDefinition> lst = new ArrayList<MachineValueDefinition>();
		String serializedList = preferences.get(IDROPreferencesConstants.KEY_VALUES_ID_LIST, StringUtils.EMPTY);
		if(StringUtils.isNotBlank(serializedList)){

			InputStream out = new ByteArrayInputStream(serializedList.getBytes()) ;
			try {
				ObjectInputStream str = new ObjectInputStream(out);
				try {
					lstString = (List<String>) str.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		for (String definitionId  : lstString) {
			lst.add(controllerService.getMachineValueDefinition(definitionId));
		}*/
		return droService.getDisplayedMachineValueDefinition();
	}


	public void savedDisplayedValues(){
		try {
			droService.saveDisplayedMachineValueDefinition(getDataModel().getDisplayedValuesTypedList());
		} catch (GkException e) {
			e.printStackTrace();
			return;
		}
	}


	/*
	 * PURE UI METHODS
	 */
	public void refreshDisplayedValuesList() throws GkException {
		List<MachineValueDefinition> lst = getSavedValues();

		getDataModel().getDisplayedValuesTypedList().clear();
		getDataModel().getDisplayedValuesTypedList().addAll(lst);

	}

	public void removeDisplayedValues(List listMachineValueDefinitionToRemove) {
		for(Object machineValueDefinition : listMachineValueDefinitionToRemove){
			MachineValueDefinition definition = (MachineValueDefinition)machineValueDefinition;
			getDataModel().getDisplayedValuesTypedList().remove(definition);
		}
	}

	public void moveDisplayedValuesUp(List listMachineValueDefinitionToMove) {
		for(Object machineValueDefinition : listMachineValueDefinitionToMove){
			MachineValueDefinition definition = (MachineValueDefinition)machineValueDefinition;
			int index = getDataModel().getDisplayedValuesTypedList().indexOf(definition);
			int newIndex = Math.max(0, index - 1);
			getDataModel().getDisplayedValuesTypedList().remove(definition);
			getDataModel().getDisplayedValuesTypedList().add(newIndex, definition);
		}
	}

	public void moveDisplayedValuesDown(List listMachineValueDefinitionToMove) {
		int maxLength = getDataModel().getDisplayedValuesTypedList().size();
		int selectionCount = listMachineValueDefinitionToMove.size();
		for(int i = selectionCount - 1; i >= 0; i--){//Object machineValueDefinition : listMachineValueDefinitionToMove){
			MachineValueDefinition definition = (MachineValueDefinition)listMachineValueDefinitionToMove.get(i);
			int index = getDataModel().getDisplayedValuesTypedList().indexOf(definition);
			int newIndex = Math.min(maxLength - 1, index + 1);
			getDataModel().getDisplayedValuesTypedList().remove(definition);
			getDataModel().getDisplayedValuesTypedList().add(newIndex, definition);
		}
	}

	public void addValuesToDisplay(List<MachineValueDefinition> lstToAdd) {
		for (MachineValueDefinition machineValueDefinition : lstToAdd) {
			if( !getDataModel().getDisplayedValuesTypedList().contains(machineValueDefinition) ){
				getDataModel().getDisplayedValuesTypedList().add(machineValueDefinition);
			}
		}
	}

}
