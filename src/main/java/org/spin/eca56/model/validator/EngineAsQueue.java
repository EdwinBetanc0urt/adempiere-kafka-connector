/************************************************************************************
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, C.A.                     *
 * Contributor(s): Yamel Senih ysenih@erpya.com                                     *
 * This program is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by             *
 * the Free Software Foundation, either version 2 of the License, or                *
 * (at your option) any later version.                                              *
 * This program is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the                     *
 * GNU General Public License for more details.                                     *
 * You should have received a copy of the GNU General Public License                *
 * along with this program.	If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/
package org.spin.eca56.model.validator;

import org.adempiere.core.domains.models.I_AD_Process;
import org.adempiere.core.domains.models.I_AD_Table;
import org.compiere.model.MClient;
import org.compiere.model.MTable;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.spin.eca56.util.queue.ApplicationDictionary;
import org.spin.eca56.util.queue.DocumentManagement;
import org.spin.queue.util.QueueLoader;

/**
 * Convert the current engines to queue, this allows process all functionalities as external engines and async
 * @author Yamel Senih ysenih@erpya.com
 *
 */
public class EngineAsQueue implements ModelValidator {

	/** Logger */
	private static CLogger log = CLogger.getCLogger(EngineAsQueue.class);
	/** Client */
	private int clientId = -1;
	
	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		// client = null for global validator
		if (client != null) {
			clientId = client.getAD_Client_ID();
			log.info(client.toString());
		} else {
			log.info("Initializing global validator: " + this.toString());
		}
		//	Add Persistence for IsDefault values
		new Query(Env.getCtx(), I_AD_Table.Table_Name, I_AD_Table.COLUMNNAME_IsDocument + " = 'Y'", null)
                .setOnlyActiveRecords(true)
                .<MTable>list().forEach(table -> {
                	engine.addDocValidate(table.getTableName(), this);
                });
	}
	
	@Override
	public int getAD_Client_ID() {
		return clientId;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		log.info("AD_User_ID=" + AD_User_ID);
		return null;
	}
	
	@Override
	public String modelChange(PO entity, int type) throws Exception {
		if(type == TYPE_BEFORE_NEW
				|| type == TYPE_BEFORE_CHANGE) {
			if(entity.get_TableName().equals(I_AD_Process.Table_Name)) {
				//	For Sales Orders
				if(entity.is_new()
						|| (!entity.is_ValueChanged(I_AD_Process.COLUMNNAME_Statistic_Count)
								&& !entity.is_ValueChanged(I_AD_Process.COLUMNNAME_Statistic_Seconds))) {
					QueueLoader.getInstance().getQueueManager(ApplicationDictionary.CODE).withEntity(entity);
				}
			}
		}
		return null;
	}

	@Override
	public String docValidate(PO entity, int timing) {
		if(timing == TIMING_AFTER_COMPLETE
				|| timing == TIMING_AFTER_REVERSECORRECT
				|| timing == TIMING_AFTER_REVERSEACCRUAL
				|| timing == TIMING_AFTER_VOID) {
			QueueLoader.getInstance().getQueueManager(DocumentManagement.CODE).withEntity(entity).addToQueue();
		}
		return null;
	}
}
