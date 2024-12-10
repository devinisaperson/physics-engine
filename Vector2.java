

public class Vector2 {
    public double x;
    public double y;

    public Vector2() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 that) {
        this.x = that.x;
        this.y = that.y;
    }

    public static Vector2 add(Vector2 v, Vector2 u) {
        return new Vector2(v.x+u.x, v.y+u.y);
    }

    public static Vector2 minus(Vector2 v, Vector2 u) {
        return new Vector2(v.x-u.x, v.y-u.y);
    }

    public static Vector2 hadamard(Vector2 v, Vector2 u) {
        return new Vector2(v.x*u.x, v.y*u.y);
    }

    public static double dot(Vector2 v, Vector2 u) {
        return v.x*u.y+v.x*u.y;
    }

    public static double cross(Vector2 v, Vector2 u) {
        return v.x*u.y-v.y*u.x;
    }

    public Vector2 add(Vector2 that) {return Vector2.add(this, that);}
    public Vector2 minus(Vector2 that) {return Vector2.minus(this, that);}
    public Vector2 hadamard(Vector2 that) {return Vector2.hadamard(this, that);}
    public double dot(Vector2 that) {return Vector2.dot(this, that);}
    public double cross(Vector2 that) {return Vector2.cross(this, that);}

    public Vector2 scale(double k) {
        return new Vector2(this.x*k, this.y*k);
    }

    public Vector2 rotate(double theta) {
        double magnitude = Math.hypot(x, y);
        theta += Math.atan2(y, x);
        return new Vector2(magnitude * Math.cos(theta), magnitude * Math.sin(theta));
    }
}
