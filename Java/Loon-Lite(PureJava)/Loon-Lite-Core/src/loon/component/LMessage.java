/**
 * Copyright 2008 - 2009
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
 * @version 0.1
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.Animation;
import loon.canvas.LColor;
import loon.component.skin.MessageSkin;
import loon.component.skin.SkinManager;
import loon.font.FontSet;
import loon.font.IFont;
import loon.opengl.GLEx;

/**
 * 信息显示用UI,支持一些简单的字符命令用于构建显示文字
 */
public class LMessage extends LContainer implements FontSet<LMessage> {

	private Animation animation;

	private IFont messageFont;

	private LColor fontColor = LColor.white;

	private long printTime, totalDuration;

	private float dx, dy, dw, dh;

	private Print print;

	public LMessage(IFont font, int width, int height) {
		this(font, 0, 0, width, height);
	}

	public LMessage(int x, int y, int width, int height) {
		this(SkinManager.get().getMessageSkin().getFont(), (LTexture) null, x, y, width, height);
	}

	public LMessage(IFont font, int x, int y, int width, int height) {
		this(font, (LTexture) null, x, y, width, height);
	}

	public LMessage(IFont font, String fileName, int x, int y) {
		this(font, LSystem.loadTexture(fileName), x, y);
	}

	public LMessage(LTexture formImage, int x, int y) {
		this(SkinManager.get().getMessageSkin().getFont(), formImage, x, y, formImage.getWidth(),
				formImage.getHeight());
	}

	public LMessage(IFont font, LTexture formImage, int x, int y) {
		this(font, formImage, x, y, formImage.getWidth(), formImage.getHeight());
	}

	public LMessage(IFont font, LTexture formImage, int x, int y, int width, int height) {
		this(font, formImage, x, y, width, height, SkinManager.get().getMessageSkin().getFontColor());
	}

	public LMessage(MessageSkin skin, int x, int y, int width, int height) {
		this(skin.getFont(), skin.getBackgroundTexture(), x, y, width, height, skin.getFontColor());
	}

	public LMessage(IFont font, LTexture formImage, int x, int y, int width, int height, LColor color) {
		super(x, y, width, height);
		this.fontColor = color;
		this.messageFont = (font == null ? SkinManager.get().getMessageSkin().getFont() : font);
		this.animation = new Animation();
		if (formImage == null) {
			this.setBackground(LSystem.base().graphics().finalColorTex());
			this.setAlpha(0.3F);
		} else {
			this.setBackground(formImage);
			if (width == -1) {
				width = formImage.getWidth();
			}
			if (height == -1) {
				height = formImage.getHeight();
			}
		}
		this.print = new Print(getLocation(), messageFont, width, height);
		this.setTipIcon(LSystem.getSystemImagePath() + "creese.png");
		this.totalDuration = 80;
		this.customRendering = true;
		this.setWait(false);
		this.setElastic(true);
		this.setLocked(true);
	}

	public void setWait(boolean flag) {
		print.setWait(flag);
	}

	public boolean isWait() {
		return print.isWait();
	}

	public void complete() {
		print.complete();
	}

	public void setLeftOffset(int left) {
		print.setLeftOffset(left);
	}

	public void setTopOffset(int top) {
		print.setTopOffset(top);
	}

	public int getLeftOffset() {
		return print.getLeftOffset();
	}

	public int getTopOffset() {
		return print.getTopOffset();
	}

	public int getMessageLength() {
		return print.getMessageLength();
	}

	public void setMessageLength(int messageLength) {
		print.setMessageLength(messageLength);
	}

	public void setTipIcon(String fileName) {
		setTipIcon(LSystem.loadTexture(fileName));
	}

	public void setTipIcon(LTexture icon) {
		if (icon != null) {
			print.setCreeseIcon(icon);
			setDisplayIconFlag(true);
		}
	}

	public void setNotTipIcon() {
		print.setCreeseIcon(null);
		setDisplayIconFlag(false);
	}

