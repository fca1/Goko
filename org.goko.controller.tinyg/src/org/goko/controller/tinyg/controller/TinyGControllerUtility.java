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
package org.goko.controller.tinyg.controller;

import org.apache.commons.lang3.StringUtils;
import org.goko.core.common.measure.quantity.Angle;
import org.goko.core.common.measure.quantity.Length;
import org.goko.core.controller.bean.MachineState;
import org.goko.core.gcode.rs274ngcv3.context.EnumUnit;
import org.goko.core.math.Tuple6b;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class TinyGControllerUtility {

	public TinyGControllerUtility() {

	}

	/**
	 * Transform a GCode command to a JsonValue
	 * @param command the command to transform
	 * @return {@link JsonValue}
	 */
	protected static JsonValue toJson(String command){
		JsonObject value = new JsonObject();
		value.add(TinyGv097.GCODE_COMMAND, StringUtils.lowerCase(command));
		return value;
	}

	/**
	 * Update the 
	 * @param lastKnownPosition
	 * @param statusReport
	 * @return
	 */
	protected static Tuple6b updatePosition(Tuple6b lastKnownPosition, JsonObject statusReport, EnumUnit unit){
		Tuple6b newPosition = new Tuple6b(lastKnownPosition);
		JsonValue newPositionX = statusReport.get(TinyGv097.STATUS_REPORT_POSITION_X);
		JsonValue newPositionY = statusReport.get(TinyGv097.STATUS_REPORT_POSITION_Y);
		JsonValue newPositionZ = statusReport.get(TinyGv097.STATUS_REPORT_POSITION_Z);
		JsonValue newPositionA = statusReport.get(TinyGv097.STATUS_REPORT_POSITION_A);
		if(newPositionX != null){
			newPosition.setX( Length.valueOf(newPositionX.asBigDecimal(), unit.getUnit()));
		}
		if(newPositionY != null){
			newPosition.setY( Length.valueOf(newPositionY.asBigDecimal() , unit.getUnit()));
		}
		if(newPositionZ != null){
			newPosition.setZ( Length.valueOf(newPositionZ.asBigDecimal() , unit.getUnit()));
		}
		if(newPositionA != null){
			newPosition.setA( Angle.valueOf(newPositionA.asBigDecimal() , lastKnownPosition.getA().getUnit()));
		}
		return newPosition;
	}
	/**
	 * Convert Integer value to Machine State
	 * @param stateCode the int value
	 * @return {@link MachineState} object
	 */
	protected static MachineState getState(Integer stateCode){
		switch(stateCode){
		case 0: return MachineState.INITIALIZING;
		case 1: return MachineState.READY;
		case 2: return MachineState.ALARM;
		case 3: return MachineState.PROGRAM_STOP;
		case 4: return MachineState.PROGRAM_END;
		case 5: return MachineState.MOTION_RUNNING;
		case 6: return MachineState.MOTION_HOLDING;
		case 7: return MachineState.PROBE_CYCLE;
		case 8: return MachineState.RUNNING;
		case 9: return MachineState.HOMING;
		default: return MachineState.INITIALIZING;
		}
	}
}
