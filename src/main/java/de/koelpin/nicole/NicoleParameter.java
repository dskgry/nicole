package de.koelpin.nicole;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

/**
 * @author Sven Koelpin
 */
@FacesComponent(value = "de.koelpin.NicoleParameter")
public class NicoleParameter extends UINamingContainer {
  protected static final String NAME = "name";
  protected static final String VALUE = "value";


  public String getParameterName() {
    return (String) getAttributes().get(NAME);
  }

  public Object getParameterValue() {
    return getAttributes().get(VALUE);
  }
}
