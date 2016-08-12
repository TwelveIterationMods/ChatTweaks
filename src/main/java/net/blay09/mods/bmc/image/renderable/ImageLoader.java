package net.blay09.mods.bmc.image.renderable;

import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.w3c.dom.NodeList;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Iterator;
import java.util.Locale;

public class ImageLoader {

	private static final int MAX_CACHE_TIME = 1000 * 60 * 60 * 24 * 7;

	public static IChatRenderable loadImage(URI uri, File saveToFile) throws MalformedURLException {
		if(saveToFile.exists() && saveToFile.lastModified() - System.currentTimeMillis() <= MAX_CACHE_TIME) {
			try {
				return loadImageInternal(new FileInputStream(saveToFile), null);
			} catch (FileNotFoundException ignored) {}
		} else {
			try (InputStream in = uri.toURL().openStream()) {
				return loadImageInternal(in, saveToFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static IChatRenderable loadImage(ResourceLocation resourceLocation) {
		try {
			IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
			if(resource != null) {
				return loadImageInternal(resource.getInputStream(), null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IChatRenderable loadImage(InputStream in, File saveToFile) {
		return loadImageInternal(in, saveToFile);
	}

	private static IChatRenderable loadImageInternal(Object obj, File saveToFile) {
		try(ImageInputStream in = ImageIO.createImageInputStream(obj)) {
			Iterator<ImageReader> it = ImageIO.getImageReaders(in);
			if(it.hasNext()) {
				ImageReader reader = it.next();
				reader.setInput(in);
				int numImages = reader.getNumImages(true);
				if(numImages > 1) {
					ImageWriter writer = null;
					ImageOutputStream out = null;
					if(saveToFile != null) {
						out = ImageIO.createImageOutputStream(saveToFile);
						writer = ImageIO.getImageWriter(reader);
						writer.setOutput(out);
					}
					int[] frameTime = new int[numImages];
					int[] offsetX = new int[numImages];
					int[] offsetY = new int[numImages];
					BufferedImage[] images = new BufferedImage[numImages];
					for(int i = 0; i < images.length; i++) {
						images[i] = reader.read(reader.getMinIndex() + i);
						IIOMetadata metadata = reader.getImageMetadata(i);
						String metaFormatName = metadata.getNativeMetadataFormatName();
						IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
						NodeList childNodes = root.getChildNodes();
						for(int j = 0; j < childNodes.getLength(); j++) {
							if(childNodes.item(j).getNodeName().equalsIgnoreCase("GraphicControlExtension")) {
								frameTime[i] = Integer.parseInt(((IIOMetadataNode) childNodes.item(j)).getAttribute("delayTime")) * 10;
							}
							if(childNodes.item(j).getNodeName().equalsIgnoreCase("ImageDescriptor")) {
								try {
									offsetX[i] = Integer.parseInt(((IIOMetadataNode) childNodes.item(j)).getAttribute("imageLeftPosition"));
								} catch (NumberFormatException ignored) {}
								try {
									offsetY[i] = Integer.parseInt(((IIOMetadataNode) childNodes.item(j)).getAttribute("imageTopPosition"));
								} catch (NumberFormatException ignored) {}
							}
						}
						if(writer != null) {
							ImageReadParam imageReadParam = new ImageReadParam();
							IIOImage ioImage = reader.readAll(i, imageReadParam);
							ImageWriteParam imageWriteParam = new ImageWriteParam(Locale.ENGLISH);
							writer.prepareWriteSequence(metadata);
							writer.writeToSequence(ioImage, imageWriteParam);
						}
					}
					if(out != null) {
						out.close();
					}
					IIOMetadata metadata = reader.getImageMetadata(0);
					String metaFormatName = metadata.getNativeMetadataFormatName();
					IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
					NodeList childNodes = root.getChildNodes();
					boolean cumulativeRendering = true;
					for(int i = 0; i < childNodes.getLength(); i++) {
						if(childNodes.item(i).getNodeName().equalsIgnoreCase("GraphicControlExtension")) {
							cumulativeRendering = ((IIOMetadataNode) childNodes.item(i)).getAttribute("disposalMethod").equals("doNotDispose");
							break;
						}
					}
					AnimatedChatRenderable image = new AnimatedChatRenderable(images, frameTime, offsetX, offsetY);
					image.setCumulativeRendering(cumulativeRendering);
					return image;
				} else {
					BufferedImage image = reader.read(0);
					if(saveToFile != null) {
						try(ImageOutputStream out = ImageIO.createImageOutputStream(saveToFile)) {
							ImageWriter writer = ImageIO.getImageWriter(reader);
							writer.setOutput(out);
							writer.write(image);
						}
					}
					return new StaticChatRenderable(image);
				}
			}
		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
}
