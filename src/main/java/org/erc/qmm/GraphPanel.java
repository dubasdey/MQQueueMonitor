package org.erc.qmm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JPanel;

public class GraphPanel extends JPanel{

	private static final long serialVersionUID = -5872443737794862007L;
	
	private int maxValuesToStore = 1000;
	
	private static int padding = 25;
	private static int labelPadding = 25;
	private static Color gridColor = new Color(200, 200, 200, 200);
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private static int pointWidth = 4;
	private static int numberYDivisions = 10;
	
	private Queue<Integer> enqueued;
	private Queue<Integer> dequeued;
	private Queue<Integer> total;
	
	private int minValue = Integer.MAX_VALUE;
	private int maxValue = Integer.MIN_VALUE;
	
	public GraphPanel(){
		enqueued = new LinkedList<Integer>();
		dequeued = new LinkedList<Integer>();
		total = new LinkedList<Integer>();
		this.setDoubleBuffered(true);	
	}
	
	public void addScore(Integer en,Integer de,Integer depth){
		enqueued.add(en);
		dequeued.add(de);
		total.add(depth);
		if(en<minValue){
			minValue = en;
		}
		if(de<minValue){
			minValue = de;
		}
		if(depth<minValue){
			minValue = depth;
		}
		if(en>maxValue){
			maxValue = en;
		}
		if(de>maxValue){
			maxValue = de;
		}
		if(depth>maxValue){
			maxValue = depth;
		}
		
		purgeOld();
		updateUI();
	}
	
	private int getMaxValue(Integer[] items){
		int res = Integer.MIN_VALUE;
		for(int item : items ){
			if(item>res){
				res = item;
			}
		}
		return res;
	}
	private int getMinValue(Integer[] items){
		int res = Integer.MAX_VALUE;
		for(int item : items ){
			if(item<res){
				res = item;
			}
		}
		return res;
	}
	
	private void purgeOld(){
		if(enqueued.size()> maxValuesToStore){
			int value1 = enqueued.poll();
			int value2 = dequeued.poll();
			int value3 = total.poll();
			
			if(value1 >= maxValue){
				maxValue = getMaxValue(enqueued.toArray(new Integer[]{}));
			}
			if(value2 >= maxValue){
				maxValue = getMaxValue(dequeued.toArray(new Integer[]{}));
			}
			if(value3 >= maxValue){
				maxValue = getMaxValue(total.toArray(new Integer[]{}));
			}
			if(value1 <= minValue){
				maxValue = getMinValue(enqueued.toArray(new Integer[]{}));
			}
			if(value2 <= minValue){
				maxValue = getMinValue(dequeued.toArray(new Integer[]{}));
			}
			if(value3 <= minValue){
				maxValue = getMinValue(total.toArray(new Integer[]{}));
			}
		}
	}
	
	private void paintBase(Graphics2D g){
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
        // draw white background
        g.setColor(Color.WHITE);
        g.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g.setColor(Color.BLACK); 
        
        boolean hasData = !enqueued.isEmpty();

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i <= numberYDivisions; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            
            if(hasData){
	            // Y Line
	            g.setColor(gridColor);
	            g.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
	            g.setColor(Color.BLACK);
	            
	            // Y Label
	            int yLabel = (int) ( ( (minValue + (maxValue - minValue) *  ( (i * 1.0) /numberYDivisions)) * 100.0) / 100);
	            String yLabelStr = yLabel + "";
	            FontMetrics metrics = g.getFontMetrics();
	            int labelWidth = metrics.stringWidth(yLabelStr);
	            g.drawString(yLabelStr, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
	            
	            // Y dot line
	            g.drawLine(x0, y0, x1, y1);
            }
        }
        
        // create x and y axes 
        g.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

	}
	
	
	private void paintSeries(Graphics2D g,Color seriesColor,Integer[] scores){
		
		// Escala
		double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (scores.length - 1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (maxValue - minValue);

        // Puntos
        List<Point> graphPoints = new ArrayList<Point>();
        for (int i = 0; i < scores.length; i++) {
            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((maxValue - scores[i]) * yScale + padding);
            graphPoints.add(new Point(x1, y1));
        }

        g.setColor(seriesColor);
        g.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g.drawLine(x1, y1, x2, y2);
        }

	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        paintBase(g2);
        paintSeries(g2,Color.BLUE,total.toArray(new Integer[]{}));
        paintSeries(g2,Color.GREEN,enqueued.toArray(new Integer[]{}));
        paintSeries(g2,Color.RED,dequeued.toArray(new Integer[]{}));

    }	
}
