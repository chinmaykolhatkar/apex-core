/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.datatorrent.stram.moduleexperiment.testModule;

import java.util.Random;

import com.datatorrent.api.Context.OperatorContext;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.api.InputOperator;

/**
 * Toy Random Input Operator.
 * Generates random integers
 */
public class RandomInputOperator implements InputOperator
{

  Random r;
  public transient DefaultOutputPort<Integer> output = new DefaultOutputPort<Integer>();
  long sentAt = System.currentTimeMillis();

  @Override
  public void beginWindow(long windowId)
  {
  }

  @Override
  public void endWindow()
  {
  }

  @Override
  public void setup(OperatorContext context)
  {
    r = new Random();
  }

  @Override
  public void teardown()
  {
  }

  @Override
  public void emitTuples()
  {
    if(System.currentTimeMillis() - sentAt > 100){
      output.emit(r.nextInt());
      sentAt = System.currentTimeMillis();
    }
  }
}