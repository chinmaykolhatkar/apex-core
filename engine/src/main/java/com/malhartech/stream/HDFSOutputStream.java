/*
 *  Copyright (c) 2012 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.stream;

import com.malhartech.dag.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chetan Narsude <chetan@malhar-inc.com>
 */
/**
 * Adapter for writing to HDFS<p>
 * <br>
 * Serializes tuples into a HDFS file<br>
 * Currently all tuples are written to a single HDFS file<br>
 * Future enhancements include options to write into a time slot/windows based files<br>
 * <br>
 *
 */
public class HDFSOutputStream implements Stream, Sink
{
  private static org.slf4j.Logger LOG = LoggerFactory.getLogger(HDFSOutputStream.class);
  private FSDataOutputStream output;
  private SerDe serde;
  private FileSystem fs;
  private Path filepath;
  private boolean append;

  @Override
  public void setup(StreamConfiguration config)
  {
    try {
      fs = FileSystem.get(config);
      filepath = new Path(config.get("filepath"));
      append = config.getBoolean("append", true);
    }
    catch (IOException ex) {
      LOG.info("", ex);
    }
  }

  @Override
  public void teardown()
  {
    fs = null;
    filepath = null;
    append = false;
  }

  @Override
  public void doSomething(Tuple t)
  {
    switch (t.getType()) {
      case SIMPLE_DATA:
      case PARTITIONED_DATA:
        LOG.debug("writing out " + t.getObject());
        byte[] serialized = serde.toByteArray(t.getObject());
        try {
          output.write(serialized);
        }
        catch (IOException ex) {
          LOG.info("", ex);
        }
        break;

      default:
        LOG.debug("ignoring tuple " + t);
        break;
    }
  }

  public boolean hasFinished()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void activate(StreamContext context)
  {
    serde = context.getSerDe();
    try {
      if (fs.exists(filepath)) {
        if (append) {
          output = fs.append(filepath);
        }
        else {
          fs.delete(filepath, true);
          output = fs.create(filepath);
        }
      }
      else {
        output = fs.create(filepath);
      }
    }
    catch (IOException ex) {
      Logger.getLogger(HDFSOutputStream.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void deactivate()
  {
    try {
      output.close();
      output = null;
    }
    catch (IOException ex) {
      LOG.info("", ex);
    }

    serde = null;
  }
}
