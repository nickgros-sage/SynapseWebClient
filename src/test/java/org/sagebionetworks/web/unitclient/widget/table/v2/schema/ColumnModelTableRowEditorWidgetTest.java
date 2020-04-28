package org.sagebionetworks.web.unitclient.widget.table.v2.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sagebionetworks.web.client.widget.table.v2.schema.ColumnFacetTypeViewEnum.None;
import static org.sagebionetworks.web.client.widget.table.v2.schema.ColumnFacetTypeViewEnum.Range;
import static org.sagebionetworks.web.client.widget.table.v2.schema.ColumnFacetTypeViewEnum.Values;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.repo.model.table.ColumnModel;
import org.sagebionetworks.repo.model.table.ColumnType;
import org.sagebionetworks.web.client.widget.table.v2.results.cell.CellEditor;
import org.sagebionetworks.web.client.widget.table.v2.results.cell.CellFactory;
import org.sagebionetworks.web.client.widget.table.v2.schema.ColumnModelTableRowEditorView;
import org.sagebionetworks.web.client.widget.table.v2.schema.ColumnModelTableRowEditorWidgetImpl;
import org.sagebionetworks.web.client.widget.table.v2.schema.ColumnTypeViewEnum;


public class ColumnModelTableRowEditorWidgetTest {

	CellFactory mockFactory;
	ColumnModelTableRowEditorView mockView;
	ColumnModelTableRowEditorWidgetImpl editor;
	ColumnModel columnModel;
	CellEditor mockStringEditor;
	CellEditor mockLinkEditor;
	CellEditor mockBooleanEditor;

	@Before
	public void before() {
		mockView = Mockito.mock(ColumnModelTableRowEditorView.class);
		mockFactory = Mockito.mock(CellFactory.class);
		mockStringEditor = Mockito.mock(CellEditor.class);
		mockLinkEditor = Mockito.mock(CellEditor.class);
		mockBooleanEditor = Mockito.mock(CellEditor.class);
		ColumnModel cm = new ColumnModel();
		cm.setColumnType(ColumnType.STRING);
		when(mockFactory.createEditor(cm)).thenReturn(mockStringEditor);
		cm = new ColumnModel();
		cm.setColumnType(ColumnType.LINK);
		when(mockFactory.createEditor(cm)).thenReturn(mockLinkEditor);
		cm = new ColumnModel();
		cm.setColumnType(ColumnType.BOOLEAN);
		when(mockFactory.createEditor(cm)).thenReturn(mockBooleanEditor);
		when(mockView.getColumnType()).thenReturn(ColumnTypeViewEnum.String);
		when(mockView.getMaxSize()).thenReturn("15");
		editor = new ColumnModelTableRowEditorWidgetImpl(mockView, mockFactory);
		columnModel = new ColumnModel();
		columnModel.setColumnType(ColumnType.STRING);
		columnModel.setMaximumSize(15L);
	}

	@Test
	public void testConfigure() {
		editor.configure(columnModel, null);
		verify(mockView).setSizeFieldVisible(true);
		verify(mockView).setMaxSize("" + ColumnModelTableRowEditorWidgetImpl.DEFAULT_STRING_SIZE);
		verify(mockView).setDefaultEditor(mockStringEditor);
		verify(mockView).setColumnType(ColumnTypeViewEnum.String);
		verify(mockView).setRestrictValuesVisible(true);
	}

	@Test
	public void testTypeChange() {
		editor.configure(columnModel, null);
		reset(mockView);
		// Change from a string to a boolean
		when(mockView.getColumnType()).thenReturn(ColumnTypeViewEnum.Boolean);
		editor.onTypeChanged();
		verify(mockView, times(1)).setSizeFieldVisible(false);
		verify(mockView, times(1)).setMaxSize(null);
		verify(mockView).setDefaultEditor(mockBooleanEditor);
		verify(mockView).setRestrictValuesVisible(false);

		// Now toggle it back to a string
		when(mockView.getColumnType()).thenReturn(ColumnTypeViewEnum.String);
		editor.onTypeChanged();
		verify(mockView, times(1)).setSizeFieldVisible(true);
		// It should keep the original value
		verify(mockView, times(1)).setMaxSize("" + ColumnModelTableRowEditorWidgetImpl.DEFAULT_STRING_SIZE);
		verify(mockView).setDefaultEditor(mockStringEditor);
		verify(mockView).setRestrictValuesVisible(true);
	}

