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

package org.drools.workbench.screens.scenariosimulation.backend.server.fluent;

import java.util.Collections;
import java.util.function.Function;

import org.drools.core.command.RequestContextImpl;
import org.drools.core.command.impl.RegistryContext;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.SingleFactValueResult;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidateFactCommandTest {

    @Mock
    private KieSession kieSession;

    @Mock
    private ScenarioResult scenarioResult;

    @Mock
    private FactMappingValue factMappingValue;

    @Test
    public void executeTest() {
        Function<Object, SingleFactValueResult> alwaysMatchFunction = SingleFactValueResult::createResult;

        ValidateFactCommand validateFactCommand = new ValidateFactCommand(asList(new FactCheckerHandle(String.class, alwaysMatchFunction, scenarioResult)));

        RegistryContext registryContext = new RequestContextImpl();
        registryContext.register(KieSession.class, kieSession);

        when(kieSession.getObjects(any(ObjectFilter.class))).thenReturn(Collections.singleton(null));
        validateFactCommand.execute(registryContext);
        verify(scenarioResult, times(1)).setResult(anyBoolean());

        reset(scenarioResult);

        boolean expectedResult = true;
        when(kieSession.getObjects(any(ObjectFilter.class))).thenReturn(Collections.emptyList());
        when(scenarioResult.getFactMappingValue()).thenReturn(factMappingValue);
        when(factMappingValue.isError()).thenReturn(expectedResult);
        validateFactCommand.execute(registryContext);
        verify(scenarioResult, times(0)).setResult(expectedResult);
    }
}