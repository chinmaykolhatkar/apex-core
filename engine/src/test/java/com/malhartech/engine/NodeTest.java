/*
 *  Copyright (c) 2012-2013 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.engine;

import com.malhartech.api.Context.OperatorContext;
import com.malhartech.api.DAG;
import com.malhartech.api.InputOperator;
import com.malhartech.api.Operator;
import com.malhartech.stram.StramLocalCluster;
import org.apache.hadoop.conf.Configuration;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Chetan Narsude <chetan@malhar-inc.com>
 */
public class NodeTest
{
  static class TestGenericOperator implements Operator
  {
    static int beginWindows;
    static int endWindows;

    @Override
    public void beginWindow(long windowId)
    {
      beginWindows++;
    }

    @Override
    public void endWindow()
    {
      endWindows++;
    }

    @Override
    public void setup(OperatorContext context)
    {
      beginWindows = 0;
      endWindows = 0;
    }

    @Override
    public void teardown()
    {
    }

  }

  static class TestInputOperator implements InputOperator
  {
    static int beginWindows;
    static int endWindows;

    @Override
    public void emitTuples()
    {
    }

    @Override
    public void beginWindow(long windowId)
    {
      beginWindows++;
    }

    @Override
    public void endWindow()
    {
      endWindows++;
    }

    @Override
    public void setup(OperatorContext context)
    {
      beginWindows = 0;
      endWindows = 0;
    }

    @Override
    public void teardown()
    {
    }

  }

  public NodeTest()
  {
  }

  @Ignore
  @Test
  public void testStreamingWindowGenericNode() throws Exception
  {
    DAG dag = new DAG(new Configuration(false));
    dag.getAttributes().attr(DAG.STRAM_WINDOW_SIZE_MILLIS).set(10);
    dag.addOperator("GenericOperator", new TestGenericOperator());

    final StramLocalCluster lc = new StramLocalCluster(dag);
    lc.run(2000);



  }

  @Test
  public void testApplicationWindowGenericNode()
  {
  }

  @Test
  public void testStreamingWindowInputNode()
  {
  }

  @Test
  public void testApplicationWindowInputNode()
  {
  }

}