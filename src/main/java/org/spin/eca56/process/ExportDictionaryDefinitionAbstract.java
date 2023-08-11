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

/** Generated Process for (Export Dictionary Definition)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.4
 */
public abstract class ExportDictionaryDefinitionAbstract extends SvrProcess {
	/** Process Value 	*/
	private static final String VALUE_FOR_PROCESS = "ECA56_ExportDictionaryDefinition";
	/** Process Name 	*/
	private static final String NAME_FOR_PROCESS = "Export Dictionary Definition";
	/** Process Id 	*/
	private static final int ID_FOR_PROCESS = 54692;
	/**	Parameter Name for Export Menu	*/
	public static final String ECA56_EXPORTMENU = "ECA56_ExportMenu";
	/**	Parameter Name for Export Windows	*/
	public static final String ECA56_EXPORTWINDOWS = "ECA56_ExportWindows";
	/**	Parameter Name for Export Process	*/
	public static final String ECA56_EXPORTPROCESS = "ECA56_ExportProcess";
	/**	Parameter Name for Export Browsers	*/
	public static final String ECA56_EXPORTBROWSERS = "ECA56_ExportBrowsers";
	/**	Parameter Value for Export Menu	*/
	private boolean isExportMenu;
	/**	Parameter Value for Export Windows	*/
	private boolean isExportWindows;
	/**	Parameter Value for Export Process	*/
	private boolean isExportProcess;
	/**	Parameter Value for Export Browsers	*/
	private boolean isExportBrowsers;

	@Override
	protected void prepare() {
		isExportMenu = getParameterAsBoolean(ECA56_EXPORTMENU);
		isExportWindows = getParameterAsBoolean(ECA56_EXPORTWINDOWS);
		isExportProcess = getParameterAsBoolean(ECA56_EXPORTPROCESS);
		isExportBrowsers = getParameterAsBoolean(ECA56_EXPORTBROWSERS);
	}

	/**	 Getter Parameter Value for Export Menu	*/
	protected boolean isExportMenu() {
		return isExportMenu;
	}

	/**	 Setter Parameter Value for Export Menu	*/
	protected void setExportMenu(boolean isExportMenu) {
		this.isExportMenu = isExportMenu;
	}

	/**	 Getter Parameter Value for Export Windows	*/
	protected boolean isExportWindows() {
		return isExportWindows;
	}

	/**	 Setter Parameter Value for Export Windows	*/
	protected void setExportWindows(boolean isExportWindows) {
		this.isExportWindows = isExportWindows;
	}

	/**	 Getter Parameter Value for Export Process	*/
	protected boolean isExportProcess() {
		return isExportProcess;
	}

	/**	 Setter Parameter Value for Export Process	*/
	protected void setExportProcess(boolean isExportProcess) {
		this.isExportProcess = isExportProcess;
	}

	/**	 Getter Parameter Value for Export Browsers	*/
	protected boolean isExportBrowsers() {
		return isExportBrowsers;
	}

	/**	 Setter Parameter Value for Export Browsers	*/
	protected void setExportBrowsers(boolean isExportBrowsers) {
		this.isExportBrowsers = isExportBrowsers;
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