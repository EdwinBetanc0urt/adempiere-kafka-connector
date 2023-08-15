/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 or later of the                                  *
 * GNU General Public License as published                                    *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2023 E.R.P. Consultores y Asociados, C.A.               *
 * All Rights Reserved.                                                       *
 * Contributor(s): Yamel Senih www.erpya.com                                  *
 *****************************************************************************/
package org.spin.eca56.util.support.documents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.compiere.model.MProcess;
import org.compiere.model.MProcessPara;
import org.spin.eca56.util.support.IGenericDocument;

/**
 * 	the document class for Process senders
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class Process implements IGenericDocument {

	//	Some default documents key
	public static final String KEY = "process";
	public static final String CHANNEL = "new";
	private Map<String, Object> document;
	
	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Map<String, Object> getValues() {
		return document;
	}
	
	public Process withProcess(MProcess process) {
		document = new HashMap<>();
		Map<String, Object> documentDetail = new HashMap<>();
		documentDetail.put("id", process.getAD_Process_ID());
		documentDetail.put("uuid", process.getUUID());
		documentDetail.put("code", process.getValue());
		documentDetail.put("name", process.getName());
		documentDetail.put("description", process.getDescription());
		documentDetail.put("help", process.getHelp());
		documentDetail.put("entity_type", process.getEntityType());
		documentDetail.put("access_level", process.getAccessLevel());
		documentDetail.put("class_name", process.getClassname());
		documentDetail.put("is_report", process.isReport());
		documentDetail.put("is_active", process.isActive());
		documentDetail.put("show_help", process.getShowHelp());
		documentDetail.put("jasper_report", process.getJasperReport());
		documentDetail.put("procedure_name", process.getProcedureName());
		documentDetail.put("workflow_id", process.getAD_Workflow_ID());
		documentDetail.put("form_id", process.getAD_Form_ID());
		documentDetail.put("browser_id", process.getAD_Browse_ID());
		documentDetail.put("report_view_id", process.getAD_ReportView_ID());
		documentDetail.put("print_format_id", process.getAD_PrintFormat_ID());
		
		//	Parameters
		List<MProcessPara> parameters = process.getParametersAsList();
		List<Map<String, Object>> parametersDetail = new ArrayList<>();
		if(parameters != null) {
			parameters.forEach(parameter -> {
				Map<String, Object> detail = new HashMap<>();
				detail.put("id", parameter.getAD_Process_Para_ID());
				detail.put("uuid", parameter.getUUID());
				detail.put("name", parameter.getName());
				detail.put("description", parameter.getDescription());
				detail.put("help", parameter.getHelp());
				detail.put("entity_type", parameter.getEntityType());
				detail.put("column_name", parameter.getColumnName());
				detail.put("element_id", parameter.getAD_Element_ID());
				detail.put("default_value", parameter.getDefaultValue());
				detail.put("default_value_to", parameter.getDefaultValue2());
				detail.put("is_range", parameter.isRange());
				detail.put("is_mandatory", parameter.isMandatory());
				detail.put("display_logic", parameter.getDisplayLogic());
				detail.put("sequence", parameter.getSeqNo());
				detail.put("value_format", parameter.getVFormat());
				detail.put("min_value", parameter.getValueMin());
				detail.put("max_value", parameter.getValueMax());
				detail.put("display_type", parameter.getAD_Reference_ID());
				detail.put("reference_value_id", parameter.getAD_Reference_Value_ID());
				detail.put("validation_id", parameter.getAD_Val_Rule_ID());
				parametersDetail.add(detail);
			});
		}
		documentDetail.put("parameters", parametersDetail);
		document.put(KEY, documentDetail);
		return this;
	}
	
	/**
	 * Default instance
	 * @return
	 */
	public static Process newInstance() {
		return new Process();
	}

	@Override
	public String getChannel() {
		return CHANNEL;
	}
	
	
}
