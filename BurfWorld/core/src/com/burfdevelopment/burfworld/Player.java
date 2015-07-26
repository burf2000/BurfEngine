package com.burfdevelopment.burfworld;

import com.badlogic.gdx.math.Vector3;

public class Player {
	private Vector3 position;
	private float yaw = 0;
	private float pitch = 0;
	private float speed = 0.3f;
	private float rotSpeed = 2f;
	private float lookUpSpeed = 0.05f;
	
	public Player(){
		position = new Vector3(0,-0.5f, 0);
	}
	
	public Vector3 getPos(){
		return position;
	}

	public float getPitch(){
		return pitch;
	}

	public float getYaw(){
		return yaw;
	}
	
	public void moveForward(){
		Vector3 move = Vector3.Zero.cpy();
		move.x = (float) Math.sin(Math.toRadians(yaw));
		move.z = (float) -Math.cos(Math.toRadians(yaw));
		move.scl(speed);
		position.add(move);
	}
	
	public void moveBackward(){
		Vector3 move = Vector3.Zero.cpy();
		move.x = (float) -Math.sin(Math.toRadians(yaw));
		move.z = (float) Math.cos(Math.toRadians(yaw));
		move.scl(speed);
		position.add(move);
	}
	
	public void moveLeft(){
		Vector3 move = Vector3.Zero.cpy();
		move.x = (float) Math.sin(Math.toRadians(yaw - 90));
		move.z = (float) -Math.cos(Math.toRadians(yaw - 90));
		move.scl(speed);
		position.add(move);
	}
	
	public void moveRight(){
		Vector3 move = Vector3.Zero.cpy();
		move.x = (float) Math.sin(Math.toRadians(yaw + 90));
		move.z = (float) -Math.cos(Math.toRadians(yaw + 90));
		move.scl(speed);
		position.add(move);
	}

	public void lookUp(){
		pitch -= lookUpSpeed;
	}

	public void lookDown(){
		pitch += lookUpSpeed;
	}
	public void turnLeft(){
		yaw -= rotSpeed;
	}
	
	public void turnRight(){
		yaw += rotSpeed;
	}
}