	@Test
	public void testChangeToLink() {
		editor.configure(columnModel, null);
		reset(mockView);
		// Change from a string to a boolean
		when(mockView.getColumnType()).thenReturn(ColumnTypeViewEnum.Link);
		editor.onTypeChanged();
		verify(mockView, times(1)).setSizeFieldVisible(true);
		// It should keep the original value
		verify(mockView, times(1)).setMaxSize("" + ColumnModelTableRowEditorWidgetImpl.MAX_STRING_SIZE);
		verify(mockView).setDefaultEditor(mockLinkEditor);
		verify(mockView).setRestrictValuesVisible(false);
	}

	@Test
	public void testNoChange() {
		editor.configure(columnModel, null);
		reset(mockView);
		// The type starts as a string so toggle should do nothing
		when(mockView.getColumnType()).thenReturn(ColumnTypeViewEnum.String);
		editor.onTypeChanged();
		verify(mockView, never()).setSizeFieldVisible(anyBoolean());
		// It should keep the original value
		verify(mockView, never()).setMaxSize(anyString());
	}

	@Test
	public void testCanHaveSize() {
		Set<ColumnTypeViewEnum> expectedTrue = new HashSet<ColumnTypeViewEnum>(Arrays.asList(ColumnTypeViewEnum.String, ColumnTypeViewEnum.StringList, ColumnTypeViewEnum.Link));
		for (ColumnTypeViewEnum type : ColumnTypeViewEnum.values()) {
			assertEquals("Unexpected for type: " + type, expectedTrue.contains(type), editor.canHaveSize(type));
		}
	}

	@Test
	public void testCanHaveRestricted() {
		Set<ColumnTypeViewEnum> expectedTrue = new HashSet<ColumnTypeViewEnum>(Arrays.asList(ColumnTypeViewEnum.String, ColumnTypeViewEnum.Integer, ColumnTypeViewEnum.Entity));
		for (ColumnTypeViewEnum type : ColumnTypeViewEnum.values()) {
			assertEquals("Unexpected for type: " + type, expectedTrue.contains(type), editor.canHaveRestrictedValues(type));
		}
	}

	@Test
	public void testValidateNameNull() {
		when(mockView.getColumnName()).thenReturn(null);
		assertFalse("Name cannot be null", editor.validateName());
		verify(mockView).setNameError(ColumnModelTableRowEditorWidgetImpl.NAME_CANNOT_BE_EMPTY);
	}

	@Test
	public void testValidateNameEmpty() {
		when(mockView.getColumnName()).thenReturn("");
		assertFalse("Name cannot be null", editor.validateName());
		verify(mockView).setNameError(ColumnModelTableRowEditorWidgetImpl.NAME_CANNOT_BE_EMPTY);
	}

	@Test
	public void testValidateNameValid() {
		when(mockView.getColumnName()).thenReturn("foo");
		assertTrue(editor.validateName());
		verify(mockView).clearNameError();
	}

	@Test
	public void testValidateBadName() {
		when(mockView.getColumnType()).thenReturn(ColumnTypeViewEnum.String);
		when(mockView.getColumnName()).thenReturn("");
		when(mockView.getMaxSize()).thenReturn("50");
		when(mockView.validateDefault()).thenReturn(true);
		assertFalse(editor.validate());
	}

	// Note: Max size editor now only support numbers (no alpha characters). No longer need to test for non-number values from the editor.
//	public void testValidateBadSize() { ...

	@Test
	public void testValidateBadDefault() {
		when(mockView.getColumnType()).thenReturn(ColumnTypeViewEnum.String);
		when(mockView.getColumnName()).thenReturn("foo");
		when(mockView.getMaxSize()).thenReturn("not a numbers");
		when(mockView.validateDefault()).thenReturn(false);
		assertFalse(editor.validate());
	}

