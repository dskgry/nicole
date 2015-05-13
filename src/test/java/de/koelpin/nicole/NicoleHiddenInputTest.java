package de.koelpin.nicole;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sven Koelpin
 */
@RunWith(MockitoJUnitRunner.class)
public class NicoleHiddenInputTest {
  private static final String CID_PREFIX = "someForm:someComponent:nicoleWrapper";
  private static final String MODULE_NAME = "testModule";
  private static final String INSTANCE_ID = "instanceId";

  @Rule
  public ExpectedException exception = ExpectedException.none();


  @Test
  public void constructAValidHiddenInputFieldWithInstanceId() {
    NicoleHiddenInput inputToTest = new NicoleHiddenInput(CID_PREFIX, MODULE_NAME, INSTANCE_ID);
    assertThat(inputToTest.getClientIdPrefix(), is(CID_PREFIX));
    assertThat(inputToTest.getFullJsfId(), is(CID_PREFIX + NicoleHiddenInput.ID_SUFFIX));
    assertThat(inputToTest.getInstanceId(), is(INSTANCE_ID));
  }

  @Test
  public void constructAValidHiddenInputFieldWithoutInstanceId() {
    NicoleHiddenInput inputToTest = new NicoleHiddenInput(CID_PREFIX, MODULE_NAME, null);
    assertThat(inputToTest.getClientIdPrefix(), is(CID_PREFIX));
    assertThat(inputToTest.getFullJsfId(), is(CID_PREFIX + NicoleHiddenInput.ID_SUFFIX));
    assertThat(inputToTest.getInstanceId(), is(nullValue()));
  }

  @Test
  public void constructAnInvalidHiddenInputFieldWithoutCid() {
    exception.expect(IllegalArgumentException.class);
    new NicoleHiddenInput(null, MODULE_NAME, null);
  }

  @Test
  public void constructAnInvalidHiddenInputFieldWithoutModuleName() {
    exception.expect(IllegalArgumentException.class);
    new NicoleHiddenInput(CID_PREFIX, null, null);
  }


  @Test
  public void allDataAttributesAreSet() throws IOException {
    FacesContext mockContext = FacesContextMock.mockContext();
    ResponseWriter responseWriterMock = mock(ResponseWriter.class);
    when(mockContext.getResponseWriter()).thenReturn(responseWriterMock);
    NicoleHiddenInput nicoleHiddenInput = new NicoleHiddenInput(CID_PREFIX, MODULE_NAME, INSTANCE_ID);
    nicoleHiddenInput.encodeBegin(mockContext);
    assertThat(nicoleHiddenInput.getDataAttributes().size(), is(3));
    assertThat(nicoleHiddenInput.getDataAttributes().get(NicoleHiddenInput.DATA_PREFIX + Nicole.MODULE_NAME),
        is(MODULE_NAME));
    assertThat(nicoleHiddenInput.getDataAttributes().get(NicoleHiddenInput.DATA_PREFIX + Nicole.INSTANCE_ID),
        is(INSTANCE_ID));
    assertThat(nicoleHiddenInput.getDataAttributes().get(NicoleHiddenInput.DATA_PREFIX + NicoleHiddenInput.CLIENT_ID),
        is(CID_PREFIX.replace(":nicoleWrapper", "")));

    nicoleHiddenInput = new NicoleHiddenInput(CID_PREFIX, MODULE_NAME, null);
    nicoleHiddenInput.addDataAttribute("foo", "bar");
    nicoleHiddenInput.encodeBegin(mockContext);
    assertThat(nicoleHiddenInput.getDataAttributes().size(), is(3));
    assertThat(nicoleHiddenInput.getDataAttributes().get(NicoleHiddenInput.DATA_PREFIX + Nicole.MODULE_NAME),
        is(MODULE_NAME));
    assertThat(nicoleHiddenInput.getDataAttributes().get(NicoleHiddenInput.DATA_PREFIX + Nicole.INSTANCE_ID),
        is(nullValue()));
    assertThat(nicoleHiddenInput.getDataAttributes().get(NicoleHiddenInput.DATA_PREFIX + NicoleHiddenInput.CLIENT_ID),
        is(CID_PREFIX.replace(":nicoleWrapper", "")));
    assertThat(nicoleHiddenInput.getDataAttributes().get(NicoleHiddenInput.DATA_PREFIX + "foo"), is("bar"));
  }

