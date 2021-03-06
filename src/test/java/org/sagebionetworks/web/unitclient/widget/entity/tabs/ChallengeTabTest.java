package org.sagebionetworks.web.unitclient.widget.entity.tabs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sagebionetworks.repo.model.entitybundle.v2.EntityBundle;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.cookie.CookieProvider;
import org.sagebionetworks.web.client.place.Synapse;
import org.sagebionetworks.web.client.place.Synapse.EntityArea;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.utils.CallbackP;
import org.sagebionetworks.web.client.widget.entity.menu.v2.ActionMenuWidget;
import org.sagebionetworks.web.client.widget.entity.tabs.ChallengeTab;
import org.sagebionetworks.web.client.widget.entity.tabs.ChallengeTabView;
import org.sagebionetworks.web.client.widget.entity.tabs.Tab;
import org.sagebionetworks.web.client.widget.evaluation.AdministerEvaluationsList;
import org.sagebionetworks.web.client.widget.evaluation.ChallengeWidget;
import com.google.gwt.user.client.ui.Widget;

@RunWith(MockitoJUnitRunner.class)
public class ChallengeTabTest {
	@Mock
	Tab mockTab;
	@Mock
	ChallengeTabView mockView;
	@Mock
	CallbackP<Tab> mockOnClickCallback;
	@Mock
	AdministerEvaluationsList mockAdministerEvaluationsList;
	@Mock
	ChallengeWidget mockChallengeWidget;
	@Mock
	PortalGinInjector mockPortalGinInjector;
	@Mock
	EntityBundle mockProjectEntityBundle;
	@Mock
	ActionMenuWidget mockActionMenuWidget;
	@Mock
	CookieProvider mockCookieProvider;
	ChallengeTab tab;
	@Mock
	AuthenticationController authenticationController;
	@Mock
	GlobalApplicationState globalApplicationState;

	@Before
	public void setUp() {
		tab = new ChallengeTab(mockTab, mockPortalGinInjector , authenticationController, globalApplicationState);
		when(mockTab.getEntityActionMenu()).thenReturn(mockActionMenuWidget);
		when(mockPortalGinInjector.getChallengeTabView()).thenReturn(mockView);
		when(mockPortalGinInjector.getAdministerEvaluationsList()).thenReturn(mockAdministerEvaluationsList);
		when(mockPortalGinInjector.getChallengeWidget()).thenReturn(mockChallengeWidget);
		tab.lazyInject();
	}

	@Test
	public void testConstruction() {
		verify(mockView).setEvaluationList(any(Widget.class));
		verify(mockView).setChallengeWidget(any(Widget.class));
	}

	@Test
	public void testSetTabClickedCallback() {
		tab.setTabClickedCallback(mockOnClickCallback);
		verify(mockTab).addTabClickedCallback(mockOnClickCallback);
	}

	@Test
	public void testConfigure() {
		String entityId = "syn1";
		String entityName = "challenge project test";
		tab.configure(entityId, entityName, mockProjectEntityBundle);

		verify(mockAdministerEvaluationsList).configure(eq(entityId), any());
		verify(mockChallengeWidget).configure(entityId, entityName);

		ArgumentCaptor<Synapse> captor = ArgumentCaptor.forClass(Synapse.class);
		verify(mockTab).setEntityNameAndPlace(eq(entityName), captor.capture());
		Synapse place = captor.getValue();
		assertEquals(entityId, place.getEntityId());
		assertNull(place.getVersionNumber());
		assertEquals(EntityArea.CHALLENGE, place.getArea());
		assertNull(place.getAreaToken());
	}

	@Test
	public void testAsTab() {
		assertEquals(mockTab, tab.asTab());
	}

}
