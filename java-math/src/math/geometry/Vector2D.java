package math.geometry;

public class Vector2D {

    public double x;
    public double y;

    public Vector2D() {
        this(0, 0);
    }

    public Vector2D(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D v) {
        this(v.x, v.y);
    }

    public Vector2D(Vector3D v) {
        this(v.x, v.y);
    }

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    public boolean isZero(double epsilon) {
        return norm() < epsilon*epsilon;
    }

    public boolean isUnit() {
        return norm() == 1;
    }

    public boolean isUnit(double epsilon) {
        return Math.abs(norm() - 1) < epsilon*epsilon;
    }

    public boolean isParallel(Vector2D v) {
        return cross(v) == 0;
    }

    public boolean isParallel(Vector2D v, double epsilon) {
        double cross = cross(v);
        return cross < epsilon && cross > -epsilon;
    }

    public boolean isPerpendicular(Vector2D v) {
        return dot(v) == 0;
    }

    public boolean isPerpendicular(Vector2D v, double epsilon) {
        double dot = dot(v);
        return dot < epsilon && dot > -epsilon;
    }

    public boolean isSameDirection(Vector2D v) {
        return dot(v) > 0;
    }

    public boolean isOppositeDirection(Vector2D v) {
        return dot(v) < 0;
    }

    public boolean isSameDirection(Vector2D v, double epsilon) {
        return dot(v) > epsilon;
    }

    public boolean isOppositeDirection(Vector2D v, double epsilon) {
        return dot(v) < -epsilon;
    }

    public boolean isCollinear(Vector2D v) {
        return isParallel(v) && isSameDirection(v);
    }

    public boolean isCollinear(Vector2D v, double epsilon) {
        return isParallel(v) && isSameDirection(v, epsilon);
    }

    public boolean isCollinearOpposite(Vector2D v) {
        return isParallel(v) && isOppositeDirection(v);
    }

    public boolean isCollinearOpposite(Vector2D v, double epsilon) {
        return isParallel(v) && isOppositeDirection(v, epsilon);
    }

    public boolean inBox(double minX, double minY, double maxX, double maxY) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    public boolean inBox(Vector2D vec1, Vector2D vec2) {
        return inBox(Math.min(vec1.x, vec2.x), Math.min(vec1.y, vec2.y), Math.max(vec1.x, vec2.x), Math.max(vec1.y, vec2.y));
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public Vector2D setY(double y) {
        this.y = y;
        return this;
    }

    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    public Vector2D sub(Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }

    public Vector2D mul(double k) {
        return new Vector2D(x * k, y * k);
    }

    public Vector2D div(double k) {
        return new Vector2D(x / k, y / k);
    }

    public double dot(Vector2D v) {
        return x * v.x + y * v.y;
    }

    public double cross(Vector2D v) {
        return x * v.y - y * v.x;
    }

    public double norm() {
        return x * x + y * y;
    }

    public double length() {
        return Math.sqrt(norm());
    }

    public Vector2D normalize() {
        return div((double) length());
    }

    public Vector2D rotate(double angle) {
        return new Vector2D((double) (x * Math.cos(angle) - y * Math.sin(angle)), (double) (x * Math.sin(angle) + y * Math.cos(angle)));
    }

    public Vector2D rotate(double angle, Vector2D center) {
        return sub(center).rotate(angle).add(center);
    }

    public Vector2D rotate(double angle, double x, double y) {
        return rotate(angle, new Vector2D(x, y));
    }

    public Vector2D rotate(double angle, double[] center) {
        return rotate(angle, new Vector2D(center[0], center[1]));
    }

    public Vector2D rotate(double angle, Vector3D center) {
        return rotate(angle, new Vector2D(center));
    }

    public double angle(Vector2D v) {
        return Math.acos(dot(v) / (length() * v.length()));
    }

    public double[] toArray() {
        return new double[]{x, y};
    }

    public Vector3D toVector3D() {
        return new Vector3D(x, y, 0);
    }

	public Vector3D to3D() {
		return new Vector3D(x, y, 0);
	}

    public Vector3D toVector3D(double z) {
        return new Vector3D(x, y, z);
    }

    public Vector2D copy() {
        return new Vector2D(this);
    }


}
