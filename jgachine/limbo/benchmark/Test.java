import java.io.*;
import org.jgachine.Color;
import org.jgachine.dscenegraph.*;
import javax.vecmath.*;
import java.lang.System.currentTimeMillis;

public class Test
{
    static public void main(String args[]) 
	throws IOException
    {
	OutputStream o=new ByteArrayOutputStream();

	// our scene graph root Node
	Node sceneGraph = new Node();
	Camera camera = new Camera(new Vector2f(1024/2,768/2));
	sceneGraph
	    .addNode(camera.addNode(new Scale(new Vector2f(1024,1024)).addNode(new Sprite("data:back.jpg"))));
	for (int i=0;i<2;++i) {
	    float xpos=1024/2-64;
	    float r=( i   %3)!=0 ? 0.6f:1.0f;
	    float g=((i+1)%3)!=0 ? 0.4f:1.0f;
	    float b=((i+2)%3)!=0 ? 0.6f:1.0f;
	    Color color = new Color(r,g,b,1.0f);
	    camera.addNode(new Translate(new Vector2f((i==0)?-xpos:xpos,0))
			   .addNode(new Scale(new Vector2f(30,30)).addNode(new Recolor(color).addNode(new Sprite("foo")))));
	}
	camera.addNode(new Translate(new Vector2f()).addNode(new Rotate().addNode(new Sprite("bar"))));

	ObjectOutputStream objOut=new ObjectOutputStream(o);

	long start = System.currentTimeMillis();
	for (int i=0;i<10;++i) {
	    objOut.writeObject(sceneGraph);
	    objOut.flush();
	    objOut.reset();
	}
	long cur = System.currentTimeMillis();
	System.out.println(cur-start);
    }
}
