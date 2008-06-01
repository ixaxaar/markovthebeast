package thebeast.pml;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 22-Feb-2007 Time: 20:02:08
 */
public interface HasProperties {

  void setProperty(PropertyName name, Object value);
  Object getProperty(PropertyName name);

}
