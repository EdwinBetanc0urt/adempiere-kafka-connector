/******************************************************************************
 * Product: ADempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2006-2017 ADempiere Foundation, All Rights Reserved.         *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * or (at your option) any later version.                                     *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * or via info@adempiere.net                                                  *
 * or https://github.com/adempiere/adempiere/blob/develop/license.html        *
 *****************************************************************************/

package org.spin.eca56.process;

import org.compiere.process.SvrProcess;

/** Generated Process for (Export Role Accesses)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.4
 */
public abstract class ExportRoleAccessesAbstract extends SvrProcess {
	/** Process Value 	*/
	private static final String VALUE_FOR_PROCESS = "ECA56_ExportRoleAccesses";
	/** Process Name 	*/
	private static final String NAME_FOR_PROCESS = "Export Role Accesses";
	/** Process Id 	*/
	private static final int ID_FOR_PROCESS = 54758;
	/**	Parameter Name for Role	*/
	public static final String AD_ROLE_ID = "AD_Role_ID";
	/**	Parameter Value for Role	*/
	private int roleId;

	@Override
	protected void prepare() {
		roleId = getParameterAsInt(AD_ROLE_ID);
	}

	/**	 Getter Parameter Value for Role	*/
	protected int getRoleId() {
		return roleId;
	}

	/**	 Setter Parameter Value for Role	*/
	protected void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	/**	 Getter Parameter Value for Process ID	*/
	public static final int getProcessId() {
		return ID_FOR_PROCESS;
	}

	/**	 Getter Parameter Value for Process Value	*/
	public static final String getProcessValue() {
		return VALUE_FOR_PROCESS;
	}

	/**	 Getter Parameter Value for Process Name	*/
	public static final String getProcessName() {
		return NAME_FOR_PROCESS;
	}
}