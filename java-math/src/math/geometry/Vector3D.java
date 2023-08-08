package math.geometry;

public class Vector3D extends Vector2D {
    
    public double z;

    public Vector3D() {
        this(0, 0, 0);
    }

    public Vector3D(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }

    public Vector3D(Vector3D v) {
        this(v.x, v.y, v.z);
    }

    public Vector3D(Vector2D v) {
        this(v.x, v.y, 0);
    }

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

    public double getZ() {
        return z;
    }

    public Vector3D setZ(double z) {
        this.z = z;
        return this;
    }

	public Vector3D rotate(Vector3D axis, double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double x = this.x * (cos + axis.x * axis.x * (1 - cos)) + this.y * (axis.x * axis.y * (1 - cos) - axis.z * sin) + this.z * (axis.x * axis.z * (1 - cos) + axis.y * sin);
		double y = this.x * (axis.y * axis.x * (1 - cos) + axis.z * sin) + this.y * (cos + axis.y * axis.y * (1 - cos)) + this.z * (axis.y * axis.z * (1 - cos) - axis.x * sin);
		double z = this.x * (axis.z * axis.x * (1 - cos) - axis.y * sin) + this.y * (axis.z * axis.y * (1 - cos) + axis.x * sin) + this.z * (cos + axis.z * axis.z * (1 - cos));
		return new Vector3D(x, y, z);
	}


    @Override
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }
	
	public boolean isParallel(Vector3D v) {
		if (this.isZero() || v.isZero()) {
			return true;
		}
		return cross(v).isZero();
	}

	public boolean isParallel(Vector3D v, double epsilon) {
		if (this.isZero(epsilon) || v.isZero(epsilon)) {
			return true;
		}
		return cross(v).isZero(epsilon);
	}

	public boolean isPerpendicular(Vector3D v) {
		if (this.isZero() || v.isZero()) {
			return true;
		}
		return dot(v) == 0;
	}

	public boolean isPerpendicular(Vector3D v, double epsilon) {
		if (this.isZero(epsilon) || v.isZero(epsilon)) {
			return true;
		}
		return dot(v) < epsilon && dot(v) > -epsilon;
	}

    public Vector3D add(Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }

    public Vector3D sub(Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    public Vector3D mul(double k) {
        return new Vector3D(x * k, y * k, z * k);
    }

	public Vector3D mul(Vector3D m) {
		return new Vector3D(x * m.x, y * m.y, z * m.z);
	}

    public Vector3D div(double k) {
        return new Vector3D(x / k, y / k, z / k);
    }

    public double dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vector3D cross(Vector3D v) {
        return new Vector3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public double norm() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(norm());
    }

    public Vector3D normalize() {
		if (this.isZero()) {
			throw new ArithmeticException("Cannot normalize a zero vector");
		}
        return div((double) length());
    }

	public Vector3D projectOnLine(Vector3D origin, Vector3D line) {
		double norm = line.norm();
		double dot = this.sub(origin).dot(line);
		double dsn = dot/norm;
		double px = origin.x + line.x * dsn;
		double py = origin.y + line.y * dsn;
		double pz = origin.z + line.z * dsn;
		return new Vector3D(px, py, pz);
	}

	public Vector3D projectOnLine(Vector3D line) {
		return line.mul(this.dot(line) / line.norm());
	}

	public Vector3D projectOnLine(Line line) {
		return projectOnLine(line.p, line.v);
	}

	public Vector3D projectOnPlane(Vector3D origin, Vector3D normal) {
		Vector3D diff = this.sub(origin);
		return this.sub(diff.projectOnLine(origin, normal));
	}


	public Vector3D changeBasis(Vector3D origin, Vector3D axe1, Vector3D axe2, Vector3D axe3) {
		Vector3D diff = this.sub(origin);
		double x = diff.x * axe1.x + diff.y * axe1.y + diff.z * axe1.z;
		double y = diff.x * axe2.x + diff.y * axe2.y + diff.z * axe2.z;
		double z = diff.x * axe3.x + diff.y * axe3.y + diff.z * axe3.z;
		return new Vector3D(x, y, z);
	}

	public Vector3D changeBasis(Vector3D axe1, Vector3D axe2, Vector3D axe3) {
		double x = this.x * axe1.x + this.y * axe1.y + this.z * axe1.z;
		double y = this.x * axe2.x + this.y * axe2.y + this.z * axe2.z;
		double z = this.x * axe3.x + this.y * axe3.y + this.z * axe3.z;
		return new Vector3D(x, y, z);
	}

	public Vector3D getPerpendicular() {
		if (this.isZero()) {
			throw new ArithmeticException("Cannot get perpendicular of a zero vector");
		}

		if (z != 0) 
			return new Vector3D(1, 1, -(x + y) / z);
		if (y != 0) 
			return new Vector3D(1, -(x + z) / y, 1);

		return new Vector3D(-(y + z) / x, 1, 1);

	}

	/**
	 * Projects this point on the plane defined by axe1 and axe2 and his origin.
	 * @param origin origin of the plane
	 * @param axe1 first axe of the plane
	 * @param axe2 second axe of the plane
	 * @return the projection of this point on the plane
	 */
	public Vector3D projectOnPlane(Vector3D origin, Vector3D axe1, Vector3D axe2 ) {
		Vector3D normal = axe1.cross(axe2);
		return projectOnPlane(origin, normal);
	}

	public Vector3D projectOnPlane(Plane plane) {
		return projectOnPlane(plane.p, plane.n);
	}
	public Vector3D projectOnPlane(Vector3D normal) {
		return this.sub(this.projectOnLine(normal));
	}
	

	public Vector3D copy() {
        return new Vector3D(this);
    }
	

}
