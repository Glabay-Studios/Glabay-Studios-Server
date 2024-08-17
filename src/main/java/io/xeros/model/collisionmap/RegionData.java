package io.xeros.model.collisionmap;

public class RegionData {
	private final int regionHash;
    private final int landscape;
    private final int objects;

	private final int x;

	private final int y;

	public int getRegionHash() {
		return regionHash;
	}

	public int getLandscape() {
		return landscape;
	}

	public int getObjects() {
		return objects;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public RegionData(int regionHash, int landscape, int objects, int x,int y) {
		this.regionHash = regionHash;
		this.landscape = landscape;
		this.objects = objects;
		this.x = x;
		this.y = y;
	}
	
}