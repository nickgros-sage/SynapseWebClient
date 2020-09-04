package org.sagebionetworks.web.client.widget.entity.act;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.TextArea;
import org.sagebionetworks.web.client.DisplayUtils;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class RejectDataAccessRequestModalViewImpl implements RejectDataAccessRequestModalView {

	public interface Binder extends UiBinder<Modal, RejectDataAccessRequestModalViewImpl> {
	}

	@UiField
	Modal modal;

	// Generated Response Preview
	@UiField
	TextArea responseField;

	// Checkboxes
	@UiField
	CheckBox fillInPrincipalInvestigatorOption;
	@UiField
	CheckBox wrongDucOption;
	@UiField
	CheckBox everyPageDucOption;
	@UiField
	CheckBox executeDucOption;
	@UiField
	CheckBox notOwnSigningOfficialOption;
	@UiField
	CheckBox requestorsMismatchSignedOption;
	@UiField
	CheckBox projectLeadMissingOption;
	@UiField
	CheckBox institutionNameMissingOption;
	@UiField
	CheckBox missingBasicInfoOption;
	@UiField
	CheckBox missingApprovalLetterOption;
	@UiField
	CheckBox customTextOption;

	// Generate response button
	@UiField
	Button generateButton;

	// Text Box for custom checkbox
	@UiField
	TextArea customText;

	// alert if no responses submitted
	@UiField
	Alert alert;

	// Cancel and Submit Buttons
	@UiField
	Button primaryButton;
	@UiField
	Button defaultButton;

	Widget widget;

	// Presenter
	Presenter presenter;
	
	@Inject
	public RejectDataAccessRequestModalViewImpl(Binder binder) {
		widget = binder.createAndBindUi(this);

		defaultButton.addClickHandler(event -> modal.hide());
		primaryButton.addClickHandler(event -> presenter.onSave());
		primaryButton.addDomHandler(DisplayUtils.getPreventTabHandler(primaryButton), KeyDownEvent.getType());

		generateButton.addClickHandler(event -> presenter.updateResponse());
		customTextOption.addClickHandler(event -> customText.setVisible(customTextOption.getValue()));
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	private CheckBox[] getCheckBoxes() {
		return new CheckBox[] {wrongDucOption, everyPageDucOption, executeDucOption, notOwnSigningOfficialOption, requestorsMismatchSignedOption, projectLeadMissingOption, fillInPrincipalInvestigatorOption, institutionNameMissingOption, missingBasicInfoOption, missingApprovalLetterOption};
	}
	public void setValue(String value) {
		responseField.setText(value);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public String getValue() {
		return responseField.getText();
	}

	@Override
	public void showError(String error) {
		alert.setVisible(true);
		alert.setText(error);
	}

	@Override
	public void hide() {
		modal.hide();
	}

	@Override
	public void show() {
		modal.show();
		responseField.setFocus(true);
	}

	@Override
	public void clear() {
		this.clearError();
		this.primaryButton.state().reset();
		this.defaultButton.state().reset();
		this.defaultButton.state().reset();
		this.customText.clear();
		this.responseField.clear();
		for (CheckBox cb : getCheckBoxes()) {
			cb.setValue(false);
		}
		this.customTextOption.setValue(false);
		this.customText.setVisible(false);
	}

	@Override
	public void clearError() {
		this.alert.setVisible(false);
	}

	@Override
	public String getSelectedCheckboxText() {
		String output = "";
		for (CheckBox checkBox : getCheckBoxes()) {
			if (checkBox.getValue()) {
				output += "\n" + checkBox.getText() + "\n";
			}
		}
		if (customTextOption.getValue()) {
			output += "\n" + customText.getText() + "\n";
		}

		return output;
	}
}
