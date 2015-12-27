package loon.opengl;

import loon.LRelease;
import loon.LSystem;
import loon.canvas.LColor;
import loon.geom.Affine2f;
import loon.geom.Shape;
import loon.geom.Triangle;
import loon.utils.MathUtils;

public class GLRenderer implements LRelease {

	public final static class GLType {

		public static final GLType Point = new GLType(GL20.GL_POINTS);

		public static final GLType Line = new GLType(GL20.GL_LINES);

		public static final GLType Filled = new GLType(GL20.GL_TRIANGLES);

		final int glType;

		GLType(int glType) {
			this.glType = glType;
		}

	}

	private GLBatch _renderer;

	private LColor _color = new LColor(1f, 1f, 1f, 1f);

	private GLType _currType = null;

	private Affine2f _affine;

	private GLEx _gl;

	public GLRenderer() {
		this(null, 3000);
	}

	public GLRenderer(GLEx gl) {
		this(gl, 3000);
	}

	public GLRenderer(GLEx gl, int maxVertices) {
		_renderer = new GLBatch(maxVertices, false, true, 0);
		_gl = gl;
	}

	public void begin(Affine2f affine, GLType type) {
		if (_currType != null) {
			throw new RuntimeException(
					"Call end() before beginning a new shape batch !");
		}
		_currType = type;
		_affine = affine;
		if (_affine == null) {
			_affine = LSystem.base().display().GL().tx();
		}
		_renderer.begin(_affine, _currType.glType);
	}

	public void setColor(int argb) {
		this._color.setColor(argb);
	}

	public void setColor(LColor color) {
		this._color.setColor(color);
	}

	public void setColor(float r, float g, float b, float a) {
		this._color.setColor(r, g, b, a);
	}

	public void point(float x, float y) {
		point(x, y, 1);
	}

	public void point(float x, float y, float z) {
		if (_currType != GLType.Point) {
			throw new RuntimeException("Must call begin(GLType.Point)");
		}
		checkDirty();
		checkFlush(1);
		_renderer.color(_color);
		_renderer.vertex(x, y, z);
	}

	public void line(float x, float y, float z, float x2, float y2, float z2) {
		if (_currType != GLType.Line) {
			throw new RuntimeException("Must call begin(GLType.Line)");
		}
		checkDirty();
		checkFlush(2);
		float colorFloat = _color.toFloatBits();
		_renderer.color(colorFloat);
		_renderer.vertex(x, y, z);
		_renderer.color(colorFloat);
		_renderer.vertex(x2, y2, z2);
	}

	public void line(float x, float y, float x2, float y2) {
		if (_currType != GLType.Line) {
			throw new RuntimeException("Must call begin(GLType.Line)");
		}
		checkDirty();
		checkFlush(2);
		float colorFloat = _color.toFloatBits();
		_renderer.color(colorFloat);
		_renderer.vertex(x, y, 0);
		_renderer.color(colorFloat);
		_renderer.vertex(x2, y2, 0);
	}

	public void curve(float x1, float y1, float cx1, float cy1, float cx2,
			float cy2, float x2, float y2, int segments) {
		if (_currType != GLType.Line) {
			throw new RuntimeException("Must call begin(GLType.Line)");
		}
		checkDirty();
		checkFlush(segments * 2 + 2);
		float subdiv_step = 1f / segments;
		float subdiv_step2 = subdiv_step * subdiv_step;
		float subdiv_step3 = subdiv_step * subdiv_step * subdiv_step;

		float pre1 = 3 * subdiv_step;
		float pre2 = 3 * subdiv_step2;
		float pre4 = 6 * subdiv_step2;
		float pre5 = 6 * subdiv_step3;

		float tmp1x = x1 - cx1 * 2 + cx2;
		float tmp1y = y1 - cy1 * 2 + cy2;

		float tmp2x = (cx1 - cx2) * 3 - x1 + x2;
		float tmp2y = (cy1 - cy2) * 3 - y1 + y2;

		float fx = x1;
		float fy = y1;

		float dfx = (cx1 - x1) * pre1 + tmp1x * pre2 + tmp2x * subdiv_step3;
		float dfy = (cy1 - y1) * pre1 + tmp1y * pre2 + tmp2y * subdiv_step3;

		float ddfx = tmp1x * pre4 + tmp2x * pre5;
		float ddfy = tmp1y * pre4 + tmp2y * pre5;

		float dddfx = tmp2x * pre5;
		float dddfy = tmp2y * pre5;

		float colorFloat = _color.toFloatBits();
		for (; segments-- > 0;) {
			_renderer.color(colorFloat);
			_renderer.vertex(fx, fy, 0);
			fx += dfx;
			fy += dfy;
			dfx += ddfx;
			dfy += ddfy;
			ddfx += dddfx;
			ddfy += dddfy;
			_renderer.color(colorFloat);
			_renderer.vertex(fx, fy, 0);
		}
		_renderer.color(colorFloat);
		_renderer.vertex(fx, fy, 0);
		_renderer.color(colorFloat);
		_renderer.vertex(x2, y2, 0);
	}

