package org.openelis.portal.modules.finalReport.client;

import org.openelis.portal.messages.Messages;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.Balloon.Options;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.IconContainer;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class FinalReportUIImpl extends ResizeComposite implements FinalReportUI {

    @UiTemplate("FinalReport.ui.xml")
    interface FinalReportUiBinder extends UiBinder<Widget, FinalReportUIImpl> {
    };

    protected static final FinalReportUiBinder uiBinder = GWT.create(FinalReportUiBinder.class);

    @UiField
    protected TextBox<Integer>                 accessionStart, accessionEnd, pwsId;

    @UiField
    protected TextBox<String>                  clientReference, collector, patientFirst,
                    patientLast;

    @UiField
    protected Calendar                         collectedStart, collectedEnd, releasedStart,
                    releasedEnd, patientBirthStart, patientBirthEnd;

    @UiField
    protected AutoComplete                     projectCode;

    @UiField
    protected Button                           getSampleListButton, resetButton;

    @UiField
    protected IconContainer                    collectedHelp, releasedHelp, accessionHelp,
                    clientReferenceHelp, projectHelp, collectorHelp, pwsHelp, patientFirstHelp,
                    patientLastHelp, patientBirthHelp;

    public FinalReportUIImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        final Options options;

        options = new Options();
        options.setDelayHide( -1);
        collectedHelp.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                collectedHelp.setTip(Messages.get().finalReport_help_collected());
                collectedHelp.setBalloonOptions(options);
            }
        });

        releasedHelp.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                releasedHelp.setTip(Messages.get().finalReport_help_collected());
                releasedHelp.setBalloonOptions(options);
            }
        });

        accessionHelp.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                accessionHelp.setTip(Messages.get().finalReport_help_collected());
                accessionHelp.setBalloonOptions(options);
            }
        });

        clientReferenceHelp.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                clientReferenceHelp.setTip(Messages.get().finalReport_help_collected());
                clientReferenceHelp.setBalloonOptions(options);
            }
        });

        projectHelp.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                projectHelp.setTip(Messages.get().finalReport_help_collected());
                projectHelp.setBalloonOptions(options);
            }
        });

        collectorHelp.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                collectorHelp.setTip(Messages.get().finalReport_help_collected());
                collectorHelp.setBalloonOptions(options);
            }
        });

        pwsHelp.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                pwsHelp.setTip(Messages.get().finalReport_help_collected());
                pwsHelp.setBalloonOptions(options);
            }
        });

        patientFirstHelp.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                patientFirstHelp.setTip(Messages.get().finalReport_help_collected());
                patientFirstHelp.setBalloonOptions(options);
            }
        });

        patientLastHelp.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                patientLastHelp.setTip(Messages.get().finalReport_help_collected());
                patientLastHelp.setBalloonOptions(options);
            }
        });

        patientBirthHelp.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                patientBirthHelp.setTip(Messages.get().finalReport_help_collected());
                patientBirthHelp.setBalloonOptions(options);
            }
        });
    }
}
