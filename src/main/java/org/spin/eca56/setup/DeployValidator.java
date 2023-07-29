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

import org.compiere.model.Query;
import org.adempiere.core.domains.models.X_AD_ModelValidator;
import org.spin.eca56.model.validator.EngineAsQueue;
import org.spin.util.ISetupDefinition;

/**
 * This functionality setup allows add a model validator for catch all changes for:
 * <li>Application Dictionary
 * <li>Material Management
 * <li>Financial Management
 * <li>Accounting Management
 * <li>Costing Management
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class DeployValidator implements ISetupDefinition {

	private static final String DESCRIPTION = "Allows send all engines to queue";
	private static final String NAME = "Process as Queue";
	private static final String ENTITY_TYPE = "ECA56";
	private static final int DEFAULT_SEQUENCE = 300;
	
	@Override
	public String doIt(Properties context, String transactionName) {
		//	Add Model Validator
		createModelValidator(context, transactionName);
		//	financial management
		return "@AD_SetupDefinition_ID@ @Ok@";
	}
	
	/**
	 * Create Model Vaidator
	 * @param context
	 * @param transactionName
	 * @return
	 */
	private X_AD_ModelValidator createModelValidator(Properties context, String transactionName) {
		X_AD_ModelValidator modelValidator = new Query(context, X_AD_ModelValidator.Table_Name, X_AD_ModelValidator.COLUMNNAME_ModelValidationClass + " = ?", transactionName)
				.setParameters(EngineAsQueue.class.getName())
				.setClient_ID()
				.<X_AD_ModelValidator>first();
		//	Validate
		if(modelValidator != null
				&& modelValidator.getAD_ModelValidator_ID() > 0) {
			return modelValidator;
		}
		//	
		modelValidator = new X_AD_ModelValidator(context, 0, transactionName);
		modelValidator.setName(NAME);
		modelValidator.setEntityType(ENTITY_TYPE);
		modelValidator.setDescription(DESCRIPTION);
		modelValidator.setSeqNo(DEFAULT_SEQUENCE);
		modelValidator.setModelValidationClass(EngineAsQueue.class.getName());
		modelValidator.saveEx();
		return modelValidator;
	}
}
