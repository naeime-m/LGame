/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite;

import loon.LObject;
import loon.LRelease;
import loon.action.ActionBind;
import loon.action.map.Attribute;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.map.TileMap;
import loon.canvas.LColor;
import loon.geom.RectBox;

public abstract class SpriteBatchObject extends LObject implements Config,
		LRelease, ActionBind {

	float scaleX = 1, scaleY = 1;

	public void setScale(final float s) {
		this.setScale(s, s);
	}

	public void setScale(final float sx, final float sy) {
		if (this.scaleX == sx && this.scaleY == sy) {
			return;
		}
		this.scaleX = sx;
		this.scaleY = sy;
	}

	public float getScaleX() {
		return this.scaleX;
	}

	public float getScaleY() {
		return this.scaleY;
	}

	protected Attribute attribute;

	protected Animation animation;

	protected TileMap tiles;

	protected RectBox rectBox;

	protected float dstWidth, dstHeight;

	protected boolean mirror;

	private LColor filterColor = new LColor(1f, 1f, 1f, 1f);

	public SpriteBatchObject(float x, float y, float dw, float dh,
			Animation animation, TileMap map) {
		this.setLocation(x, y);
		this.tiles = map;
		this.animation = animation;
		this.dstWidth = dw;
		this.dstHeight = dh;
		if (dw < 1 && dh < 1) {
			this.rectBox = new RectBox(x, y,
					animation.getSpriteImage().width(), animation
							.getSpriteImage().height());
		} else {
			this.rectBox = new RectBox(x, y, dw, dh);
		}
	}

	public SpriteBatchObject(float x, float y, Animation animation, TileMap map) {
		this.setLocation(x, y);
		this.tiles = map;
		this.animation = animation;
		this.dstWidth = animation.getSpriteImage().width();
		this.dstHeight = animation.getSpriteImage().height();
		if (dstWidth < 1 && dstHeight < 1) {
			this.rectBox = new RectBox(x, y,
					animation.getSpriteImage().width(), animation
							.getSpriteImage().height());
		} else {
			this.rectBox = new RectBox(x, y, dstWidth, dstHeight);
		}
	}

	public void draw(SpriteBatch batch, float offsetX, float offsetY) {
		float tmp = batch.color();
		batch.setAlpha(_alpha);
		if (!filterColor.equals(1f, 1f, 1f, 1f)) {
			batch.setColor(filterColor);
		}
		if (scaleX == 1 && scaleY == 1) {
			if (mirror) {
				if (getRotation() != 0) {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, getRotation());
					} else {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, dstWidth,
								dstHeight, getRotation());
					}
				} else {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY);
					} else {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, dstWidth,
								dstHeight);
					}
				}
			} else {
				if (getRotation() != 0) {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY,
								getRotation());
					} else {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, dstWidth,
								dstHeight, getRotation());
					}
				} else {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY);
					} else {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, dstWidth,
								dstHeight);
					}
				}
			}
		} else {
			final float width = animation.getSpriteImage().width();
			final float height = animation.getSpriteImage().height();
			if (mirror) {
				if (getRotation() != 0) {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, width * scaleX,
								height * scaleY, getRotation());
					} else {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, dstWidth * scaleX,
								dstHeight * scaleY, getRotation());
					}
				} else {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, width * scaleX, height * scaleY,
								getY() + offsetY);
					} else {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, dstWidth * scaleX,
								dstHeight * scaleY);
					}
				}
			} else {
				if (getRotation() != 0) {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, width
										* scaleX, height * scaleY,
								getRotation());
					} else {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, dstWidth
										* scaleX, dstHeight * scaleY,
								getRotation());
					}
				} else {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, width
										* scaleX, height * scaleY);
					} else {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, dstWidth
										* scaleX, dstHeight * scaleY);
					}
				}
			}
		}
		if (_alpha != 1f || !filterColor.equals(1f, 1f, 1f, 1f)) {
			batch.resetColor();
		}
		batch.setAlpha(tmp);
	}

	public TileMap getTileMap() {
		return tiles;
	}

	public Field2D getField2D() {
		return tiles.getField();
	}

	public void setFilterColor(LColor f) {
		this.filterColor.setColor(f);
	}

	public LColor getFilterColor() {
		return this.filterColor;
	}

	public void setSize(int width, int height) {
		this.dstWidth = width;
		this.dstHeight = height;
	}

	public boolean isCollision(SpriteBatchObject o) {
		RectBox src = getCollisionArea();
		RectBox dst = o.getCollisionArea();
		if (src.intersects(dst)) {
			return true;
		}
		return false;
	}

	public int getWidth() {
		return (int) ((dstWidth > 1 ? (int) dstWidth : animation
				.getSpriteImage().width()) * scaleX);
	}

	public int getHeight() {
		return (int) ((dstHeight > 1 ? (int) dstHeight : animation
				.getSpriteImage().height()) * scaleY);
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public void close() {
		if (animation != null) {
			animation.close();
		}
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation a) {
		this.animation = a;
	}

	public void setIndex(int index) {
		if (animation instanceof AnimationStorage) {
			((AnimationStorage) animation).playIndex(index);
		}
	}

	public boolean isMirror() {
		return mirror;
	}

	public void setMirror(boolean mirror) {
		this.mirror = mirror;
	}

	public boolean isBounded() {
		return false;
	}

	public boolean isContainer() {
		return false;
	}

	public boolean inContains(int x, int y, int w, int h) {
		return false;
	}

	public RectBox getRectBox() {
		return getCollisionArea();
	}

	public int getContainerWidth() {
		return 0;
	}

	public int getContainerHeight() {
		return 0;
	}
}
