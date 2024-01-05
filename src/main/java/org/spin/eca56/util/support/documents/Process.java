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
import java.util.Optional;

import org.adempiere.core.domains.models.I_AD_Browse;
import org.adempiere.core.domains.models.I_AD_Form;
import org.adempiere.core.domains.models.I_AD_Process;
import org.adempiere.core.domains.models.I_AD_Process_Para;
import org.adempiere.core.domains.models.I_AD_Workflow;
import org.adempiere.model.MBrowse;
import org.compiere.model.MForm;
import org.compiere.model.MProcess;
import org.compiere.model.MProcessPara;
import org.compiere.model.MReportView;
import org.compiere.model.PO;
import org.compiere.wf.MWorkflow;
import org.spin.eca56.util.support.DictionaryDocument;
import org.spin.util.AbstractExportFormat;
import org.spin.util.ReportExportHandler;

/**
 * 	the document class for Process senders
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class Process extends DictionaryDocument {

	//	Some default documents key
	public static final String KEY = "new";
	public static final String CHANNEL = "process";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public DictionaryDocument withEntity(PO entity) {
		MProcess process = (MProcess) entity;
		Map<String, Object> documentDetail = new HashMap<>();
		documentDetail.put("id", process.getAD_Process_ID());
		documentDetail.put("uuid", process.getUUID());
		documentDetail.put("code", process.getValue());
		documentDetail.put("name", process.get_Translation(I_AD_Process.COLUMNNAME_Name, getLanguage()));
		documentDetail.put("description", process.get_Translation(I_AD_Process.COLUMNNAME_Description, getLanguage()));
		documentDetail.put("help", process.get_Translation(I_AD_Process.COLUMNNAME_Help, getLanguage()));
		documentDetail.put("is_report", process.isReport());
		documentDetail.put("is_active", process.isActive());
		documentDetail.put("show_help", process.getShowHelp());
		documentDetail.put("workflow_id", process.getAD_Workflow_ID());
		documentDetail.put("form_id", process.getAD_Form_ID());
		documentDetail.put("browser_id", process.getAD_Browse_ID());
		documentDetail.put("report_view_id", process.getAD_ReportView_ID());
		documentDetail.put("print_format_id", process.getAD_PrintFormat_ID());
		if(process.isReport()) {
			MReportView reportView = null;
			if(process.getAD_ReportView_ID() > 0) {
				reportView = MReportView.get(entity.getCtx(), process.getAD_ReportView_ID());
			}
			ReportExportHandler exportHandler = new ReportExportHandler(entity.getCtx(), reportView);
			Map<String, Object> reportExportReference = new HashMap<>();
			for(AbstractExportFormat reportType : exportHandler.getExportFormatList()) {
				reportExportReference.put("name", reportType.getName());
				reportExportReference.put("type", reportType.getExtension());
			}
			documentDetail.put("report_export_types", reportExportReference);
		}
		if(process.getAD_Form_ID() > 0) {
			MForm form = new MForm(process.getCtx(), process.getAD_Form_ID(), null);
			Map<String, Object> referenceDetail = new HashMap<>();
			referenceDetail.put("id", form.getAD_Form_ID());
			referenceDetail.put("uuid", form.getUUID());
			referenceDetail.put("name", form.get_Translation(I_AD_Form.COLUMNNAME_Name, getLanguage()));
			referenceDetail.put("description", form.get_Translation(I_AD_Form.COLUMNNAME_Description, getLanguage()));
			referenceDetail.put("help", form.get_Translation(I_AD_Form.COLUMNNAME_Help, getLanguage()));
			documentDetail.put("form", referenceDetail);
		}
		if(process.getAD_Browse_ID() > 0) {
			MBrowse smartBrowser = MBrowse.get(process.getCtx(), process.getAD_Browse_ID());
			Map<String, Object> referenceDetail = new HashMap<>();
			referenceDetail.put("id", smartBrowser.getAD_Browse_ID());
			referenceDetail.put("uuid", smartBrowser.getUUID());
			referenceDetail.put("name", smartBrowser.get_Translation(I_AD_Browse.COLUMNNAME_Name, getLanguage()));
			referenceDetail.put("description", smartBrowser.get_Translation(I_AD_Browse.COLUMNNAME_Description, getLanguage()));
			referenceDetail.put("help", smartBrowser.get_Translation(I_AD_Browse.COLUMNNAME_Help, getLanguage()));
			documentDetail.put("browse", referenceDetail);
		}
		if(process.getAD_Workflow_ID() > 0) {
			MWorkflow workflow = MWorkflow.get(process.getCtx(), process.getAD_Workflow_ID());
			Map<String, Object> referenceDetail = new HashMap<>();
			referenceDetail.put("id", workflow.getAD_Workflow_ID());
			referenceDetail.put("uuid", workflow.getUUID());
			referenceDetail.put("name", workflow.get_Translation(I_AD_Workflow.COLUMNNAME_Name, getLanguage()));
			referenceDetail.put("description", workflow.get_Translation(I_AD_Workflow.COLUMNNAME_Description, getLanguage()));
			referenceDetail.put("help", workflow.get_Translation(I_AD_Workflow.COLUMNNAME_Help, getLanguage()));
			documentDetail.put("workflow", referenceDetail);
		}
		//	Parameters
		List<MProcessPara> parameters = process.getParametersAsList();
		List<Map<String, Object>> parametersDetail = new ArrayList<>();
		if(parameters != null) {
			parameters.forEach(parameter -> {
				Map<String, Object> detail = new HashMap<>();
				detail.put("id", parameter.getAD_Process_Para_ID());
				detail.put("uuid", parameter.getUUID());
				detail.put("name", parameter.get_Translation(I_AD_Process_Para.COLUMNNAME_Name, getLanguage()));
				detail.put("description", parameter.get_Translation(I_AD_Process_Para.COLUMNNAME_Description, getLanguage()));
				detail.put("help", parameter.get_Translation(I_AD_Process_Para.COLUMNNAME_Help, getLanguage()));
				detail.put("column_name", parameter.getColumnName());
				detail.put("element_id", parameter.getAD_Element_ID());
				detail.put("default_value", parameter.getDefaultValue());
				detail.put("default_value_to", parameter.getDefaultValue2());
				detail.put("is_range", parameter.isRange());
				detail.put("is_info_only", parameter.isInfoOnly());
				detail.put("is_mandatory", parameter.isMandatory());
				detail.put("display_logic", parameter.getDisplayLogic());
				detail.put("read_only_logic", parameter.getReadOnlyLogic());
				detail.put("sequence", parameter.getSeqNo());
				detail.put("value_format", parameter.getVFormat());
				detail.put("min_value", parameter.getValueMin());
				detail.put("max_value", parameter.getValueMax());
				detail.put("reference_id", parameter.getAD_Reference_ID());
				detail.put("reference_value_id", parameter.getAD_Reference_Value_ID());
				detail.put("validation_id", parameter.getAD_Val_Rule_ID());
				detail.put("display_type", parameter.getAD_Reference_ID());
				String embeddedContextColumn = null;
				ReferenceValues referenceValues = ReferenceUtil.getReferenceDefinition(parameter.getColumnName(), parameter.getAD_Reference_ID(), parameter.getAD_Reference_Value_ID(), parameter.getAD_Val_Rule_ID());
				if(referenceValues != null) {
//					Map<String, Object> referenceDetail = new HashMap<>();
//					referenceDetail.put("id", referenceValues.getReferenceId());
//					referenceDetail.put("table_name", referenceValues.getTableName());
//					detail.put("display_type", referenceDetail);
					embeddedContextColumn = referenceValues.getEmbeddedContextColumn();
				}
				detail.put("context_column_names", ReferenceUtil.getContextColumnNames(Optional.ofNullable(parameter.getDefaultValue()).orElse("")
						+ Optional.ofNullable(parameter.getDefaultValue2()).orElse("")
						+ Optional.ofNullable(parameter.getDisplayLogic()).orElse("")
						+ Optional.ofNullable(parameter.getReadOnlyLogic()).orElse("")
						+ Optional.ofNullable(embeddedContextColumn).orElse("")));
				detail.put("dependent_fields", DependenceUtil.generateDependentProcessParameters(parameter));
				parametersDetail.add(detail);
			});
		}
		documentDetail.put("parameters", parametersDetail);
		documentDetail.put("has_parameters", parametersDetail.size() > 0);
		putDocument(documentDetail);
		return this;
	}
	
	private Process() {
		super();
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
