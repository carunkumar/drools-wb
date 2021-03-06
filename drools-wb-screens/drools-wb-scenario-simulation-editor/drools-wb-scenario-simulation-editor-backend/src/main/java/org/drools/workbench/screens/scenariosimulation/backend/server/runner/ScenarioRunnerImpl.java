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

package org.drools.workbench.screens.scenariosimulation.backend.server.runner;

import java.util.List;
import java.util.function.Function;

import org.drools.workbench.screens.scenariosimulation.backend.server.expression.BaseExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.expression.ExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioRunnerData;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.kie.api.runtime.KieContainer;

import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.executeScenario;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.extractExpectedValues;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.extractGivenValues;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.validateAssertion;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.verifyConditions;

public class ScenarioRunnerImpl extends Runner {

    private final ClassLoader classLoader;
    private Function<ClassLoader, ExpressionEvaluator> expressionEvaluatorFactory = BaseExpressionEvaluator::new;
    private final Description desc;
    private final KieContainer kieContainer;
    private final SimulationDescriptor simulationDescriptor;
    private List<Scenario> scenarios;

    public ScenarioRunnerImpl(KieContainer kieContainer, Simulation simulation) {
        this(kieContainer, simulation.getSimulationDescriptor(), simulation.getUnmodifiableScenarios());
    }

    public ScenarioRunnerImpl(KieContainer kieContainer, SimulationDescriptor simulationDescriptor, List<Scenario> scenarios) {
        this.kieContainer = kieContainer;
        this.simulationDescriptor = simulationDescriptor;
        this.scenarios = scenarios;
        this.desc = getDescriptionForSimulationDescriptor(simulationDescriptor);
        this.classLoader = kieContainer.getClassLoader();
    }

    @Override
    public void run(RunNotifier notifier) {

        for (Scenario scenario : scenarios) {
            internalRunScenario(scenario, getSingleNotifier(notifier, scenario));
        }
    }

    @Override
    public Description getDescription() {
        return this.desc;
    }

    private EachTestNotifier getSingleNotifier(RunNotifier notifier, Scenario scenario) {
        Description childDescription = Description.createTestDescription(getClass(),
                                                                         scenario.getDescription());
        desc.addChild(childDescription);
        return new EachTestNotifier(notifier, childDescription);
    }

    protected List<ScenarioResult> internalRunScenario(Scenario scenario, EachTestNotifier singleNotifier) {
        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();

        singleNotifier.fireTestStarted();

        try {
            ExpressionEvaluator expressionEvaluator = createExpressionEvaluator();
            extractGivenValues(simulationDescriptor, scenario.getUnmodifiableFactMappingValues(), classLoader, expressionEvaluator)
                    .forEach(scenarioRunnerData::addInput);
            extractExpectedValues(scenario.getUnmodifiableFactMappingValues()).forEach(scenarioRunnerData::addOutput);

            executeScenario(kieContainer,
                            scenarioRunnerData,
                            expressionEvaluator,
                            simulationDescriptor);

            verifyConditions(simulationDescriptor,
                             scenarioRunnerData,
                             expressionEvaluator);
            validateAssertion(scenarioRunnerData.getResultData(),
                              scenario,
                              singleNotifier);
        } catch (ScenarioException e) {
            singleNotifier.addFailure(e);
        } catch (Throwable e) {
            singleNotifier.addFailure(new IllegalStateException(new StringBuilder().append("Unexpected test error in scenario '")
                                                                        .append(scenario.getDescription()).append("'").toString(), e));
        }

        singleNotifier.fireTestFinished();
        return scenarioRunnerData.getResultData();
    }

    public ExpressionEvaluator createExpressionEvaluator() {
        return expressionEvaluatorFactory.apply(classLoader);
    }

    public void setExpressionEvaluatorFactory(Function<ClassLoader, ExpressionEvaluator> expressionEvaluatorFactory) {
        this.expressionEvaluatorFactory = expressionEvaluatorFactory;
    }

    public static Description getDescriptionForSimulationDescriptor(SimulationDescriptor simulationDescriptor) {
        return Description.createSuiteDescription("Test Scenarios (Preview) tests");
    }
}
