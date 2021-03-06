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

package com.facebook.buck.rules.keys;

import com.facebook.buck.rules.RuleKey;
import com.facebook.buck.rules.SourcePathResolver;
import com.facebook.buck.util.cache.FileHashCache;

/**
 * A variant of {@link InputBasedRuleKeyBuilderFactory} which ignores inputs when calculating the
 * {@link RuleKey}, allowing them to specified explicitly.
 */
public class DependencyFileRuleKeyBuilderFactory extends InputBasedRuleKeyBuilderFactory {

  public DependencyFileRuleKeyBuilderFactory(
      FileHashCache hashCache,
      SourcePathResolver pathResolver) {
    super(hashCache, pathResolver, NOOP_ADD_DEPS_TO_RULE_KEY, InputHandling.IGNORE);
  }

}
