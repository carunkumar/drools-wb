/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.client.metadata;

import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioHeaderMetaDataTest {

    @Mock
    private ScenarioHeaderTextBoxSingletonDOMElementFactory factoryMock;

    @Mock
    private FactMapping factMapping;

    @Test(expected = IllegalStateException.class)
    public void constructorFail() {
        new ScenarioHeaderMetaData("", "", "", factoryMock, true, true);
    }

    @Test(expected = IllegalStateException.class)
    public void edit_ReadOnly() {
        ScenarioHeaderMetaData scenarioHeaderMetaData = new ScenarioHeaderMetaData("", "", "", factoryMock, true, false);
        scenarioHeaderMetaData.setReadOnly(true);
        scenarioHeaderMetaData.edit(null);
    }

    @Test
    public void editTest_EditingMode() {
        ScenarioHeaderMetaData scenarioHeaderMetaData = new ScenarioHeaderMetaData("", "", "", factoryMock, false, false);
        scenarioHeaderMetaData.setReadOnly(false);
        scenarioHeaderMetaData.setEditingMode(true);
        scenarioHeaderMetaData.edit(null);
        verify(factoryMock, never()).attachDomElement(any(), any(), any());
    }

    @Test
    public void editTest_NotEditingMode() {
        ScenarioHeaderMetaData scenarioHeaderMetaData = new ScenarioHeaderMetaData("", "", "", factoryMock, false, false);
        scenarioHeaderMetaData.setReadOnly(false);
        scenarioHeaderMetaData.setEditingMode(false);
        scenarioHeaderMetaData.edit(null);
        verify(factoryMock, times(1)).attachDomElement(any(), any(), any());
    }

    @Test
    public void testSupportedEditAction() {
        ScenarioHeaderMetaData scenarioHeaderMetaData = new ScenarioHeaderMetaData("", "", "", factoryMock, false, false);
        assertEquals(GridCellEditAction.DOUBLE_CLICK, scenarioHeaderMetaData.getSupportedEditAction());
    }
}