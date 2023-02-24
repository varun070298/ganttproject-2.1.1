/*
 * Created on 17.06.2004
 *
 */
package net.sourceforge.ganttproject.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author bard
 */
class GraphicPrimitiveContainer {
    private ArrayList myRectangles = new ArrayList();

    private ArrayList myLines = new ArrayList();

    private ArrayList myTexts = new ArrayList();

    private Map myModelObject2primitive = new WeakHashMap();

    private List myLayers = new ArrayList();

    static class GraphicPrimitive {
        private Color myBackgroundColor;

        private Color myForegroundColor;

        private String myStyleName;

        private Object myModelObject;

        
        public void setStyle(String styleName) {
            myStyleName = styleName;
        }

        public String getStyle() {
            return myStyleName;
        }

        public Color getBackgroundColor() {
            return myBackgroundColor;
        }

        public void setBackgroundColor(Color myBackgroundColor) {
            this.myBackgroundColor = myBackgroundColor;
        }

        public Color getForegroundColor() {
            return myForegroundColor;
        }

        public void setForegroundColor(Color myForegroundColor) {
            this.myForegroundColor = myForegroundColor;
        }

        /**
         * @return
         */
        public Object getModelObject() {
            return myModelObject;
        }

        /**
         * @param modelObject
         */
        void setModelObject(Object modelObject) {
            myModelObject = modelObject;
        }
    }

    static class Rectangle extends GraphicPrimitive {
        final int myLeftX;

        final int myTopY;

        final int myWidth;

        final int myHeight;

        private Rectangle(int leftx, int topy, int width, int height) {
            myLeftX = leftx;
            myTopY = topy;
            myWidth = width;
            myHeight = height;
        }

        public int getRightX() {
            return myLeftX + myWidth;
        }

        public int getBottomY() {
            return myTopY + myHeight;
        }

        public String toString() {
            return "leftx=" + myLeftX + " topy=" + myTopY + " width=" + myWidth
                    + " height=" + myHeight;
        }

        public int getMiddleY() {
            return myTopY + myHeight / 2;
        }

        public int getMiddleX() {
            return myLeftX + myWidth / 2;
        }
    }

    static class Line extends GraphicPrimitive {
        private final int myStartX;

        private final int myStartY;

        private final int myFinishX;

        private final int myFinishY;

        private Line(int startx, int starty, int finishx, int finishy) {
            myStartX = startx;
            myStartY = starty;
            myFinishX = finishx;
            myFinishY = finishy;
        }
    }

    static class Text extends GraphicPrimitive {
        private final int myLeftX;

        private final int myBottomY;

        private final String myText;

        private Font myFont;

        private int myMaxLength;

        private HAlignment myHAlignment = HAlignment.LEFT;

        private VAlignment myVAlignment = VAlignment.BOTTOM;

        Text(int leftX, int bottomY, String text) {
            myLeftX = leftX;
            myBottomY = bottomY;
            myText = text;
            myMaxLength = -1;
        }

        public void setFont(Font font) {
            myFont = font;
        }

        public void setMaxLength(int maxLength) {
            myMaxLength = maxLength;
        }

        public int getMaxLength() {
            return myMaxLength;
        }

        public Font getFont() {
            return myFont;
        }

        public String getText() {
            return myText;
        }

        public int getLeftX() {
            return myLeftX;
        }

        public int getBottomY() {
            return myBottomY;
        }
        
        public void setAlignment(HAlignment halignment, VAlignment valignment) {
            myHAlignment = halignment;
            myVAlignment = valignment;
        }
        
        public HAlignment getHAlignment() {
            return myHAlignment;
        }
        
        public VAlignment getVAlignment() {
            return myVAlignment;
        }
    }

    static final class HAlignment {
        public static final HAlignment CENTER = new HAlignment();
        public static final HAlignment LEFT = new HAlignment();
        public static final HAlignment RIGHT = new HAlignment();
    }
    
    static final class VAlignment {
        public static final VAlignment CENTER = new VAlignment();
        public static final VAlignment TOP = new VAlignment();
        public static final VAlignment BOTTOM = new VAlignment();
        
    }
    public GraphicPrimitiveContainer() {
        myLayers.add(this);
    }

    Rectangle createRectangle(int leftx, int topy, int width, int height) {
        if (width < 0) {
            width = -width;
            leftx = leftx - width;
        }
        Rectangle result = new Rectangle(leftx, topy, width, height);
        myRectangles.add(result);
        return result;
    }

    Line createLine(int startx, int starty, int finishx, int finishy) {
        Line result = new Line(startx, starty, finishx, finishy);
        myLines.add(result);
        return result;
    }

    Text createText(int leftx, int bottomy, String text) {
        Text result = new Text(leftx, bottomy, text);
        myTexts.add(result);
        return result;
    }

    void paint(Painter painter, Graphics g) {
        for (int i = 0; i < myRectangles.size(); i++) {
            Rectangle next = (Rectangle) myRectangles.get(i);
            painter.paint(next);
        }
        for (int i = 0; i < myLines.size(); i++) {
            Line next = (Line) myLines.get(i);
            Color foreColor = next.getForegroundColor();
            if (foreColor == null) {
                foreColor = Color.BLACK;
            }
            g.setColor(foreColor);
            g.drawLine(next.myStartX, next.myStartY, next.myFinishX,
                    next.myFinishY);
        }
        for (int i = 0; i < myTexts.size(); i++) {
            Text next = (Text) myTexts.get(i);
            painter.paint(next);
        }
    }

    public void clear() {
        myRectangles.clear();
        myLines.clear();
        myTexts.clear();
        myModelObject2primitive.clear();
    }

    void bind(GraphicPrimitive primitive, Object modelObject) {
        myModelObject2primitive.put(modelObject, primitive);
        primitive.setModelObject(modelObject);
    }

    GraphicPrimitive getPrimitive(Object modelObject) {
        return (GraphicPrimitive) myModelObject2primitive.get(modelObject);
    }

    public GraphicPrimitive getPrimitive(int x, int y) {
        // System.err.println("looking for primitive under point x="+x+" y="+y);
        return getPrimitive(x, 0, y, 0);
//        for (int i = 0; i < myRectangles.size(); i++) {
//            Rectangle next = (Rectangle) myRectangles.get(i);
//            // System.err.println(" next rectangle="+next);
//            if (next.myLeftX <= x && next.myLeftX + next.myWidth >= x
//                    && next.myTopY <= y && next.myTopY + next.myHeight >= y) {
//                return next;
//            }
//        }
//        return null;
    }
    
    public GraphicPrimitive getPrimitive(int x, int xThreshold, int y, int yThreshold) {
        for (int i = 0; i < myRectangles.size(); i++) {
            Rectangle next = (Rectangle) myRectangles.get(i);
            // System.err.println(" next rectangle="+next);
            if (next.myLeftX <= x+xThreshold && next.myLeftX + next.myWidth >= x-xThreshold
                    && next.myTopY <= y+yThreshold && next.myTopY + next.myHeight >= y-yThreshold) {
                return next;
            }
        }
        return null;
        
    }

    public GraphicPrimitiveContainer getLayer(int layer) {
        if (layer < 0 || layer >= myLayers.size()) {
            return this;
        }
        return (GraphicPrimitiveContainer) myLayers.get(layer);
    }

    public GraphicPrimitiveContainer newLayer() {
        GraphicPrimitiveContainer result = new GraphicPrimitiveContainer();
        myLayers.add(result);
        return result;

    }

}
