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

package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;

/**
 * @author Heiko Braun
 * @date 4/15/11
 */
public class NewDatasourceWizard {

    private DataSourcePresenter presenter;

    private DeckPanel deck;
    private DatasourceStep2 step2;
    private DataSourceStep3 step3;


    private DataSource baseAttributes = null;
    private DataSource driverAttributes = null;

    public NewDatasourceWizard(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {


        deck = new DeckPanel();

        deck.add(new DatasourceStep1(this).asWidget());

        step2 = new DatasourceStep2(this);
        deck.add(step2.asWidget());

        step3 = new DataSourceStep3(this);
        deck.add(step3.asWidget());

        deck.showWidget(0);

        return deck;
    }

    public DataSourcePresenter getPresenter() {
        return presenter;
    }

    public void onConfigureBaseAttributes(DataSource entity) {
        this.baseAttributes = entity;
        step2.edit(entity);
        deck.showWidget(1);
    }

    public void onConfigureDriver(DataSource entity) {
        this.driverAttributes = entity;
        step3.edit(entity);
        deck.showWidget(2);
    }

    public void onFinish(DataSource updatedEntity) {

        // merge previous attributes into single entity

        updatedEntity.setName(baseAttributes.getName());
        updatedEntity.setJndiName(baseAttributes.getJndiName());
        updatedEntity.setEnabled(baseAttributes.isEnabled());

        updatedEntity.setDriverName(driverAttributes.getDriverName());
        updatedEntity.setDriverClass(driverAttributes.getDriverClass());

        presenter.onCreateNewDatasource(updatedEntity);
    }
}
