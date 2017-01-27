package com.datatorrent.common.util;

import com.datatorrent.api.Operator;

import java.util.Map;

public interface SchemaAware
{
  Map<Operator.OutputPort, Schema> registerSchema(Map<Operator.InputPort, Schema> inputSchema);
}
