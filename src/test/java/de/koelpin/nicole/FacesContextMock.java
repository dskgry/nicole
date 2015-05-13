package de.koelpin.nicole;

import javax.faces.context.FacesContext;

import static org.mockito.Mockito.mock;

/**
 * @author Sven Koelpin
 */
public abstract class FacesContextMock extends FacesContext {
  public static FacesContext mockContext() {
    FacesContext context = mock(FacesContext.class);
    setCurrentInstance(context);
    return context;
  }
}
