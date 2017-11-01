package org.sagebionetworks.web.client.widget.team;

import org.sagebionetworks.web.client.widget.user.UserBadge;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface OpenUserInvitationsWidgetView extends IsWidget {
	
	/**
	 * Set this view's presenter
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);

	void hideMoreButton();

	void showMoreButton();

	void hideInvitations();

	void showInvitations();

	void setSynAlert(IsWidget w);

	void addInvitation(EmailInvitationBadge badge, String misId, String message, String createdOn);

	void addInvitation(UserBadge userBadge, String inviteeEmail, String misId, String message, String createdOn);

	void clear();

	interface Presenter {
		//use to go to user profile page
		void goTo(Place place);
		void removeInvitation(String ownerId);
		void getNextBatch();
	}
}
