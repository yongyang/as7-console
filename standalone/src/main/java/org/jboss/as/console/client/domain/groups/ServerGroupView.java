/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.domain.groups;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.TitleBar;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Shows an editable view of a single server group.
 *
 * @author Heiko Braun
 * @date 2/16/11
 */
public class ServerGroupView extends SuspendableViewImpl implements ServerGroupPresenter.MyView {

    private ServerGroupPresenter presenter;
    private Form<ServerGroupRecord> form;
    private ContentHeaderLabel nameLabel;
    private ComboBoxItem profileItem;

    private ComboBoxItem socketBindingItem;
    private ComboBoxItem jvmField;
    private ToolButton edit;

    private LayoutPanel layout;
    private VerticalPanel panel;

    private TabLayoutPanel tabLayoutpanel;

    PropertyEditor propertyEditor;
    JvmEditor jvmEditor;

    @Override
    public void setPresenter(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        layout = new LayoutPanel();

        TitleBar titleBar = new TitleBar("Server Group");
        layout.add(titleBar);

        // ----

        final ToolStrip toolStrip = new ToolStrip();
        edit = new ToolButton("Edit");
        edit.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(edit.getText().equals("Edit"))
                {
                    onEdit();
                }
                else
                {
                    onSave();
                }
            }
        });

        toolStrip.addToolButton(edit);
        ToolButton delete = new ToolButton("Delete");
        delete.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                Feedback.confirm(
                        "Delete Server Group",
                        "Do you want to delete server group '"+form.getEditedEntity().getGroupName()+"'?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                    presenter.deleteCurrentRecord();
                            }
                        });
            }
        });
        toolStrip.addToolButton(delete);

        toolStrip.addToolButtonRight(new ToolButton("New Group", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewGroupDialoge();
            }
        }));

        layout.add(toolStrip);
        // ----

        panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");
        panel.getElement().setAttribute("style", "padding:15px;");

        layout.add(panel);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(toolStrip, 28, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(panel, 58, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---------------------------------------------

        nameLabel = new ContentHeaderLabel("Name here ...");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.serverGroup());
        horzPanel.add(image);
        horzPanel.add(nameLabel);
        image.getElement().getParentElement().setAttribute("width", "25");

        panel.add(horzPanel);

        // ---------------------------------------------------

        form = new Form<ServerGroupRecord>(ServerGroupRecord.class);
        form.setNumColumns(2);

        TextItem nameField = new TextItem("groupName", "Group Name");
        //jvmField = new ComboBoxItem("jvm", "Virtual Machine");
        //jvmField.setValueMap(new String[] {"default"}); // TODO: https://issues.jboss.org/browse/JBAS-9156

        socketBindingItem = new ComboBoxItem("socketBinding", "Socket Binding");
        socketBindingItem.setDefaultToFirstOption(true);

        profileItem = new ComboBoxItem("profileName", "Profile");

        form.setFields(nameField, profileItem);
        form.setFieldsInGroup("Advanced", new DisclosureGroupRenderer(), socketBindingItem);

        panel.add(new ContentGroupLabel("Attributes"));

        panel.add(form.asWidget());

        // ---------------------------------------------------


        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        propertyEditor = new PropertyEditor(presenter);
        tabLayoutpanel.add(propertyEditor.asWidget(), "System Properties");

        jvmEditor = new JvmEditor(presenter);
        tabLayoutpanel.add(jvmEditor.asWidget(), "Virtual Machine");

        tabLayoutpanel.selectTab(0);

        layout.add(tabLayoutpanel);
        layout.setWidgetBottomHeight(tabLayoutpanel, 0, Style.Unit.PX, 40, Style.Unit.PCT);

        return layout;
    }

    private void onSave() {
        ServerGroupRecord updatedEntity = form.getUpdatedEntity();
        updatedEntity.setProperties(new HashMap<String,String>());

        presenter.onSaveChanges(updatedEntity.getGroupName(), form.getChangedValues());
    }

    private void onEdit() {
        presenter.editCurrentRecord();
    }


    public void setSelectedRecord(final ServerGroupRecord record) {

        // title
        final String selectedGroupName = record.getGroupName();
        nameLabel.setHTML(selectedGroupName);

        // form
        form.edit(record);

        propertyEditor.setSelectedRecord(record);
        jvmEditor.setSelectedRecord(record);
    }

    @Override
    public void updateProfiles(List<ProfileRecord> result) {

        List<String> names = new ArrayList<String>(result.size());
        for(ProfileRecord rec : result)
            names.add(rec.getName());

        profileItem.setValueMap(names);
    }

    public void setEnabled(boolean isEnabled) {

        if(isEnabled)
            panel.addStyleName("edit-panel");
        else
            panel.removeStyleName("edit-panel");

        form.setEnabled(isEnabled);


        edit.setText(
            isEnabled ? "Save" : "Edit"
        );
    }

    @Override
    public void updateSocketBindings(List<String> result) {
        socketBindingItem.setValueMap(result);
    }
}
