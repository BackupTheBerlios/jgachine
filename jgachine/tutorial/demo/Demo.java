/*
  small demo for JGachine using the optional scenegraph

  what you should know: (TODO: add some links)
  - little trigonometry
  - scene graph concept
*/
package tutorial.demo;

import java.io.*;
import java.util.*;
import javax.vecmath.*;
import org.jgachine.*;
import org.jgachine.scenegraph.*;
import org.jgachine.signal.*;
import org.jgachine.event.*;

//! sprite mover
class Mover extends Node
{
    Mover(int tid, Vector2f _pos)
    {
	pos=_pos;
	vel=0;
	dir=0;
	ddir=0;
	scale=new Vector2f(64,64);

	// node which draws rotated and scaled car without shadow
	Translate t=new Translate(pos);
	rotate=new Rotate(0);
	Scale s=new Scale(scale);
	t.addNode(rotate.addNode(s.addNode(new Sprite(tid))));

	// node which draws a shadow (black, alpha blended car)
	Recolor shadow = (Recolor)new Recolor(new Color(0.0f,0.0f,0.0f,0.3f)).addNode(t);

	// node which draws car and shadow (car and 2 "shadows" with offset)
	Node carNode = new Node();
	carNode
	    .addNode(new Translate(new Vector2f(6.0f,-6.0f)).addNode(shadow))
	    .addNode(new Translate(new Vector2f(12.0f,-12.0f)).addNode(shadow))
	    .addNode(t);

	// just for fun we paint more than one car
	for (int y=0;y<2;++y)
	    for (int x=0;x<2;++x){
		Vector2f tp = new Vector2f();
		final float delta=(64+10)/2*3;
		tp.add(new Vector2f((float)x*delta,(float)y*delta));
		addNode(new Translate(tp).addNode(carNode));
	}
    }
    
    void step(float dt){
	// drive the car
	pos.add(new Vector2f(vel*dt*(float)Math.sin(dir),vel*dt*(float)Math.cos(dir)));
	// rotate the car
	if (vel>0)
	    dir+=ddir*dt;
	else if (vel<0)
	    dir-=ddir*dt;
	// convert radians to degrees and rotate counter clockwise
	rotate.r=-dir*180.0f/(float)Math.PI;
    }

    //! current car position
    Vector2f pos;
    //! current car velocity
    public float vel;
    //! car direction
    float dir;
    //! car rotational speed (delta dir)
    public float ddir;
    //! size of car / scale
    Vector2f scale;
    //! rotation node in scene graph
    Rotate rotate;
}

public class Demo implements Runnable
{
    Demo()
    {
	// how many input devices are available?
	int devs=JGachine.numDevices();
	// create array holding the current state of each input device
	devState=new DevState[devs];
	// create as many Movers as input devices (each Mover paints 4 cars)
	car=new Mover[devs];
	// dont forget to initialize our input state
	for (int i=0;i<devs;++i) {
	    devState[i]=new DevState((byte)i);
	}
	// register our input callbacks (slots)
	JGachine.input.connect(new Slot(this,"handleDevState"));
    }

    //! handle input - is called when the state of a device changed
    public void handleDevState(DevState s)
    {
	debug("New Input: "+s.devno+": "+s.x+s.y+s.buttons);
	devState[s.devno]=s;
	car[s.devno].ddir=s.x;
	car[s.devno].vel=s.y*170;
	// send message to Server
	if (JGachine.isNetAvailable()) {
	    try {
		JGachine.sendObject(s);
	    }catch(java.io.IOException e){
		debug("lost connection to server => quit");
		JGachine.quit=true;
	    }
	}
    }

