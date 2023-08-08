package math.geometry;

public class Line {

	public Vector3D p;
	public Vector3D v;

	public Line() {
		this(new Vector3D(), new Vector3D(1, 0, 0));
	}

	public Line(Vector3D p, Vector3D v) {
		super();
		this.p = p;
		this.v = v;
	}

	public Line(Line l) {
		this(l.p.copy(), l.v.copy());
	}

	public Line copy() {
		return new Line(this);
	}



}
