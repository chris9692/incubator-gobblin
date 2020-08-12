/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.gobblin.multistage.configuration;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.gobblin.configuration.SourceState;
import org.apache.gobblin.configuration.State;
import org.joda.time.DateTime;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;


@Test
public class JobPropertiesTest {
  private Gson gson = new Gson();

  @Test
  void validateNonblankWithDefault() {
    SourceState state = new SourceState();
    Assert.assertEquals(JobProperties.MSTAGE_PARAMETERS.getValidNonblankWithDefault(state), new JsonArray());
    Assert.assertEquals(JobProperties.MSTAGE_DATA_FIELD.getValidNonblankWithDefault(state), "");
    Assert.assertEquals(JobProperties.MSTAGE_ABSTINENT_PERIOD_DAYS.getValidNonblankWithDefault(state), new Integer(0));
    Assert.assertEquals(JobProperties.MSTAGE_AUTHENTICATION.getValidNonblankWithDefault(state), new JsonObject());
    Assert.assertEquals(JobProperties.MSTAGE_HTTP_STATUSES.getValidNonblankWithDefault(state).toString(),
        "{\"success\":[200,201,202],\"pagination_error\":[401]}");
    Assert.assertEquals(JobProperties.MSTAGE_PAGINATION.getValidNonblankWithDefault(state), new JsonObject());
    Assert.assertFalse(JobProperties.MSTAGE_PAGINATION.validateNonblank(state));
    state.setProp(JobProperties.MSTAGE_PAGINATION.getConfig(), "[]");
    Assert.assertFalse(JobProperties.MSTAGE_PAGINATION.validateNonblank(state));
    state.setProp(JobProperties.MSTAGE_PAGINATION.getConfig(), "{}");
    Assert.assertFalse(JobProperties.MSTAGE_PAGINATION.validateNonblank(state));
    state.setProp(JobProperties.MSTAGE_PAGINATION.getConfig(), "{null}}");
    Assert.assertFalse(JobProperties.MSTAGE_PAGINATION.validateNonblank(state));
  }

  /**
   * Test ms.wait.timeout.seconds under 2 scenarios
   * Scenario 1: test default value
   * Scenario 2: test user defined value
   */
  @Test
  void validateWaitTimeoutProperty() {
    SourceState state = new SourceState();

    /**
     * Scenario 1:  test default value
     *
     * Input: State object without setting ms.wait.time.seconds
     * Output: 600 seconds, or 10 minutes, or 600,000 milli-seconds
     */
    Assert.assertEquals(JobProperties.MSTAGE_WAIT_TIMEOUT_SECONDS.getMillis(state).longValue(), 600000L);

    /**
     * Scenario 2: test user defined value
     *
     * Input: State object by setting ms.wait.time.seconds = 1000
     * Output: 1000 seconds, or 1,000,000 milli-seconds
     */
    state.setProp(JobProperties.MSTAGE_WAIT_TIMEOUT_SECONDS.toString(), 1000);
    Assert.assertEquals(JobProperties.MSTAGE_WAIT_TIMEOUT_SECONDS.getMillis(state).longValue(), 1000000L);
  }

  /**
   * Test getDefaultValue for MSTAGE_RETENTION
   */
  @Test
  public void testGetDefaultValue1() {
    JsonObject expected = gson.fromJson("{\"state.store\":\"P90D\",\"publish.dir\":\"P731D\",\"log\":\"P30D\"}", JsonObject.class);
    Assert.assertEquals(JobProperties.MSTAGE_RETENTION.getDefaultValue(), expected);
  }

  /**
   * Test getDefaultValue for MSTAGE_ENABLE_DYNAMIC_FULL_LOAD
   */
  @Test
  public void testGetDefaultValue2() {
    Assert.assertEquals(JobProperties.MSTAGE_ENABLE_DYNAMIC_FULL_LOAD.getDefaultValue(), Boolean.TRUE);
  }

  /**
   * Test getDefaultValue for MSTAGE_ENABLE_SCHEMA_BASED_FILTERING
   */
  @Test
  public void testGetDefaultValue3() {
    Assert.assertEquals(JobProperties.MSTAGE_ENABLE_SCHEMA_BASED_FILTERING.getDefaultValue(), Boolean.TRUE);
  }

  /**
   * Test getDefaultValue for MSTAGE_SOURCE_FILES_PATTERN
   */
  @Test
  public void testGetDefaultValue4() {
    Assert.assertEquals(JobProperties.MSTAGE_SOURCE_FILES_PATTERN.getDefaultValue(), ".*");
  }

  /**
   * Test getDefaultValue for EXTRACT_IS_FULL
   */
  @Test
  public void testGetDefaultValue5() {
    Assert.assertEquals(JobProperties.EXTRACT_IS_FULL.getDefaultValue(), (Boolean) false);
  }

  /**
   * Test getDefaultValue for MSTAGE_ENDCODING
   */
  @Test
  public void testGetDefaultValue6() {
    Assert.assertEquals(JobProperties.MSTAGE_ENCODING.getDefaultValue(), "UTF-8");
  }

