/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.fx;

import javafx.animation.AnimationTimer;
import javafx.scene.image.WritableImage;
import loon.Graphics;
import loon.canvas.Canvas;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.Dimension;
import loon.utils.Scale;

public class JavaFXGraphics extends Graphics {

	private final JavaFXGame game;

	private Dimension screenSize = new Dimension();

	protected AnimationTimer loopRunner;

	protected JavaFXGraphics(JavaFXGame game) {
		this(game, Scale.ONE);
	}

	protected JavaFXGraphics(JavaFXGame game, Scale scale) {
		super(game, scale);
		this.game = game;
	}

	void onSizeChanged(int viewWidth, int viewHeight) {
		if (!isAllowResize(viewWidth, viewHeight)) {
			return;
		}
		screenSize.width = viewWidth / scale.factor;
		screenSize.height = viewHeight / scale.factor;
		game.log().info("Updating size " + viewWidth + "x" + viewHeight + " / " + scale.factor + " -> " + screenSize);
		viewportChanged(scale, viewWidth, viewHeight);
	}

	@Override
	public Dimension screenSize() {
		return this.screenSize;
	}

	@Override
	public TextLayout layoutText(String text, TextFormat format) {
		return JavaFXTextLayout.layoutText(this, text, format);
	}

	@Override
	public TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap) {
		return JavaFXTextLayout.layoutText(this, text, format, wrap);
	}

	@Override
	protected Canvas createCanvasImpl(Scale scale, int pixelWidth, int pixelHeight) {
		WritableImage bitmap = new WritableImage(pixelWidth, pixelHeight);
		JavaFXImage image = new JavaFXImage(this, scale, bitmap, "<canvas>");
		return new JavaFXCanvas(this, image);
	}

	protected void start() {
		if (loopRunner == null) {
			init();
		}
		loopRunner.start();
    }
    
	protected void resume() {
		if (loopRunner == null) {
			return;
		}
		start();
	}

	protected void pause() {
		stop();
	}

	protected void stop() {
		if (loopRunner == null) {
			return;
		}
		loopRunner.stop();
	}
	
	protected void init() {
		if (loopRunner != null) {
			loopRunner.stop();
			loopRunner = null;
		}
		loopRunner = new AnimationTimer() {

			@Override
			public void handle(long time) {

			}
		};
	}

}