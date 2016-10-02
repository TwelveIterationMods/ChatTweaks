package net.blay09.mods.bmc.image.renderable;

import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.api.image.IAnimatedChatRenderable;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class AnimatedChatRenderable extends StaticChatRenderable implements IAnimatedChatRenderable {

	private static class DrawImageCallback implements ImageObserver {
		private boolean isReady;

		public void prepare() {
			isReady = false;
		}

		@Override
		public boolean imageUpdate(Image img, int infoFlags, int x, int y, int width, int height) {
			if((infoFlags & ALLBITS) == ALLBITS) {
				isReady = true;
			} else if((infoFlags & ABORT) == ABORT) {
				isReady = true;
			}
			return false;
		}

		public boolean isReady() {
			return isReady;
		}
	}

	private int[] frameTimes;
	private boolean cumulativeRendering;
	private int animationFrames;
	private int spriteSheetWidth;
	private int spriteSheetHeight;

	private int animationTime;
	private int currentFrameTime;
	private int currentFrame;
	private int currentFrameTexCoordX;
	private int currentFrameTexCoordY;
	private long lastRenderTime;

	public AnimatedChatRenderable(BufferedImage[] images, int[] frameTime, int[] offsetX, int[] offsetY) {
		this.frameTimes = frameTime;
		width = images[0].getWidth();
		height = images[0].getHeight();
		animationFrames = images.length;
		calculateScale();
		spriteSheetWidth = width * images.length;
		spriteSheetHeight = height;
		int maxTextureSize = Minecraft.getGLMaximumTextureSize();
		if(spriteSheetWidth > maxTextureSize) {
			int overflowX = (maxTextureSize % width);
			spriteSheetWidth = maxTextureSize - overflowX;
			spriteSheetHeight = (int) (height * (Math.ceil(overflowX / maxTextureSize) + 1));
			if(spriteSheetHeight > maxTextureSize) {
				loadBuffer = images[0];
				return;
			}
		}
		int framesPerX = spriteSheetWidth / width;
		int framesPerY = spriteSheetHeight / height;
		loadBuffer = new BufferedImage(spriteSheetWidth, spriteSheetHeight, BufferedImage.TYPE_INT_ARGB);
		DrawImageCallback callback = new DrawImageCallback();
		Graphics2D g = loadBuffer.createGraphics();
		for(int y = 0; y < framesPerY; y++) {
			for(int x = 0; x < framesPerX; x++) {
				int frameIdx = x + y * framesPerX;
				if(cumulativeRendering) {
					if (frameIdx > 0) {
						int prevFrameIdx = frameIdx - 1;
						int prevFrameX = prevFrameIdx % framesPerX;
						int prevFrameY = (int) Math.floor((float) prevFrameIdx / (float) framesPerX);
						int dx = x * width - prevFrameX * width;
						int dy = y * height - prevFrameY * height;
						g.copyArea(prevFrameX * width, prevFrameY * height, width, height, dx, dy);
					}
				}
				callback.prepare();
				if(!g.drawImage(images[frameIdx], x * width + offsetX[frameIdx], y * height + offsetY[frameIdx], callback)) {
					while (!callback.isReady()) ;
				}
			}
		}
	}

	@Override
	public void updateAnimation() {
		long now = System.currentTimeMillis();
		if(lastRenderTime == 0) {
			lastRenderTime = now;
			currentFrameTime = frameTimes[0];
		}
		animationTime += now - lastRenderTime;
		int lastFrame = currentFrame;
		while(animationTime > currentFrameTime) {
			animationTime -= currentFrameTime;
			currentFrame++;
			if(currentFrame >= animationFrames) {
				currentFrame = 0;
			}
			currentFrameTime = frameTimes[currentFrame];
		}
		if(currentFrame != lastFrame) {
			currentFrameTexCoordX = currentFrame * width;
			// TODO turn this into math when you got a brain
			while(currentFrameTexCoordX > spriteSheetWidth) {
				currentFrameTexCoordX -= spriteSheetWidth;
				currentFrameTexCoordY += height;
			}
		}
		lastRenderTime = now;
	}

	@Override
	public int getSheetWidth() {
		return spriteSheetWidth;
	}

	@Override
	public int getSheetHeight() {
		return spriteSheetHeight;
	}

	@Override
	public int getTexCoordX() {
		return currentFrameTexCoordX;
	}

	@Override
	public int getTexCoordY() {
		return currentFrameTexCoordY;
	}

	public void setCumulativeRendering(boolean cumulativeRendering) {
		this.cumulativeRendering = cumulativeRendering;
	}

	public boolean isCumulativeRendering() {
		return cumulativeRendering;
	}
}
