public class TestSplit {
	public static void main (String[] args) {
		String name = "xxx.map";
		String[] comp = name.split("\\.");
		System.out.println("Found " + comp.length + " components for " + name);
	}

} 