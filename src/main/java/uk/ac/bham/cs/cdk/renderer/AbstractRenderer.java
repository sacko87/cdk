/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.bham.cs.cdk.renderer;

// i'm being nice
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.vecmath.Point2d;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.BoundsCalculator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.ZoomFactor;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.elements.WedgeLineElement;

/**
 *
 *
 * @author sacko
 * @param <T>
 */
public abstract class AbstractRenderer<T> {
    /**
     *
     */
    protected final double DEFAULT_SCALE = 30;

    /**
     *
     */
    protected final double DEFAULT_XPAD = 2;

    /**
     *
     */
    protected final double DEFAULT_YPAD = 2;

    /**
     *
     */
    protected static final double DEFAULT_WIDTH = 400.;

    /**
     *
     */
    protected static final double DEFAULT_HEIGHT = 400.;

    /**
     *
     */
    protected final Font DEFAULT_FONT = new Font("serif", Font.PLAIN, 15);

    /**
     *
     */
    private Color color;

    /**
     *
     */
    private BasicStroke stroke;

    /**
     *
     */
    protected AffineTransform transform;

    /**
     *
     */
    private final RendererModel model;

    /**
     *
     */
    private Point2d modelCentre = new Point2d(0, 0);

    /**
     *
     */
    private Point2d drawingCentre = new Point2d(100, 100);

    /**
     *
     */
    private final List<IGenerator<IAtomContainer>> generators;

    /**
     *
     * @param model
     */
    protected AbstractRenderer(RendererModel model, List<IGenerator<IAtomContainer>> generators) {
        this.model = model;
        this.generators = generators;
        for(IGenerator<IAtomContainer> generator: this.generators) {
            this.model.registerParameters(generator);
        }

        this.updateTransformer();
    }

    /**
     *
     * @return
     */
    public Color getColor() {
        return color;
    }

    /**
     *
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     *
     * @return
     */
    public Point2d getModelCentre() {
        return this.modelCentre;
    }

    /**
     *
     * @param modelCentre
     */
    public void setModelCentre(Point2d modelCentre) {
        this.modelCentre = modelCentre;
        this.updateTransformer();
    }

    /**
     *
     * @return
     */
    public Point2d getDrawingCentre() {
        return this.drawingCentre;
    }

    /**
     *
     * @param drawingCentre
     */
    public void setDrawingCentre(Point2d drawingCentre) {
        this.drawingCentre = drawingCentre;
        this.updateTransformer();
    }

    /**
     *
     * @return
     */
    public final Stroke getStroke() {
        return this.stroke;
    }

    /**
     *
     * @param stroke
     */
    public final void setStroke(Stroke stroke) {
        if(stroke instanceof BasicStroke) {
            this.stroke = (BasicStroke) stroke;
        }
    }

    /**
     *
     * @return
     */
    public final Double getScale() {
       return this.getModel().getParameter(Scale.class).getValue();
    }

    /**
     *
     * @param atomContainer
     */
    public final void setScale(IAtomContainer atomContainer) {
        this.getModel().getParameter(Scale.class).setValue(this.calculateScaleForBondLength(GeometryTools.getBondLengthAverage(atomContainer)));
        this.updateTransformer();
    }

    /**
     *
     * @return
     */
    public final Double getZoom() {
       return this.getModel().getParameter(ZoomFactor.class).getValue();
    }

    /**
     *
     * @param value
     */
    public final void setZoom(Double value) {
        this.getModel().getParameter(ZoomFactor.class).setValue(value);
        this.updateTransformer();
    }

    /**
     *
     * @return
     */
    public RendererModel getModel() {
        return this.model;
    }

    /**
     *
     * @return
     */
    public List<IGenerator<IAtomContainer>> getGenerators() {
        return this.generators;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    protected Point2d XY(Double x, Double y) {
        double[] i = new double[] {
            x, y
        };

        this.transform.transform(i, 0, i, 0, 1);

        return new Point2d(i);
    }

    /**
     *
     * @param element
     * @return
     */
    protected abstract Point2d WH(T element);

    /**
     *
     */
    protected final void updateTransformer() {
        this.transform = new AffineTransform();
        this.transform.translate(this.drawingCentre.x, this.drawingCentre.y);
        this.transform.scale(1, -1);
        this.transform.scale(this.getScale(), this.getScale());
        this.transform.scale(this.getZoom(), this.getZoom());
        this.transform.translate(-this.modelCentre.x, -this.modelCentre.y);
    }

    /**
     * Given a bond length for a model, calculate the scale that will transform
     * this length to the on screen bond length in RendererModel.
     *
     * @param bondLenght the average bond length of the model
     * @return the scale necessary to transform this to a screen bond
     */
    public double calculateScaleForBondLength(Double bondLenght) {
        if (Double.isNaN(bondLenght) || bondLenght == 0) {
            return DEFAULT_SCALE;
        } else {
            return getModel().getParameter(
                    BasicSceneGenerator.BondLength.class).getValue() / bondLenght;
        }
    }

    /**
     *
     * @param atomContainer
     * @param width
     * @param height
     * @return
     */
    public T render(IAtomContainer atomContainer) {
        this.setScale(atomContainer);
        this.setDrawingCentre(new Point2d(DEFAULT_WIDTH / 2, DEFAULT_HEIGHT / 2));
        ElementGroup diagram = new ElementGroup();
        for(IGenerator<IAtomContainer> generator: this.getGenerators()) {
            diagram.add(generator.generate(atomContainer, this.getModel()));
        }

        return this.render(diagram, atomContainer);
    }

    /**
     *
     * @param element
     * @param atomContainer
     * @return
     */
    protected T render(IRenderingElement element, IAtomContainer atomContainer) {
        Rectangle2D boundBox = BoundsCalculator.calculateBounds(atomContainer);
        this.setModelCentre(new Point2d(boundBox.getCenterX(), boundBox.getCenterY()));

        return this.render(element);
    }

    /**
     *
     * @param element
     * @return
     */
    protected T render(IRenderingElement element) {
        // save current colours/stroke
        Color  pColor = this.getColor();
        Stroke pStroke = this.getStroke();

        T result;
        // generate the result
        if(element instanceof WedgeLineElement) {
            result = this.render((WedgeLineElement) element);
        } else if(element instanceof LineElement) {
            result = this.render((LineElement) element);
        } else if(element instanceof ElementGroup) {
            result = this.render((ElementGroup) element);
        } else if(element instanceof AtomSymbolElement) {
            result = this.render((AtomSymbolElement) element);
        } else {
            throw new UnsupportedOperationException(
                    "The rendering of " + element.getClass().getCanonicalName()
                            + " is not supported.");
        }

        // restore the colours/strokes
        this.setColor(pColor);
        this.setStroke(pStroke);

        return result;
    }


    /**
     *
     * @param element
     * @return
     */
    protected abstract T render(WedgeLineElement element);


    /**
     *
     * @param element
     * @return
     */
    protected abstract T render(LineElement element);

    /**
     *
     * @param element
     * @return
     */
    protected abstract T render(ElementGroup element);

    /**
     *
     * @param element
     * @return
     */
    protected abstract T render(AtomSymbolElement element);

    /**
     *
     * @param element
     */
    protected abstract void setFill(T element);

    /**
     *
     * @param element
     */
    protected abstract void setStroke(T element);
}
