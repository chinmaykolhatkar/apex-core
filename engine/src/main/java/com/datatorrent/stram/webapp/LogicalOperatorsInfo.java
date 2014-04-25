/**
 * Copyright (c) 2012-2013 DataTorrent, Inc.
 * All rights reserved.
 */
package com.datatorrent.stram.webapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * Provides plan level operator data<p>
 * <br>
 * This call provides restful access to individual operator instance data<br>
 * <br>
 *
 * @since 0.3.2
 */

@XmlRootElement(name = "operators")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({OperatorInfo.class})
public class LogicalOperatorsInfo {

  protected List<LogicalOperatorInfo> operators = new ArrayList<LogicalOperatorInfo>();

  /**
   *
   * @param operatorInfo
   */
  public void add(LogicalOperatorInfo operatorInfo) {
    operators.add(operatorInfo);
  }

  /**
   *
   * @return list of operator info
   *
   */
  public List<LogicalOperatorInfo> getOperators() {
    return Collections.unmodifiableList(operators);
  }

}