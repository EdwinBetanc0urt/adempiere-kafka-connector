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
 * Copyright (C) 2003-2015 E.R.P. Consultores y Asociados, C.A.               *
 * All Rights Reserved.                                                       *
 * Contributor(s): Yamel Senih www.erpya.com                                  *
 *****************************************************************************/
package org.spin.eca56.setup;

import java.util.Properties;

import org.adempiere.core.domains.models.I_AD_AppRegistration;
import org.adempiere.core.domains.models.I_AD_AppSupport;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.Query;
import org.spin.eca56.util.support.kafka.Sender;
import org.spin.model.MADAppRegistration;
import org.spin.model.MADAppSupport;
import org.spin.util.ISetupDefinition;

/**
 * Create all queues and app registrations
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class CreateConfig implements ISetupDefinition {

	private static final String DESCRIPTION = "(*Created from Setup Automatically*)";
	private static final String APPLICATION_TYPE = "MQS";
	
	@Override
	public String doIt(Properties context, String transactionName) {
		//	App registration
		createDefaultSender(context, transactionName);
		//	financial management
		return "@AD_SetupDefinition_ID@ @Ok@";
	}
	
	/**
	 * Create notifiers as app registration
	 * @param context
	 * @param transactionName
	 */
	private void createDefaultSender(Properties context, String transactionName) {
		MADAppRegistration processSender = new Query(context, I_AD_AppRegistration.Table_Name, "EXISTS(SELECT 1 FROM AD_AppSupport s "
				+ "WHERE s.AD_AppSupport_ID = AD_AppRegistration.AD_AppSupport_ID "
				+ "AND s.Classname = ?)", transactionName)
		.setParameters(Sender.class.getName())
		.setClient_ID()
		.first();
		if(processSender == null
				|| processSender.getAD_AppRegistration_ID() <= 0) {
			MADAppSupport queueSupport = new Query(context, I_AD_AppSupport.Table_Name, "Classname = ?", transactionName)
				.setParameters(Sender.class.getName())
				.first();
			if(queueSupport == null
					|| queueSupport.getAD_AppSupport_ID() <= 0) {
				throw new AdempiereException("@AD_AppSupport_ID@ @NotFound@");
			}
			processSender = new MADAppRegistration(context, 0, transactionName);
			processSender.setValue("Engine-Queue");
			processSender.setApplicationType(APPLICATION_TYPE);
			processSender.setAD_AppSupport_ID(queueSupport.getAD_AppSupport_ID());
			processSender.setName("Engine as Queue");
			processSender.setDescription(DESCRIPTION);
			processSender.setVersionNo("1.0");
			processSender.setHost("localhost");
			processSender.setPort(29092);
			processSender.saveEx();
		}
	}
}
