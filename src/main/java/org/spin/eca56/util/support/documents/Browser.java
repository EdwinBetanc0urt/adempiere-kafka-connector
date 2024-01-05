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
import org.adempiere.core.domains.models.I_AD_Browse_Field;
import org.adempiere.core.domains.models.I_AD_Process;
import org.adempiere.core.domains.models.I_AD_Table;
import org.adempiere.core.domains.models.I_AD_Window;
import org.adempiere.model.MBrowse;
import org.adempiere.model.MBrowseField;
import org.adempiere.model.MViewColumn;
import org.compiere.model.MProcess;
import org.compiere.model.MTable;
import org.compiere.model.MWindow;
import org.compiere.model.PO;
import org.spin.eca56.util.support.DictionaryDocument;

/**
 * 	the document class for Browse senders
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class Browser extends DictionaryDocument {

	//	Some default documents key
	public static final String KEY = "new";
	public static final String CHANNEL = "browser";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public DictionaryDocument withEntity(PO entity) {
		MBrowse browser = (MBrowse) entity;
		Map<String, Object> documentDetail = new HashMap<>();
		documentDetail.put("id", browser.getAD_Process_ID());
		documentDetail.put("uuid", browser.getUUID());
		documentDetail.put("code", browser.getValue());
		documentDetail.put("name", browser.get_Translation(I_AD_Browse.COLUMNNAME_Name, getLanguage()));
		documentDetail.put("description", browser.get_Translation(I_AD_Browse.COLUMNNAME_Description, getLanguage()));
		documentDetail.put("help", browser.get_Translation(I_AD_Browse.COLUMNNAME_Help, getLanguage()));
		documentDetail.put("is_collapsible_by_default", browser.isCollapsibleByDefault());
		documentDetail.put("is_deleteable", browser.isDeleteable());
		documentDetail.put("is_execute_query_by_default", browser.isExecutedQueryByDefault());
		documentDetail.put("is_selected_by_default", browser.isSelectedByDefault());
		documentDetail.put("is_show_total", browser.isShowTotal());
		documentDetail.put("is_updateable", browser.isUpdateable());
		if(browser.getAD_Process_ID() > 0) {
			MProcess process = MProcess.get(browser.getCtx(), browser.getAD_Process_ID());
			Map<String, Object> referenceDetail = new HashMap<>();
			referenceDetail.put("id", process.getAD_Process_ID());
			referenceDetail.put("uuid", process.getUUID());
			referenceDetail.put("name", process.get_Translation(I_AD_Process.COLUMNNAME_Name, getLanguage()));
			referenceDetail.put("description", process.get_Translation(I_AD_Process.COLUMNNAME_Description, getLanguage()));
			referenceDetail.put("help", process.get_Translation(I_AD_Process.COLUMNNAME_Help, getLanguage()));
			documentDetail.put("process", referenceDetail);
		}
		if(browser.getAD_Window_ID() > 0) {
			MWindow window = MWindow.get(browser.getCtx(), browser.getAD_Window_ID());
			Map<String, Object> referenceDetail = new HashMap<>();
			referenceDetail.put("id", window.getAD_Window_ID());
			referenceDetail.put("uuid", window.getUUID());
			referenceDetail.put("name", window.get_Translation(I_AD_Window.COLUMNNAME_Name, getLanguage()));
			referenceDetail.put("description", window.get_Translation(I_AD_Window.COLUMNNAME_Description, getLanguage()));
			referenceDetail.put("help", window.get_Translation(I_AD_Window.COLUMNNAME_Help, getLanguage()));
			documentDetail.put("window", referenceDetail);
		}
		if(browser.getAD_Table_ID() > 0) {
			MTable table = MTable.get(browser.getCtx(), browser.getAD_Table_ID());
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
			documentDetail.put("table", referenceDetail);
		}
		documentDetail.put("display_fields", convertFields(browser.getDisplayFields()));
		documentDetail.put("criteria_fields", convertFields(browser.getCriteriaFields()));
		documentDetail.put("identifier_fields", convertFields(browser.getIdentifierFields()));
		documentDetail.put("order_fields", convertFields(browser.getOrderByFields()));
		documentDetail.put("editable_fields", convertFields(browser.getNotReadOnlyFields()));
		putDocument(documentDetail);
		return this;
	}
	
	private List<Map<String, Object>> convertFields(List<MBrowseField> fields) {
		List<Map<String, Object>> fieldsDetail = new ArrayList<>();
		if(fields == null) {
			return fieldsDetail;
		}
		fields.forEach(field -> {
			fieldsDetail.add(parseField(field));
		});
		return fieldsDetail;
	}
	
	private Map<String, Object> parseField(MBrowseField field) {
		Map<String, Object> detail = new HashMap<>();
		MViewColumn viewColumn = MViewColumn.getById(field.getCtx(), field.getAD_View_Column_ID(), null);
		String columnName = viewColumn.getColumnName();
		detail.put("id", field.getAD_Browse_Field_ID());
		detail.put("uuid", field.getUUID());
		detail.put("name", field.get_Translation(I_AD_Browse_Field.COLUMNNAME_Name, getLanguage()));
		detail.put("description", field.get_Translation(I_AD_Browse_Field.COLUMNNAME_Description, getLanguage()));
		detail.put("help", field.get_Translation(I_AD_Browse_Field.COLUMNNAME_Help, getLanguage()));
		detail.put("column_name", columnName);
		detail.put("element_id", field.getAD_Element_ID());
		detail.put("default_value", field.getDefaultValue());
		detail.put("default_value_to", field.getDefaultValue2());
		detail.put("is_range", field.isRange());
		detail.put("is_info_only", field.isInfoOnly());
		detail.put("is_mandatory", field.isMandatory());
		detail.put("display_logic", field.getDisplayLogic());
		detail.put("read_only_logic", field.getReadOnlyLogic());
		detail.put("sequence", field.getSeqNo());
		detail.put("value_format", field.getVFormat());
		detail.put("min_value", field.getValueMin());
		detail.put("max_value", field.getValueMax());
		detail.put("reference_id", field.getAD_Reference_ID());
		detail.put("reference_value_id", field.getAD_Reference_Value_ID());
		detail.put("validation_id", field.getAD_Val_Rule_ID());
		detail.put("display_type", field.getAD_Reference_ID());
		String embeddedContextColumn = null;
		ReferenceValues referenceValues = ReferenceUtil.getReferenceDefinition(columnName, field.getAD_Reference_ID(), field.getAD_Reference_Value_ID(), field.getAD_Val_Rule_ID());
		if(referenceValues != null) {
//			Map<String, Object> referenceDetail = new HashMap<>();
//			referenceDetail.put("id", referenceValues.getReferenceId());
//			referenceDetail.put("table_name", referenceValues.getTableName());
//			detail.put("display_type", referenceDetail);
			embeddedContextColumn = referenceValues.getEmbeddedContextColumn();
		}
		detail.put("context_column_names", ReferenceUtil.getContextColumnNames(Optional.ofNullable(field.getDefaultValue()).orElse("")
				+ Optional.ofNullable(field.getDefaultValue2()).orElse("")
				+ Optional.ofNullable(field.getDisplayLogic()).orElse("")
				+ Optional.ofNullable(field.getReadOnlyLogic()).orElse("")
				+ Optional.ofNullable(embeddedContextColumn).orElse("")));
		detail.put("dependent_fields", DependenceUtil.generateDependentBrowseFields(field));
		return detail;
	}
	
	private Browser() {
		super();
	}
	
	/**
	 * Default instance
	 * @return
	 */
	public static Browser newInstance() {
		return new Browser();
	}

	@Override
	public String getChannel() {
		return CHANNEL;
	}
}