  /**
   * Test getDefaultValue
   */
  @Test
  public void testGetDefaultValue7() {
    Assert.assertEquals(JobProperties.MSTAGE_WORKUNIT_STARTTIME_KEY.getDefaultValue(), new Long(0L));
  }

  /**
   * test default gaap connection read timeout value
   */
  @Test
  public void testGetDefaultValueGaapReadTimeout() {
    Assert.assertEquals(JobProperties.MSTAGE_GAAP_READ_TIMEOUT_MILLIS.getValidNonblankWithDefault(new State()), new Integer(3600000));
  }

  /**
   * Test getValidNonblankWithDefault
   */
  @Test
  public void testGetValidNonblankWithDefault1() {
    State state = Mockito.mock(State.class);
    when(state.getPropAsInt(JobProperties.MSTAGE_ABSTINENT_PERIOD_DAYS.getConfig(), 0)).thenReturn(0);
    Assert.assertEquals(JobProperties.MSTAGE_ABSTINENT_PERIOD_DAYS.getValidNonblankWithDefault(state), new Integer(0));

    when(state.getPropAsInt(JobProperties.MSTAGE_ABSTINENT_PERIOD_DAYS.getConfig(), 0)).thenReturn(1);
    Assert.assertEquals(JobProperties.MSTAGE_ABSTINENT_PERIOD_DAYS.getValidNonblankWithDefault(state), new Integer(1));
  }

  /**
   * Test getValidNonblankWithDefault for MSTAGE_EXTRACTOR_TARGET_FILE_PERMISSION
   */
  @Test
  public void testGetValidNonblankWithDefault2() {
    State state = Mockito.mock(State.class);
    String expected = "input";
    when(state.getProp(JobProperties.MSTAGE_EXTRACTOR_TARGET_FILE_PERMISSION.getConfig(), StringUtils.EMPTY)).thenReturn(expected);
    Assert.assertEquals(JobProperties.MSTAGE_EXTRACTOR_TARGET_FILE_PERMISSION.getValidNonblankWithDefault(state), expected.toUpperCase());

    when(state.getProp(JobProperties.MSTAGE_EXTRACTOR_TARGET_FILE_PERMISSION.getConfig(), StringUtils.EMPTY)).thenReturn("");
    Assert.assertEquals(JobProperties.MSTAGE_EXTRACTOR_TARGET_FILE_PERMISSION.getValidNonblankWithDefault(state), "755");
  }

  /**
   * Test getMillis for MSTAGE_GRACE_PERIOD_DAYS
   */
  @Test
  public void testGetMillis1() {
    State state = new State();
    Long expected = 24L * 3600L * 1000L * (Integer) JobProperties.MSTAGE_GRACE_PERIOD_DAYS.getProp(state);
    Assert.assertEquals(JobProperties.MSTAGE_GRACE_PERIOD_DAYS.getMillis(state), expected);

    Assert.assertEquals(JobProperties.MSTAGE_SOURCE_FILES_PATTERN.getMillis(state), (Long) 0L);
  }

  /**
   * Test getMillis for MSTAGE_ABSTINENT_PERIOD_DAYS
   */
  @Test
  public void testGetMillis2() {
    State state = new State();
    Long expected = 24L * 3600L * 1000L * (Integer) JobProperties.MSTAGE_ABSTINENT_PERIOD_DAYS.getProp(new State());
    Assert.assertEquals(JobProperties.MSTAGE_ABSTINENT_PERIOD_DAYS.getMillis(state), expected);
  }

  /**
   * Test validate for MSTAGE_ACTIVATION_PROPERTY
   */
  @Test
  public void testValidate1() {
    State state = Mockito.mock(State.class);
    when(state.getProp(JobProperties.MSTAGE_ACTIVATION_PROPERTY.getConfig(), new JsonObject().toString())).thenReturn("");
    Assert.assertTrue(JobProperties.MSTAGE_ACTIVATION_PROPERTY.validate(state));

    when(state.getProp(JobProperties.MSTAGE_ACTIVATION_PROPERTY.getConfig(), new JsonObject().toString())).thenReturn("{\"state.store\":\"P90D\"}");
    Assert.assertTrue(JobProperties.MSTAGE_ACTIVATION_PROPERTY.validate(state));
  }

  /**
   * Test validate for MSTAGE_DERIVED_FIELDS
   */
  @Test
  public void testValidate2() {
    State state = Mockito.mock(State.class);
    when(state.getProp(JobProperties.MSTAGE_DERIVED_FIELDS.getConfig(), new JsonArray().toString())).thenReturn("");
    Assert.assertTrue(JobProperties.MSTAGE_DERIVED_FIELDS.validate(state));

    when(state.getProp(JobProperties.MSTAGE_DERIVED_FIELDS.getConfig(), new JsonArray().toString())).thenReturn("[]");
    Assert.assertTrue(JobProperties.MSTAGE_DERIVED_FIELDS.validate(state));

    when(state.getProp(JobProperties.MSTAGE_DERIVED_FIELDS.getConfig(), new JsonArray().toString())).thenReturn("[{\"random\":\"value\"}]");
    Assert.assertFalse(JobProperties.MSTAGE_DERIVED_FIELDS.validate(state));

    when(state.getProp(JobProperties.MSTAGE_DERIVED_FIELDS.getConfig(), new JsonArray().toString())).thenReturn("[{\"name\":\"value\"}]");
    Assert.assertFalse(JobProperties.MSTAGE_DERIVED_FIELDS.validate(state));

    when(state.getProp(JobProperties.MSTAGE_DERIVED_FIELDS.getConfig(), new JsonArray().toString())).thenReturn("[{\"name\":\"value\", \"formula\":\"formulaValue\"}]");
    Assert.assertTrue(JobProperties.MSTAGE_DERIVED_FIELDS.validate(state));
  }

