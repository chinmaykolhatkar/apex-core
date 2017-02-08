package org.apache.apex.api;

/**
 * Created by dtadmin on 08-Feb-17.
 */
public abstract class SchemaAwareInputPort extends ControlAwareDefaultInputPort
{
  @Override
  public boolean processControl(UserDefinedControlTuple payload)
  {
    if (payload instanceof Schema) {
      processSchema((Schema) payload);
      return true;
    }
    return false;
  }

  public abstract void processSchema(Schema schema);
}
