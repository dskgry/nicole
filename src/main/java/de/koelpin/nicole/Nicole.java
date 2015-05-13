package de.koelpin.nicole;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * This class represents the naming-container for the nicole-component.
 * The nicole-hidden-input field is created and added to the component in this class
 *
 * @author Sven Koelpin
 */
@FacesComponent(value = "de.koelpin.Nicole")
@ResourceDependencies({
    @ResourceDependency(library = "javax.faces", name = "jsf.js", target = "body"),
    @ResourceDependency(library = "scripts", name = "dist/nicole.min.js", target = "body")
})
public class Nicole extends UINamingContainer {
  protected static final String MODULE_NAME = "modulename";
  protected static final String INSTANCE_ID = "instanceId";


  @Override
  public void encodeBegin(FacesContext context) throws IOException {
    //encode nicoles' hidden input into the component.
    //the input is NOT added to the component tree, since
    //it's only needed on the client-side
    NicoleHiddenInput nicoleHiddenInput = new NicoleHiddenInput(
        this.getClientId(),
        getAttributes().get(MODULE_NAME),
        getAttributes().get(INSTANCE_ID));

    //add all values from the nicole-parameters
    // (which are declared as children of the nicole-module)
    for (UIComponent child : getChildren()) {
      if (child instanceof NicoleParameter) {
        NicoleParameter parameter = (NicoleParameter) child;
        nicoleHiddenInput.addDataAttribute(parameter.getParameterName(), parameter.getParameterValue());
      }
    }

    //render the nicole-input
    nicoleHiddenInput.encodeBegin(context);
    super.encodeBegin(context);
  }
}
