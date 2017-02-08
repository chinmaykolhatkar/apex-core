package org.apache.apex.api;

/**
 * Created by dtadmin on 08-Feb-17.
 */
public class SchemaAwareOutputPort extends ControlAwareDefaultOutputPort
{
  public void emitSchema(Schema schema) {
    super.emitControl(schema);
  }
}
