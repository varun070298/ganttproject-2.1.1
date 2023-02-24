package net.sourceforge.ganttproject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.ganttproject.font.Fonts;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import net.sourceforge.ganttproject.task.ResourceAssignment;
import net.sourceforge.ganttproject.util.TextLengthCalculator;
import net.sourceforge.ganttproject.util.TextLengthCalculatorImpl;

class ResourceTreeImageGenerator {
    private HumanResourceManager myResourceManager;
    private final Color BORDER_COLOR_3D = new Color((float) 0.807, (float) 0.807, (float) 0.807);
    private final Color ODD_ROW_COLOR = new Color(0.933f, 0.933f, 0.933f);
    private final int myRowHeight;
    
    ResourceTreeImageGenerator(HumanResourceManager resourceManager) {
        myResourceManager = resourceManager;
        final BufferedImage testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        final Graphics g = testImage.getGraphics();
        final int nameFontHeight = g.getFontMetrics().getHeight();
        final int nameLinePadding = 3;
        myRowHeight = nameFontHeight+2*nameLinePadding;
    }
    
    BufferedImage createImage() {
        Dimension d = calculateDimension();
        BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);
        printResources(g, d.width);
        return image;
    }
    
    protected int getRowHeight() {
        return myRowHeight;
    }
    
    private Dimension calculateDimension() {
        int width = 0;
        int assignmentsCount = 0;
        final BufferedImage testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        final Graphics g = testImage.getGraphics();
        final int tabSize = 5;
        final List users = myResourceManager.getResources();
        for (Iterator user = users.iterator(); user.hasNext();) {
            HumanResource hr = (HumanResource) user.next();
            int nameWidth = TextLengthCalculatorImpl.getTextLength(g, hr.getName());
            if (nameWidth > width) {
                width = nameWidth;
            }
            ResourceAssignment[] assignments = hr.getAssignments();
            if (assignments != null) {
                for (int i = 0; i < assignments.length; i++) {
                    if (isAssignmentVisible(assignments[i])) {
                        int taskWidth = tabSize + TextLengthCalculatorImpl.getTextLength(g, assignments[i].getTask()
                                .getName());
                        if (taskWidth > width) {
                            width = taskWidth;
                        }
                        assignmentsCount++;
                    }
                }
            }
        }
        width += 20;
        int height = (assignmentsCount+users.size())*getRowHeight()+90;
        return new Dimension(width, height);
    }
    
    private void printResources(Graphics g, int width) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.black);
        g.setFont(Fonts.RESSOURCE_FONT);

        List users = myResourceManager.getResources();

        int y = 67;

        final int nameLinePadding = 3;
        final int nameLineHeight = getRowHeight();
        boolean isOddRow = false;
        for (Iterator user = users.iterator(); user.hasNext();) {
            HumanResource hr = (HumanResource) user.next();
            {
                // paint resource name here
                String nameOfRes = hr.toString();
    
                if (isOddRow) {
                    g.setColor(ODD_ROW_COLOR);
                    g.fillRect(0, y, width, nameLineHeight);
                }
                g.setColor(Color.black);
                //
                g.drawRect(0, y, width, nameLineHeight);
                
                g.drawString(nameOfRes, 5, y+nameLineHeight-nameLinePadding);
                g.setColor(BORDER_COLOR_3D);
                g.drawLine(1, y+nameLineHeight-1, width-1, y+nameLineHeight-1);
                y += nameLineHeight;
                isOddRow = !isOddRow;
            }
            {
                //paint assigned task names
                ResourceAssignment[] assignments = hr.getAssignments();
                if (assignments != null) {
                    for (int i = 0; i < assignments.length; i++) {
                        if (isAssignmentVisible(assignments[i])) {
                            if (isOddRow) {
                                g.setColor(ODD_ROW_COLOR);
                                g.fillRect(0, y, width, nameLineHeight);
                            }
                            g.setColor(Color.black);
                            g.drawRect(0, y, width, nameLineHeight);
                            g.drawString("  " + assignments[i].getTask().getName(),
                                    5, y+nameLineHeight-nameLinePadding);
                            g.setColor(BORDER_COLOR_3D);
                            g.drawLine(1, y+nameLineHeight, width-1, y+nameLineHeight-1);
                            y += nameLineHeight;
                            isOddRow = !isOddRow;
                        }
                    }
                }
            }
        }

    }

    protected boolean isAssignmentVisible(ResourceAssignment assignment) {
        return true;
    }

    
}
