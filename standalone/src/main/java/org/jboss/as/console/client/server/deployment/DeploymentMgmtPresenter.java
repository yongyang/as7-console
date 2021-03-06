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

package org.jboss.as.console.client.server.deployment;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.profiles.ProfileHeader;

/**
 * Manages deployments on a standalone server.
 * Acts as a presenter component.
 *
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DeploymentMgmtPresenter extends Presenter<DeploymentMgmtPresenter.DeploymentToolView,
        DeploymentMgmtPresenter.DeploymentToolProxy> {

    private PlaceManager placeManager;
    private boolean hasBeenRevealed = false;

    public interface DeploymentToolView extends SuspendableView {
        void setPresenter(DeploymentMgmtPresenter presenter);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.DeploymentMgmtPresenter)
    public interface DeploymentToolProxy extends ProxyPlace<DeploymentMgmtPresenter> {}

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent = new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public DeploymentMgmtPresenter(
            EventBus eventBus, DeploymentToolView view,
            DeploymentToolProxy proxy, PlaceManager placeManager) {

        super(eventBus, view, proxy);
        this.placeManager = placeManager;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        Console.MODULES.getHeader().highlight(NameTokens.DeploymentMgmtPresenter);
        ProfileHeader header = new ProfileHeader("Deployments");
        Console.MODULES.getHeader().setContent(header);

        if(!hasBeenRevealed)
        {
            placeManager.revealRelativePlace(
                    new PlaceRequest(NameTokens.DeploymentListPresenter)
            );
            hasBeenRevealed = true;
        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }
}
