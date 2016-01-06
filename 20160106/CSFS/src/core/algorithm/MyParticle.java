package core.algorithm;

import net.sourceforge.jswarm_pso.Particle;

/**
 * Simple particle example
 * @author Pablo Cingolani <pcingola@users.sourceforge.net>
 */
public class MyParticle extends Particle {

	private static int dimention;

	/** Default constructor */
	public MyParticle() {
		super(dimention); // Create a 2-dimentional particle
	}

	public static void setDimention(int dimention) {
		MyParticle.dimention = dimention;
	}
}