	public void triangle(float x1, float y1, float x2, float y2, float x3,
			float y3) {
		if (_currType != GLType.Filled && _currType != GLType.Line) {
			throw new RuntimeException(
					"Must call begin(GLType.Filled) or begin(GLType.Line)");
		}
		checkDirty();
		checkFlush(6);
		float colorFloat = _color.toFloatBits();
		if (_currType == GLType.Line) {
			_renderer.color(colorFloat);
			_renderer.vertex(x1, y1, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x2, y2, 0);

			_renderer.color(colorFloat);
			_renderer.vertex(x2, y2, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x3, y3, 0);

			_renderer.color(colorFloat);
			_renderer.vertex(x3, y3, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x1, y1, 0);
		} else {
			_renderer.color(colorFloat);
			_renderer.vertex(x1, y1, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x2, y2, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x3, y3, 0);
		}
	}

	public void rect(float x, float y, float width, float height) {
		if (_currType != GLType.Filled && _currType != GLType.Line) {
			throw new RuntimeException(
					"Must call begin(GLType.Filled) or begin(GLType.Line)");
		}

		checkDirty();
		checkFlush(8);
		float colorFloat = _color.toFloatBits();
		if (_currType == GLType.Line) {
			_renderer.color(colorFloat);
			_renderer.vertex(x, y, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x + width, y, 0);

			_renderer.color(colorFloat);
			_renderer.vertex(x + width, y, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x + width, y + height, 0);

			_renderer.color(colorFloat);
			_renderer.vertex(x + width, y + height, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x, y + height, 0);

			_renderer.color(colorFloat);
			_renderer.vertex(x, y + height, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x, y, 0);
		} else {
			_renderer.color(colorFloat);
			_renderer.vertex(x, y, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x + width, y, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x + width, y + height, 0);

			_renderer.color(colorFloat);
			_renderer.vertex(x + width, y + height, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x, y + height, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x, y, 0);
		}
	}

	public void rect(float x, float y, float width, float height, LColor col1,
			LColor col2, LColor col3, LColor col4) {
		if (_currType != GLType.Filled && _currType != GLType.Line) {
			throw new RuntimeException(
					"Must call begin(GLType.Filled) or begin(GLType.Line)");
		}
		checkDirty();
		checkFlush(8);

		if (_currType == GLType.Line) {
			_renderer.color(col1.r, col1.g, col1.b, col1.a);
			_renderer.vertex(x, y, 0);
			_renderer.color(col2.r, col2.g, col2.b, col2.a);
			_renderer.vertex(x + width, y, 0);

			_renderer.color(col2.r, col2.g, col2.b, col2.a);
			_renderer.vertex(x + width, y, 0);
			_renderer.color(col3.r, col3.g, col3.b, col3.a);
			_renderer.vertex(x + width, y + height, 0);

			_renderer.color(col3.r, col3.g, col3.b, col3.a);
			_renderer.vertex(x + width, y + height, 0);
			_renderer.color(col4.r, col4.g, col4.b, col4.a);
			_renderer.vertex(x, y + height, 0);

			_renderer.color(col4.r, col4.g, col4.b, col4.a);
			_renderer.vertex(x, y + height, 0);
			_renderer.color(col1.r, col1.g, col1.b, col1.a);
			_renderer.vertex(x, y, 0);
		} else {
			_renderer.color(col1.r, col1.g, col1.b, col1.a);
			_renderer.vertex(x, y, 0);
			_renderer.color(col2.r, col2.g, col2.b, col2.a);
			_renderer.vertex(x + width, y, 0);
			_renderer.color(col3.r, col3.g, col3.b, col3.a);
			_renderer.vertex(x + width, y + height, 0);

			_renderer.color(col3.r, col3.g, col3.b, col3.a);
			_renderer.vertex(x + width, y + height, 0);
			_renderer.color(col4.r, col4.g, col4.b, col4.a);
			_renderer.vertex(x, y + height, 0);
			_renderer.color(col1.r, col1.g, col1.b, col1.a);
			_renderer.vertex(x, y, 0);
		}
	}

