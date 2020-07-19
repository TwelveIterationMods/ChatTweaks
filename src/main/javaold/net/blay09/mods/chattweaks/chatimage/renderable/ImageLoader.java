package net.blay09.mods.chattweaks.chatimage.renderable;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.api.IChatRenderable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

public class ImageLoader {

    public static IChatRenderable loadImage(ResourceLocation resourceLocation) {
        try {
            IResource resource = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
            return loadImage(resource.getInputStream());
        } catch (IOException e) {
            ChatTweaks.logger.error("Failed to load inbuilt image {}: ", resourceLocation, e);
            return NullRenderable.INSTANCE;
        }
    }

    private static IChatRenderable loadImage(Object obj) throws IOException {
        try (ImageInputStream in = ImageIO.createImageInputStream(obj)) {
            if (in == null) {
                throw new IOException("Failed to load image: input stream is null");
            }
            Iterator<ImageReader> it = ImageIO.getImageReaders(in);
            if (it.hasNext()) {
                ImageReader reader = it.next();
                reader.setInput(in);
                int numImages = reader.getNumImages(true);
                if (numImages > 1) {
                    int[] frameTime = new int[numImages];
                    int[] offsetX = new int[numImages];
                    int[] offsetY = new int[numImages];
                    BufferedImage[] images = new BufferedImage[numImages];
                    for (int i = 0; i < images.length; i++) {
                        images[i] = reader.read(reader.getMinIndex() + i);
                        IIOMetadata metadata = reader.getImageMetadata(i);
                        String metaFormatName = metadata.getNativeMetadataFormatName();
                        if (metaFormatName == null) {
                            throw new IOException("Failed to load image: meta format name is null");
                        }
                        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
                        NodeList childNodes = root.getChildNodes();
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            if (childNodes.item(j).getNodeName().equalsIgnoreCase("GraphicControlExtension")) {
                                frameTime[i] = Integer.parseInt(((IIOMetadataNode) childNodes.item(j)).getAttribute("delayTime")) * 10;
                            }
                            if (childNodes.item(j).getNodeName().equalsIgnoreCase("ImageDescriptor")) {
                                try {
                                    offsetX[i] = Integer.parseInt(((IIOMetadataNode) childNodes.item(j)).getAttribute("imageLeftPosition"));
                                } catch (NumberFormatException ignored) {
                                }
                                try {
                                    offsetY[i] = Integer.parseInt(((IIOMetadataNode) childNodes.item(j)).getAttribute("imageTopPosition"));
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    }
                    IIOMetadata metadata = reader.getImageMetadata(0);
                    String metaFormatName = metadata.getNativeMetadataFormatName();
                    IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
                    NodeList childNodes = root.getChildNodes();
                    boolean cumulativeRendering = true;
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        if (childNodes.item(i).getNodeName().equalsIgnoreCase("GraphicControlExtension")) {
                            cumulativeRendering = ((IIOMetadataNode) childNodes.item(i)).getAttribute("disposalMethod").equals("doNotDispose");
                            break;
                        }
                    }
                    AnimatedChatRenderable image = new AnimatedChatRenderable(images, frameTime, offsetX, offsetY);
                    image.setCumulativeRendering(cumulativeRendering);
                    return image;
                } else {
                    BufferedImage image = reader.read(0);
                    return new StaticChatRenderable(image);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
        return NullRenderable.INSTANCE;
    }
}
