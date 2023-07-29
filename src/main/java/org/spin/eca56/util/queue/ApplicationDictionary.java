/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2013 E.R.P. Consultores y Asociados, C.A.               *
 * All Rights Reserved.                                                       *
 * Contributor(s): Yamel Senih www.erpcya.com                                 *
 *****************************************************************************/
package org.spin.eca56.util.queue;

import org.adempiere.core.domains.models.I_AD_Process;
import org.compiere.model.MProcess;
import org.compiere.model.PO;
import org.compiere.util.Util;
import org.spin.eca56.util.support.documents.Process;
import org.spin.queue.util.QueueManager;

/**
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Just a test for service  
 */
public class ApplicationDictionary extends QueueManager implements IEngineManager {

	public static final String CODE = "ADM";
	
	@Override
	public void add(int queueId) {
		logger.fine("Queue Added: " + queueId);
		process(queueId);
	}

	@Override
	public void process(int queueId) {
		PO process = getEntity();
		if(process != null) {
			Process processEngine = getDocumentManager(process.get_TableName());
			if(processEngine != null) {
				processEngine.setProcess((MProcess) process);
				System.err.println(processEngine.getValues());
			}
			logger.fine("Queue Processed: " + queueId);
		}
	}

	@Override
	public Process getDocumentManager(String tableName) {
		if(Util.isEmpty(tableName)) {
			return null;
		}
		if(tableName.equals(I_AD_Process.Table_Name)) {
			return Process.newInstance();
		}
		return null;
	}
}
