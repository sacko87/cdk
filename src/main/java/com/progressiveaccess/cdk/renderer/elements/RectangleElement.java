/* Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.progressiveaccess.cdk.renderer.elements;

import java.awt.Color;

import org.openscience.cdk.interfaces.IChemObject;


/**
 * A rectangle, with width and height.
 *
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class RectangleElement extends org.openscience.cdk.renderer.elements.RectangleElement implements ILinkedElement {
  private IChemObject chemicalObject;

  public RectangleElement(double xCoord, double yCoord, double width, double height, boolean filled, Color color) {
    super(xCoord, yCoord, width, height, filled, color);
  }

  public RectangleElement(double xCoord1, double yCoord1, double xCoord2, double yCoord2, Color color) {
    super(xCoord1, yCoord1, xCoord2, yCoord2, color);
  }

  @Override
  public IChemObject getChemicalObject() {
    return this.chemicalObject;
  }

  @Override
  public void setChemicalObject(IChemObject chemicalObject) {
    this.chemicalObject = chemicalObject;
  }
}
