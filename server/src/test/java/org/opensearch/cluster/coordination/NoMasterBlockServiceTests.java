/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.cluster.coordination;

import org.opensearch.common.settings.ClusterSettings;
import org.opensearch.common.settings.Settings;
import org.opensearch.test.OpenSearchTestCase;

import static org.opensearch.cluster.coordination.NoMasterBlockService.NO_MASTER_BLOCK_ALL;
import static org.opensearch.cluster.coordination.NoMasterBlockService.NO_MASTER_BLOCK_METADATA_WRITES;
import static org.opensearch.cluster.coordination.NoMasterBlockService.NO_MASTER_BLOCK_SETTING;
import static org.opensearch.cluster.coordination.NoMasterBlockService.NO_MASTER_BLOCK_WRITES;
import static org.opensearch.common.settings.ClusterSettings.BUILT_IN_CLUSTER_SETTINGS;
import static org.hamcrest.Matchers.sameInstance;

public class NoMasterBlockServiceTests extends OpenSearchTestCase {

    private NoMasterBlockService noMasterBlockService;
    private ClusterSettings clusterSettings;

    private void createService(Settings settings) {
        clusterSettings = new ClusterSettings(settings, BUILT_IN_CLUSTER_SETTINGS);
        noMasterBlockService = new NoMasterBlockService(settings, clusterSettings);
    }

    private void assertDeprecatedWarningEmitted() {
        assertWarnings(
            "[discovery.zen.no_master_block] setting was deprecated in OpenSearch and will be removed in a future release! "
                + "See the breaking changes documentation for the next major version."
        );
    }

    public void testBlocksWritesByDefault() {
        createService(Settings.EMPTY);
        assertThat(noMasterBlockService.getNoMasterBlock(), sameInstance(NO_MASTER_BLOCK_WRITES));
    }

    public void testBlocksWritesIfConfiguredBySetting() {
        createService(Settings.builder().put(NO_MASTER_BLOCK_SETTING.getKey(), "write").build());
        assertThat(noMasterBlockService.getNoMasterBlock(), sameInstance(NO_MASTER_BLOCK_WRITES));
    }

    public void testBlocksAllIfConfiguredBySetting() {
        createService(Settings.builder().put(NO_MASTER_BLOCK_SETTING.getKey(), "all").build());
        assertThat(noMasterBlockService.getNoMasterBlock(), sameInstance(NO_MASTER_BLOCK_ALL));
    }

    public void testBlocksMetadataWritesIfConfiguredBySetting() {
        createService(Settings.builder().put(NO_MASTER_BLOCK_SETTING.getKey(), "metadata_write").build());
        assertThat(noMasterBlockService.getNoMasterBlock(), sameInstance(NO_MASTER_BLOCK_METADATA_WRITES));
    }

    public void testRejectsInvalidSetting() {
        expectThrows(
            IllegalArgumentException.class,
            () -> createService(Settings.builder().put(NO_MASTER_BLOCK_SETTING.getKey(), "unknown").build())
        );
<<<<<<< HEAD
=======
    }

    public void testRejectsInvalidLegacySetting() {
        expectThrows(
            IllegalArgumentException.class,
            () -> createService(Settings.builder().put(LEGACY_NO_MASTER_BLOCK_SETTING.getKey(), "unknown").build())
        );
        assertDeprecatedWarningEmitted();
>>>>>>> origin/1.2
    }

    public void testSettingCanBeUpdated() {
        createService(Settings.builder().put(NO_MASTER_BLOCK_SETTING.getKey(), "all").build());
        assertThat(noMasterBlockService.getNoMasterBlock(), sameInstance(NO_MASTER_BLOCK_ALL));

        clusterSettings.applySettings(Settings.builder().put(NO_MASTER_BLOCK_SETTING.getKey(), "write").build());
        assertThat(noMasterBlockService.getNoMasterBlock(), sameInstance(NO_MASTER_BLOCK_WRITES));

        clusterSettings.applySettings(Settings.builder().put(NO_MASTER_BLOCK_SETTING.getKey(), "metadata_write").build());
        assertThat(noMasterBlockService.getNoMasterBlock(), sameInstance(NO_MASTER_BLOCK_METADATA_WRITES));
    }
}
