package com.daddyeric.guardian.protection;

public class Plot {

    public String world;
    public String owner;
    public String members;
    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;
    public int price;

    public Plot(String world, String owner, String members, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int price) {
	this.world = world;
	this.owner = owner;
	this.members = members;
	this.minX = minX;
	this.minY = minY;
	this.minZ = minZ;
	this.maxX = maxX;
	this.maxY = maxY;
	this.maxZ = maxZ;
	this.price = price;
    }
}
