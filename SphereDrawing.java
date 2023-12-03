//https://math.stackexchange.com/questions/1741282/3d-calculate-new-location-of-point-after-rotation-around-origin
//https://math.stackexchange.com/questions/3071711/how-would-one-find-the-equation-for-the-normal-line-to-a-3-dimensional-equation
//https://math.stackexchange.com/questions/549421/tangent-plane-and-normal-line
//https://keisan.casio.com/exec/system/1359533867
//https://math.stackexchange.com/questions/231221/great-arc-distance-between-two-points-on-a-unit-sphere
//https://www.cmu.edu/biolphys/deserno/pdf/sphere_equi.pdf
//https://www.desmos.com/calculator/9noidbmszl
//https://scholar.rose-hulman.edu/cgi/viewcontent.cgi?article=1387&context=rhumj
//http://mathworld.wolfram.com/SpherePointPicking.html
//https://stackoverflow.com/questions/9600801/evenly-distributing-n-points-on-a-sphere
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.util.Arrays;

public class SphereDrawing extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int ADJ = 320;
	private static final double LIGHT_SPHERE_RADIUS = 5;
	private static final double LIGHT_X = -Math.sqrt(5);
	private static final double LIGHT_Y = 2;
	private static final double LIGHT_Z = 4;
	private static final double DRAWN_SPHERE_RADIUS = 1;
	private static final int POINT_COUNT = 400000;
	private static Coord[] points;
	private static final double SCALE = 200;
	
	public SphereDrawing() {
		setPreferredSize(new Dimension(640, 640));
        setBackground(Color.white);
		
		points = new Coord[POINT_COUNT];
		initializePoints();
		
		for (int i = 0; i < points.length; i++) {
			points[i].scale();
		}
		
		new Timer(17, (ActionEvent e) -> {
            repaint();
        }).start();
	}
	
	public void initializePoints() { //finding the points on the surface of the sphere (hopefully somewhat equidistant)
		double random = Math.random() * (double)POINT_COUNT;
		double offset = 2/(double)POINT_COUNT;
		double increment = Math.PI * (3 - Math.sqrt(5));
		
		for (int i = 0; i < POINT_COUNT; i++) {
			double y = ((i * offset) - 1) + (offset / 2); 
			double r = Math.sqrt(1 - Math.pow(y, 2));
			
			double phi = ((i + random) % (double)POINT_COUNT) * increment;
			
			double x = Math.cos(phi) * r;
			double z = Math.sin(phi) * r;
			
			points[i] = new Coord(x, y, z);
		}
	}

	
	public void drawSphere(Graphics2D g) {
		g.translate(ADJ, ADJ); //shifting from origin for drawing purposes
		
		Arrays.sort(points); //sorting points by their z coordinates
		
		double iHat = -1 * LIGHT_X/LIGHT_SPHERE_RADIUS;
		double jHat = -1 * LIGHT_Y/LIGHT_SPHERE_RADIUS; //Light vector
		double kHat = -1 * LIGHT_Z/LIGHT_SPHERE_RADIUS;
		
		double angL1 = 0;
		if (Math.abs(iHat) != 0.0)
			angL1 = Math.atan(jHat / iHat); //converting light vector to spherical coordinates
		else
			angL1 = Math.PI/2;
		double angL2 = Math.atan(Math.sqrt(Math.pow(iHat, 2) + Math.pow(jHat, 2))/ kHat);
		
		double maxArcLength = Math.PI; // maximum arc length
		
		for (int i = 0; i < points.length; i++) {
			if(points[i].checkValid()) {
				double siHat = points[i].x/SCALE; 
				double sjHat = points[i].y/SCALE; //finding normal vector for the given point on the sphere
				double skHat = points[i].z/SCALE;
				
				double angSF1 = -1 * Math.abs(Math.atan(sjHat / siHat)); // converting vector to spherical coordinates
				double angSF2 = Math.atan(Math.sqrt(Math.pow(siHat, 2) + Math.pow(sjHat, 2))/ skHat); 
				
				//double actArcLength = Math.acos(Math.cos(angL1) * Math.cos(angSF1) + Math.sin(angL1) * Math.sin(angSF1) * Math.cos(angL2 - angSF2)); //calculating arc length at this point
				double actArcLength = Math.acos(siHat * iHat + sjHat * jHat + skHat * kHat);
//				if (i == 500000)
//					System.out.println(siHat + " " + iHat + " " + sjHat + " " + jHat + " " + skHat + " " + kHat);
				
				double comp = actArcLength / maxArcLength; // comparing the maximum arc length to the calculated arc length for this vector
				
				int col = (int)(comp * 255);
				col = Math.abs(col);
				g.setColor(new Color(col, col, col));
				
				double ovalDim = (4 * Math.PI * Math.pow(DRAWN_SPHERE_RADIUS, 2))/POINT_COUNT; //using surface area to determine how large size of each point should be drawn
				if (ovalDim < 1) // if it too small, make less small
					ovalDim = 2;
				g.fillOval((int)points[i].x, (int)points[i].y, (int)ovalDim, (int)ovalDim); //draw this oval
			}
		}
	}
 
    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
 
        drawSphere(g);
    }
    
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("Sphere");
            f.setResizable(false);
            f.add(new SphereDrawing(), BorderLayout.CENTER);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }


	private class Coord implements Comparable<Coord> {
		public double x;
		public double y;
		public double z;
		
		public Coord(double x2, double y2, double z2) {
			x = x2;
			y = y2;
			z = z2;
		}
		
		public void scale() {
			x *= SCALE;
			y *= SCALE; //drawing purposes
			z *= SCALE;
		}
		
		public String toString() {
			return x + " " + y + " " + z;
		}
		
		public int compareTo(Coord c) {
			double diff = this.z - c.z;
			if (diff < 0)
				return -1;
			else if (diff > 0) //for sorting the array of points
				return 1;
			else
				return 0;
		}
		
		public boolean checkValid() {
			return (z > 0); //checks if need to draw this point
		}
	}
}