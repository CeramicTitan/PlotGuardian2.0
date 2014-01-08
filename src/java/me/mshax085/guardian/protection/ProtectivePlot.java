package java.me.mshax085.guardian.protection;

public class ProtectivePlot {

    public String world;
    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;

    public ProtectivePlot(String world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
	this.world = world;
	this.minX = minX;
	this.minY = minY;
	this.minZ = minZ;
	this.maxX = maxX;
	this.maxY = maxY;
	this.maxZ = maxZ;
    }
}
