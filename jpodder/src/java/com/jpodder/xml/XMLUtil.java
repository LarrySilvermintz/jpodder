package com.jpodder.xml;

import net.n3.nanoxml.*;
import java.util.*;

public class XMLUtil {
  public XMLUtil() {
  }

  /**
 *
 * @param elem IXMLElement
 * @param name String
 * @return Vector
 */
public static Vector getElementNamed(IXMLElement elem, String name) {
  Vector nodeList = new Vector();

  if( elem == null){
    return nodeList;
  }
  Vector children = elem.getChildren();
  Iterator it = children.iterator();
  while (it.hasNext()) {
    IXMLElement child = (IXMLElement) it.next();
    String cName = child.getName();
    if( cName != null)
      if (cName.equals(name)) {
        nodeList.add(child);
    }
    nodeList.addAll(getElementNamed(child, name));
  }
  return nodeList;
}


}
