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
package com.datatorrent.stram.engine;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.apex.api.UserDefinedControlTuple;
import org.apache.commons.lang.UnhandledException;

import com.google.common.collect.Maps;

import com.datatorrent.api.CustomControlTupleEnabledSink;
import com.datatorrent.api.Operator;
import com.datatorrent.api.Operator.InputPort;
import com.datatorrent.api.Sink;
import com.datatorrent.stram.plan.logical.Operators.PortContextPair;
import com.datatorrent.stram.stream.OiOStream;
import com.datatorrent.stram.tuple.CustomControlTuple;
import com.datatorrent.stram.tuple.Tuple;

/**
 * OiONode is driver for the OiO (ThreadLocal) operator.
 *
 * It mostly replicates the functionality of the GenericNode but the logic here is a lot simpler as most of the
 * validation is already handled by the upstream operator.
 *
 * @since 0.3.5
 */
public class OiONode extends GenericNode
{
  private long lastResetWindowId = WindowGenerator.MIN_WINDOW_ID - 1;
  private long lastEndStreamWindowId = WindowGenerator.MAX_WINDOW_ID - 1;
  private int expectingEndWindows = 0;

  public OiONode(Operator operator, OperatorContext context)
  {
    super(operator, context);
  }

  private class ControlSink extends DefaultCustomControlTupleEnabledSink<Tuple>
  {
    final SweepableReservoir reservoir;

    private ControlSink(SweepableReservoir sr)
    {
      reservoir = sr;
    }

    private Map<Long,LinkedHashSet<CustomControlTuple>> customControlTuples = Maps.newHashMap();

    @Override
    public void put(Tuple t)
    {
      switch (t.getType()) {
        case BEGIN_WINDOW:
          expectingEndWindows++;
          if (t.getWindowId() != currentWindowId) {
            currentWindowId = t.getWindowId();
            for (int s = sinks.length; s-- > 0;) {
              sinks[s].put(t);
            }
            controlTupleCount++;

            if (applicationWindowCount == 0) {
              insideWindow = true;
              operator.beginWindow(currentWindowId);
            }
          }
          break;

        case END_WINDOW:
          endWindowDequeueTimes.put(reservoir, System.currentTimeMillis());
          if (--expectingEndWindows == 0) {

            /* process custom control tuples here */

            if (customControlTuples.get(currentWindowId) != null) {
              for (CustomControlTuple cct : customControlTuples.get(currentWindowId)) {
                if (((OiOStream.OiOReservoir)reservoir).getSink() instanceof CustomControlTupleEnabledSink) {
                  if (!((CustomControlTupleEnabledSink)((OiOStream.OiOReservoir)reservoir)
                      .getSink())
                      .putControl((UserDefinedControlTuple)cct.getUserObject())) {

                    // Operator will not handle. Propagate.
                    for (int s = sinks.length; s-- > 0; ) {
                      sinks[s].put(cct);
                    }
                    controlTupleCount++;
                  }
                } else {
                  // Operator will not handle. Propagate.
                  for (int s = sinks.length; s-- > 0; ) {
                    sinks[s].put(cct);
                  }
                  controlTupleCount++;
                }
              }
            }
            processEndWindow(t);
          }
          break;

        case CUSTOM_CONTROL:
          if (!customControlTuples.containsKey(currentWindowId)) {
            customControlTuples.put(currentWindowId, new LinkedHashSet<CustomControlTuple>());
          }
          CustomControlTuple cct = ((CustomControlTuple)t);

          if (!isDuplicate(customControlTuples.get(currentWindowId), cct)) {
            customControlTuples.get(currentWindowId).add(cct);
          }
          break;

        case CHECKPOINT:
          dagCheckpointOffsetCount = 0;
          if (lastCheckpointWindowId < t.getWindowId() && !doCheckpoint) {
            if (checkpointWindowCount == 0) {
              checkpoint(t.getWindowId());
              lastCheckpointWindowId = t.getWindowId();
            } else {
              doCheckpoint = true;
            }

            for (int s = sinks.length; s-- > 0;) {
              sinks[s].put(t);
            }
            controlTupleCount++;
          }
          break;

        case RESET_WINDOW:
          if (t.getWindowId() != lastResetWindowId) {
            lastResetWindowId = t.getWindowId();
            for (int s = sinks.length; s-- > 0;) {
              sinks[s].put(t);
            }
            controlTupleCount++;
          }
          break;

        case END_STREAM:
          if (lastEndStreamWindowId != t.getWindowId()) {
            lastEndStreamWindowId = t.getWindowId();
            for (Entry<String, SweepableReservoir> e: inputs.entrySet()) {
              PortContextPair<InputPort<?>> pcpair = descriptor.inputPorts.get(e.getKey());
              if (pcpair != null) {
                pcpair.component.setConnected(false);
              }
            }
            inputs.clear();

            Iterator<DeferredInputConnection> dici = deferredInputConnections.iterator();
            while (dici.hasNext()) {
              DeferredInputConnection dic = dici.next();
              if (!inputs.containsKey(dic.portname)) {
                dici.remove();
                connectInputPort(dic.portname, dic.reservoir);
              }
            }

            if (inputs.isEmpty()) {
              if (insideWindow) {
                applicationWindowCount = APPLICATION_WINDOW_COUNT - 1;
                expectingEndWindows = 0;
                endWindowDequeueTimes.put(reservoir, System.currentTimeMillis());
                processEndWindow(null);
              }
              emitEndStream();
            }
          }
          break;

        default:
          throw new UnhandledException("Unrecognized Control Tuple", new IllegalArgumentException(t.toString()));
      }
    }

    @Override
    public int getCount(boolean reset)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

  }

  public Sink<Tuple> getControlSink(SweepableReservoir reservoir)
  {
    return new ControlSink(reservoir);
  }

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(OiONode.class);
}
