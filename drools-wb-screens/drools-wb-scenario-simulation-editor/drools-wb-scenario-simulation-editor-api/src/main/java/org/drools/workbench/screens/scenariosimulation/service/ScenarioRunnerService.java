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
package org.drools.workbench.screens.scenariosimulation.service;

import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.guvnor.common.services.shared.test.TestService;
import org.uberfire.backend.vfs.Path;

public interface ScenarioRunnerService
        extends TestService {

    ScenarioSimulationModel runTest(final String identifier, final Path path, final ScenarioSimulationModel model);
}
