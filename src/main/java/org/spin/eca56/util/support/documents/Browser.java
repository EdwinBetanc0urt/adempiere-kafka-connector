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
import org.adempiere.core.domains.models.I_AD_Element;
import org.adempiere.model.MBrowse;
import org.adempiere.model.MBrowseField;
import org.adempiere.model.MViewColumn;
import org.compiere.model.MColumn;
import org.compiere.model.MProcess;
import org.compiere.model.MTable;
import org.compiere.model.MWindow;
import org.compiere.model.PO;
import org.compiere.util.Util;
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

	private Map<String, Object> parseDictionaryEntity(PO entity) {
		Map<String, Object> documentEntity = new HashMap<>();
		documentEntity.put("id", entity.get_ID());
		documentEntity.put("uuid", entity.get_UUID());
		documentEntity.put("name", entity.get_Translation(I_AD_Element.COLUMNNAME_Name, getLanguage()));
		documentEntity.put("description", entity.get_Translation(I_AD_Element.COLUMNNAME_Description, getLanguage()));
		documentEntity.put("help", entity.get_Translation(I_AD_Element.COLUMNNAME_Help, getLanguage()));
		return documentEntity;
	}

	@Override
	public DictionaryDocument withEntity(PO entity) {
		MBrowse browser = (MBrowse) entity;
		Map<String, Object> documentDetail = new HashMap<>();
		documentDetail.put("id", browser.getAD_Browse_ID());
		documentDetail.put("uuid", browser.getUUID());
		documentDetail.put("code", browser.getValue());
		documentDetail.put("name", browser.get_Translation(I_AD_Browse.COLUMNNAME_Name, getLanguage()));
		documentDetail.put("description", browser.get_Translation(I_AD_Browse.COLUMNNAME_Description, getLanguage()));
		documentDetail.put("help", browser.get_Translation(I_AD_Browse.COLUMNNAME_Help, getLanguage()));
		documentDetail.put("is_execute_query_by_default", browser.isExecutedQueryByDefault());
		documentDetail.put("is_collapsible_by_default", browser.isCollapsibleByDefault());
		documentDetail.put("is_selected_by_default", browser.isSelectedByDefault());
		documentDetail.put("is_show_total", browser.isShowTotal());
		
		MBrowseField fieldKey = browser.getFieldKey();
		if (fieldKey != null && fieldKey.getAD_Browse_Field_ID() > 0) {
			MViewColumn viewColumn = MViewColumn.getById(browser.getCtx(), fieldKey.getAD_View_Column_ID(), null);
			documentDetail.put("field_key", viewColumn.getColumnName());
		}

		// Record Attributes
		documentDetail.put("access_level", browser.getAccessLevel());
		documentDetail.put("is_updateable", browser.isUpdateable());
		documentDetail.put("is_deleteable", browser.isUpdateable());
		if(browser.getAD_Table_ID() > 0) {
			MTable table = MTable.get(browser.getCtx(), browser.getAD_Table_ID());
			documentDetail.put("table_name", table.getTableName());
		}

		// External Reference
		documentDetail.put("process_id", browser.getAD_Process_ID());
		if(browser.getAD_Process_ID() > 0) {
			MProcess process = MProcess.get(browser.getCtx(), browser.getAD_Process_ID());
			Map<String, Object> referenceDetail = parseDictionaryEntity(process);
			documentDetail.put("process", referenceDetail);
		}

		documentDetail.put("window_id", browser.getAD_Window_ID());
		if(browser.getAD_Window_ID() > 0) {
			MWindow window = MWindow.get(browser.getCtx(), browser.getAD_Window_ID());
			Map<String, Object> referenceDetail = parseDictionaryEntity(window);
			documentDetail.put("window", referenceDetail);
		}
		documentDetail.put("context_column_names", ReferenceUtil.getContextColumnNames(
				Optional.ofNullable(browser.getWhereClause()).orElse("")
			)
		);

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

		detail.put("id", field.getAD_Browse_Field_ID());
		detail.put("uuid", field.getUUID());
		detail.put("name", field.get_Translation(I_AD_Browse_Field.COLUMNNAME_Name, getLanguage()));
		detail.put("description", field.get_Translation(I_AD_Browse_Field.COLUMNNAME_Description, getLanguage()));
		detail.put("help", field.get_Translation(I_AD_Browse_Field.COLUMNNAME_Help, getLanguage()));
		detail.put("display_type", field.getAD_Reference_ID());
		detail.put("callout", field.getCallout());

		//
		detail.put("is_order_by", field.isOrderBy());
		detail.put("sort_sequence", field.getSortNo());
		detail.put("is_key", field.isKey());
		detail.put("is_identifier", field.isIdentifier());

		MViewColumn viewColumn = MViewColumn.getById(field.getCtx(), field.getAD_View_Column_ID(), null);
		String columnName = viewColumn.getColumnName();
		detail.put("column_name", columnName);

		//	Value Properties
		detail.put("is_range", field.isRange());
		detail.put("default_value", field.getDefaultValue());
		detail.put("default_value_to", field.getDefaultValue2());
		detail.put("value_format", field.getVFormat());
		detail.put("min_value", field.getValueMin());
		detail.put("max_value", field.getValueMax());

		//	Display Properties
		detail.put("is_displayed", field.isDisplayed());
		detail.put("is_query_criteria", field.isQueryCriteria());
		detail.put("display_logic", field.getDisplayLogic());
		detail.put("sequence", field.getSeqNo());
		detail.put("grid_sequence", field.getSeqNoGrid());
		//	Custom display
		detail.put("is_displayed_as_panel", field.isQueryCriteria() ? "Y" : "N");
		detail.put("is_displayed_as_table", field.isDisplayed() ? 'Y' : 'N');

		//	Editable Properties
		detail.put("is_read_only", field.isReadOnly());
		detail.put("read_only_logic", field.getReadOnlyLogic());
		detail.put("is_info_only", field.isInfoOnly());
		
		//	Mandatory Properties
		detail.put("is_mandatory", field.isMandatory());

		//	External Info
		String elementName = null;
		if(viewColumn.getAD_Column_ID() > 0) {
			MColumn column = MColumn.get(field.getCtx(), viewColumn.getAD_Column_ID());
			elementName = column.getColumnName();
		}
		if(Util.isEmpty(elementName)) {
			elementName = field.getAD_Element().getColumnName();
		}
		detail.put("element_name", elementName);
		ReferenceValues referenceValues = ReferenceUtil.getReferenceDefinition(
			elementName,
			field.getAD_Reference_ID(),
			field.getAD_Reference_Value_ID(),
			field.getAD_Val_Rule_ID()
		);
		if(referenceValues != null) {
			Map<String, Object> referenceDetail = new HashMap<>();
			// referenceDetail.put("id", referenceValues.getReferenceId());
			referenceDetail.put("table_name", referenceValues.getTableName());
			referenceDetail.put("context_column_names", ReferenceUtil.getContextColumnNames(
					referenceValues.getEmbeddedContextColumn()
				)
			);
			detail.put("reference", referenceDetail);
		}
		detail.put("context_column_names", ReferenceUtil.getContextColumnNames(
				Optional.ofNullable(field.getDefaultValue()).orElse("")
				+ Optional.ofNullable(field.getDefaultValue2()).orElse("")
			)
		);
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
