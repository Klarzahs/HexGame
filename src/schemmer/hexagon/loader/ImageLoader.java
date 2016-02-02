package schemmer.hexagon.loader;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import javax.imageio.ImageIO;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import schemmer.hexagon.game.Main;
import schemmer.hexagon.utils.Log;

public class ImageLoader {
	private GraphicsConfiguration gc;
	
	public static int progress = -1;
	public static int maxProgress = 0;
	public static int[] progressArr;
	
	public ImageLoader(Main main, Class<? extends Annotation> annotation, Class<? extends Annotation> annotation2){
		gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		try{
			this.runAllAnnotatedWith(annotation);
			this.getNumberOfAllImagesAnnotatedWith(annotation2);
		}
		catch(Exception e){
			Log.e(e.getCause().getMessage());
		}
	}
	
	public void getNumberOfAllImagesAnnotatedWith(Class<? extends Annotation> annotation) throws Exception {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath()).setScanners(
                        new FieldAnnotationsScanner()));
        Set<Field> fields = reflections.getFieldsAnnotatedWith(annotation); 
        Field[] fieldsArr = fields.toArray(new Field[fields.size()]);
        progressArr = new int[fields.size()];
        for (int i = 0; i < fieldsArr.length; i++) {
        	ImageNumber an = fieldsArr[i].getAnnotation(ImageNumber.class);
        	progressArr[i] = an.number();
        	maxProgress += an.number();
            //Log.d(""+an.number());
        }
        Log.d("Max Progress: "+maxProgress);
	}
	
	public void runAllAnnotatedWith(Class<? extends Annotation> annotation) throws Exception {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath()).setScanners(
                        new MethodAnnotationsScanner()));
        Set<Method> methods = reflections.getMethodsAnnotatedWith(annotation);
        progress = 0;
        if(methods != null){
        	for(Method m : methods){
                try {
                	if(m.getName() == "loadImages"){
                		m.invoke(null, gc);
                	}
                } catch (Exception e) {
                    e.printStackTrace();
                }
        	}
        }
	}
	
	public static BufferedImage loadImage(String s){
		try{
			progress = progress + 1;
			return ImageIO.read(ImageLoader.class.getResourceAsStream(s));
		}catch(Exception e){
			Log.d("Couldn't load "+e.getMessage());
			return null;
		}
	}
	
	public boolean isFinishedLoading(){
		if(progress == maxProgress) return true;
		return false;
	}
}
