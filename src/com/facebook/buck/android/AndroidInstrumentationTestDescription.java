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

import com.facebook.buck.model.BuildTarget;
import com.facebook.buck.rules.BuildRule;
import com.facebook.buck.rules.BuildRules;
import com.facebook.buck.rules.BuildRuleParams;
import com.facebook.buck.rules.BuildRuleResolver;
import com.facebook.buck.rules.BuildRuleType;
import com.facebook.buck.rules.Description;
import com.facebook.buck.rules.InstallableApk;
import com.facebook.buck.rules.Hint;
import com.facebook.buck.rules.Label;
import com.facebook.buck.rules.SourcePathResolver;
import com.facebook.buck.util.HumanReadableException;
import com.facebook.infer.annotation.SuppressFieldNotInitialized;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

public class AndroidInstrumentationTestDescription
    implements Description<AndroidInstrumentationTestDescription.Arg> {

  public static final BuildRuleType TYPE = BuildRuleType.of("android_instrumentation_test");

  private final Optional<Long> testRuleTimeoutMs;

  public AndroidInstrumentationTestDescription(Optional<Long> testRuleTimeoutMs) {
    this.testRuleTimeoutMs = testRuleTimeoutMs;
  }

  @Override
  public BuildRuleType getBuildRuleType() {
    return TYPE;
  }

  @Override
  public Arg createUnpopulatedConstructorArg() {
    return new Arg();
  }

  @Override
  public <A extends Arg> AndroidInstrumentationTest createBuildRule(
      BuildRuleParams params,
      BuildRuleResolver resolver,
      A args) {
    BuildRule baseApk = resolver.getRule(args.apk);
    if (!(baseApk instanceof InstallableApk)) {
      throw new HumanReadableException(
          "In %s, apk='%s' must be an android_binary() or apk_genrule() but was %s().",
          params.getBuildTarget(),
          baseApk.getFullyQualifiedName(),
          baseApk.getType());
    }

    BuildRule instrumentationApk = resolver.getRule(args.instrumentationApk);
    if (!(instrumentationApk instanceof AndroidInstrumentationApk)) {
      throw new HumanReadableException(
          "In %s, apk='%s' must be an android_instrumentation_apk() but was %s().",
          params.getBuildTarget(),
          instrumentationApk.getFullyQualifiedName(),
          instrumentationApk.getType());
    }

    return new AndroidInstrumentationTest(
        params.appendExtraDeps(
                BuildRules.getExportedRules(
                    Iterables.concat(
                        params.getDeclaredDeps(),
                        resolver.getAllRules(args.deps.get())))),
        new SourcePathResolver(resolver),
        (InstallableApk) baseApk,
        (AndroidInstrumentationApk) instrumentationApk,
        args.testRunner.orNull(), // If null, RemoteTestRunner uses default test runner
        args.labels.get(),
        args.contacts.get(),
        validateAndGetSourcesUnderTest(
            args.sourceUnderTest.get(),
            resolver),
        testRuleTimeoutMs);
  }

  public static ImmutableSet<BuildRule> validateAndGetSourcesUnderTest(
      ImmutableSet<BuildTarget> sourceUnderTestTargets,
      BuildRuleResolver resolver) {
    ImmutableSet.Builder<BuildRule> sourceUnderTest = ImmutableSet.builder();
    for (BuildTarget target : sourceUnderTestTargets) {
      BuildRule rule = resolver.getRule(target);
      sourceUnderTest.add(rule);
    }
    return sourceUnderTest.build();
  }


  @SuppressFieldNotInitialized
  public static class Arg {
    public BuildTarget apk;
    public BuildTarget instrumentationApk;
    public Optional<ImmutableSortedSet<BuildTarget>> deps;
    public Optional<String> testRunner;
    public Optional<ImmutableSortedSet<Label>> labels;
    public Optional<ImmutableSortedSet<String>> contacts;
    @Hint(isDep = false) public Optional<ImmutableSortedSet<BuildTarget>> sourceUnderTest;

  }

}
