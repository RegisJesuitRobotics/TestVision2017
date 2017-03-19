package org.usfirst.frc.team3279.vision;


public class RangeFinder {
	double distance;
	public enum DirectionToTurn  {Left, Straight, Right};
	double leftHeight;
	double rightHeight;
	double camerFOV = 68.5;
	double halfFOV = camerFOV/2;
	DirectionToTurn directionToTurn = DirectionToTurn.Straight;
	
	public double getCamerFOV() {
		return camerFOV;
	}
	public void setCamerFOV(double camerFOV) {
		this.camerFOV = camerFOV;
	}
	public double getDistance() {
		return distance;
	}
	public double getLeftHeight() {
		return leftHeight;
	}
	public double getRightHeight() {
		return rightHeight;
	}
	public DirectionToTurn getDirectionToTurn() {
		return directionToTurn;
	}

	
	
}
