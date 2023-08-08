package math.geometry;

public class Plane {
	
	public Vector3D p;
	public Vector3D n;


	public Plane() {
		this(new Vector3D(), new Vector3D(0, 0, 1));
	}

	public Plane(Vector3D p, Vector3D n) {
		super();
		this.p = p;
		this.n = n;
	}

	public Plane(Plane plane) {
		this(plane.p.copy(), plane.n.copy());
	}

	public Plane copy() {
		return new Plane(this);
	}

	public Plane(Vector3D p1, Vector3D p2, Vector3D p3) {
		this(p1, p2.sub(p1).cross(p3.sub(p1)));
	}

	public void normalize() {
		n.normalize();
	}
	
}
