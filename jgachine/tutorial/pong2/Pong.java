/*
  JGachine tutorial
  a small pong clone
  using the optional scenegraph

  what you should know: (TODO: add some links)
  - a bit of math (Vector2f)
  - a bit of physics (s=V*t)
  - scene graph concept
  - java basics
*/
package tutorial.pong2;

import java.io.*;
import java.util.*;
import javax.vecmath.*;
import org.jgachine.*;
import org.jgachine.scenegraph.*;
import org.jgachine.signal.*;
import org.jgachine.event.*;

//! a racket
/*!
  we start with the rackets

  a pong game has 2 rackets
  they can move up and down
*/
class RacketMover
{
    RacketMover(Vector2f _pos){
	pos = new Vector2f(_pos);
    }

    //! during the game we call step for each frame and pass in the time passed
    void step(float dt){
	// little physics: s=V*t (assuming V is constant)
	pos.y+=vel*dt;

	// do not move out of the screen
	if (pos.y>768/2)
	    pos.y=768/2;
	else if (pos.y<-768/2)
	    pos.y=-768/2;
    }
    
    //! current position
    protected Vector2f pos;

    //! current velocity (may be set directly)
    public float vel;
}


//! a ball
/*!
  a pong game needs of course a ball
*/
class Ball
{
    Ball(){
	pos=new Vector2f(0,0);
	dir=0;
	ddir=1;
	vel=new Vector2f(-200,-200);
    }

    //! is called after a player gets a point
    void restart() {
	pos.x=pos.y=0;
	vel.normalize();
	vel.scale(300);
    }

    void step(float dt){
	// the ball can move in x and y directions
	pos.x+=vel.x*dt;
	pos.y+=vel.y*dt;

	// check collision with upper border
	if (pos.y>768/2) {
	    pos.y=768/2;
	    vel.y=-vel.y;
	}else if (pos.y<-768/2){
	    pos.y=-768/2;
	    vel.y=-vel.y;
	}
	// check left/right out
	if (pos.x<-1024/2) {
	    restart();
	    Pong.win(1);
	}else if (pos.x>1024/2) {
	    restart();
	    Pong.win(0);
	}
	// check if racket hits
	final float srx=1024/2-64-64/2;
	final float erx=srx+64;
	float ydiff;
	ydiff=pos.y-Pong.racket[0].pos.y;
	if (((pos.x>-erx)&&(pos.x<-srx)) && (Math.abs(ydiff)<32) && (vel.x<0)) {
	    vel.x=-vel.x*(1.0f+(float)Pong.devState[0].x/5);
	    vel.y+=ydiff*10;
	    ddir=-ddir;
	}
	ydiff=pos.y-Pong.racket[1].pos.y;
	if (((pos.x> srx)&&(pos.x< erx)) && (Math.abs(ydiff)<32) && (vel.x>0)) {
	    vel.x=-vel.x*(1.0f-(float)Pong.devState[1].x/5);
	    vel.y+=ydiff*10;
	    ddir=-ddir;
	}
	
	dir+=ddir*dt*vel.length();
    }

    //! current position
    Vector2f pos;
    //! velocity
    Vector2f vel;
    //! direction
    float dir;
    //! rotational velocity
    float ddir;
}

//! ball sprite
/*!
  we now have the physics of the ball right
  but of course we must paint it

  we paint a ball consisting of:
  a ball sprite + light map + shadow
*/
class BallSprite extends Node
{
    BallSprite() throws java.io.IOException
    {
	// load textures if not yet loaded
	if (textureID==-1) 
	    textureID=JGachine.createTexture("data:ball.png");
	if (lightMapTextureID==-1) 
	    lightMapTextureID=JGachine.createTexture("data:ball-light.png");

	pos = new Vector2f();

	// node which draws rotated and scaled sprite without shadow (the ball)
	Node ball=new Translate(pos);
	rotate = new Rotate();
	ball.addNode(rotate.addNode(new Scale(size).addNode(new Sprite(textureID))));

	// node which draws the lightmap
	Node lightMap=new Translate(pos).addNode(new Scale(size).addNode(new Sprite(lightMapTextureID)));

	// node which draws the shadow
	Node shadow=new Translate(new Vector2f(12,-12)).addNode(new AdjustColor(new Color(0,0,0,0.3f)).addNode(ball));

	// put it all together
	this.addNode(shadow)
	    .addNode(ball)
	    .addNode(lightMap);
    }

