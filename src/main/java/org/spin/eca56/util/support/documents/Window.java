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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.adempiere.core.domains.models.I_AD_Field;
import org.adempiere.core.domains.models.I_AD_Process;
import org.adempiere.core.domains.models.I_AD_Tab;
import org.adempiere.core.domains.models.I_AD_Table;
import org.adempiere.core.domains.models.I_AD_Window;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MProcess;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MWindow;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.spin.eca56.util.support.DictionaryDocument;

/**
 * 	the document class for Window senders
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class Window extends DictionaryDocument {

	//	Some default documents key
	public static final String KEY = "new";
	public static final String CHANNEL = "window";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public DictionaryDocument withEntity(PO entity) {
		MWindow window = (MWindow) entity;
		Map<String, Object> documentDetail = new HashMap<>();
		documentDetail.put("id", window.getAD_Window_ID());
		documentDetail.put("uuid", window.getUUID());
		documentDetail.put("name", window.get_Translation(I_AD_Window.COLUMNNAME_Name, getLanguage()));
		documentDetail.put("description", window.get_Translation(I_AD_Window.COLUMNNAME_Description, getLanguage()));
		documentDetail.put("help", window.get_Translation(I_AD_Window.COLUMNNAME_Help, getLanguage()));
		documentDetail.put("entity_type", window.getEntityType());
		documentDetail.put("window_type", window.getWindowType());
		documentDetail.put("is_sales_transaction", window.isSOTrx());
		//	Tabs
		documentDetail.put("tabs", convertTabs(Arrays.asList(window.getTabs(true, null))));
		putDocument(documentDetail);
		return this;
	}
	
	private List<Map<String, Object>> convertTabs(List<MTab> tabs) {
		List<Map<String, Object>> tabsDetail = new ArrayList<>();
		if(tabs == null) {
			return tabsDetail;
		}
		tabs.forEach(tab -> {
			tabsDetail.add(parseTab(tab));
		});
		return tabsDetail;
	}
	
	private Map<String, Object> parseTab(MTab tab) {
		Map<String, Object> detail = new HashMap<>();
		detail.put("id", tab.getAD_Tab_ID());
		detail.put("uuid", tab.getUUID());
		detail.put("name", tab.get_Translation(I_AD_Tab.COLUMNNAME_Name, getLanguage()));
		detail.put("description", tab.get_Translation(I_AD_Tab.COLUMNNAME_Description, getLanguage()));
		detail.put("help", tab.get_Translation(I_AD_Tab.COLUMNNAME_Help, getLanguage()));
		detail.put("commit_warning", tab.get_Translation(I_AD_Tab.COLUMNNAME_CommitWarning, getLanguage()));
		detail.put("entity_type", tab.getEntityType());
		detail.put("display_logic", tab.getDisplayLogic());
		detail.put("read_only_logic", tab.getReadOnlyLogic());
		detail.put("is_active", tab.isActive());
		detail.put("sequence", tab.getSeqNo());
		detail.put("tab_level", tab.getTabLevel());
		detail.put("is_single_row", tab.isSingleRow());
		detail.put("is_has_tree", tab.isHasTree());
		detail.put("is_sort_tab", tab.isSortTab());
		detail.put("is_advanced_tab", tab.isAdvancedTab());
		detail.put("is_info_tab", tab.isInfoTab());
		detail.put("is_translation_tab", tab.isTranslationTab());
		detail.put("is_insert_record", tab.isInsertRecord());
		detail.put("is_read_only", tab.isReadOnly());
		if(tab.getAD_Table_ID() > 0) {
			MTable table = new MTable(tab.getCtx(), tab.getAD_Table_ID(), null);
			Map<String, Object> referenceDetail = new HashMap<>();
			referenceDetail.put("id", table.getAD_Window_ID());
			referenceDetail.put("uuid", table.getUUID());
			referenceDetail.put("table_name", table.getTableName());
			referenceDetail.put("name", table.get_Translation(I_AD_Table.COLUMNNAME_Name, getLanguage()));
			referenceDetail.put("description", table.get_Translation(I_AD_Table.COLUMNNAME_Description, getLanguage()));
			referenceDetail.put("help", table.get_Translation(I_AD_Table.COLUMNNAME_Help, getLanguage()));
			referenceDetail.put("is_document", table.isDocument());
			referenceDetail.put("is_deleteable", table.isDeleteable());
			referenceDetail.put("is_view", table.isView());
			detail.put("table", referenceDetail);
		}
		if(tab.getAD_Process_ID() > 0) {
			MProcess process = MProcess.get(tab.getCtx(), tab.getAD_Process_ID());
			Map<String, Object> referenceDetail = new HashMap<>();
			referenceDetail.put("id", process.getAD_Process_ID());
			referenceDetail.put("uuid", process.getUUID());
			referenceDetail.put("name", process.get_Translation(I_AD_Process.COLUMNNAME_Name, getLanguage()));
			referenceDetail.put("description", process.get_Translation(I_AD_Process.COLUMNNAME_Description, getLanguage()));
			referenceDetail.put("help", process.get_Translation(I_AD_Process.COLUMNNAME_Help, getLanguage()));
			detail.put("process", referenceDetail);
		}
		List<MField> fields = getFieldsFromTab(tab);
		detail.put("fields", convertFields(fields));
		detail.put("row_fields", convertFields(fields.stream().filter(field -> field.isDisplayed()).collect(Collectors.toList())));
		detail.put("grid_fields", convertFields(fields.stream().filter(field -> field.isDisplayedGrid()).collect(Collectors.toList())));
		//	Processes
		detail.put("process", convertProcesses(getProcessFromTab(tab)));
		return detail;
	}
	
	private List<MField> getFieldsFromTab(MTab tab) {
		return new Query(tab.getCtx(), I_AD_Field.Table_Name, I_AD_Field.COLUMNNAME_AD_Tab_ID + " = ?", null)
				.setParameters(tab.getAD_Tab_ID())
				.list();
	}
	
	private List<MProcess> getProcessFromTab(MTab tab) {
		return new Query(tab.getCtx(), I_AD_Process.Table_Name, "EXISTS(SELECT 1 FROM AD_Table_Process tp WHERE tp.AD_Process_ID = AD_Process.AD_Process_ID AND tp.AD_Table_ID = ?)", null)
				.setParameters(tab.getAD_Table_ID())
				.list();
	}
	
	private List<Map<String, Object>> convertFields(List<MField> fields) {
		List<Map<String, Object>> fieldsDetail = new ArrayList<>();
		if(fields == null) {
			return fieldsDetail;
		}
		fields.forEach(field -> {
			fieldsDetail.add(parseField(field));
		});
		return fieldsDetail;
	}
	
	private List<Map<String, Object>> convertProcesses(List<MProcess> process) {
		List<Map<String, Object>> processesDetail = new ArrayList<>();
		if(process == null) {
			return processesDetail;
		}
		process.forEach(field -> {
			processesDetail.add(parseProcess(field));
		});
		return processesDetail;
	}
	
	private Map<String, Object> parseProcess(MProcess process) {
		Map<String, Object> detail = new HashMap<>();
		detail.put("id", process.getAD_Process_ID());
		detail.put("uuid", process.getUUID());
		detail.put("name", process.get_Translation(I_AD_Process.COLUMNNAME_Name, getLanguage()));
		detail.put("description", process.get_Translation(I_AD_Process.COLUMNNAME_Description, getLanguage()));
		detail.put("help", process.get_Translation(I_AD_Process.COLUMNNAME_Help, getLanguage()));
		return detail;
	}
	
	private Map<String, Object> parseField(MField field) {
		MColumn column = MColumn.get(field.getCtx(), field.getAD_Column_ID());
		Map<String, Object> detail = new HashMap<>();
		detail.put("id", field.getAD_Field_ID());
		detail.put("uuid", field.getUUID());
		detail.put("name", field.get_Translation(I_AD_Field.COLUMNNAME_Name, getLanguage()));
		detail.put("description", field.get_Translation(I_AD_Field.COLUMNNAME_Description, getLanguage()));
		detail.put("help", field.get_Translation(I_AD_Field.COLUMNNAME_Help, getLanguage()));
		detail.put("entity_type", field.getEntityType());
		detail.put("column_name", column.getColumnName());
		detail.put("default_value", Optional.ofNullable(field.getDefaultValue()).orElse(column.getDefaultValue()));
		detail.put("display_logic", field.getDisplayLogic());
		detail.put("read_only_logic", column.getReadOnlyLogic());
		detail.put("mandatory_logic", column.getMandatoryLogic());
		detail.put("is_mandatory", (field.getIsMandatory() != null && field.getIsMandatory().equals("Y")? true: column.isMandatory()));
		detail.put("sequence", field.getSeqNo());
		detail.put("grid_sequence", field.getSeqNoGrid());
		detail.put("reference_id", field.getAD_Reference_ID());
		String embeddedContextColumn = null;
		int referenceId = field.getAD_Reference_ID();
		if(referenceId <= 0) {
			referenceId = column.getAD_Reference_ID();
		}
		int referenceValueId = field.getAD_Reference_Value_ID();
		if(referenceValueId <= 0) {
			referenceValueId = column.getAD_Reference_Value_ID();
		}
		int validationRuleId = field.getAD_Val_Rule_ID();
		if(validationRuleId <= 0) {
			validationRuleId = column.getAD_Val_Rule_ID();
		}
		ReferenceValues referenceValues = ReferenceUtil.getReferenceDefinition(column.getColumnName(), referenceId, referenceValueId, validationRuleId);
		if(referenceValues != null) {
			Map<String, Object> referenceDetail = new HashMap<>();
			referenceDetail.put("id", referenceValues.getReferenceId());
			referenceDetail.put("table_name", referenceValues.getTableName());
			detail.put("display_type", referenceDetail);
			embeddedContextColumn = referenceValues.getEmbeddedContextColumn();
		}
		detail.put("context_column_names", ReferenceUtil.getContextColumnNames(Optional.ofNullable(field.getDefaultValue()).orElse(column.getDefaultValue())
				+ Optional.ofNullable(field.getDisplayLogic()).orElse("")
				+ Optional.ofNullable(column.getMandatoryLogic()).orElse("")
				+ Optional.ofNullable(column.getReadOnlyLogic()).orElse("")
				+ Optional.ofNullable(embeddedContextColumn).orElse("")));
		detail.put("reference_value_id", field.getAD_Reference_Value_ID());
		detail.put("validation_id", field.getAD_Val_Rule_ID());
		detail.put("dependent_fields", DependenceUtil.generateDependentWindowFields(field));
		return detail;
	}
	
	private Window() {
		super();
	}
	
	/**
	 * Default instance
	 * @return
	 */
	public static Window newInstance() {
		return new Window();
	}

	@Override
	public String getChannel() {
		return CHANNEL;
	}
}
