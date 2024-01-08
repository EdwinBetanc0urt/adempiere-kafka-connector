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

import org.adempiere.core.domains.models.I_C_Order;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MOrder;
import org.compiere.model.PO;
import org.compiere.util.Util;
import org.spin.eca56.util.support.IGenericSender;
import org.spin.eca56.util.support.documents.Order;
import org.spin.queue.model.MADQueue;
import org.spin.queue.util.QueueManager;

/**
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Just a test for service  
 */
public class DocumentManagement extends QueueManager implements IEngineManager {

	public static final String CODE = "DMM";
	
	@Override
	public void add(int queueId) {
		logger.fine("Queue Added: " + queueId);
		try {
			send(queueId);
			MADQueue queue = new MADQueue(getContext(), queueId, getTransactionName());
			queue.setProcessed(true);
			queue.saveEx();
		} catch (Throwable e) {
			logger.warning(e.getLocalizedMessage());
		}
	}

	@Override
	public void process(int queueId) {
		send(queueId);
	}
	
	private void send(int queueId) {
		PO document = getEntity();
		if(document != null) {
			Order entityEngine = getDocumentManager(document);
			if(entityEngine != null) {
				IGenericSender sender = DefaultEngineQueueUtil.getEngineManager();
				if(sender != null) {
					sender.send(entityEngine, entityEngine.getChannel());
				} else {
					throw new AdempiereException("@AD_AppRegistration_ID@ @NotFound@");
				}
			}
			logger.fine("Queue Processed: " + queueId);
		}
	}

	@Override
	public Order getDocumentManager(PO entity) {
		if(entity == null) {
			return null;
		}
		String tableName = entity.get_TableName();
		if(Util.isEmpty(tableName)) {
			return null;
		}
		if(tableName.equals(I_C_Order.Table_Name)) {
			return Order.newInstance().withOrder((MOrder) entity);
		}
		return null;
	}
}