    public void run()  throws java.io.IOException
    {
	// create sceneGraph

	// our scene graph root Node
	Node sceneGraph = new Node();

	int track=JGachine.createTexture("data:back.jpg");
	Camera camera = new Camera(new Vector2f(1024/2,768/2));

	List menu = (List)new ArrayList();
	menu.add("Menu Item 1");
	menu.add("Menu Item 2");
	menu.add("Menu Item 3");
	menu.add("Menu Item 4");
	menu.add("Menu Item 5");

	sceneGraph
	    .addNode(camera.addNode(new Scale(new Vector2f(1024,1024)).addNode(new Sprite(track))))
	    .addNode(new Translate(new Vector2f(1024/2,768/2)).addNode(new Scale(new Vector2f(80,80))
								       .addNode(new Window(new Color(1.0f,0.0f,0.0f,0.3f),new Color(0.0f,0.0f,0.0f,1.0f), 0.04f))))
	    .addNode(new Recolor(new Color(1.0f,0.3f,0.3f,0.7f)).addNode(new Translate(new Vector2f(11*32/2+30,5*32/2+64)).addNode(new Scale(new Vector2f(11*32,5*32)).addNode(new Menu(menu,new Color(0.0f,0.0f,0.0f,0.5f))))));

	int cart=JGachine.createTexture("data:car.png");
	for (int i=0;i<car.length;++i) {
	    car[i]=new Mover(cart,new Vector2f((64+10)/2.0f*i,32.0f*i+100.0f));
	    float r=( i   %3)!=0 ? 0.6f:1.0f;
	    float g=((i+1)%3)!=0 ? 0.4f:1.0f;
	    float b=((i+2)%3)!=0 ? 0.6f:1.0f;
	    camera.addNode(new Recolor(new Color(r,g,b,1.0f)).addNode(car[i]));
	}

	// add moving banner
	String bannerStr = "....JGachine is a Game Engine/Machine/Browser......";
	int bannerWidth=32*bannerStr.length();
	Text banner = new Text(bannerStr);
	Vector2f bannerPos = new Vector2f(0.0f,768.0f-36);
	sceneGraph.addNode(new Translate(bannerPos).addNode(new Recolor(new Color(1.0f,0.3f,0.3f,0.9f)).addNode(new Scale(new Vector2f(32.0f,32.0f)).addNode(banner))));

	// add fps display
	Text fpsText = new Text("FPS: ");
	sceneGraph.addNode(new Recolor(new Color(0.3f,0.7f,1.0f,0.9f)).addNode(new Scale(new Vector2f(32.0f,32.0f)).addNode(fpsText)));

	long start = JGachine.time();
	long last = start;
	long mark = last;
	long 
	    cur=0,
	    dt=0,
	    frame=0;
	float fdt=0.002f;
	float averagedt=fdt;
	float dterror=0;
	float fakedt;

	// main game loop
	while (!JGachine.quit) {
	    // get input
	    JGachine.poll();
	    // drive cars
	    for (int i=0;i < car.length;++i)
		car[i].step(fdt);
	    // move banner
	    bannerPos.x-=fdt*75.0f;
	    if (bannerPos.x<-bannerWidth) bannerPos.x=1030;

	    // center the first car
	    if (car.length > 0) {
		camera.rotateTo(car[0].rotate.r);
		camera.scrollTo(car[0].pos);
	    }

	    // paint
	    JGachine.clear();
	    sceneGraph.apply();
	    JGachine.swapBuffers();
	    ++frame;
	    cur = JGachine.time();
	    dt = cur - last;

	    // give other processes some time - limit to 100Hz
	    while (dt<10000) {
		JGachine.uSleep(10000-dt);
		cur = JGachine.time();
		dt = cur - last;
	    }
	    


	    last = cur;
	    fdt=(float)dt/1000000.0f;

	    // smooth the stepsize
	    fakedt=averagedt+dterror/20;
	    averagedt=(averagedt*19+fakedt)/20;
	    dterror+=fdt-fakedt;
	    fdt=fakedt;

	    debug("fdt: "+fdt+" error: "+dterror);
	    // after 200 frames update the fps counter
	    if (frame%200 == 0) {
		long diff = last - mark;
		mark=last;
		float sec=(float)diff/1000000.0f;
		float fps=200.0f/sec;
		fpsText.set(new String("FPS: ")+(int)fps+new String(" / Frames: ")+frame);
	    }
	}
    }

    static protected void debug(String s){
	//	System.out.println("Demo.java: "+s);
    }

    //! our cars
    Mover[] car;
    //! state of out input devices
    DevState[] devState;
}
