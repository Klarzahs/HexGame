package schemmer.hexagon.loader;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import schemmer.hexagon.utils.Log;

public class ImageLoader {
	private GraphicsConfiguration gc;
	
	public ImageLoader(){
		gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}
	
	public void runAllAnnotatedWith(Class<? extends Annotation> annotation) throws Exception {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath()).setScanners(
                        new MethodAnnotationsScanner()));
        Set<Method> methods = reflections.getMethodsAnnotatedWith(annotation);
        if(methods != null){
        	for(Method m : methods){
                try {
                	if(m.getName() == "loadImages")
                		m.invoke(null, gc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        	}
        }
	}
	
	public int getNumberOfAllImagesAnnotatedWith(Class<? extends Annotation> annotation) throws Exception {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath()).setScanners(
                        new FieldAnnotationsScanner()));
        Set<Field> fields = reflections.getFieldsAnnotatedWith(annotation);    
        for (Field f : fields) {
        	ImageNumber an = f.getAnnotation(ImageNumber.class);
            Log.d(""+an.number());
        }
        
        return 0;
	}
}
