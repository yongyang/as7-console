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

package org.jboss.as.console.client.core;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * A default {@link SuspendableView} implementation.
 * Subclasses can override {@link #onResume()} but need to
 * provide {@link #createWidget()}
 *
 * @author Heiko Braun
 * @date 2/10/11
 */
public abstract class SuspendableViewImpl extends ViewImpl implements SuspendableView {

    protected Widget widgetInstance = null;

    @Override
    public Widget asWidget() {
        if(null==widgetInstance)
            widgetInstance = createWidget();
        else
            onResume();

        return widgetInstance;
    }

    @Override
    public void onResume() {

    }
}
