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

package com.facebook.buck.test;

import com.facebook.buck.test.result.type.ResultType;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.IOException;
import java.nio.file.Path;

public class AdbTestResultParser {

  /** Utility Class:  Do not instantiate. */
  private AdbTestResultParser() {}

  public static TestResultSummary parse(
      String fullyQualifiedName,
      Path adbOutputFile)
      throws IOException {
    String fileContent = Files.toString(adbOutputFile.toFile(), Charsets.UTF_8);
    String message;
    ResultType type;
    System.out.println(adbOutputFile);
    if (fileContent.contains("FAILURES!!!")) {
      type = ResultType.FAILURE;
      message = "failure";
    } else {
      type = ResultType.SUCCESS;
      message = null;
    }
    TestResultSummary testResult = new TestResultSummary(
        fullyQualifiedName,
        "testName",
        type,
        0L,
        message,
        null,
        fileContent,
        null);

    return testResult;
  }

}
