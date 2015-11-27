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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

public class GraphPanel extends JPanel{

	private static final long serialVersionUID = -5872443737794862007L;
	
	private static int padding = 25;
	private static int labelPadding = 25;
	private static Color gridColor = new Color(200, 200, 200, 200);
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private static int pointWidth = 4;
	private static int numberYDivisions = 10;
	
	
	private Map<String, List<Integer>> series;
	
	private int minValue = Integer.MAX_VALUE;
	private int maxValue = Integer.MIN_VALUE;
	
	public GraphPanel(){
		series 		= new HashMap<String,List<Integer>>();
//		addScore("E",10);
//		addScore("E",50);
//		addScore("E",30);
//		addScore("E",60);
//		addScore("E",20);
//		
//		addScore("D",15);
//		addScore("D",55);
//		addScore("D",35);
//		addScore("D",65);
//		addScore("D",25);
//		
//		addScore("T",5);
//		addScore("T",33);
//		addScore("T",32);
//		addScore("T",11);
//		addScore("T",98);		
	}
	
	public void addScore(String serie,Integer en,Integer de,Integer depth){
		addScore("T",depth);
		addScore("D",de);
		addScore("E",en);
	}
	
	private void addScore(String serie,Integer value){
		if(!series.containsKey(serie)){
			series.put(serie, new ArrayList<Integer>());
		}
		if(value < minValue){
			minValue = value;
		}
		if(value > maxValue){
			maxValue = value;
		}
		series.get(serie).add(value);
		invalidate();
		repaint();
	}
	
	
	private void paintBase(Graphics2D g){
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
        // draw white background
        g.setColor(Color.WHITE);
        g.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g.setColor(Color.BLACK); 
        
        boolean hasData = !series.isEmpty();

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
	
	
	private void paintSeries(Graphics2D g,Color seriesColor, List<Integer> scores){
		
		// Escala
		double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (scores.size() - 1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (maxValue - minValue);

        // Puntos
        List<Point> graphPoints = new ArrayList<Point>();
        for (int i = 0; i < scores.size(); i++) {
            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((maxValue - scores.get(i)) * yScale + padding);
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
        for (Entry<String,List<Integer>> v:series.entrySet()){
        	Color color = Color.BLUE;
        	
        	String serie = v.getKey();
        	if (serie.equals("D")){
        		color = Color.GREEN;
        	}else if (serie.equals("E")){
        		color = Color.RED;
        	}
        	
        	paintSeries(g2,color,v.getValue());
        }
    }	
}