  @Test
  public void dataClientIdAttributeIsCreatedCorrectly() throws IOException {
    FacesContext mockContext = FacesContextMock.mockContext();
    ResponseWriter responseWriterMock = mock(ResponseWriter.class);
    when(mockContext.getResponseWriter()).thenReturn(responseWriterMock);
    NicoleHiddenInput nicoleHiddenInput = new NicoleHiddenInput(CID_PREFIX, MODULE_NAME, INSTANCE_ID);
    nicoleHiddenInput.encodeBegin(mockContext);
    assertThat(nicoleHiddenInput.getDataAttributes().get(NicoleHiddenInput.DATA_PREFIX + NicoleHiddenInput.CLIENT_ID),
        is(CID_PREFIX.replace(":nicoleWrapper", "")));

    nicoleHiddenInput = new NicoleHiddenInput("someForm:nicoleWrapper", MODULE_NAME, INSTANCE_ID);
    nicoleHiddenInput.encodeBegin(mockContext);
    assertThat(nicoleHiddenInput.getDataAttributes().get(NicoleHiddenInput.DATA_PREFIX + NicoleHiddenInput.CLIENT_ID),
        is("someForm"));

    nicoleHiddenInput = new NicoleHiddenInput("nicoleWrapper", MODULE_NAME, INSTANCE_ID);
    nicoleHiddenInput.encodeBegin(mockContext);
    assertThat(nicoleHiddenInput.getDataAttributes().get(NicoleHiddenInput.DATA_PREFIX + NicoleHiddenInput.CLIENT_ID),
        is(""));
  }


  @Test
  public void inputFieldIsEncodedCorrectly() throws IOException {
    FacesContext mockContext = FacesContextMock.mockContext();
    ResponseWriter responseWriterMock = mock(ResponseWriter.class);
    final StringBuilder inputStringBuilder = new StringBuilder();

    doAnswer(invocation -> {
      inputStringBuilder.append("<").append((String) invocation.getArguments()[0]).append(" ");
      return inputStringBuilder;
    }).when(responseWriterMock).startElement(eq(NicoleHiddenInput.INPUT), any(NicoleHiddenInput.class));

    doAnswer(invocation -> {
      inputStringBuilder.append(" ").append("/>");
      return inputStringBuilder;
    }).when(responseWriterMock).endElement(eq(NicoleHiddenInput.INPUT));

    doAnswer(invocation -> {
      inputStringBuilder.append((String) invocation.getArguments()[0])
          .append("=")
          .append((String) invocation.getArguments()[1])
          .append(" ");
      return inputStringBuilder;
    }).when(responseWriterMock).writeAttribute(any(String.class), any(String.class), any());
    when(mockContext.getResponseWriter()).thenReturn(responseWriterMock);


    NicoleHiddenInput nicoleHiddenInput = new NicoleHiddenInput(CID_PREFIX, MODULE_NAME, INSTANCE_ID);
    nicoleHiddenInput.encodeBegin(mockContext);
    assertThat(inputStringBuilder.toString(),
        is("<input id=someForm:someComponent:nicoleWrapper:nicole type=hidden data-modulename=testModule data-clientid=someForm:someComponent data-instanceId=instanceId  />"));


    inputStringBuilder.setLength(0);

    nicoleHiddenInput = new NicoleHiddenInput(CID_PREFIX, MODULE_NAME, null);
    nicoleHiddenInput.addDataAttribute("foo", "bar");
    nicoleHiddenInput.encodeBegin(mockContext);
    assertThat(inputStringBuilder.toString(),
        is("<input id=someForm:someComponent:nicoleWrapper:nicole type=hidden data-foo=bar data-modulename=testModule data-clientid=someForm:someComponent  />"));
  }

}
