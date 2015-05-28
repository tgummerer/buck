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
import com.facebook.buck.rules.InstallableApk;
import com.facebook.buck.step.ExecutionContext;
import com.facebook.buck.step.Step;
import com.google.common.base.Optional;

public class ApkInstallStep implements Step {

  private final InstallableApk installableApk;

  public ApkInstallStep(InstallableApk apk) {
    this.installableApk = apk;
  }

  public ApkInstallStep(AndroidInstrumentationApk apk) {
    this.installableApk = apk;
  }

  @Override
  public int execute(ExecutionContext context) throws InterruptedException {
    final AdbHelper adbHelper = new AdbHelper(
        new AdbOptions(),
        new TargetDeviceOptions(),
        context,
        context.getConsole(),
        Optional.<BuckEventBus>absent(),
        false);

    boolean installSuccess;
    // TODO: make this work with exopackages
    installSuccess = adbHelper.installApk(installableApk, false);
    if (!installSuccess) {
      return 1;
    }
    return 0;
  }

  @Override
  public String getShortName() {
    return "install apk";
  }

  @Override
  public String getDescription(ExecutionContext context) {
    // TODO: return description
    return "Install Apk on connected devices";
  }

}
