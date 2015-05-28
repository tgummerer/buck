/*
 * Copyright 2015-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.android;

import com.facebook.buck.event.BuckEventBus;
import com.facebook.buck.step.ExecutionContext;
import com.facebook.buck.step.Step;
import com.google.common.base.Optional;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.XmlTestRunListener;

import java.io.IOException;
import java.nio.file.Path;

public class InstrumentationStep implements Step {

  private final AndroidInstrumentationApk instrumentationApk;
  private final String testRunner;
  private final Path directoryForTestResults;

  private Optional<Long> testRuleTimeoutMs;

  public InstrumentationStep(
      AndroidInstrumentationApk apk,
      String testRunner,
      Path directoryForTestResults,
      Optional<Long> testRuleTimeoutMs) {
    this.instrumentationApk = apk;
    this.testRunner = testRunner;
    this.directoryForTestResults = directoryForTestResults;
    this.testRuleTimeoutMs = testRuleTimeoutMs;
  }

  @Override
  public int execute(ExecutionContext context)
      throws
      IOException,
      InterruptedException {
    String packageName = AdbHelper.tryToExtractPackageNameFromManifest(
        instrumentationApk,
        context);

    try {
      AdbHelper helper = new AdbHelper(
          new AdbOptions(),
          new TargetDeviceOptions(),
          context,
          context.getConsole(),
          Optional.<BuckEventBus>absent(),
          false);
      for (IDevice device : helper.getDevices()) {
        RemoteAndroidTestRunner runner = new RemoteAndroidTestRunner(
            packageName,
            testRunner,
            device);

        XmlTestRunListener listener = new XmlTestRunListener();
        listener.setReportDir(directoryForTestResults.toFile());
        runner.run(listener);
      }
    } catch (IOException e) {
      e.printStackTrace(context.getStdErr());
      return 1;
    } catch(TimeoutException|AdbCommandRejectedException|ShellCommandUnresponsiveException e) {
      return 1;
    }
    return 0;
  }

  @Override
  public String getShortName() {
    return "instrumentationTest";
  }

  protected Optional<Long> getTimeout() {
    return testRuleTimeoutMs;
  }

  @Override
  public String getDescription(ExecutionContext context) {
    return "Execute the InstrumentationTest on the Connected Devices";
  }

}
