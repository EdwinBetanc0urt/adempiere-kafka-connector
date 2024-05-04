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

import java.util.concurrent.atomic.AtomicInteger;

import org.adempiere.core.domains.models.I_AD_Browse;
import org.adempiere.core.domains.models.I_AD_Form;
import org.adempiere.core.domains.models.I_AD_Menu;
import org.adempiere.core.domains.models.I_AD_Process;
import org.adempiere.core.domains.models.I_AD_Window;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.MBrowse;
import org.compiere.model.MForm;
import org.compiere.model.MMenu;
import org.compiere.model.MProcess;
import org.compiere.model.MWindow;
import org.compiere.model.Query;
import org.spin.eca56.util.queue.ApplicationDictionary;
import org.spin.queue.util.QueueLoader;

/** Generated Process for (Export Current Dictionary Definition)
 *  @author Edwin Betancourt
 *  @version Release 3.9.4
 */
public class ExportCurrentDictionaryDefinition extends ExportCurrentDictionaryDefinitionAbstract
{

	private AtomicInteger counter = new AtomicInteger();

	@Override
	protected void prepare()
	{
		super.prepare();

		// Valid Record Identifier
		if (this.getRecord_ID() <= 0) {
			throw new AdempiereException("@FillMandatory@ @Record_ID@");
		}

		if(this.getTable_ID() <= 0) {
			throw new AdempiereException("@FillMandatory@ @AD_Table@");
		}
	}

	@Override
	protected String doIt() throws Exception {
		//	For menu
		if (this.getTable_ID() == I_AD_Menu.Table_ID) {
			exportMenuDefinition();
		}

		//	For Window Definition
		if(this.getTable_ID() == I_AD_Window.Table_ID) {
			exportWindowDefinition();
		}

		//	For Process Definition
		if(this.getTable_ID() == I_AD_Process.Table_ID) {
			exportProcessDefinition();
		}

		//	For Browser Definition
		if(this.getTable_ID() == I_AD_Browse.Table_ID) {
			exportBrowserDefinition();
		}

		//	For Form Definition
		if(this.getTable_ID() == I_AD_Form.Table_ID) {
			exportFormDefinition();
		}

		return "Ok";
	}


	private void exportMenuDefinition() {
		addLog("@AD_Menu_ID@");
		//	For only specific Menu node
		MMenu menuNode = new Query(
			getCtx(),
			I_AD_Menu.Table_Name,
			I_AD_Menu.COLUMNNAME_AD_Menu_ID + " = ?",
			get_TrxName()
		)
			.setParameters(this.getRecord_ID())
			.first()
		;
		QueueLoader.getInstance()
			.getQueueManager(ApplicationDictionary.CODE)
			.withEntity(menuNode)
			.addToQueue()
		;
		counter.incrementAndGet();
	}


	private void exportWindowDefinition() {
		addLog("@AD_Window_ID@");

		// Add filter a specific Window
		MWindow window = new MWindow(getCtx(), this.getRecord_ID(), get_TrxName());
		QueueLoader.getInstance()
			.getQueueManager(ApplicationDictionary.CODE)
			.withEntity(window)
			.addToQueue()
		;
		addLog(window.getName());
		counter.incrementAndGet();
	}


	private void exportProcessDefinition() {
		addLog("@AD_Process_ID@");

		// Add filter a specific Process
		MProcess process = new MProcess(getCtx(), this.getRecord_ID(), get_TrxName());
		QueueLoader.getInstance()
			.getQueueManager(ApplicationDictionary.CODE)
			.withEntity(process)
			.addToQueue()
		;
		addLog(process.getValue() + " - " + process.getName());
		counter.incrementAndGet();
	}

	private void exportBrowserDefinition() {
		addLog("@AD_Browse_ID@");

		// Add filter a specific Browse
		MBrowse browser = new MBrowse(getCtx(), this.getRecord_ID(), get_TrxName());
		QueueLoader.getInstance()
			.getQueueManager(ApplicationDictionary.CODE)
			.withEntity(browser)
			.addToQueue()
		;
		addLog(browser.getValue() + " - " + browser.getName());
		counter.incrementAndGet();
	}


	private void exportFormDefinition() {
		addLog("@AD_Form_ID@");

		// Add filter a specific Form
		MForm form = new MForm(getCtx(), this.getRecord_ID(), get_TrxName());
		QueueLoader.getInstance()
			.getQueueManager(ApplicationDictionary.CODE)
			.withEntity(form)
			.addToQueue()
		;
		addLog(form.getClassname() + " - " + form.getName());
		counter.incrementAndGet();
	}
}
