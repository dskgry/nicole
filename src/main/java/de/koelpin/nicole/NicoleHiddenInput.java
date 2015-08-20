package de.koelpin.nicole;

import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This renders the custom nicole-hidden-input field
 * with the required and optional data-* attributes
 *
 * @author Sven Koelpin
 */
public class NicoleHiddenInput extends HtmlInputHidden {
  protected static final String CLIENT_ID = "clientid";
  protected static final String ID_SUFFIX = ":nicole";
  protected static final String INPUT = "input";
  protected static final String ID = "id";
  protected static final String TYPE = "type";
  protected static final String HIDDEN = "hidden";
  protected static final String DATA_PREFIX = "data-";

  private Map<String, Object> dataAttributes = new LinkedHashMap<>();
  private String fullJsfId;
  private String clientIdPrefix;
  private String instanceId;
  private String moduleName;

  /**
   * @param clientIdPrefix the clientId-prefix of the parent component (to create a full-qualified jsf-id for the input field)
   * @param moduleName     the name of the module which is set in the composite-interface (is translated to data-clientid in the input-field)
   * @param instanceId     the optional client-variable-id of the nicole module
   */
  public NicoleHiddenInput(String clientIdPrefix, Object moduleName, Object instanceId) {
    setClientIdPrefix(clientIdPrefix);
    setModuleName(moduleName);
    setInstanceId(instanceId);
  }

  @Override
  public void encodeBegin(FacesContext context) throws IOException {
    //add data attributes
    initialize();

    //write HTML-representation of input
    ResponseWriter writer = context.getResponseWriter();
    writer.startElement(INPUT, this);
    writer.writeAttribute(ID, getFullJsfId(), null);
    writer.writeAttribute(TYPE, HIDDEN, null);
    for (Map.Entry<String, Object> dataEntry : dataAttributes.entrySet()) {
      writer.writeAttribute(dataEntry.getKey(), String.valueOf(dataEntry.getValue()), null);
    }
    writer.endElement(INPUT);
  }

  /**
   * adds the data-attributes to the hidden input
   * getPassThroughAttributes() is not used here to be
   * compatible with JEE 6
   */
  private void initialize() {
    //always add modulename and client id
    this.addDataAttribute(Nicole.MODULE_NAME, getModuleName());
    this.addDataAttribute(CLIENT_ID, createClientIdForComponent());
    //set instance id if it was declared
    if (getInstanceId() != null) {
      this.addDataAttribute(Nicole.INSTANCE_ID, getInstanceId());
    }
  }

  private String createClientIdForComponent() {
    String[] idSplit = getClientIdPrefix().split(":");
    if (idSplit.length > 1) {
      idSplit = Arrays.copyOfRange(idSplit, 0, idSplit.length - 1);
    } else {
      //nicole was placed outside of a form
      return "";
    }
    StringBuilder clientId = new StringBuilder();
    for (String id : idSplit) {
      clientId.append(id).append(":");
    }
    clientId.deleteCharAt(clientId.length() - 1);
    return clientId.toString();
  }

  /**
   * Add a data-attribute to the input field. The attributes are rendered
   * on encode-begin
   *
   * @param name  name of the data-attribute without the data-* prefix
   * @param value value of the data-atteibute
   */
  public void addDataAttribute(String name, Object value) {
    dataAttributes.put(DATA_PREFIX + name, value);
  }


  private void setClientIdPrefix(String clientIdPrefix) {
    if (clientIdPrefix == null) {
      throw new IllegalArgumentException("clientIdPrefix cannot be null");
    }
    this.clientIdPrefix = clientIdPrefix;
    this.fullJsfId = this.clientIdPrefix + ID_SUFFIX;
  }

  private void setInstanceId(Object instanceId) {
    if (instanceId != null && instanceId instanceof String) {
      this.instanceId = (String) instanceId;
    }
  }

  private void setModuleName(Object moduleName) {
    if (moduleName == null || !(moduleName instanceof String)) {
      throw new IllegalArgumentException("moduleName cannot be null and must be a string.");
    }
    this.moduleName = (String) moduleName;
  }

  protected String getClientIdPrefix() {
    return clientIdPrefix;
  }

  protected String getInstanceId() {
    return instanceId;
  }

  protected String getModuleName() {
    return moduleName;
  }

  protected String getFullJsfId() {
    return fullJsfId;
  }

  protected Map<String, Object> getDataAttributes() {
    return dataAttributes;
  }
}