  /**
   * Test validate for MSTAGE_SECONDARY_INPUT
   */
  @Test
  public void testValidate3() {
    State state = Mockito.mock(State.class);
    when(state.getProp(JobProperties.MSTAGE_SECONDARY_INPUT.getConfig(), new JsonArray().toString())).thenReturn(null);
    Assert.assertFalse(JobProperties.MSTAGE_SECONDARY_INPUT.validate(state));

    when(state.getProp(JobProperties.MSTAGE_SECONDARY_INPUT.getConfig(), new JsonArray().toString())).thenReturn("[{\"name\":\"value\"}]");
    Assert.assertTrue(JobProperties.MSTAGE_SECONDARY_INPUT.validate(state));
  }

  /**
   * Test validate for MSTAGE_SECONDARY_INPUT
   */
  @Test
  public void testValidate4() {
    Assert.assertTrue(JobProperties.MSTAGE_ABSTINENT_PERIOD_DAYS.validate(new State()));
  }

  /**
   * Test validateNonblank for MSTAGE_AUTHENTICATION
   */
  @Test
  public void testValidateNonblank1() {
    State state = Mockito.mock(State.class);
    JsonObject obj = new JsonObject();
    when(state.getProp(JobProperties.MSTAGE_AUTHENTICATION.getConfig(), new JsonObject().toString())).thenReturn(obj.toString());
    Assert.assertFalse(JobProperties.MSTAGE_AUTHENTICATION.validateNonblank(state));

    obj.addProperty("test", "testValue");
    when(state.getProp(JobProperties.MSTAGE_AUTHENTICATION.getConfig(), new JsonObject().toString())).thenReturn(obj.toString());
    Assert.assertFalse(JobProperties.MSTAGE_AUTHENTICATION.validateNonblank(state));

    obj.addProperty("method", "testMethodValue");
    when(state.getProp(JobProperties.MSTAGE_AUTHENTICATION.getConfig(), new JsonObject().toString())).thenReturn(obj.toString());
    Assert.assertFalse(JobProperties.MSTAGE_AUTHENTICATION.validateNonblank(state));

    obj.addProperty("encryption", "testEncryptionValue");
    when(state.getProp(JobProperties.MSTAGE_AUTHENTICATION.getConfig(), new JsonObject().toString())).thenReturn(obj.toString());
    Assert.assertTrue(JobProperties.MSTAGE_AUTHENTICATION.validateNonblank(state));
  }

  /**
   * Test validateNonblank for MSTAGE_CSV_COLUMN_PROJECTION
   */
  @Test
  public void testValidateNonblank2() {
    State state = Mockito.mock(State.class);
    when(state.getProp(JobProperties.MSTAGE_CSV_COLUMN_PROJECTION.getConfig(), StringUtils.EMPTY)).thenReturn(null);
    Assert.assertFalse(JobProperties.MSTAGE_CSV_COLUMN_PROJECTION.validateNonblank(state));

    when(state.getProp(JobProperties.MSTAGE_CSV_COLUMN_PROJECTION.getConfig(), StringUtils.EMPTY)).thenReturn("test");
    Assert.assertTrue(JobProperties.MSTAGE_CSV_COLUMN_PROJECTION.validateNonblank(state));

    when(state.getProp(JobProperties.MSTAGE_CSV_COLUMN_PROJECTION.getConfig(), StringUtils.EMPTY)).thenReturn("test1,test2");
    Assert.assertTrue(JobProperties.MSTAGE_CSV_COLUMN_PROJECTION.validateNonblank(state));
  }

  /**
   * Test validateNonblank
   */
  @Test
  public void testValidateNonblank3() {
    State state = Mockito.mock(State.class);
    when(state.getProp(JobProperties.MSTAGE_BACKFILL.getConfig(), StringUtils.EMPTY)).thenReturn("non-validate");
    Assert.assertFalse(JobProperties.MSTAGE_BACKFILL.validateNonblank(state));

    when(state.getProp(JobProperties.MSTAGE_BACKFILL.getConfig(), StringUtils.EMPTY)).thenReturn("false");
    Assert.assertTrue(JobProperties.MSTAGE_BACKFILL.validateNonblank(state));

    when(state.getProp(JobProperties.MSTAGE_BACKFILL.getConfig(), StringUtils.EMPTY)).thenReturn("true");
    Assert.assertTrue(JobProperties.MSTAGE_BACKFILL.validateNonblank(state));
  }
}