	@Test
	public void testValidateHappy() {
		when(mockView.getColumnType()).thenReturn(ColumnTypeViewEnum.String);
		when(mockView.getColumnName()).thenReturn("foo");
		when(mockView.getMaxSize()).thenReturn("50");
		when(mockView.validateDefault()).thenReturn(true);
		assertTrue(editor.validate());
	}

	@Test
	public void testConfigureFacetsForTypeString() {
		editor.configureFacetsForType(ColumnTypeViewEnum.String);
		verify(mockView).setFacetValues(None.toString(), Values.toString());
		verify(mockView).setFacetVisible(true);
	}

	@Test
	public void testConfigureFacetsForTypeInteger() {
		editor.configureFacetsForType(ColumnTypeViewEnum.Integer);
		verify(mockView).setFacetValues(None.toString(), Values.toString(), Range.toString());
		verify(mockView).setFacetVisible(true);
	}

	@Test
	public void testConfigureFacetsForTypeBoolean() {
		editor.configureFacetsForType(ColumnTypeViewEnum.Boolean);
		verify(mockView).setFacetValues(None.toString(), Values.toString());
		verify(mockView).setFacetVisible(true);
	}

	@Test
	public void testConfigureFacetsForTypeDouble() {
		editor.configureFacetsForType(ColumnTypeViewEnum.Double);
		verify(mockView).setFacetValues(None.toString(), Range.toString());
		verify(mockView).setFacetVisible(true);
	}

	@Test
	public void testConfigureFacetsForTypeEntity() {
		editor.configureFacetsForType(ColumnTypeViewEnum.Entity);
		verify(mockView).setFacetValues(None.toString(), Values.toString());
		verify(mockView).setFacetVisible(true);
	}


	@Test
	public void testConfigureFacetsForTypeDate() {
		editor.configureFacetsForType(ColumnTypeViewEnum.Date);
		verify(mockView).setFacetValues(None.toString(), Range.toString());
		verify(mockView).setFacetVisible(true);
	}

	@Test
	public void testConfigureFacetsForTypeUnsupported() {
		editor.configureFacetsForType(ColumnTypeViewEnum.LargeText);
		verify(mockView, never()).setFacetValues(anyString());
		verify(mockView).setFacetVisible(false);
	}

	@Test
	public void testCanHaveFacet() {
		assertTrue(editor.canHaveFacet(ColumnTypeViewEnum.String));
		assertTrue(editor.canHaveFacet(ColumnTypeViewEnum.Integer));
		assertTrue(editor.canHaveFacet(ColumnTypeViewEnum.Boolean));
		assertTrue(editor.canHaveFacet(ColumnTypeViewEnum.Double));
		assertTrue(editor.canHaveFacet(ColumnTypeViewEnum.Date));
		assertTrue(editor.canHaveFacet(ColumnTypeViewEnum.Entity));

		// other
		assertFalse(editor.canHaveFacet(ColumnTypeViewEnum.LargeText));
		assertFalse(editor.canHaveFacet(ColumnTypeViewEnum.Link));
	}

	@Test
	public void testCanHaveDefault() {
		assertTrue(editor.canHaveDefault(ColumnTypeViewEnum.Boolean));
		assertTrue(editor.canHaveDefault(ColumnTypeViewEnum.Integer));
		assertTrue(editor.canHaveDefault(ColumnTypeViewEnum.Boolean));
		assertTrue(editor.canHaveDefault(ColumnTypeViewEnum.Double));
		assertTrue(editor.canHaveDefault(ColumnTypeViewEnum.Date));

		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.Entity));
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.User));
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.File));
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.LargeText));

		editor.setCanHaveDefault(false);
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.Boolean));
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.Integer));
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.Boolean));
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.Double));
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.Date));
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.Entity));
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.User));
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.File));
		assertFalse(editor.canHaveDefault(ColumnTypeViewEnum.LargeText));
	}

}
