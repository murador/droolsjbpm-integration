/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.server.integrationtests.jbpm;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.client.KieServicesException;

import static org.junit.Assert.*;

public class ImageServiceIntegrationTest extends JbpmKieServerBaseIntegrationTest {

    private static ReleaseId releaseId = new ReleaseId("org.kie.server.testing", "definition-project",
            "1.0.0.Final");

    private static final String CONTAINER_ID = "definition-project";
    private static final String HIRING_PROCESS_ID = "hiring";

    @BeforeClass
    public static void buildAndDeployArtifacts() {

        buildAndDeployCommonMavenParent();
        buildAndDeployMavenProject(ClassLoader.class.getResource("/kjars-sources/definition-project").getFile());

    }

    @Test
    public void testGetProcessImageViaUIClientTest() throws Exception {
        KieContainerResource resource = new KieContainerResource(CONTAINER_ID, releaseId);
        assertSuccess(client.createContainer(CONTAINER_ID, resource));

        String result = uiServicesClient.getProcessImage(CONTAINER_ID, HIRING_PROCESS_ID);
        logger.debug("Image content is '{}'", result);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test(expected = KieServicesException.class)
    public void testGetProcessNotExistingImageViaUIClientTest() throws Exception {
        KieContainerResource resource = new KieContainerResource(CONTAINER_ID, releaseId);
        assertSuccess(client.createContainer(CONTAINER_ID, resource));

        uiServicesClient.getProcessImage(CONTAINER_ID, "not-existing");
    }

    @Test
    public void testGetProcessInstanceImageViaUIClientTest() throws Exception {
        KieContainerResource resource = new KieContainerResource(CONTAINER_ID, releaseId);
        assertSuccess(client.createContainer(CONTAINER_ID, resource));

        long processInstanceId = processClient.startProcess(CONTAINER_ID, HIRING_PROCESS_ID);
        assertTrue(processInstanceId > 0);
        try {
            String result = uiServicesClient.getProcessInstanceImage(CONTAINER_ID, processInstanceId);
            logger.debug("Image content is '{}'", result);
            assertNotNull(result);
            assertFalse(result.isEmpty());
        } finally {
            processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
        }
    }

    @Test(expected = KieServicesException.class)
    public void testGetProcessInstanceNotExistingImageViaUIClientTest() throws Exception {
        KieContainerResource resource = new KieContainerResource(CONTAINER_ID, releaseId);
        assertSuccess(client.createContainer(CONTAINER_ID, resource));

        uiServicesClient.getProcessInstanceImage(CONTAINER_ID, 9999l);
    }
}