	public void oval(float x, float y, float radius) {
		oval(x, y, radius, 40);
	}

	public void oval(float x, float y, float radius, int segments) {
		if (segments <= 0)
			throw new IllegalArgumentException("segments must be >= 0.");
		if (_currType != GLType.Filled && _currType != GLType.Line)
			throw new RuntimeException(
					"Must call begin(GLType.Filled) or begin(GLType.Line)");
		checkDirty();
		checkFlush(segments * 2 + 2);
		float angle = 2 * 3.1415926f / segments;
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float cx = radius, cy = 0;
		float colorFloat = _color.toFloatBits();
		if (_currType == GLType.Line) {
			for (int i = 0; i < segments; i++) {
				_renderer.color(colorFloat);
				_renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				_renderer.color(colorFloat);
				_renderer.vertex(x + cx, y + cy, 0);
			}
			_renderer.color(colorFloat);
			_renderer.vertex(x + cx, y + cy, 0);
		} else {
			segments--;
			for (int i = 0; i < segments; i++) {
				_renderer.color(colorFloat);
				_renderer.vertex(x, y, 0);
				_renderer.color(colorFloat);
				_renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				_renderer.color(colorFloat);
				_renderer.vertex(x + cx, y + cy, 0);
			}
			_renderer.color(colorFloat);
			_renderer.vertex(x, y, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x + cx, y + cy, 0);
		}
		cx = radius;
		cy = 0;
		_renderer.color(colorFloat);
		_renderer.vertex(x + cx, y + cy, 0);
	}

	public void polygon(float[] vertices) {
		if (_currType != GLType.Line) {
			throw new RuntimeException("Must call begin(GLType.Line)");
		}
		if (vertices.length < 6) {
			System.out.println(vertices.length);
			throw new IllegalArgumentException(
					"Polygons must contain at least 3 points.");
		}
		if (vertices.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Polygons must have a pair number of vertices.");
		}
		final int numFloats = vertices.length;

		float colorFloat = _color.toFloatBits();
		checkDirty();
		checkFlush(numFloats);

		float firstX = vertices[0];
		float firstY = vertices[1];

		for (int i = 0; i < numFloats; i += 2) {
			float x1 = vertices[i];
			float y1 = vertices[i + 1];

			float x2;
			float y2;

			if (i + 2 >= numFloats) {
				x2 = firstX;
				y2 = firstY;
			} else {
				x2 = vertices[i + 2];
				y2 = vertices[i + 3];
			}

			_renderer.color(colorFloat);
			_renderer.vertex(x1, y1, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x2, y2, 0);
		}
	}

	public void polyline(float[] vertices) {
		polyline(vertices, 0, vertices.length);
	}

	public void polyline(float[] vertices, int offset, int count) {
		if (_currType != GLType.Line) {
			throw new RuntimeException("Must call begin(GLType.Line)");
		}
		if (count < 4) {
			if (count == 2) {
				line(vertices[0], vertices[1], vertices[0] + 1, vertices[1] + 1);
				return;
			}
			throw new IllegalArgumentException(
					"Polylines must contain at least 2 points.");
		}
		if (count % 2 != 0) {
			throw new IllegalArgumentException(
					"Polylines must have an even number of vertices.");
		}
		checkDirty();
		checkFlush(count);

		float colorFloat = _color.toFloatBits();
		for (int i = offset, n = offset + count - 2; i < n; i += 2) {
			float x1 = vertices[i];
			float y1 = vertices[i + 1];

			float x2;
			float y2;

			x2 = vertices[i + 2];
			y2 = vertices[i + 3];

			_renderer.color(colorFloat);
			_renderer.vertex(x1, y1, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x2, y2, 0);
		}
	}