    Rotate rotate;
    Vector2f pos;

    static int textureID=-1;
    static int lightMapTextureID=-1;
    static Vector2f size=new Vector2f(64,64);
}


//! paint blured ballsprite
/*!
  now lets get a bit fancy - we have a lighted ball with shadow
  but to get a visualization for the speed and to make the animation more smooth
  let us add a bluring effect

  the idea is simply to blend old positions of the ball
*/
class BluredBallSprite extends Node
{
    BluredBallSprite() throws java.io.IOException
    {
	// we simply create some BallSprite objects
	balls=new BallSprite[blured];
	for (int i=0;i<blured;++i) {
	    balls[i]=new BallSprite();
	    Node node = balls[i];
	    if (i<blured-1) {
		// we blend all ball sprites except the last one and start with an alpha of startAlpha and fade
		// out linear
		final float startAlpha=0.1f;
		node = new AdjustColor(new Color(1.0f,1.0f,1.0f,startAlpha*((float)i+1)/(blured-1) ) ).addNode(node);
	    }
	    addNode(node);
	}
    }
    void rotateTo(float dir){
	balls[blured-1].rotate.r=dir;
    }
    void moveTo(Vector2f pos){
	for (int i=0;i<blured-1;++i)
	    balls[i].pos.set(balls[i+1].pos);
	balls[blured-1].pos.set(pos);
    }

    // how many sprites do we paint (try to increase or descrease it)
    static final int blured = 6;
    BallSprite balls[];
}

//! moveable and rotateable sprite with shadow (which is blured)
/*!
  \todo fix this - nearly the same as above .... 
*/
class ShadowedSprite extends Node
{
    ShadowedSprite(Node sprite,
		   Vector2f _pos,
		   float dir) 
    {
	pos=new Vector2f[blured];
	for (int i=0;i<blured;++i) {
	    pos[i]=new Vector2f(_pos);
	    
	    // node which draws rotated and scaled sprite without shadow
	    Translate t=new Translate(pos[i]);
	    rotate = new Rotate(dir);
	    t.addNode(rotate.addNode(sprite));

	    // node which draws a shadow (black, alpha blended sprite)
	    AdjustColor shadow = (AdjustColor)new AdjustColor(new Color(0.0f,0.0f,0.0f,0.3f)).addNode(t);

	    Node base = this;
	    if (i<blured-1) {
		base = new AdjustColor(new Color(1.0f,1.0f,1.0f,0.1f*((float)i+1)/(blured-1) ) );
		addNode(base);
	    }
	    base
		.addNode(new Translate(new Vector2f(6.0f,-6.0f)).addNode(shadow))
		.addNode(new Translate(new Vector2f(12.0f,-12.0f)).addNode(shadow))
		.addNode(t);
	}
    }
    void rotateTo(float dir){
	rotate.r=dir;
    }
    void moveTo(Vector2f _pos){
	//	if ((++moved%2)==0)
	for (int i=0;i<blured-1;++i)
	    pos[i].set(pos[i+1]);
	pos[blured-1].set(_pos);
    }

    // how many sprites do we paint
    static final int blured = 8;

    Rotate rotate;
    Vector2f pos[];
    int moved=0;
}

//! simple pong clone
/*!
  now let us put it all together
  
  we must implement Runnable
*/
public class Pong implements Runnable
{
    Pong()
    {
	// how many input devices are available?
	int devs=JGachine.numDevices();
	// create array holding the current state of each input device
	devState=new DevState[devs];
	// create 2 rackets and corresponding sprites
	racket=new RacketMover[2];
	racketSprite=new ShadowedSprite[2];

	// don't forget to initialize our input state
	for (int i=0;i<devs;++i) {
	    devState[i]=new DevState((byte)i);
	}
	// register our input callbacks (slots)
	JGachine.input.connect(new Slot(this,"handleDevState"));
    }

