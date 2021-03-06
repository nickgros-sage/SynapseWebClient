package org.sagebionetworks.web.unitclient.widget.table.modal.fileview;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.sagebionetworks.repo.model.entitybundle.v2.EntityBundle;
import org.sagebionetworks.repo.model.table.EntityView;
import org.sagebionetworks.repo.model.table.Table;
import org.sagebionetworks.repo.model.table.ViewType;
import org.sagebionetworks.repo.model.table.ViewTypeMask;
import org.sagebionetworks.web.client.SynapseJavascriptClient;
import org.sagebionetworks.web.client.events.EntityUpdatedEvent;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.client.widget.table.modal.fileview.EntityContainerListWidget;
import org.sagebionetworks.web.client.widget.table.modal.fileview.EntityViewScopeWidget;
import org.sagebionetworks.web.client.widget.table.modal.fileview.EntityViewScopeWidgetView;
import org.sagebionetworks.web.client.widget.table.modal.fileview.TableType;
import org.sagebionetworks.web.shared.WebConstants;
import org.sagebionetworks.web.test.helper.AsyncMockStubber;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

@RunWith(MockitoJUnitRunner.class)
public class ScopeWidgetTest {

	@Mock
	EntityViewScopeWidgetView mockView;
	@Mock
	SynapseJavascriptClient mockJsClient;
	@Mock
	SynapseAlert mockSynapseAlert;
	@Mock
	EntityContainerListWidget mockViewScopeWidget;
	@Mock
	EntityContainerListWidget mockEditScopeWidget;
	@Mock
	EntityBundle mockBundle;
	@Mock
	List<String> mockScopeIds;
	@Mock
	List<String> mockNewScopeIds;
	EntityViewScopeWidget widget;
	@Mock
	EntityView mockEntityView;
	@Mock
	EntityView mockUpdatedEntityView;
	@Mock
	Table mockTable;
	@Mock
	EventBus mockEventBus;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		widget = new EntityViewScopeWidget(mockView, mockJsClient, mockViewScopeWidget, mockEditScopeWidget, mockSynapseAlert, mockEventBus);
		when(mockEntityView.getId()).thenReturn("syn123");
		when(mockEntityView.getScopeIds()).thenReturn(mockScopeIds);
		when(mockBundle.getEntity()).thenReturn(mockEntityView);
		when(mockEntityView.getType()).thenReturn(ViewType.file);
		when(mockEntityView.getViewTypeMask()).thenReturn(null);
		AsyncMockStubber.callSuccessWith(mockUpdatedEntityView).when(mockJsClient).updateEntity(any(Table.class), anyString(), anyBoolean(), any(AsyncCallback.class));
		when(mockView.isFileSelected()).thenReturn(false);
		when(mockView.isFolderSelected()).thenReturn(false);
		when(mockView.isTableSelected()).thenReturn(false);
	}

	@Test
	public void testConstruction() {
		verify(mockView).setPresenter(widget);
		verify(mockView).setEditableEntityListWidget(any(Widget.class));
		verify(mockView).setEntityListWidget(any(Widget.class));
		verify(mockView).setSynAlert(any(Widget.class));
	}

	@Test
	public void testConfigureHappyCase() {
		// configure with an entityview, edit the scope, and save.
		boolean isEditable = true;
		widget.configure(mockBundle, isEditable);

		// The view scope widget does not allow edit of the scope. That occurs in the modal (with the
		// editScopeWidget)
		verify(mockViewScopeWidget).configure(mockScopeIds, false, TableType.files);
		verify(mockView).setEditButtonVisible(true);
		verify(mockView).setVisible(true);

		// edit
		widget.onEdit();
		verify(mockEditScopeWidget).configure(mockScopeIds, true, TableType.files);
		verify(mockView).showModal();

		// update file view to file+table view
		when(mockView.isFileSelected()).thenReturn(true);
		when(mockView.isTableSelected()).thenReturn(true);
		widget.updateViewTypeMask();

		// save new scope
		widget.onSave();

		verify(mockSynapseAlert).clear();
		verify(mockView).setLoading(true);
		// verify view type has been updated
		// clears out old ViewType, replaced with mask
		verify(mockEntityView).setType(null);
		verify(mockEntityView).setViewTypeMask(ViewTypeMask.getMaskForDepricatedType(ViewType.file_and_table));
		verify(mockJsClient).updateEntity(any(Table.class), anyString(), anyBoolean(), any(AsyncCallback.class));
		verify(mockView).setLoading(false);
		verify(mockView).hideModal();
		verify(mockEventBus).fireEvent(any(EntityUpdatedEvent.class));
	}

	@Test
	public void testConfigureUnsupportedViewTypeMask() {
		// should be editable, but it isn't because the web client does not support the view type mask
		when(mockEntityView.getViewTypeMask()).thenReturn(new Long(WebConstants.PROJECT | WebConstants.FILE));
		boolean isEditable = true;
		widget.configure(mockBundle, isEditable);

		// The view scope widget does not allow edit of the scope. That occurs in the modal (with the
		// editScopeWidget)
		verify(mockViewScopeWidget).configure(mockScopeIds, false, null);
		verify(mockView).setEditButtonVisible(false);
		verify(mockView).setVisible(true);
	}

	@Test
	public void testConfigureHappyCaseProjectView() {
		when(mockEntityView.getType()).thenReturn(ViewType.project);
		// configure with an entityview, edit the scope, and save.
		boolean isEditable = true;
		widget.configure(mockBundle, isEditable);

		// The view scope widget does not allow edit of the scope. That occurs in the modal (with the
		// editScopeWidget)
		verify(mockViewScopeWidget).configure(mockScopeIds, false, TableType.projects);

		// edit
		widget.onEdit();
		// Do not show File View options for project view
		verify(mockView).setViewTypeOptionsVisible(false);
		verify(mockEditScopeWidget).configure(mockScopeIds, true, TableType.projects);
	}

	@Test
	public void testConfigureFileView() {
		when(mockEntityView.getType()).thenReturn(ViewType.file);
		boolean isEditable = true;

		widget.configure(mockBundle, isEditable);
		widget.onEdit();

		// Show File View options for project view
		verify(mockView).setViewTypeOptionsVisible(true);
		verify(mockView).setIsFileSelected(true);
		verify(mockView).setIsFolderSelected(false);
		verify(mockView).setIsTableSelected(false);
	}

	@Test
	public void testConfigureFileAndTableView() {
		when(mockEntityView.getType()).thenReturn(ViewType.file_and_table);
		boolean isEditable = true;

		widget.configure(mockBundle, isEditable);
		widget.onEdit();

		// Show File View options for project view
		verify(mockView).setViewTypeOptionsVisible(true);
		verify(mockView).setIsFileSelected(true);
		verify(mockView).setIsFolderSelected(false);
		verify(mockView).setIsTableSelected(true);

		// verify update view type from file+table to file
		when(mockView.isFileSelected()).thenReturn(true);
		widget.updateViewTypeMask();
		widget.onSave();

		verify(mockEntityView).setViewTypeMask(ViewTypeMask.File.getMask());
	}

	@Test
	public void testConfigureNotEntityView() {
		boolean isEditable = true;
		when(mockBundle.getEntity()).thenReturn(mockTable);
		widget.configure(mockBundle, isEditable);

		verify(mockView).setVisible(false);
	}

	@Test
	public void testConfigureNotEditable() {
		boolean isEditable = false;
		widget.configure(mockBundle, isEditable);

		verify(mockViewScopeWidget).configure(mockScopeIds, false, TableType.files);
		verify(mockView).setEditButtonVisible(false);
		verify(mockView).setVisible(true);
	}

	@Test
	public void testOnSaveFailure() {
		Exception ex = new Exception("error on save");
		AsyncMockStubber.callFailureWith(ex).when(mockJsClient).updateEntity(any(Table.class), anyString(), anyBoolean(), any(AsyncCallback.class));
		boolean isEditable = true;

		widget.configure(mockBundle, isEditable);
		widget.onSave();

		verify(mockSynapseAlert).clear();
		verify(mockView).setLoading(true);
		verify(mockJsClient).updateEntity(any(Table.class), anyString(), anyBoolean(), any(AsyncCallback.class));
		verify(mockView).setLoading(false);
		verify(mockSynapseAlert).handleException(ex);
		verify(mockView, never()).hideModal();
		verify(mockEventBus, never()).fireEvent(any(EntityUpdatedEvent.class));
	}

	@Test
	public void testAsWidget() {
		widget.asWidget();
		verify(mockView).asWidget();
	}
}
