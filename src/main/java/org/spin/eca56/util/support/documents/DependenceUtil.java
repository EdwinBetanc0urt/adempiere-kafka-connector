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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.adempiere.model.MBrowse;
import org.adempiere.model.MBrowseField;
import org.adempiere.model.MViewColumn;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MProcess;
import org.compiere.model.MProcessPara;
import org.compiere.model.MTab;
import org.compiere.model.MValRule;
import org.compiere.model.MWindow;
import org.compiere.util.Env;
import org.compiere.util.Util;

/**
 * 	The util class for all documents
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class DependenceUtil {
	
	/**
	 * Determinate if columnName is used on context values
	 * @param columnName
	 * @param context
	 * @return boolean
	 */
	public static boolean isUseParentColumnOnContext(String columnName, String context) {
		if (Util.isEmpty(columnName, true)) {
			return false;
		}
		if (Util.isEmpty(context, true)) {
			return false;
		}

		// @ColumnName@ , @#ColumnName@ , @$ColumnName@
		StringBuffer patternValue = new StringBuffer()
			.append("@")
			.append("($|#){0,1}")
			.append(columnName)
			.append("(@)")
		;

		Pattern pattern = Pattern.compile(
			patternValue.toString(),
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL
		);
		Matcher matcher = pattern.matcher(context);
		boolean isUsedParentColumn = matcher.find();

		// TODO: Delete this condition when fix evaluator (readonlyLogic on Client Info)
		// TODO: https://github.com/adempiere/adempiere/pull/4124
		if (!isUsedParentColumn) {
			// @ColumnName , @#ColumnName , @$ColumnName
			patternValue.append("{0,1}");
			Pattern pattern2 = Pattern.compile(
				patternValue.toString(),
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			);
			Matcher matcher2 = pattern2.matcher(context);
			isUsedParentColumn = matcher2.find();
		}

		return isUsedParentColumn;
	}
	
	public static List<Map<String, Object>> generateDependentProcessParameters(MProcessPara processParameter) {
		List<Map<String, Object>> depenentFieldsList = new ArrayList<>();
		if (processParameter == null) {
			return depenentFieldsList;
		}

		String parentColumnName = processParameter.getColumnName();

		MProcess process = MProcess.get(processParameter.getCtx(), processParameter.getAD_Process_ID());
		List<MProcessPara> parametersList = process.getParametersAsList();
		if (parametersList == null || parametersList.isEmpty()) {
			return depenentFieldsList;
		}

		parametersList.parallelStream()
			.filter(currentParameter -> {
				if (!currentParameter.isActive()) {
					return false;
				}
				// Display Logic
				if (isUseParentColumnOnContext(parentColumnName, currentParameter.getDisplayLogic())) {
					return true;
				}
				// Default Value of Column
				if (isUseParentColumnOnContext(parentColumnName, currentParameter.getDefaultValue())) {
					return true;
				}
				// ReadOnly Logic
				if (isUseParentColumnOnContext(parentColumnName, currentParameter.getReadOnlyLogic())) {
					return true;
				}
				// Dynamic Validation
				if (currentParameter.getAD_Val_Rule_ID() > 0) {
					MValRule validationRule = MValRule.get(Env.getCtx(), currentParameter.getAD_Val_Rule_ID());
					if (isUseParentColumnOnContext(parentColumnName, validationRule.getCode())) {
						return true;
					}
				}
				return false;
			})
			.forEach(currentParameter -> {
				Map<String, Object> detail = new HashMap<>();
				detail.put("id", currentParameter.getAD_Process_Para_ID());
				detail.put("uuid", currentParameter.getUUID());
				detail.put("column_name", currentParameter.getColumnName());
				//	Process
				detail.put("parent_id", process.getAD_Process_ID());
				detail.put("parent_uuid", process.getUUID());
				detail.put("parent_name", process.getName());
				depenentFieldsList.add(detail);
			});

		return depenentFieldsList;
	}
	
	public static List<Map<String, Object>> generateDependentWindowFields(MField field) {
		List<Map<String, Object>> depenentFieldsList = new ArrayList<>();
		if (field == null) {
			return depenentFieldsList;
		}
		String parentColumnName = MColumn.getColumnName(field.getCtx(), field.getAD_Column_ID());
		MTab parentTab = MTab.get(field.getCtx(), field.getAD_Tab_ID());
		List<MTab> tabsList = Arrays.asList(MWindow.get(field.getCtx(), parentTab.getAD_Window_ID()).getTabs(false, null));
		if (tabsList == null || tabsList.isEmpty()) {
			return depenentFieldsList;
		}
		tabsList.parallelStream()
			.filter(currentTab -> {
				// transaltion tab is not rendering on client
				return currentTab.isActive() && !currentTab.isTranslationTab() && !currentTab.isSortTab();
			})
			.forEach(tab -> {
				List<MField> fieldsList = Arrays.asList(tab.getFields(false, null));
				if (fieldsList == null || fieldsList.isEmpty()) {
					return;
				}

				fieldsList.parallelStream()
					.filter(currentField -> {
						if (!currentField.isActive()) {
							return false;
						}
						// Display Logic
						if (isUseParentColumnOnContext(parentColumnName, currentField.getDisplayLogic())) {
							return true;
						}
						// Default Value of Field
						if (isUseParentColumnOnContext(parentColumnName, currentField.getDefaultValue())) {
							return true;
						}
						// Dynamic Validation
						if (currentField.getAD_Val_Rule_ID() > 0) {
							MValRule validationRule = MValRule.get(Env.getCtx(), currentField.getAD_Val_Rule_ID());
							if (isUseParentColumnOnContext(parentColumnName, validationRule.getCode())) {
								return true;
							}
						}

						MColumn currentColumn = MColumn.get(Env.getCtx(), currentField.getAD_Column_ID());
						// Default Value of Column
						if (isUseParentColumnOnContext(parentColumnName, currentColumn.getDefaultValue())) {
							return true;
						}
						// ReadOnly Logic
						if (isUseParentColumnOnContext(parentColumnName, currentColumn.getReadOnlyLogic())) {
							return true;
						}
						// Mandatory Logic
						if (isUseParentColumnOnContext(parentColumnName, currentColumn.getMandatoryLogic())) {
							return true;
						}
						// Dynamic Validation
						if (currentColumn.getAD_Val_Rule_ID() > 0) {
							MValRule validationRule = MValRule.get(Env.getCtx(), currentColumn.getAD_Val_Rule_ID());
							if (isUseParentColumnOnContext(parentColumnName, validationRule.getCode())) {
								return true;
							}
						}
						return false;
					})
					.forEach(currentField -> {
						String currentColumnName = MColumn.getColumnName(Env.getCtx(), currentField.getAD_Column_ID());
						Map<String, Object> detail = new HashMap<>();
						detail.put("id", currentField.getAD_Field_ID());
						detail.put("uuid", currentField.getUUID());
						detail.put("column_name", currentColumnName);
						//	Tab
						detail.put("parent_id", tab.getAD_Tab_ID());
						detail.put("parent_uuid", tab.getUUID());
						detail.put("parent_name", tab.getName());
						depenentFieldsList.add(detail);
					});
			});
		return depenentFieldsList;
	}
	
	public static List<Map<String, Object>> generateDependentBrowseFields(MBrowseField browseField) {
		List<Map<String, Object>> depenentFieldsList = new ArrayList<>();
		if (browseField == null) {
			return depenentFieldsList;
		}

		MViewColumn viewColumn = MViewColumn.getById(Env.getCtx(), browseField.getAD_View_Column_ID(), null);
		String parentColumnName = viewColumn.getColumnName();

		String elementName = null;
		if(viewColumn.getAD_Column_ID() != 0) {
			MColumn column = MColumn.get(Env.getCtx(), viewColumn.getAD_Column_ID());
			elementName = column.getColumnName();
		}
		if(Util.isEmpty(elementName, true)) {
			elementName = browseField.getAD_Element().getColumnName();
		}
		String parentElementName = elementName;

		MBrowse browse = MBrowse.get(browseField.getCtx(), browseField.getAD_Browse_ID());
		List<MBrowseField> browseFieldsList = browse.getFields();
		if (browseFieldsList == null || browseFieldsList.isEmpty()) {
			return depenentFieldsList;
		}

		browseFieldsList.parallelStream()
			.filter(currentBrowseField -> {
				if(!currentBrowseField.isActive()) {
					return false;
				}
				// Display Logic
				if (isUseParentColumnOnContext(parentColumnName, currentBrowseField.getDisplayLogic())
					|| isUseParentColumnOnContext(parentElementName, currentBrowseField.getDisplayLogic())) {
					return true;
				}
				// Default Value
				if (isUseParentColumnOnContext(parentColumnName, currentBrowseField.getDefaultValue())
					|| isUseParentColumnOnContext(parentElementName, currentBrowseField.getDefaultValue())) {
					return true;
				}
				// Default Value 2
				if (isUseParentColumnOnContext(parentColumnName, currentBrowseField.getDefaultValue2())
					|| isUseParentColumnOnContext(parentElementName, currentBrowseField.getDefaultValue2())) {
					return true;
				}
				// ReadOnly Logic
				if (isUseParentColumnOnContext(parentColumnName, currentBrowseField.getReadOnlyLogic())
					|| isUseParentColumnOnContext(parentElementName, currentBrowseField.getReadOnlyLogic())) {
					return true;
				}
				// Dynamic Validation
				if (currentBrowseField.getAD_Val_Rule_ID() > 0) {
					MValRule validationRule = MValRule.get(Env.getCtx(), currentBrowseField.getAD_Val_Rule_ID());
					if (isUseParentColumnOnContext(parentColumnName, validationRule.getCode())
						|| isUseParentColumnOnContext(parentElementName, validationRule.getCode())) {
						return true;
					}
				}
				return false;
			})
			.forEach(currentBrowseField -> {
				MViewColumn currentViewColumn = MViewColumn.getById(Env.getCtx(), currentBrowseField.getAD_View_Column_ID(), null);
				Map<String, Object> detail = new HashMap<>();
				detail.put("id", currentBrowseField.getAD_Browse_Field_ID());
				detail.put("uuid", currentBrowseField.getUUID());
				detail.put("column_name", currentViewColumn.getColumnName());
				//	Browse
				detail.put("parent_id", browse.getAD_Browse_ID());
				detail.put("parent_uuid", browse.getUUID());
				detail.put("parent_name", browse.getName());
				depenentFieldsList.add(detail);
			});

		return depenentFieldsList;
	}
}