    //! handle input - is called when the state of a device changed
    public void handleDevState(DevState s)
    {
	// we are only interested in the first 2 devices
	if (s.devno>1) return;
	// debug("New Input: "+s.devno+": "+s.x+s.y+s.buttons);
	devState[s.devno]=s;
	// update racket velocity
	racket[s.devno].vel=s.y*400;
    }
    
    //! is called if a player get's a point
    static void win(int who) {
	String s=new Integer(++points[who]).toString();
	if (s.length()<2) s="0"+s;
	pointsText[who].set(s);
    }

    public void run()  throws java.io.IOException
    {
	// create sceneGraph

	// our scene graph root Node
	Node sceneGraph = new Node();

	// set up camera
	Camera camera = new Camera(new Vector2f(1024/2,768/2));

	// add background
	int bgTextureID=JGachine.createTexture("data:back.jpg");
	sceneGraph
	    .addNode(camera.addNode(new Scale(new Vector2f(1024,1024)).addNode(new Sprite(bgTextureID))));

	// add rackets
	for (int i=0;i<racket.length;++i) {
	    float xpos=1024/2-64;
	    racket[i]=new RacketMover(new Vector2f((i==0)?-xpos:xpos,0));
	    float r=( i   %3)!=0 ? 0.6f:1.0f;
	    float g=((i+1)%3)!=0 ? 0.4f:1.0f;
	    float b=((i+2)%3)!=0 ? 0.6f:1.0f;
	    racketSprite[i]=new ShadowedSprite(new Scale(new Vector2f(64,64)).addNode(new Sprite(JGachine.createTexture("data:car.png"))),racket[i].pos,0);
	    camera.addNode(new AdjustColor(new Color(r,g,b,1.0f)).addNode(racketSprite[i]));
	}

	// add ball
	Ball ball = new Ball();
	BluredBallSprite ballSprite= new BluredBallSprite();
	camera.addNode(ballSprite);

	// add point displays
	pointsText = new Text[2];
	pointsText[0]=new Text("00");
	pointsText[1]=new Text("00");

	camera
	    .addNode(new AdjustColor(new Color(1.0f,0.4f,0.6f,0.9f)).addNode(new Translate(new Vector2f(-1024/2   ,768/2-32-5)).addNode(new Scale(new Vector2f(32.0f,32.0f)).addNode(pointsText[0]))))
	    .addNode(new AdjustColor(new Color(0.6f,0.4f,1.0f,0.9f)).addNode(new Translate(new Vector2f( 1024/2-64,768/2-32-5)).addNode(new Scale(new Vector2f(32.0f,32.0f)).addNode(pointsText[1]))));
	

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
	    // move rackets
	    for (int i=0;i < racket.length;++i) {
		racket[i].step(fdt);
		racketSprite[i].moveTo(racket[i].pos);
	    }
	    // move ball
	    ball.step(fdt);
	    ballSprite.rotateTo(ball.dir);
	    ballSprite.moveTo(ball.pos);

	    // paint
	    sceneGraph.apply();
	    JGachine.swapBuffers();
	    ++frame;
	    cur = JGachine.time();
	    dt = cur - last;

	    // give other processes some time
	    while (dt<20000) {
		debug("sleep");
		// JGachine.uSleep(20000-dt);
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

	    debug("fdt: "+fdt+" error: "+dterror);
	    // debug("mem: "+Runtime.getRuntime().freeMemory());

	    // after 200 frames update the fps counter
	    if (frame%200 == 0) {
		//		debug("run gc");
		//		Runtime.getRuntime().gc();

		long diff = last - mark;
		mark=last;
		float sec=(float)diff/1000000.0f;
		float fps=200.0f/sec;
	    }
	}
    }

    static protected void debug(String s){
	//	System.out.println("Pong.java: "+s);
    }

    //! our rackets
    static RacketMover[] racket;
    static ShadowedSprite[] racketSprite;

    //! state of our input devices
    static DevState[] devState;
    //! points
    static Text pointsText[];
    static int points[]={0,0};
	
}
