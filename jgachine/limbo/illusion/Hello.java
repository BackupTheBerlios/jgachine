import org.jgachine.*;
import org.jgachine.scenegraph.*;
import javax.vecmath.*;

//! all JGachine games must implement the Runnable interface
class Hello implements Runnable
{
    public void run() throws java.io.IOException {
	// create scenegraph
	Node root = new Node();
	root.addNode(new Translate(new Vector2f(0,32)).addNode(new Scale(new Vector2f(16,16)).addNode(new Text(
		     "Inspired by the book \"Motion ...\"\n"+
		     "by XXX\n"+
		     "You should see a different object after a half rotation"))));
	
	int texID=JGachine.createTexture("data:texture.png");
	Rotate rotate = new Rotate();
	root.addNode(new Translate(new Vector2f(1024/2,768/2+64)).addNode(rotate.addNode(new Scale(new Vector2f(512,512)).addNode(new Sprite(texID)))));

	long start = JGachine.time();
	long last = start;
	long mark = last;
	long 
	    cur=0,
	    dt=0,
	    frame=0;
	float fdt=0.02f;
	float averagedt=fdt;
	float dterror=0;
	float fakedt;

	// main game loop
	while (!JGachine.quit) {
	    // get input
	    JGachine.poll();
	    // clear screen
	    JGachine.clear();
	    // paint scenegraph
	    root.apply();
	    // rotate
	    rotate.r+=fdt*20;
	    // swap front and back buffer
	    JGachine.swapBuffers();

	    cur = JGachine.time();
	    dt = cur - last;

	    // give other processes some time
	    while (dt<20000) {
		JGachine.uSleep(19000);
		cur = JGachine.time();
		dt = cur - last;
	    }
	    while (dt<30000) {
		cur = JGachine.time();
		dt = cur - last;
		}
	    
	    last = cur;
	    fdt=(float)dt/1000000.0f;

	    // smooth the stepsize
	    fakedt=averagedt+dterror/40;
	    averagedt=(averagedt*19+fakedt)/20;
	    dterror+=fdt-fakedt;
	    fdt=fakedt;

	    // after 200 frames update the fps counter
	    if (frame%200 == 0) {
		long diff = last - mark;
		mark=last;
		float sec=(float)diff/1000000.0f;
		float fps=200.0f/sec;
	    }

	}
    }
}
