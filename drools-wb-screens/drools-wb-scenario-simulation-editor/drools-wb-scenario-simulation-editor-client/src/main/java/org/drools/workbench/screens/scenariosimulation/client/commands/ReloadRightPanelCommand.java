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
package org.drools.workbench.screens.scenariosimulation.client.commands;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.uberfire.mvp.Command;

/**
 * <code>Command</code> to <b>reload</b> the <code>RightPanelView</code>, <b>eventually</b> showing it (if required by original event)
 */
@Dependent
public class ReloadRightPanelCommand implements Command {

    private ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;

    private boolean disable = true;

    private boolean openDock = false;

    public ReloadRightPanelCommand() {
        // CDI
    }

    /**
     *
     * @param scenarioSimulationEditorPresenter
     *
     * @param disable set this to <code>true</code> to <b>also</b> disable the panel.
     * @param openDock set this to <code>true</code> to <b>also</b> open the dock in case it is closed
     */
    public ReloadRightPanelCommand(ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter, boolean disable, boolean openDock) {
        this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenter;
        this.disable = disable;
        this.openDock = openDock;
    }



    @Override
    public void execute() {
        if (scenarioSimulationEditorPresenter != null) {
            if (openDock) {
                scenarioSimulationEditorPresenter.expandToolsDock();
            }
            scenarioSimulationEditorPresenter.reloadRightPanel(disable);
        }
    }
}
