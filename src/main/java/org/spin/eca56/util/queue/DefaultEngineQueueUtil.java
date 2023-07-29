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
 * Copyright (C) 2003-Present E.R.P. Consultores y Asociados, C.A.            *
 * All Rights Reserved.                                                       *
 * Contributor(s): Yamel Senih www.erpcya.com                                 *
 *****************************************************************************/
package org.spin.eca56.util.queue;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.util.Env;
import org.spin.eca56.util.support.IGenericSender;
import org.spin.model.MADAppRegistration;
import org.spin.util.support.AppSupportHandler;
import org.spin.util.support.IAppSupport;

/**
 * A Default engine queue manager
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class DefaultEngineQueueUtil {
	/** Default Connector Type = MQS-Kafka-Connector-Sender */
	public static final String QUEUETYPE_DefaultAppSupportValue = "MQS-Kafka-Connector-Sender";
	/** Default Connector Application Type = MQS*/
	public static final String QUEUETYPE_DefaultAppType = "MQS";
	
	/**
	 * Get notifier from queue definition
	 * @param queue
	 * @return
	 */
	public static IGenericSender getEngineManager() {
		try {
			MADAppRegistration registeredApplication = getRegistration();
			if(registeredApplication == null) {
				throw new AdempiereException("@AD_AppRegistration_ID@ @NotFound@");
			}
			//	Load support
			IAppSupport supportedApplication = AppSupportHandler.getInstance().getAppSupport(registeredApplication);
			//	Exists an Application available for it?
			if(supportedApplication != null && IGenericSender.class.isAssignableFrom(supportedApplication.getClass())) {
				//	Instance of fiscal printer
				return (IGenericSender) supportedApplication;
			}
		} catch (Exception exception) {
			throw new AdempiereException(exception);
		}
		//	default
		return null;
	}
	
	/**
	 * Get registration from cache or search it
	 * @return
	 */
	private static MADAppRegistration getRegistration() {
		MADAppRegistration registration = MADAppRegistration.getByApplicationType(
				Env.getCtx(),
				QUEUETYPE_DefaultAppType,
				null
		);
		//	return
		return registration;
	}
}