	public void drawShape(Shape shape, float x, float y) {
		Triangle tris = shape.getTriangles();
		if (tris.getTriangleCount() == 0) {
			return;
		}
		float[] points = shape.getPoints();
		if (points.length == 0) {
			return;
		}
		float colorFloat = _color.toFloatBits();
		for (int i = 0; i < tris.getTriangleCount(); i++) {
			for (int p = 0; p < 3; p++) {
				float[] pt = tris.getTrianglePoint(i, p);
				_renderer.color(colorFloat);
				_renderer.vertex(pt[0] + x, pt[1] + y);
			}
		}
	}

	public void arc(float x, float y, float radius, float start, float degrees) {
		arc(x, y, radius, start, degrees, 40);
	}

	public void arc(float x, float y, float radius, float start, float end,
			int segments) {
		checkDirty();
		if (segments <= 0) {
			throw new IllegalArgumentException("segments must be >= 0.");
		}
		if (_currType != GLType.Filled && _currType != GLType.Line) {
			throw new RuntimeException(
					"Must call begin(GLType.Filled) or begin(GLType.Line)");
		}
		float arcAngle = end - start;
		if (arcAngle < 0) {
			start = 360 - arcAngle;
			arcAngle = 360 + arcAngle;
		}
		start %= 360;
		if (start < 0) {
			start += 360;
		}
		float theta = (2 * MathUtils.PI * (arcAngle / 360.0f)) / segments;
		float cos = MathUtils.cos(theta);
		float sin = MathUtils.sin(theta);
		float cx = radius * MathUtils.cos(start * MathUtils.DEG_TO_RAD);
		float cy = radius * MathUtils.sin(start * MathUtils.DEG_TO_RAD);
		float colorFloat = _color.toFloatBits();

		if (_currType == GLType.Line) {
			checkFlush(segments * 2 + 2);
			_renderer.color(colorFloat);
			_renderer.vertex(x, y, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x + cx, y + cy, 0);
			for (int i = 0; i < segments; i++) {
				_renderer.color(colorFloat);
				_renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				_renderer.color(colorFloat);
				_renderer.vertex(x + cx, y + cy, 0);
			}
			_renderer.color(colorFloat);
			_renderer.vertex(x + cx, y + cy, 0);
		} else {
			checkFlush(segments * 3 + 3);
			for (int i = 0; i < segments; i++) {
				_renderer.color(colorFloat);
				_renderer.vertex(x, y, 0);
				_renderer.color(colorFloat);
				_renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				_renderer.color(colorFloat);
				_renderer.vertex(x + cx, y + cy, 0);
			}
			_renderer.color(colorFloat);
			_renderer.vertex(x, y, 0);
			_renderer.color(colorFloat);
			_renderer.vertex(x + cx, y + cy, 0);
		}
		cx = 0;
		cy = 0;
		_renderer.color(colorFloat);
		_renderer.vertex(x + cx, y + cy, 0);
	}

	private void checkDirty() {
		GLType type = _currType;
		end();
		begin(_affine, type);
	}

	private void checkFlush(int newVertices) {
		if (_renderer.getMaxVertices() - _renderer.getNumVertices() >= newVertices) {
			return;
		}
		GLType type = _currType;
		end();
		begin(_affine, type);
	}

	public void end() {
		if (_renderer != null) {
			LSystem.mainEndDraw();
			if (_gl == null) {
				_gl = LSystem.base().display().GL();
			}
			int tmp = _gl.getBlendMode();
			if (!LColor.white.equals(_color)) {
				_gl.setBlendMode(LSystem.MODE_SPEED);
			}
			_renderer.end();
			_gl.setBlendMode(tmp);
			_currType = null;
			LSystem.mainBeginDraw();
		}
	}

	public void flush() {
		GLType type = _currType;
		end();
		begin(_affine, type);
	}

	public GLType getCurrentType() {
		return _currType;
	}

	@Override
	public void close() {
		if (_renderer != null) {
			_renderer.close();
		}
	}

}