	public LMessage setDisplayIconFlag(boolean flag) {
		print.setIconFlag(flag);
		return this;
	}

	public boolean isDisplayIconFlag() {
		return print.isIconFlag();
	}

	public float getOffsetIconX() {
		return print.getOffsetIconX();
	}

	public LMessage setOffsetIconX(float offsetIconX) {
		print.setOffsetIconX(offsetIconX);
		return this;
	}

	public float getOffsetIconY() {
		return print.getOffsetIconY();
	}

	public LMessage setOffsetIconY(float offsetIconY) {
		print.setOffsetIconX(offsetIconY);
		return this;
	}

	public void setEnglish(boolean e) {
		print.setEnglish(true);
	}

	public boolean isEnglish() {
		return print.isEnglish();
	}

	public void setDelay(long delay) {
		this.totalDuration = (delay < 1 ? 1 : delay);
	}

	public long getDelay() {
		return totalDuration;
	}

	public boolean isComplete() {
		return print.isComplete();
	}

	public void setPauseIconAnimationLocation(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public void setMessage(String context, boolean isComplete) {
		print.setMessage(context, messageFont, isComplete);
	}

	public void setMessage(String context) {
		print.setMessage(context, messageFont);
	}

	public String getMessage() {
		return print.getMessage();
	}

	public Print getPrint() {
		return print;
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.doClick();
		}
	}

	@Override
	protected void processTouchPressed() {
		if (!input.isMoving()) {
			super.processTouchPressed();
		}
	}

	@Override
	protected void processTouchReleased() {
		if (!input.isMoving()) {
			super.processTouchReleased();
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (!isVisible()) {
			return;
		}
		super.update(elapsedTime);
		if (print.isComplete()) {
			animation.update(elapsedTime);
		}
		printTime += elapsedTime;
		if (printTime >= totalDuration) {
			printTime = printTime % totalDuration;
			print.next();
		}
	}

	private int tmpColor;

	@Override
	protected synchronized void createCustomUI(GLEx g, int x, int y, int w, int h) {
		if (!isVisible()) {
			return;
		}
		tmpColor = g.color();
		print.draw(g, fontColor);
		if (print.isComplete() && animation != null) {
			if (animation.getSpriteImage() != null) {
				float alpha = g.getAlpha();
				g.setAlpha(1f);
				updateIcon();
				g.draw(animation.getSpriteImage(), dx, dy);
				g.setAlpha(alpha);
			}
		}
		g.setColor(tmpColor);
	}

	@Override
	protected void processTouchDragged() {
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this.input.getTouchDX(), this.input.getTouchDY());
			if (Click != null) {
				Click.DragClick(this, getUITouchX(), getUITouchY());
			}
			this.updateIcon();
		}
		super.dragClick();
	}

	public void setPauseIconAnimation(Animation animation) {
		this.animation = animation;
		if (animation != null) {
			LTexture image = animation.getSpriteImage(0);
			if (image != null) {
				this.dw = image.getWidth();
				this.dh = image.getHeight();
				this.updateIcon();
			}
		}
	}

	private void updateIcon() {
		this.setPauseIconAnimationLocation((int) (getScreenX() + getWidth() - dw / 2 - 20),
				(int) (getScreenY() + getHeight() - dh - 10));
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public LMessage setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
		return this;
	}

	@Override
	public IFont getFont() {
		return getMessageFont();
	}

	public IFont getMessageFont() {
		return messageFont;
	}

	@Override
	public LMessage setFont(IFont font) {
		return this.setMessageFont(font);
	}

	/**
	 * 注入一个实现了IFont接口的字体
	 * 
	 * @param messageFont
	 */
	public LMessage setMessageFont(IFont messageFont) {
		this.messageFont = messageFont;
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {

	}

	@Override
	public String getUIName() {
		return "Message";
	}

	@Override
	public void close() {
		super.close();
		if (print != null) {
			print.close();
			print = null;
		}
		if (animation != null) {
			animation.close();
			animation = null;
		}
	}

}
